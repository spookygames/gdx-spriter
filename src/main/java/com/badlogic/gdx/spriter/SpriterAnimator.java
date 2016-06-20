// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterAssetProvider;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterMapInstruction;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterObjectInfo;
import com.badlogic.gdx.spriter.data.SpriterSound;
import com.badlogic.gdx.spriter.data.SpriterSpatial;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * The {@code SpriterAnimator} class is a central piece of gdx-spriter as it
 * allows rendering of Spriter animations in a libGDX application.
 * 
 * @see #play(SpriterAnimation)
 * @see #blend(SpriterAnimation, SpriterAnimation, float)
 * @see #transition(SpriterAnimation, float)
 * @see #update(float)
 * @see #draw(Batch, ShapeRenderer)
 * 
 * @see SpriterData
 * @see SpriterEntity
 * @see SpriterAnimation
 * @see SpriterAnimationListener
 * @see SpriterAssetProvider
 * @see FrameData
 * 
 * @author thorthur
 * 
 */
public class SpriterAnimator {

	private final SpriterData spriterData;
	private final SpriterEntity entity;
	private final SpriterAssetProvider assets;
	private final ObjectMap<String, SpriterAnimation> animations = new ObjectMap<String, SpriterAnimation>();

	private SpriterAnimation currentAnimation = null;
	private SpriterAnimation nextAnimation = null;
	private final Array<SpriterCharacterMap> characterMaps = new Array<SpriterCharacterMap>(true, 12);
	private final SnapshotArray<SpriterAnimationListener> listeners = new SnapshotArray<SpriterAnimationListener>(true,
			12, SpriterAnimationListener.class);

	// This one will be used for all things geometric
	private final SpriterSpatial spatial = new SpriterSpatial();
	private float pivotX = 0f;
	private float pivotY = 0f;

	private float speed = 1.0f;
	private float time = 0f;

	private float totalTransitionTime = 0f;
	private float transitionTime = 0f;
	private float factor = 0f;

	private final FrameDataUpdateConfiguration frameUpdateConfiguration = new FrameDataUpdateConfiguration();
	private final FrameData frameData = new FrameData();

	private final Rectangle boundingBox = new Rectangle();
	private boolean dirtyBoundingBox = true;

	/**
	 * Initialize a new {@code SpriterAnimator} with given {@link SpriterEntity}
	 * .
	 * 
	 * Be sure to provide an entity which {@link SpriterData} has a proper
	 * {@link SpriterAssetProvider}!
	 * 
	 * @param spriterEntity
	 *            Entity to create an animator for
	 */
	public SpriterAnimator(SpriterEntity spriterEntity) {
		entity = spriterEntity;
		spriterData = spriterEntity.data;

		if (spriterData.assetProvider == null)
			throw new IllegalArgumentException("Asset provider of SpriterData cannot be null");

		assets = spriterData.assetProvider;

		for (SpriterAnimation animation : spriterEntity.animations)
			animations.put(animation.name, animation);
	}

	/**
	 * Get the {@link SpriterData} behind the {@link SpriterEntity}.
	 * 
	 * @return Data current entity comes from
	 */
	public SpriterData getSpriterData() {
		return spriterData;
	}

	/**
	 * Get {@link SpriterEntity} associated to this {@link SpriterAnimator}.
	 * 
	 * @return Current entity
	 */
	public SpriterEntity getEntity() {
		return entity;
	}

	/**
	 * Get names of all {@link SpriterAnimation}s available for current
	 * {@link SpriterEntity}.
	 * 
	 * @return Names of all the animations
	 */
	public Iterable<String> getAnimationNames() {
		return animations.keys();
	}

	/**
	 * Get all {@link SpriterAnimation}s available for current
	 * {@link SpriterEntity}.
	 * 
	 * @return All the animations
	 */
	public Iterable<SpriterAnimation> getAnimations() {
		return animations.values();
	}

	/**
	 * Get the {@link SpriterAnimation} currently being played or null if
	 * nothing is being played.
	 * 
	 * @return Current animation if any, null otherwise
	 */
	public SpriterAnimation getCurrentAnimation() {
		return currentAnimation;
	}

	/**
	 * Get second {@link SpriterAnimation} in line for a transition/blending or
	 * null if no transition/blending is being performed.
	 * 
	 * @return Second animation in blend if any, null otherwise
	 */
	public SpriterAnimation getNextAnimation() {
		return nextAnimation;
	}

	/**
	 * Add a {@link SpriterCharacterMap} to this {@link SpriterAnimator} given
	 * its name.
	 * 
	 * If there is no character map with given name or the map has already been
	 * added to this {@link SpriterAnimator}, nothing happens.
	 * 
	 * @param characterMapName
	 *            Name of the character map to add
	 */
	public void addCharacterMap(String characterMapName) {
		for (SpriterCharacterMap map : entity.characterMaps) {
			if (characterMapName.equals(map.name)) {
				addCharacterMap(map);
				break;
			}
		}
	}

	/**
	 * Add a {@link SpriterCharacterMap} to this {@link SpriterAnimator}.
	 * 
	 * If the map has already been added to this {@link SpriterAnimator},
	 * nothing happens.
	 * 
	 * @param characterMap
	 *            Character map to add
	 */
	public void addCharacterMap(SpriterCharacterMap characterMap) {
		if (characterMap == null || this.characterMaps.contains(characterMap, true))
			return;
		this.characterMaps.add(characterMap);

		SpriterAnimationListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++)
			items[i].onCharacterMapAdded(this, characterMap);
		listeners.end();
	}

	/**
	 * Get all {@link SpriterCharacterMap}s currently registered to this
	 * {@link SpriterAnimator}.
	 * 
	 * Order in the array is important as character maps are queried as they
	 * come in this array. In order to reorganize character map priority,
	 * manipulate items in this array or perform proper calls to
	 * {@link #removeCharacterMap(SpriterCharacterMap)} and
	 * {@link #addCharacterMap(SpriterCharacterMap)}.
	 * 
	 * @return All character maps currently registered
	 */
	public Array<SpriterCharacterMap> getCharacterMaps() {
		return this.characterMaps;
	}

	/**
	 * Remove a {@link SpriterCharacterMap} from this {@link SpriterAnimator}.
	 * 
	 * @param characterMap
	 *            Character map to remove
	 * @return True if the map was effectively removed, false otherwise.
	 */
	public boolean removeCharacterMap(SpriterCharacterMap characterMap) {
		if (this.characterMaps.removeValue(characterMap, true)) {
			SpriterAnimationListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++)
				items[i].onCharacterMapRemoved(this, characterMap);
			listeners.end();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Remove all {@link SpriterCharacterMap}s from this {@link SpriterAnimator}
	 * .
	 */
	public void clearCharacterMaps() {
		while (characterMaps.size > 0) {
			SpriterCharacterMap removed = characterMaps.pop();
			SpriterAnimationListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++)
				items[i].onCharacterMapRemoved(this, removed);
			listeners.end();
		}
	}

	/**
	 * Add a {@link SpriterAnimationListener} to this {@link SpriterAnimator}.
	 * 
	 * Order in the array is important as listeners are triggered as they come
	 * in this array. In order to reorganize listener priority, perform proper
	 * calls to {@link #addAnimationListener(SpriterAnimationListener)} and
	 * {@link #removeAnimationListener(SpriterAnimationListener)}.
	 * 
	 * @param listener
	 *            Animation listener to add
	 */
	public void addAnimationListener(SpriterAnimationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a {@link SpriterAnimationListener} from this
	 * {@link SpriterAnimator}.
	 * 
	 * @param listener
	 *            Animation listener to remove
	 * @return True if the listener was effectively removed, false otherwise.
	 */
	public boolean removeAnimationListener(SpriterAnimationListener listener) {
		return listeners.removeValue(listener, true);
	}

	/**
	 * Get the name of the {@link SpriterAnimation} currently playing.
	 * 
	 * @return The name of current animation.
	 */
	public String getName() {
		return currentAnimation.name;
	}

	/**
	 * Get the animation speed (factor applied to delta time).
	 * 
	 * @return The animation speed.
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Set the animation speed (factor applied to delta time).
	 * 
	 * @param speed
	 *            Animation speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Get the length of the {@link SpriterAnimation} currently playing.
	 * 
	 * @return The length of current animation.
	 */
	public float getLength() {
		return currentAnimation.length;
	}

	/**
	 * Get the progress of the {@link SpriterAnimation} currently playing.
	 * 
	 * Progress value is included between 0 and 1.
	 * 
	 * @return The progress of current animation.
	 */
	public float getProgress() {
		return time / currentAnimation.length;
	}

	/**
	 * Set the progress (factor applied to delta time) of the
	 * {@link SpriterAnimation} currently playing.
	 * 
	 * Progress value should be included between 0 and 1. No check will be
	 * performed.
	 * 
	 * @param progress
	 *            Animation progress
	 */
	public void setProgress(float progress) {
		this.time = progress * currentAnimation.length;
	}

	/**
	 * Get the time (Spriter time) of the {@link SpriterAnimation} currently
	 * playing.
	 * 
	 * @return The time of current animation.
	 */
	public float getTime() {
		return time;
	}

	/**
	 * Set the time (Spriter time) of the {@link SpriterAnimation} currently
	 * playing.
	 * 
	 * @param time
	 *            Animation time
	 */
	public void setTime(float time) {
		this.time = time;
	}

	/**
	 * Get the X coordinate of this {@link SpriterAnimator}.
	 * 
	 * @return The x coordinate.
	 */
	public float getX() {
		return this.spatial.x;
	}

	/**
	 * Set the X coordinate of this {@link SpriterAnimator}.
	 * 
	 * @param x
	 *            The x coordinate
	 */
	public void setX(float x) {
		this.spatial.x = x;
	}

	/**
	 * Get the Y coordinate of this {@link SpriterAnimator}.
	 * 
	 * @return The y coordinate.
	 */
	public float getY() {
		return spatial.y;
	}

	/**
	 * Set the Y coordinate of this {@link SpriterAnimator}.
	 * 
	 * @param y
	 *            The y coordinate
	 */
	public void setY(float y) {
		this.spatial.y = y;
	}

	/**
	 * Set the X and Y coordinates of this {@link SpriterAnimator}.
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 */
	public void setPosition(float x, float y) {
		this.spatial.x = x;
		this.spatial.y = y;
	}

	/**
	 * Get the X pivot of this {@link SpriterAnimator}.
	 * 
	 * @return The x pivot.
	 */
	public float getPivotX() {
		return pivotX;
	}

	/**
	 * Set the X pivot of this {@link SpriterAnimator}.
	 * 
	 * @param pivotX
	 *            The x pivot
	 */
	public void setPivotX(float pivotX) {
		this.pivotX = pivotX;
	}

	/**
	 * Get the Y pivot of this {@link SpriterAnimator}.
	 * 
	 * @return The y pivot.
	 */
	public float getPivotY() {
		return pivotY;
	}

	/**
	 * Set the Y pivot of this {@link SpriterAnimator}.
	 * 
	 * @param pivotY
	 *            The y pivot
	 */
	public void setPivotY(float pivotY) {
		this.pivotY = pivotY;
	}

	/**
	 * Set the X and Y pivots of this {@link SpriterAnimator}.
	 * 
	 * @param pivotX
	 *            The x pivot
	 * @param pivotY
	 *            The y pivot
	 */
	public void setPivot(float pivotX, float pivotY) {
		this.pivotX = pivotX;
		this.pivotY = pivotY;
	}

	/**
	 * Get the horizontal scale of this {@link SpriterAnimator}.
	 * 
	 * @return The horizontal scale.
	 */
	public float getScaleX() {
		return spatial.scaleX;
	}

	/**
	 * Set the horizontal scale of this {@link SpriterAnimator}.
	 * 
	 * @param scaleX
	 *            The horizontal scale
	 */
	public void setScaleX(float scaleX) {
		this.spatial.scaleX = scaleX;
	}

	/**
	 * Get the vertical scale of this {@link SpriterAnimator}.
	 * 
	 * @return The vertical scale.
	 */
	public float getScaleY() {
		return spatial.scaleY;
	}

	/**
	 * Set the vertical scale of this {@link SpriterAnimator}.
	 * 
	 * @param scaleY
	 *            The vertical scale
	 */
	public void setScaleY(float scaleY) {
		this.spatial.scaleY = scaleY;
	}

	/**
	 * Set the horizontal and vertical scales of this {@link SpriterAnimator}.
	 * 
	 * @param scaleX
	 *            The horizontal scale
	 * @param scaleY
	 *            The vertical scale
	 */
	public void setScale(float scaleX, float scaleY) {
		this.spatial.scaleX = scaleX;
		this.spatial.scaleY = scaleY;
	}

	/**
	 * Get the angle (in degrees) of this {@link SpriterAnimator}.
	 * 
	 * @return The angle in degrees.
	 */
	public float getAngle() {
		return spatial.angle;
	}

	/**
	 * Set the angle (in degrees) of this {@link SpriterAnimator}.
	 * 
	 * @param angle
	 *            The angle in degrees
	 */
	public void setAngle(float angle) {
		this.spatial.angle = angle;
	}

	/**
	 * Get the alpha value of this {@link SpriterAnimator}.
	 * 
	 * This alpha factor will be applied on top of existing alpha value for
	 * sprite rendering.
	 * 
	 * @return The alpha applied to sprites color
	 */
	public float getAlpha() {
		return spatial.alpha;
	}

	/**
	 * Set the alpha value of this {@link SpriterAnimator}.
	 * 
	 * This alpha factor will be applied on top of existing alpha value for
	 * sprite rendering.
	 * 
	 * @param alpha
	 *            The alpha applied to sprites color
	 */
	public void setAlpha(float alpha) {
		this.spatial.alpha = alpha;
	}

	/**
	 * Get the {@link FrameDataUpdateConfiguration} of this
	 * {@link SpriterAnimator}, specifying what should be updated during a call
	 * to {@link #update(float)}.
	 * 
	 * @return The frame update configuration applied by this animator.
	 */
	public FrameDataUpdateConfiguration getFrameUpdateConfiguration() {
		return frameUpdateConfiguration;
	}

	/**
	 * Get current {@link FrameData} of this {@link SpriterAnimator}, as
	 * generated by last call to {@link #update(float)}.
	 * 
	 * @return Current frame data
	 */
	public FrameData getCurrentFrameData() {
		return frameData;
	}

	/**
	 * Get current bounding {@link Rectangle} of this {@link SpriterAnimator}.
	 * 
	 * @return Current bounding box
	 */
	public Rectangle getBoundingBox() {
		if (dirtyBoundingBox) {
			updateBoundingBox();
			dirtyBoundingBox = false;
		}
		return boundingBox;
	}

	/**
	 * Play given {@link SpriterAnimation} given its name. It becomes the
	 * current animation of this {@link SpriterAnimator}.
	 * 
	 * @param animationName
	 *            Name of the animation to play
	 */
	public void play(String animationName) {
		SpriterAnimation animation = animations.get(animationName);
		play(animation);
	}

	/**
	 * Play given {@link SpriterAnimation}. It becomes the current animation of
	 * this {@link SpriterAnimator}.
	 * 
	 * @param animation
	 *            Animation to play
	 */
	public void play(SpriterAnimation animation) {
		time = 0;

		SpriterAnimation former = currentAnimation;
		currentAnimation = animation;

		SpriterAnimationListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++)
			items[i].onAnimationChanged(this, former, animation);
		listeners.end();

		nextAnimation = null;
	}

	/**
	 * Play given {@link SpriterAnimation} next given its name, progressively
	 * blending from current animation to given one.
	 * 
	 * @param animationName
	 *            Name of the animation to play next
	 * @param totalTransitionTime
	 *            Time before next animation is the only one being played
	 */
	public void transition(String animationName, float totalTransitionTime) {
		transition(animations.get(animationName), totalTransitionTime);
	}

	/**
	 * Play given {@link SpriterAnimation} next, progressively blending from
	 * current animation to given one.
	 * 
	 * @param animation
	 *            Animation to play next
	 * @param totalTransitionTime
	 *            Time before next animation is the only one being played
	 */
	public void transition(SpriterAnimation animation, float totalTransitionTime) {
		this.totalTransitionTime = totalTransitionTime;
		transitionTime = 0;
		nextAnimation = animation;
	}

	/**
	 * Play two {@link SpriterAnimation}s given their name, blending them
	 * together with given weight factor.
	 * 
	 * 
	 * @param first
	 *            Name of the first animation to display
	 * @param second
	 *            Name of the second animation to display, if first == second
	 *            then no blending takes place and factor is of no use
	 * @param factor
	 *            Weight factor between first and second, should be between 0
	 *            (display first only) and 1 (display second only)
	 */
	public void blend(String first, String second, float factor) {
		blend(animations.get(first), animations.get(second), factor);
	}

	/**
	 * Play two {@link SpriterAnimation}s, blending them together with given
	 * weight factor.
	 * 
	 * 
	 * @param first
	 *            First animation to display
	 * @param second
	 *            Second animation to display, if first == second then no
	 *            blending takes place and factor is of no use
	 * @param factor
	 *            Weight factor between first and second, should be between 0
	 *            (display first only) and 1 (display second only)
	 */
	public void blend(SpriterAnimation first, SpriterAnimation second, float factor) {
		play(first);
		nextAnimation = second;
		totalTransitionTime = 0;
		this.factor = factor;
	}

	/**
	 * Update current {@link SpriterAnimation} with given delta time.
	 * 
	 * This update results in the update of existing {@link FrameData} to be
	 * used with {@link #draw(Batch, ShapeRenderer)}.
	 * 
	 * Any modification of displayed data should be performed after a call to
	 * {@link #update(float)} and prior to a call to {@link #draw(Batch)}.
	 * 
	 * @param deltaTime
	 *            Time (GDX time) since last update
	 */
	public void update(float deltaTime) {

		if (currentAnimation == null)
			return;

		deltaTime *= 1000f; // We're talking milliseconds here
		float elapsed = deltaTime * speed;
		float length = currentAnimation.length;

		if (nextAnimation != null && totalTransitionTime != 0.0f) {
			elapsed += elapsed * factor * length / nextAnimation.length;

			transitionTime += Math.abs(elapsed);
			factor = transitionTime / totalTransitionTime;
			if (transitionTime >= totalTransitionTime) {
				float tmpTime = time;
				play(nextAnimation);
				time = tmpTime;
				nextAnimation = null;
			}
		}

		time += elapsed;

		if (time < 0.0f) {
			if (currentAnimation.looping)
				time += length;
			else
				time = 0.0f;

			SpriterAnimationListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++)
				items[i].onAnimationFinished(this, currentAnimation);
			listeners.end();

		} else if (time >= length) {

			if (currentAnimation.looping)
				time -= length;
			else
				time = length;

			SpriterAnimationListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++)
				items[i].onAnimationFinished(this, currentAnimation);
			listeners.end();
		}

		if (nextAnimation == null) {
			FrameData.update(frameData, frameUpdateConfiguration, currentAnimation, time, deltaTime);
		} else {
			FrameData.update(frameData, frameUpdateConfiguration, currentAnimation, nextAnimation, time, deltaTime,
					factor);
		}

		// Local postprocessing
		postProcess(frameData);

		dirtyBoundingBox = true;
	}

	/**
	 * Display data from current {@link FrameData}:
	 * 
	 * Sprites are drawn with given {@link Batch}. Character maps apply.
	 * 
	 * Sounds are automatically played with libGDX backend. Character maps also
	 * apply.
	 * 
	 * Events are dispatched to registered {@link SpriterAnimationListener}s.
	 * 
	 * @param batch
	 *            Batch to draw sprites
	 */
	public void draw(Batch batch) {
		draw(batch, null);
	}

	/**
	 * Display data from current {@link FrameData}:
	 * 
	 * Sprites are drawn with given {@link Batch}. Character maps apply.
	 * 
	 * Sounds are automatically played with libGDX backend. Character maps also
	 * apply.
	 * 
	 * Points and boxes are drawn with given {@link ShapeRenderer} if it is not
	 * null.
	 * 
	 * Events are dispatched to registered {@link SpriterAnimationListener}s.
	 * 
	 * @param batch
	 *            Batch to draw sprites
	 * @param renderer
	 *            Renderer to draw points and boxes, no render if null
	 */
	public void draw(Batch batch, ShapeRenderer renderer) {
		for (SpriterObject info : frameData.spriteData) {
			SpriterFileInfo file = info.file;
			if (file.folderId >= 0 && file.fileId >= 0) {
				// Negative id means "don't display"
				drawObject(batch, assets.getSprite(file), info);
			}
		}

		for (SpriterSound info : frameData.sounds) {
			SpriterFileInfo file = info.file;
			if (file.folderId >= 0 && file.fileId >= 0) {
				// Negative id means "don't display"
				playSound(assets.getSound(file), info);
			}
		}

		if (renderer != null)
			drawDebug(renderer);

		for (String eventName : frameData.events)
			dispatchEvent(eventName);
	}

	/**
	 * Draw points and boxes of current {@link FrameData} with given
	 * {@link ShapeRenderer}.
	 * 
	 * This method is called from {@link #draw(Batch, ShapeRenderer)} if a
	 * not-null {@link ShapeRenderer} is provided, so regular usage does not
	 * imply calls to this very method.
	 * 
	 * @param renderer
	 *            Renderer to draw points and boxes
	 */
	public void drawDebug(ShapeRenderer renderer) {
		for (ObjectMap.Entry<String, SpriterObject> entry : frameData.pointData)
			drawPoint(renderer, entry.key, entry.value);

		for (IntMap.Entry<SpriterObject> entry : frameData.boxData)
			drawBox(renderer, entity.objectInfos.get(entry.key), entry.value);
	}

	/**
	 * Draw a {@link SpriterObject} (and associated {@link Sprite}) on given
	 * {@link Batch}.
	 * 
	 * Override this method if you want custom sprite drawing.
	 * 
	 * @param batch
	 *            Batch to draw sprite to
	 * @param sprite
	 *            Sprite associated to object
	 * @param object
	 *            Object to draw
	 */
	protected void drawObject(Batch batch, Sprite sprite, SpriterObject object) {

		float scaleX = object.scaleX;
		float scaleY = object.scaleY;

		float originX = sprite.getWidth() * object.pivotX;
		float originY = sprite.getHeight() * object.pivotY;

		float x = object.x - originX - this.pivotX;
		float y = object.y - originY - this.pivotY;

		float angle = object.angle;

		float alpha = object.alpha;

		sprite.setOrigin(originX, originY);
		sprite.setScale(scaleX, scaleY);
		sprite.setRotation(angle);
		sprite.setPosition(x, y);
		sprite.setAlpha(alpha);

		sprite.draw(batch);
	}

	/**
	 * Play given {@link Sound} with its information.
	 * 
	 * Override this method if you want custom sound playing.
	 * 
	 * @param sound
	 *            Sound to play
	 * @param info
	 *            Info related to the sound
	 */
	protected void playSound(Sound sound, SpriterSound info) {
		sound.play(info.volume, 1.0f, info.panning);
	}

	/**
	 * Draw a point with given {@link ShapeRenderer}.
	 * 
	 * Override this method if you want custom point drawing.
	 * 
	 * @param shapeRenderer
	 *            Renderer to display the point
	 * @param timelineName
	 *            Name of the associated timeline
	 * @param info
	 *            Point object
	 */
	protected void drawPoint(ShapeRenderer shapeRenderer, String timelineName, SpriterObject info) {
		float x = this.spatial.x + info.x - this.pivotX;
		float y = this.spatial.y + info.y - this.pivotY;
		float radius = Math.max(info.scaleX * this.spatial.scaleX, info.scaleY * this.spatial.scaleY);
		shapeRenderer.circle(x, y, radius);
	}

	/**
	 * Draw a box with given {@link ShapeRenderer}.
	 * 
	 * Override this method if you want custom box drawing.
	 * 
	 * @param shapeRenderer
	 *            Renderer to display the box
	 * @param objInfo
	 *            Object info related to the box
	 * @param info
	 *            Box object
	 */
	protected void drawBox(ShapeRenderer shapeRenderer, SpriterObjectInfo objInfo, SpriterObject info) {
		float x = this.spatial.x + info.x - this.pivotX;
		float y = this.spatial.y + info.y - this.pivotY;
		float width = objInfo.width * info.scaleX * this.spatial.scaleX;
		// Don't forget y-axis is the other way round.
		float height = objInfo.height * info.scaleY * this.spatial.scaleY * -1f;
		shapeRenderer.rect(x, y, width, height);
	}

	/**
	 * Dispatch an event triggered by current animation.
	 * 
	 * Override this method if you want custom event handling.
	 * 
	 * @param eventName
	 *            Event to dispatch
	 */
	protected void dispatchEvent(String eventName) {
		SpriterAnimationListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++)
			items[i].onEventTriggered(this, eventName);
		listeners.end();
	}

	private void updateBoundingBox() {
		boolean firstItem = true;

		for (SpriterObject info : frameData.spriteData) {
			SpriterFileInfo file = info.file;

			// Negative id means "don't display"
			if (file.folderId >= 0 && file.fileId >= 0) {
				Sprite sprite = assets.getSprite(file);

				float originX = sprite.getWidth() * info.pivotX;
				float originY = sprite.getHeight() * info.pivotY;

				sprite.setOrigin(originX, originY);
				sprite.setScale(info.scaleX, info.scaleY);
				sprite.setRotation(info.angle);
				sprite.setPosition(info.x - originX - this.pivotX, info.y - originY - this.pivotY);

				Rectangle localBoundingBox = sprite.getBoundingRectangle();

				if (firstItem) {
					boundingBox.set(localBoundingBox);
					firstItem = false;
				} else {
					boundingBox.merge(localBoundingBox);
				}
			}
		}
	}

	private void postProcess(FrameData frameData) {

		for (SpriterObject info : frameData.spriteData) {
			SpriterFileInfo fileInfo = info.file = applyCharacterMaps(info.file);

			FrameData.applyParentTransform(info, spatial);

			// Pivot points may be affected by character map
			if ((Float.isNaN(info.pivotX) || Float.isNaN(info.pivotY))
					&& (fileInfo.folderId != -1 && fileInfo.fileId != -1)) {
				SpriterFile file = spriterData.folders.get(fileInfo.folderId).files.get(fileInfo.fileId);
				info.pivotX = file.pivotX;
				info.pivotY = file.pivotY;
			}

		}

		for (SpriterSound info : frameData.sounds)
			info.file = applyCharacterMaps(info.file);
	}

	private SpriterFileInfo applyCharacterMaps(SpriterFileInfo file) {
		// Check values from character maps
		if (characterMaps.size > 0) {
			for (int i = characterMaps.size - 1; i >= 0; i--) {
				SpriterCharacterMap characterMap = characterMaps.get(i);
				for (SpriterMapInstruction map : characterMap.maps) {
					if (map.file.equals(file))
						return map.target;
				}
			}
		}

		return file;
	}
}
