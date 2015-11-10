// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.demo;

import java.io.File;
import java.io.FileFilter;

import net.dermetfan.gdx.scenes.scene2d.ui.FileChooser.Listener;
import net.dermetfan.gdx.scenes.scene2d.ui.TreeFileChooser;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.spriter.SpriterAnimator;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.loader.SpriterDataLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SpriterDemoApp implements ApplicationListener {

	// Display stuff
	ShapeRenderer renderer;
	SpriteBatch batch;
	OrthographicCamera camera;
	Skin skin;

	// Widgets
	Stage stage;
	Table rootTable;
	Label spriterContainer;
	SelectBox<FileHandle> fileChooser;
	TreeFileChooser fileSelector;
	SelectBox<SpriterAnimator> entityChooser;
	SelectBox<String> animationChooser;
	Slider timeSlider;
	ChangeListener timeSliderListener;
	Label timeLabel;
	CheckBox playCheckbox;

	// Data
	AssetManager assetManager;
	Array<FileHandle> files = new Array<FileHandle>();
	Array<SpriterAnimator> animators = new Array<SpriterAnimator>();

	// Current data
	FileHandle file = null;
	SpriterAnimator animator = null;
	boolean play = true;

	@Override
	public void create() {
		// Initialize object
		renderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager = new AssetManager(resolver);
		assetManager.setLoader(SpriterData.class, new SpriterDataLoader(resolver));

		assetManager.load("uiskin.json", Skin.class);
		assetManager.finishLoading();

		// Setup screen
		stage = new Stage(new ScreenViewport(camera), batch);

		skin = assetManager.get("uiskin.json");

		rootTable = new Table(skin);

		final Stack contentTable = new Stack();

		spriterContainer = new Label("No animator", skin) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				if (animator == null) {
					super.draw(batch, parentAlpha);
				} else {
					// Update position
					animator.setPosition(getX() + getWidth() / 2f, getY() + getHeight() / 4f);

					// Draw position
					renderer.circle(animator.getX(), animator.getY(), 1f);

					// Draw animator
					animator.draw(batch, renderer);
				}
			}
		};

		fileSelector = new TreeFileChooser(skin, new Listener() {
			@Override
			public void choose(Array<FileHandle> files) {
				choose(files.first());
			}

			@Override
			public void choose(FileHandle file) {
				files.add(file);
				loadSpriterFile(file);
				contentTable.removeActor(fileSelector);
				contentTable.add(spriterContainer);
			}

			@Override
			public void cancel() {
				contentTable.removeActor(fileSelector);
				contentTable.add(spriterContainer);
			}
		});
		fileSelector.add(Gdx.files.external("/"));
		fileSelector.setDirectoriesChoosable(false);
		fileSelector.setNewFilesChoosable(false);
		fileSelector.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory())
					return true;

				String extension = SpriterDemoUtils.getFileExtension(file.getName());
				return "scml".equalsIgnoreCase(extension) || "scon".equalsIgnoreCase(extension);
			}
		});

		contentTable.add(spriterContainer);

		Table menuTable = new Table(skin);

		Table selectionTable = new Table(skin);

		fileChooser = new SelectBox<FileHandle>(skin);
		fileChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				loadSpriterFile(fileChooser.getSelected());
			}
		});

		Button fileButton = new TextButton("Add Spriter file", skin);
		fileButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				contentTable.removeActor(spriterContainer);
				contentTable.add(fileSelector);
			}
		});

		entityChooser = new SelectBox<SpriterAnimator>(skin);
		entityChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setAnimator(entityChooser.getSelected());
			}
		});

		animationChooser = new SelectBox<String>(skin);
		animationChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setAnimation(animationChooser.getSelected());
			}
		});

		selectionTable.row().pad(3f);
		selectionTable.add(new Label("File", skin)).right();
		selectionTable.add(fileChooser).fill();
		selectionTable.row().pad(3f);
		selectionTable.add(fileButton).expand().colspan(2).right();
		selectionTable.row().pad(3f);
		selectionTable.add(new Label("Entity", skin)).right();
		selectionTable.add(entityChooser).fill();
		selectionTable.row().pad(3f);
		selectionTable.add(new Label("Animation", skin)).right();
		selectionTable.add(animationChooser).fill();

		Table controlTable = new Table(skin);

		timeSlider = new Slider(0f, 2000f, 1, false, skin);
		timeSliderListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (animator == null)
					return;
				animator.setTime(timeSlider.getValue());
				animator.update(0f);
			}
		};
		timeSlider.addListener(timeSliderListener);

		timeLabel = new Label("---", skin);

		playCheckbox = new CheckBox("Play", skin);
		playCheckbox.setChecked(play);
		playCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				play = playCheckbox.isChecked();
			}
		});

		controlTable.row();
		controlTable.add(new Label("Timeline", skin)).expandX().align(Align.bottom);
		controlTable.row();
		controlTable.add(timeSlider).expandX().fillX();
		controlTable.row();
		controlTable.add(timeLabel).expandX().align(Align.top);
		controlTable.row();
		controlTable.add(playCheckbox).expandX().fillX();

		menuTable.add(selectionTable);
		menuTable.add(controlTable).expandX().fill();

		rootTable.row();
		rootTable.add(contentTable).expand().fill();
		rootTable.row();
		rootTable.add(menuTable).expandX().fillX();

		stage.addActor(rootTable);
		rootTable.setFillParent(true);
		// SpriterDemoUtils.debug(rootTable);

		Gdx.input.setInputProcessor(stage);

		Array<FileHandle> spriterFiles = SpriterDemoUtils.findFiles(new String[] { "scml", "scon" });
		for (FileHandle f : spriterFiles) {
			files.add(new FileHandle(f.file()) {
				@Override
				public String toString() {
					return name();
				}
			});
		}
		fileChooser.setItems(files);

		if (files.size > 0)
			loadSpriterFile(files.first());
	}

	private void loadSpriterFile(FileHandle file) {

		AssetDescriptor<SpriterData> desc = new AssetDescriptor<SpriterData>(file, SpriterData.class);
		assetManager.load(desc);

		boolean done = false;
		boolean failed = false;
		while (!done) {
			try {
				assetManager.finishLoading();
				done = true;
			} catch (GdxRuntimeException ex) {
				failed = true;
				new Dialog("Loading error", skin, "dialog")
				.text(ex.getLocalizedMessage())
				.button("I understand", true)
				.key(Keys.ENTER, true)
				.key(Keys.ESCAPE, true)
				.show(stage);
			}
		}
		if (failed)
			return;

		SpriterData data = assetManager.get(desc);

		animators.clear();

		for (SpriterEntity entity : data.entities) {
			animators.add(new SpriterAnimator(entity) {
				@Override
				public String toString() {
					SpriterEntity entity = getEntity();
					return entity.id + ": " + entity.name;
				}
			});
		}

		fileChooser.setSelected(file);

		entityChooser.setItems(animators);

		if (animators.size > 0)
			setAnimator(animators.first());
	}

	private void setAnimator(SpriterAnimator anim) {
		this.animator = anim;

		entityChooser.setSelected(animator);

		Array<String> anims = new Array<String>();
		for (String animation : animator.getAnimations())
			anims.add(animation);

		animationChooser.setItems(anims);
		animationChooser.setSelectedIndex(0);
	}

	private void setAnimation(String anim) {
		animator.play(anim);
		SpriterAnimation animation = animator.getCurrentAnimation();
		animation.looping = true;
		timeSlider.setRange(0f, animation.length);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();

		if (animator != null) {
			if (play) {
				animator.update(delta);
				timeSlider.removeListener(timeSliderListener);
				timeSlider.setValue(animator.getTime());
				timeSlider.addListener(timeSliderListener);
			}
			timeLabel.setText(String.format("[%4.0f / %.0f]", animator.getTime(), animator.getLength()));
		}
		stage.act(delta);
		camera.update();

		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Line);
		stage.draw();
		renderer.end();

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		if (animator != null)
			animator.setPosition(width / 4f, 12f);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		renderer.dispose();
		batch.dispose();
		assetManager.dispose();
	}
}
