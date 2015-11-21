// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.demo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.spriter.FrameMetadata;
import com.badlogic.gdx.spriter.SpriterAnimationListener;
import com.badlogic.gdx.spriter.SpriterAnimator;
import com.badlogic.gdx.spriter.SpriterTestData;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.data.SpriterVarValue;
import com.badlogic.gdx.spriter.demo.SpriterDemoUtils.PrettyDisplayFileHandle;
import com.badlogic.gdx.spriter.loader.SpriterDataLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SpriterDemoApp implements ApplicationListener {

	// Display stuff
	ShapeRenderer renderer;
	SpriteBatch batch;
	OrthographicCamera camera;
	OrthographicCamera spriterCamera;
	Skin skin;

	// Widgets
	Stage stage;
	Table rootTable;
	Label metaLabel;
	Label fpsLabel;
	Label spriterPlaceholder;
	SelectBox<FileHandle> fileChooser;
	SelectBox<SpriterAnimator> entityChooser;
	SelectBox<String> animationChooser;
	SelectBox<SpriterCharacterMap> charmapChooser;
	Slider timeSlider;
	ChangeListener timeSliderListener;
	Label timeLabel;
	CheckBox playCheckbox;
	CheckBox transitionCheckbox;

	// Data
	AssetManager assetManager;
	Array<FileHandle> files = new Array<FileHandle>();
	Array<SpriterAnimator> animators = new Array<SpriterAnimator>();

	// Current data
	FileHandle file = null;
	SpriterAnimator animator = null;
	boolean play = true;
	boolean transition = false;
	SelectBox<?> lastUsedSelectBox = null;
	int offsetX, offsetY;

	@Override
	public void create() {
		// Initialize object
		renderer = new ShapeRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		spriterCamera = new OrthographicCamera();

		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager = new AssetManager(resolver);
		assetManager.setLoader(SpriterData.class, new SpriterDataLoader(resolver));

		assetManager.load("uiskin.json", Skin.class);
		assetManager.finishLoading();

		// Setup screen
		stage = new Stage(new ScreenViewport(camera), batch);

		skin = assetManager.get("uiskin.json");

		rootTable = new Table(skin);

		metaLabel = new Label("Meta: ", skin);
		metaLabel.setWrap(true);
		metaLabel.setAlignment(Align.topLeft);

		fpsLabel = new Label("FPS: ", skin);

		spriterPlaceholder = new Label("No animator", skin);
		spriterPlaceholder.setAlignment(Align.center);

		Table menuTable = new Table(skin);

		Table selectionTable = new Table(skin);

		fileChooser = new SelectBox<FileHandle>(skin);
		fileChooser.setName("Files");
		fileChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				loadSpriterFile(fileChooser.getSelected());
				lastUsedSelectBox = fileChooser;
			}
		});

		entityChooser = new SelectBox<SpriterAnimator>(skin);
		entityChooser.setName("Entities");
		entityChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setAnimator(entityChooser.getSelected());
				lastUsedSelectBox = entityChooser;
			}
		});

		animationChooser = new SelectBox<String>(skin);
		animationChooser.setName("Animations");
		animationChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setAnimation(animationChooser.getSelected());
				lastUsedSelectBox = animationChooser;
			}
		});

		charmapChooser = new SelectBox<SpriterCharacterMap>(skin);
		charmapChooser.setName("Charmaps");
		charmapChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setCharacterMap(charmapChooser.getSelected());
				lastUsedSelectBox = charmapChooser;
			}
		});

		selectionTable.row().pad(3f);
		selectionTable.add(new Label("File", skin)).right();
		selectionTable.add(fileChooser).fill();
		selectionTable.row().pad(3f);
		selectionTable.add(new Label("Entity", skin)).right();
		selectionTable.add(entityChooser).fill();
		selectionTable.row().pad(3f);
		selectionTable.add(new Label("Animation", skin)).right();
		selectionTable.add(animationChooser).fill();
		selectionTable.row().pad(3f);
		selectionTable.add(new Label("Character map", skin)).right();
		selectionTable.add(charmapChooser).fill();

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
				boolean playing = playCheckbox.isChecked();
				playCheckbox.setText(playing ? "Play" : "Pause");
				play = playing;
			}
		});

		transitionCheckbox = new CheckBox("Transition", skin);
		transitionCheckbox.setChecked(transition);
		transitionCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				transition = transitionCheckbox.isChecked();
			}
		});

		controlTable.row();
		controlTable.add(new Label("Timeline", skin)).colspan(2).expandX().align(Align.bottom);
		controlTable.row();
		controlTable.add(timeSlider).colspan(2).expandX().fillX();
		controlTable.row();
		controlTable.add(timeLabel).colspan(2).expandX().align(Align.top);
		controlTable.row();
		controlTable.add(playCheckbox).expandX().fillX();
		controlTable.add(transitionCheckbox).expandX().fillX();
		controlTable.row();
		controlTable.add(fpsLabel).colspan(2).expandX().padRight(10f).right();

		menuTable.add(selectionTable);
		menuTable.add(controlTable).expandX().fill();

		Stack contentStack = new Stack();
		contentStack.add(spriterPlaceholder);
		contentStack.add(metaLabel);

		// rootTable.row().pad(3f);
		// rootTable.add(metaLabel).expandX().left();
		rootTable.row();
		rootTable.add(contentStack).expand().fill();
		rootTable.row();
		rootTable.add(menuTable).expandX().fillX();

		stage.addActor(rootTable);
		rootTable.setFillParent(true);
		// SpriterDemoUtils.debug(rootTable);

		InputProcessor globalInput = new InputProcessor() {
			int firstX, firstY;

			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.UP:
				case Keys.Z:
				case Keys.W:
					if (lastUsedSelectBox != null)
						lastUsedSelectBox.setSelectedIndex(Math.max(lastUsedSelectBox.getSelectedIndex() - 1, 0));
					break;
				case Keys.DOWN:
				case Keys.S:
					if (lastUsedSelectBox != null)
						lastUsedSelectBox.setSelectedIndex(Math.min(lastUsedSelectBox.getSelectedIndex() + 1, lastUsedSelectBox.getItems().size - 1));
					break;

				default:
					break;
				}
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				firstX = screenX;
				firstY = screenY;
				return true;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				int x = screenX - firstX;
				int y = screenY - firstY;
				offsetX += x;
				offsetY -= y;
				firstX = screenX;
				firstY = screenY;
				return true;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				spriterCamera.zoom += amount * 0.05f;
				return false;
			}
		};
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, globalInput));

		// Add default test resources
		Array<String> testResources = new Array<String>(SpriterTestData.scml);
		testResources.addAll(SpriterTestData.scon);
		testResources.sort();
		for(String path : testResources)
			files.add(new PrettyDisplayFileHandle(Gdx.files.internal(path.replaceFirst("/", ""))));

		// Also go discover some unknown exotic resources! (won't work in jar though)
		Array<FileHandle> spriterFiles = SpriterDemoUtils.findFiles(new String[] { "scml", "scon" });
		for (FileHandle f : spriterFiles)
			if (!files.contains(f, false))
				files.add(new PrettyDisplayFileHandle(f));
		
		fileChooser.setItems(files);

		if (files.size > 0)
			loadSpriterFile(files.first());

		lastUsedSelectBox = fileChooser;
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
				popup("Loading error", ex.getLocalizedMessage());
			}
		}
		if (failed)
			return;

		SpriterData data = assetManager.get(desc);

		animators.clear();

		for (SpriterEntity entity : data.entities) {
			// Change toString method for charmaps
			Array<SpriterCharacterMap> replacements = new Array<SpriterCharacterMap>();
			for (SpriterCharacterMap map : entity.characterMaps) {
				SpriterCharacterMap newMap = new SpriterCharacterMap() {
					@Override
					public String toString() {
						return id + ": " + name;
					}
				};
				newMap.id = map.id;
				newMap.name = map.name;
				newMap.maps = map.maps;
				replacements.add(newMap);
			}
			entity.characterMaps = replacements;

			final SpriterAnimator animator = new SpriterAnimator(entity) {
				@Override
				public String toString() {
					SpriterEntity entity = getEntity();
					return entity.id + ": " + entity.name;
				}
			};
			animator.getListeners().add(new SpriterAnimationListener() {
				@Override
				public void onEventTriggered(String eventName) {
					popup("SpriterEvent", eventName);
				}

				@Override
				public void onAnimationFinished(String animationName) {
					SpriterAnimation animation = animator.getCurrentAnimation();
					if (!animation.looping)
						animator.play(animation);
				}
			});

			animators.add(animator);
		}

		fileChooser.setSelected(file);

		entityChooser.setItems(animators);

		if (animators.size > 0)
			setAnimator(animators.first());
	}

	private void setAnimator(SpriterAnimator anim) {
		this.animator = anim;

		spriterPlaceholder.setVisible(false);

		entityChooser.setSelected(animator);

		Array<String> anims = new Array<String>();
		for (String animation : animator.getAnimations())
			anims.add(animation);

		animationChooser.setItems(anims);
		animationChooser.setSelectedIndex(0);

		Array<SpriterCharacterMap> characterMaps = animator.getEntity().characterMaps;
		if (characterMaps.size > 0) {
			charmapChooser.setDisabled(false);
			charmapChooser.setItems((SpriterCharacterMap[]) characterMaps.toArray(SpriterCharacterMap.class));
			charmapChooser.setSelectedIndex(0);
		} else {
			charmapChooser.setItems(new SpriterCharacterMap[0]);
			charmapChooser.setDisabled(true);
		}
	}

	private void setAnimation(String anim) {
		if (transition) {
			animator.transition(anim, animator.getLength() - animator.getTime());
		} else {
			animator.play(anim);
		}
		timeSlider.setRange(0f, animator.getLength());
	}

	private void setCharacterMap(SpriterCharacterMap characterMap) {
		animator.setCharacterMap(characterMap);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();

		fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + " FPS");

		camera.update();
		spriterCamera.update();

		stage.act(delta);

		if (animator != null) {
			if (play) {
				animator.update(delta);
				timeSlider.removeListener(timeSliderListener);
				timeSlider.setValue(animator.getTime());
				timeSlider.addListener(timeSliderListener);
			}
			String metaText = "";
			FrameMetadata md = animator.getMetadata();
			if (md.animationVars.size > 0) {
				metaText += "Animation vars:";
				for (Entry<String, SpriterVarValue> entry : md.animationVars)
					metaText += " " + entry.key + "=" + entry.value.stringValue;
				metaText += System.lineSeparator();
			}
			if (md.objectVars.size > 0) {
				metaText += "Object vars:";
				for (Entry<String, ObjectMap<String, SpriterVarValue>> entry : md.objectVars)
					for (Entry<String, SpriterVarValue> sub : entry.value)
						metaText += " " + entry.key + "|" + sub.key + "=" + sub.value.stringValue;
				metaText += System.lineSeparator();
			}
			if (md.animationTags.size > 0) {
				metaText += "Animation tags:";
				for (String entry : md.animationTags)
					metaText += " " + entry;
				metaText += System.lineSeparator();
			}
			if (md.objectTags.size > 0) {
				metaText += "Object tags:";
				for (Entry<String, Array<String>> entry : md.objectTags)
					metaText += " " + entry.key + "=" + entry.value;
				metaText += System.lineSeparator();
			}
			if (md.events.size > 0) {
				metaText += "Events:";
				for (String entry : md.events)
					metaText += " " + entry;
				metaText += System.lineSeparator();
			}
			if (md.sounds.size > 0) {
				metaText += "Sounds: ";
				metaText += md.sounds;
				metaText += System.lineSeparator();
			}

			metaLabel.setText(metaText);
			timeLabel.setText(String.format("[%4.0f / %.0f]", animator.getTime(), animator.getLength()));

			// Update position
			animator.setPosition(offsetX + spriterPlaceholder.getX() + spriterPlaceholder.getWidth() / 2f, offsetY + spriterPlaceholder.getY() + spriterPlaceholder.getHeight() / 4f);

			renderer.setProjectionMatrix(spriterCamera.combined);
			batch.setProjectionMatrix(spriterCamera.combined);

			batch.begin();
			renderer.begin(ShapeType.Line);

			// Draw position
			renderer.circle(animator.getX(), animator.getY(), 1f);

			// Draw animator
			animator.draw(batch, renderer);

			batch.end();
			renderer.end();
		}

		// Draw stage
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		spriterCamera.setToOrtho(false, width, height);
		if (animator != null)
			animator.setPosition(width / 4f, 12f);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	private void popup(String title, String content) {
		new Dialog(title, skin, "dialog").text(content).button("I understand", true).key(Keys.ENTER, true).key(Keys.ESCAPE, true).show(stage);
	}

	@Override
	public void dispose() {
		stage.dispose();
		renderer.dispose();
		batch.dispose();
		assetManager.dispose();
	}
}
