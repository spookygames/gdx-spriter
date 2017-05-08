/**
 * Copyright (c) 2015-2016 Spooky Games
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgement in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package net.spookygames.gdx.spriter.demo;

import java.awt.FileDialog;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.spookygames.gdx.spriter.FrameData;
import net.spookygames.gdx.spriter.SpriterAnimationAdapter;
import net.spookygames.gdx.spriter.SpriterAnimator;
import net.spookygames.gdx.spriter.SpriterAnimatorActor;
import net.spookygames.gdx.spriter.SpriterTestData;
import net.spookygames.gdx.spriter.data.SpriterAnimation;
import net.spookygames.gdx.spriter.data.SpriterCharacterMap;
import net.spookygames.gdx.spriter.data.SpriterData;
import net.spookygames.gdx.spriter.data.SpriterEntity;
import net.spookygames.gdx.spriter.data.SpriterVarValue;
import net.spookygames.gdx.spriter.loader.SpriterDataLoader;

public class SpriterDemoApp implements ApplicationListener {

	// Display stuff
	SpriteBatch batch;
	OrthographicCamera camera;
	Skin skin;

	// Widgets
	Stage stage;
	public Table rootTable;

	SelectBox<SpriterDemoFileHandle> fileChooser;
	SelectBox<SpriterAnimator> entityChooser;
	SelectBox<String> animationChooser;
	List<SpriterCharacterMap> charmapChooser;

	SpriterDemoAnimatorSlider angleSlider;
	SpriterDemoAnimatorSlider pivotXSlider;
	SpriterDemoAnimatorSlider pivotYSlider;
	SpriterDemoAnimatorSlider scaleXSlider;
	SpriterDemoAnimatorSlider scaleYSlider;
	SpriterDemoAnimatorSlider alphaSlider;
	SpriterDemoAnimatorSlider speedSlider;
	SpriterDemoAnimatorSlider[] allAnimatorSliders;

	Label metaLabel;
	Label spriterPlaceholder;
	SpriterAnimatorActor spriterAnimator;

	Button playPauseButton;
	Slider timeSlider;
	ChangeListener timeSliderListener;
	Label timeLabel;

	// Data
	AssetManager assetManager;
	AssetManager externalAssetManager;
	Array<SpriterDemoFileHandle> files = new Array<SpriterDemoFileHandle>();
	Array<SpriterAnimator> animators = new Array<SpriterAnimator>();

	// Current data
	FileHandle file = null;
	SpriterAnimator animator = null;
	boolean transition = false;
	SelectBox<?> lastUsedSelectBox = null;
	String lastFolderBrowsed = System.getProperty("user.home");

	@Override
	public void create() {
		// Initialize object
		batch = new SpriteBatch();
		camera = new OrthographicCamera();

		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager = new AssetManager(resolver);
		assetManager.setLoader(SpriterData.class, new SpriterDataLoader(resolver));

		resolver = new AbsoluteFileHandleResolver();
		externalAssetManager = new AssetManager(resolver);
		externalAssetManager.setLoader(SpriterData.class, new SpriterDataLoader(resolver));

		assetManager.load("uiskin.json", Skin.class);
		assetManager.finishLoading();

		skin = assetManager.get("uiskin.json");

		// Create widgets
		stage = new Stage(new ScreenViewport(camera), batch);

		Label titleLabel = new Label("gdx-spriter", skin);
		titleLabel.setFontScale(3f);

		Label fpsLabel = new Label("FPS", skin) {
			@Override
			public void act(float delta) {
				this.setText(Gdx.graphics.getFramesPerSecond() + " FPS");
				super.act(delta);
			}
		};

		fileChooser = new SelectBox<SpriterDemoFileHandle>(skin);
		fileChooser.setName("Files");
		fileChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changeSpriterFile(fileChooser.getSelected());
				lastUsedSelectBox = fileChooser;
			}
		});

		Button fileFinder = new TextButton("Browse", skin);
		fileFinder.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				FileDialog fileDialog = new FileDialog((java.awt.Frame) null, "Choose Spriter file", FileDialog.LOAD);
				fileDialog.setDirectory(lastFolderBrowsed);
				fileDialog.setResizable(true);
				fileDialog.setVisible(true);
				String file = fileDialog.getFile();
				String directory = fileDialog.getDirectory();
				if (directory != null) {
					lastFolderBrowsed = directory;
				}
				if (file != null) {
					String path = directory + file;
					addFile(Gdx.files.absolute(path), externalAssetManager);
					fileChooser.setItems(files);
					fileChooser.setSelectedIndex(fileChooser.getItems().size - 1);
				}
			}
		});

		entityChooser = new SelectBox<SpriterAnimator>(skin);
		entityChooser.setName("Entities");
		entityChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changeAnimator(entityChooser.getSelected());
				lastUsedSelectBox = entityChooser;
			}
		});

		animationChooser = new SelectBox<String>(skin);
		animationChooser.setName("Animations");
		animationChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changeAnimation(animationChooser.getSelected());
				lastUsedSelectBox = animationChooser;
			}
		});

		final CheckBox transitionCheckbox = new CheckBox("Transition", skin);
		transitionCheckbox.setChecked(transition);
		transitionCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				transition = transitionCheckbox.isChecked();
			}
		});

		charmapChooser = new List<SpriterCharacterMap>(skin);
		ArraySelection<SpriterCharacterMap> selection = charmapChooser.getSelection();
		selection.setMultiple(true);
		selection.setRangeSelect(false);
		selection.setToggle(false);
		selection.setRequired(false);
		charmapChooser.setName("Charmaps");
		charmapChooser.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changeCharacterMaps(charmapChooser.getSelection().items().orderedItems());
			}
		});

		angleSlider = new SpriterDemoAnimatorSlider(0, 360, 1f, skin, "%.0f") {
			@Override
			public void setValue(SpriterAnimator animator, float value) {
				spriterAnimator.setRotation(value);
			}

			@Override
			protected float getValue(SpriterAnimator animator) {
				return spriterAnimator.getRotation();
			}
		};

		pivotXSlider = new SpriterDemoAnimatorSlider(-1000f, 1000f, 1f, skin, "%.0f") {
			@Override
			public void setValue(SpriterAnimator animator, float value) {
				animator.setPivotX(value);
			}

			@Override
			protected float getValue(SpriterAnimator animator) {
				return animator.getPivotX();
			}
		};

		pivotYSlider = new SpriterDemoAnimatorSlider(-1000f, 1000f, 1f, skin, "%.0f") {
			@Override
			public void setValue(SpriterAnimator animator, float value) {
				animator.setPivotY(value);
			}

			@Override
			protected float getValue(SpriterAnimator animator) {
				return animator.getPivotY();
			}
		};

		scaleXSlider = new SpriterDemoAnimatorSlider(-10f, 10f, 0.1f, skin) {
			@Override
			public void setValue(SpriterAnimator animator, float value) {
				spriterAnimator.setScaleX(value);
			}

			@Override
			protected float getValue(SpriterAnimator animator) {
				return spriterAnimator.getScaleX();
			}
		};

		scaleYSlider = new SpriterDemoAnimatorSlider(-10f, 10f, 0.1f, skin) {
			@Override
			public void setValue(SpriterAnimator animator, float value) {
				spriterAnimator.setScaleY(value);
			}

			@Override
			protected float getValue(SpriterAnimator animator) {
				return spriterAnimator.getScaleY();
			}
		};

		alphaSlider = new SpriterDemoAnimatorSlider(0f, 1f, 0.01f, skin, "%.2f") {
			@Override
			public void setValue(SpriterAnimator animator, float value) {
				animator.setAlpha(value);
			}

			@Override
			protected float getValue(SpriterAnimator animator) {
				return animator.getAlpha();
			}
		};

		speedSlider = new SpriterDemoAnimatorSlider(0f, 10f, 0.1f, skin) {
			@Override
			public void setValue(SpriterAnimator animator, float value) {
				animator.setSpeed(value);
			}

			@Override
			protected float getValue(SpriterAnimator animator) {
				return animator.getSpeed();
			}
		};

		allAnimatorSliders = new SpriterDemoAnimatorSlider[] { scaleXSlider, scaleYSlider, pivotXSlider, pivotYSlider,
				angleSlider, alphaSlider, speedSlider };

		metaLabel = new Label("Meta: ", skin);
		metaLabel.setWrap(true);
		metaLabel.setAlignment(Align.topLeft);

		spriterPlaceholder = new Label("No animator", skin);
		spriterPlaceholder.setAlignment(Align.center);

		spriterAnimator = new SpriterAnimatorActor(animator);
		spriterAnimator.debug();

		playPauseButton = new ImageButton(skin, "play");
		playPauseButton.setChecked(true);
		playPauseButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean playing = playPauseButton.isChecked();
				spriterAnimator.setDisabled(!playing);
			}
		});

		timeSlider = new Slider(0f, 2000f, 1, false, skin);
		timeSlider.addListener(timeSliderListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (animator == null)
					return;
				animator.setTime(timeSlider.getValue());
				animator.update(0f);
			}
		});

		timeLabel = new Label("---", skin);

		// Put everything in place

		Table titleTable = new Table(skin);
		titleTable.add(titleLabel).pad(6f);
		titleTable.add().expandX();
		titleTable.add(fpsLabel).padRight(10f);

		Table selectionTable = new Table(skin);
		selectionTable.defaults().pad(3f);

		Table filesTable = new Table(skin);
		filesTable.row();
		filesTable.add(fileChooser).expand().fillX();
		filesTable.add(fileFinder).padLeft(2f).padRight(1f);

		Table animationsTable = new Table(skin);
		animationsTable.row();
		animationsTable.add(animationChooser).expand().fill();
		animationsTable.add(transitionCheckbox).fillX().padLeft(2f);

		ScrollPane scrollableCharmapChooser = new ScrollPane(charmapChooser);

		Table menuTable = new Table(skin);
		menuTable.defaults().pad(3f).expandX().fillX();
		menuTable.row();
		menuTable.add(titleTable).colspan(2);
		menuTable.row();
		menuTable.add("File");
		menuTable.add(filesTable).pad(4f);
		menuTable.row();
		menuTable.add("Entity");
		menuTable.add(entityChooser).pad(4f);
		menuTable.row();
		menuTable.add("Animation");
		menuTable.add(animationsTable).pad(4f);
		menuTable.row();
		menuTable.add("Maps");
		menuTable.add(scrollableCharmapChooser).pad(4f);
		menuTable.row();
		menuTable.add("Angle");
		menuTable.add(angleSlider);
		menuTable.row();
		menuTable.add("Pivot X");
		menuTable.add(pivotXSlider);
		menuTable.row();
		menuTable.add("Pivot Y");
		menuTable.add(pivotYSlider);
		menuTable.row();
		menuTable.add("Scale X");
		menuTable.add(scaleXSlider);
		menuTable.row();
		menuTable.add("Scale Y");
		menuTable.add(scaleYSlider);
		menuTable.row();
		menuTable.add("Alpha");
		menuTable.add(alphaSlider);
		menuTable.row();
		menuTable.add("Speed");
		menuTable.add(speedSlider);
		menuTable.row();
		menuTable.add().expandY();

		Table contentTable = new Table(skin);
		contentTable.row();
		contentTable.add(metaLabel).left().expandX().maxHeight(1f);
		contentTable.row();
		contentTable.stack(spriterPlaceholder, spriterAnimator).expand();

		Table timelineTable = new Table(skin);
		timelineTable.row();
		timelineTable.add("Timeline").expandX().align(Align.bottom);
		timelineTable.row();
		timelineTable.add(timeSlider).expandX().fillX();
		timelineTable.row();
		timelineTable.add(timeLabel).expandX().align(Align.top);

		Table controlTable = new Table(skin);
		controlTable.add(playPauseButton).space(5f).expandY().fillY();
		controlTable.add(timelineTable).expandX().fillX();

		rootTable = new Table(skin);
		rootTable.setFillParent(true);
		rootTable.row();
		rootTable.add(menuTable).expandY().fill();
		rootTable.add(contentTable).expand().fill();
		rootTable.row();
		rootTable.add(controlTable).colspan(2).expandX().fillX();

		stage.addActor(rootTable);

		// Bring input processing to the party

		InputProcessor globalInput = new InputProcessor() {
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
						lastUsedSelectBox.setSelectedIndex(Math.min(lastUsedSelectBox.getSelectedIndex() + 1,
								lastUsedSelectBox.getItems().size - 1));
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
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				amount *= 0.05f; // Zoom coefficient
				scaleXSlider.setValue(scaleXSlider.getValue() + amount);
				scaleYSlider.setValue(scaleYSlider.getValue() + amount);
				return false;
			}
		};
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, globalInput));

		// Add default test resources
		Array<String> testResources = new Array<String>(SpriterTestData.scml);
		testResources.addAll(SpriterTestData.scon);
		testResources.sort();
		for (String path : testResources)
			addFile(Gdx.files.internal(path.replaceFirst("/", "")), assetManager);

		// Also go discover some unknown exotic resources! (won't work in jar
		// though)
		Array<FileHandle> spriterFiles = SpriterDemoUtils.findFiles(new String[] { "scml", "scon" });
		for (FileHandle f : spriterFiles)
			addFile(f, assetManager);

		fileChooser.setItems(files);

		lastUsedSelectBox = fileChooser;

		if (playPauseButton.isChecked())
			animator.play(animator.getAnimations().iterator().next());
	}

	private void addFile(FileHandle file, AssetManager manager) {
		SpriterDemoFileHandle f = new SpriterDemoFileHandle(file, manager);
		if (!files.contains(f, false))
			files.add(f);
	}

	private void changeSpriterFile(SpriterDemoFileHandle file) {

		AssetManager manager = file.manager;

		AssetDescriptor<SpriterData> desc = new AssetDescriptor<SpriterData>(file, SpriterData.class);

		try {
			manager.load(desc);
			manager.finishLoading();
		} catch (GdxRuntimeException ex) {
			popup("Loading error", ex.getLocalizedMessage());
			return;
		}

		SpriterData data = manager.get(desc);

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

			SpriterAnimator animator = new SpriterAnimator(entity) {
				@Override
				public String toString() {
					SpriterEntity entity = getEntity();
					return entity.id + ": " + entity.name;
				}
			};
			animator.addAnimationListener(new SpriterAnimationAdapter() {
				@Override
				public void onEventTriggered(SpriterAnimator animator, String eventName) {
					popup("SpriterEvent", eventName);
				}

				@Override
				public void onAnimationFinished(SpriterAnimator animator, SpriterAnimation animation) {
					if (!animation.looping)
						animator.play(animation);
				}
			});

			animators.add(animator);
		}

		fileChooser.setSelected(file);

		entityChooser.setItems(animators);

		if (animators.size > 0)
			changeAnimator(animators.first());
	}

	private void changeAnimator(SpriterAnimator anim) {
		this.animator = anim;

		spriterPlaceholder.setVisible(false);
		spriterAnimator.setAnimator(anim);

		entityChooser.setSelected(animator);

		Array<String> anims = new Array<String>();
		for (String animation : animator.getAnimationNames())
			anims.add(animation);

		animationChooser.setItems(anims);
		animationChooser.setSelectedIndex(0);

		Array<SpriterCharacterMap> characterMaps = animator.getEntity().characterMaps;
		if (characterMaps.size > 0) {
			charmapChooser.setVisible(true);
			charmapChooser.setItems((SpriterCharacterMap[]) characterMaps.toArray(SpriterCharacterMap.class));
		} else {
			charmapChooser.setItems(new SpriterCharacterMap[0]);
			charmapChooser.setVisible(false);
		}

		for (SpriterDemoAnimatorSlider slider : allAnimatorSliders)
			slider.update(animator);
	}

	private void changeAnimation(String anim) {
		if (transition && animator.getCurrentAnimation() != null) {
			animator.transition(anim, animator.getLength() - animator.getTime());
		} else {
			animator.play(anim);
		}
		timeSlider.setRange(0f, animator.getLength());
	}

	private void changeCharacterMaps(Array<SpriterCharacterMap> selected) {
		Array<SpriterCharacterMap> current = animator.getCharacterMaps();

		for (SpriterCharacterMap currentMap : current)
			if (!selected.contains(currentMap, true))
				animator.removeCharacterMap(currentMap);

		for (SpriterCharacterMap selectedMap : selected)
			if (!current.contains(selectedMap, true))
				animator.addCharacterMap(selectedMap);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();

		camera.update();

		stage.act(delta);
		
		String separator = System.getProperty("line.separator");	// System.lineSeparator();

		if (animator != null && animator.getCurrentAnimation() != null) {
			timeSlider.removeListener(timeSliderListener);
			timeSlider.setValue(animator.getTime());
			timeSlider.addListener(timeSliderListener);
			String metaText = "";
			FrameData md = animator.getCurrentFrameData();
			if (md.animationVars.size > 0) {
				metaText += "Animation vars:";
				for (Entry<String, SpriterVarValue> entry : md.animationVars)
					metaText += " " + entry.key + "=" + entry.value.stringValue;
				metaText += separator;
			}
			if (md.objectVars.size > 0) {
				metaText += "Object vars:";
				for (Entry<String, ObjectMap<String, SpriterVarValue>> entry : md.objectVars)
					for (Entry<String, SpriterVarValue> sub : entry.value)
						metaText += " " + entry.key + "|" + sub.key + "=" + sub.value.stringValue;
				metaText += separator;
			}
			if (md.animationTags.size > 0) {
				metaText += "Animation tags:";
				for (String entry : md.animationTags)
					metaText += " " + entry;
				metaText += separator;
			}
			if (md.objectTags.size > 0) {
				metaText += "Object tags:";
				for (Entry<String, Array<String>> entry : md.objectTags)
					metaText += " " + entry.key + "=" + entry.value;
				metaText += separator;
			}
			if (md.events.size > 0) {
				metaText += "Events:";
				for (String entry : md.events)
					metaText += " " + entry;
				metaText += separator;
			}
			if (md.sounds.size > 0) {
				metaText += "Sounds: ";
				metaText += md.sounds;
				metaText += separator;
			}

			metaLabel.setText(metaText);
			timeLabel.setText(String.format("[%4.0f / %.0f]", animator.getTime(), animator.getLength()));
		}

		// Draw stage
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	private void popup(String title, String content) {
		new Dialog(title, skin, "dialog").text(content).button("I understand", true).key(Keys.ENTER, true)
				.key(Keys.ESCAPE, true).show(stage);
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		assetManager.dispose();
	}

	abstract class SpriterDemoAnimatorSlider extends Table {
		private final Slider slider;
		private final ChangeListener listener;
		private final Label valueLabel;
		private final String textFormat;

		public SpriterDemoAnimatorSlider(float min, float max, float step, Skin skin) {
			this(min, max, step, skin, "%.1f");
		}

		public SpriterDemoAnimatorSlider(float min, float max, float step, Skin skin, final String format) {
			textFormat = format;
			slider = new Slider(min, max, step, false, skin);
			slider.setValue((max - min) / 2f);
			slider.addListener(listener = new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (animator == null)
						return;
					setValue(animator, slider.getValue());
				}
			});
			slider.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					valueLabel.setText(String.format(textFormat, slider.getValue()));
				}
			});
			valueLabel = new Label("", skin);

			this.add(slider).expandX().fill();
			this.add(valueLabel).minWidth(30f).padLeft(3f);
		}

		public float getValue() {
			return slider.getValue();
		}

		public void setValue(float value) {
			slider.setValue(value);
		}

		public void setRange(float min, float max) {
			slider.setRange(min, max);
		}

		public void update(SpriterAnimator animator) {
			slider.removeListener(listener);
			float value = getValue(animator);
			slider.setValue(value);
			slider.addListener(listener);
		}

		public abstract void setValue(SpriterAnimator animator, float value);

		protected abstract float getValue(SpriterAnimator animator);
	}
}
