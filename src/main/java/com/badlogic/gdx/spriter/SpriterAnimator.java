// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.ObjectMap;

public class SpriterAnimator {

	protected final SpriterData spriterData;
	protected final SpriterEntity entity;
	private final SpriterAssetProvider assets;
	protected final ObjectMap<String, SpriterAnimation> animations = new ObjectMap<String, SpriterAnimation>();

	private SpriterAnimation currentAnimation = null;
	private SpriterAnimation nextAnimation = null;
	private final Array<SpriterCharacterMap> characterMaps = new Array<SpriterCharacterMap>();
	private final Array<SpriterAnimationListener> listeners = new Array<SpriterAnimationListener>();

	// This one will be used for all things geometric
	private final SpriterSpatial spatial = new SpriterSpatial();
	private float pivotX = 0f;
	private float pivotY = 0f;

	private float speed = 1.0f;
	private float time = 0f;

	private float totalTransitionTime = 0f;
	private float transitionTime = 0f;
	private float factor = 0f;

	private FrameData frameData = new FrameData();
	private FrameMetadata metaData = new FrameMetadata();

	public SpriterAnimator(SpriterEntity spriterEntity) {
		entity = spriterEntity;
		spriterData = spriterEntity.data;
		assets = spriterData.assetProvider;

		for (SpriterAnimation animation : spriterEntity.animations)
			animations.put(animation.name, animation);
	}

	public SpriterData getSpriterData() {
		return spriterData;
	}

	public SpriterEntity getEntity() {
		return entity;
	}

	public Iterable<String> getAnimationNames() {
		return animations.keys();
	}

	public Iterable<SpriterAnimation> getAnimations() {
		return animations.values();
	}

	public SpriterAnimation getCurrentAnimation() {
		return currentAnimation;
	}

	public SpriterAnimation getNextAnimation() {
		return nextAnimation;
	}

	public void addCharacterMap(String characterMapName) {
		for (SpriterCharacterMap map : entity.characterMaps) {
			if (characterMapName.equals(map.name)) {
				addCharacterMap(map);
				break;
			}
		}
	}

	public void addCharacterMap(SpriterCharacterMap characterMap) {
		if (characterMap == null || this.characterMaps.contains(characterMap, true))
			return;
		this.characterMaps.add(characterMap);
	}

	public Array<SpriterCharacterMap> getCharacterMaps() {
		return this.characterMaps;
	}

	public boolean removeCharacterMap(SpriterCharacterMap characterMap) {
		return this.characterMaps.removeValue(characterMap, true);
	}

	public void clearCharacterMaps() {
		this.characterMaps.clear();
	}

	public void addAnimationListener(SpriterAnimationListener listener) {
		listeners.add(listener);
	}

	public boolean removeAnimationListener(SpriterAnimationListener listener) {
		return listeners.removeValue(listener, true);
	}

	public String getName() {
		return currentAnimation.name;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getLength() {
		return currentAnimation.length;
	}

	public float getProgress() {
		return time / currentAnimation.length;
	}

	public void setProgress(float progress) {
		this.time = progress * currentAnimation.length;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public float getX() {
		return this.spatial.x;
	}

	public void setX(float x) {
		this.spatial.x = x;
	}

	public float getY() {
		return spatial.y;
	}

	public void setY(float y) {
		this.spatial.y = y;
	}

	public void setPosition(float x, float y) {
		this.spatial.x = x;
		this.spatial.y = y;
	}

	public float getPivotX() {
		return pivotX;
	}

	public void setPivotX(float pivotX) {
		this.pivotX = pivotX;
	}

	public float getPivotY() {
		return pivotY;
	}

	public void setPivotY(float pivotY) {
		this.pivotY = pivotY;
	}

	public void setPivot(float pivotX, float pivotY) {
		this.pivotX = pivotX;
		this.pivotY = pivotY;
	}

	public float getScaleX() {
		return spatial.scaleX;
	}

	public void setScaleX(float scaleX) {
		this.spatial.scaleX = scaleX;
	}

	public float getScaleY() {
		return spatial.scaleY;
	}

	public void setScaleY(float scaleY) {
		this.spatial.scaleY = scaleY;
	}

	public void setScale(float scaleX, float scaleY) {
		this.spatial.scaleX = scaleX;
		this.spatial.scaleY = scaleY;
	}

	public float getAngle() {
		return spatial.angle;
	}

	public void setAngle(float angle) {
		this.spatial.angle = angle;
	}

	public float getAlpha() {
		return spatial.alpha;
	}

	public void setAlpha(float alpha) {
		this.spatial.alpha = alpha;
	}

	public FrameData getCurrentData() {
		return frameData;
	}

	public FrameMetadata getCurrentMetadata() {
		return metaData;
	}

	public void play(String name) {
		SpriterAnimation animation = animations.get(name);
		play(animation);
	}

	public void play(SpriterAnimation animation) {
		time = 0;

		currentAnimation = animation;

		nextAnimation = null;
	}

	public void transition(String name, float totalTransitionTime) {
		transition(animations.get(name), totalTransitionTime);
	}

	public void transition(SpriterAnimation animation, float totalTransitionTime) {
		this.totalTransitionTime = totalTransitionTime;
		transitionTime = 0;
		nextAnimation = animation;
	}

	public void blend(String first, String second, float factor) {
		blend(animations.get(first), animations.get(second), factor);
	}

	public void blend(SpriterAnimation first, SpriterAnimation second, float factor) {
		play(first);
		nextAnimation = second;
		totalTransitionTime = 0;
		this.factor = factor;
	}

	public void update(float deltaTime) {

		if (currentAnimation == null)
			return;

		deltaTime *= 1000f; // We're talking milliseconds here
		float elapsed = deltaTime * speed;

		if (nextAnimation != null && totalTransitionTime != 0.0f) {
			elapsed += elapsed * factor * currentAnimation.length / nextAnimation.length;

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
				time += currentAnimation.length;
			else
				time = 0.0f;

			for (SpriterAnimationListener listener : listeners)
				listener.onAnimationFinished(this, currentAnimation);

		} else if (time >= currentAnimation.length) {

			if (currentAnimation.looping)
				time -= currentAnimation.length;
			else
				time = currentAnimation.length;

			for (SpriterAnimationListener listener : listeners)
				listener.onAnimationFinished(this, currentAnimation);
		}

		if (nextAnimation == null) {
			frameData = FrameData.create(currentAnimation, time);
			metaData = FrameMetadata.create(currentAnimation, time, deltaTime);
		} else {
			frameData = FrameData.create(currentAnimation, nextAnimation, time, factor);
			metaData = FrameMetadata.create(currentAnimation, nextAnimation, time, deltaTime, factor);
		}
	}

	public void draw(Batch batch) {
		draw(batch, null);
	}

	public void draw(Batch batch, ShapeRenderer renderer) {
		for (SpriterObject info : frameData.spriteData) {
			SpriterFileInfo file = applyCharacterMap(info.file);
			preprocessObject(info, file);
			drawObject(batch, assets.getSprite(file), info);
		}

		for (SpriterSound info : metaData.sounds)
			playSound(assets.getSound(applyCharacterMap(info.file)), info);

		if (renderer != null)
			drawDebug(renderer);

		for (String eventName : metaData.events)
			dispatchEvent(eventName);
	}

	public void drawDebug(ShapeRenderer renderer) {
		for (SpriterObject info : frameData.pointData)
			drawPoint(renderer, info);

		for (Entry<SpriterObject> entry : frameData.boxData)
			drawBox(renderer, entity.objectInfos.get(entry.key), entry.value);
	}

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

	protected void playSound(Sound sound, SpriterSound info) {
		sound.play(info.volume, 1.0f, info.panning);
	}

	protected void drawPoint(ShapeRenderer shapeRenderer, SpriterObject info) {
		float x = this.spatial.x + info.x - this.pivotX;
		float y = this.spatial.y + info.y - this.pivotY;
		float radius = Math.max(info.scaleX * this.spatial.scaleX, info.scaleY * this.spatial.scaleY);
		shapeRenderer.circle(x, y, radius);
	}

	protected void drawBox(ShapeRenderer shapeRenderer, SpriterObjectInfo objInfo, SpriterObject info) {
		float x = this.spatial.x + info.x - this.pivotX;
		float y = this.spatial.y + info.y - this.pivotY;
		float width = objInfo.width * info.scaleX * this.spatial.scaleX;
		// Don't forget y-axis is the other way round.
		float height = objInfo.height * info.scaleY * this.spatial.scaleY * -1f;
		shapeRenderer.rect(x, y, width, height);
	}

	protected void dispatchEvent(String eventName) {
		for (SpriterAnimationListener listener : listeners)
			listener.onEventTriggered(this, eventName);
	}

	private SpriterFileInfo applyCharacterMap(SpriterFileInfo file) {
		// Check values from character map
		if (characterMaps.size > 0) {
			for (int i = characterMaps.size - 1; i >= 0; i--) {
				SpriterCharacterMap characterMap = characterMaps.get(i);
				for (SpriterMapInstruction map : characterMap.maps) {
					if (map.file.equals(file) && map.target.folderId >= 0 && map.target.fileId >= 0)
						return map.target;
				}
			}
		}

		return file;
	}

	private void preprocessObject(SpriterObject object, SpriterFileInfo info) {
		FrameData.applyParentTransform(object, spatial);

		// Pivot points may be affected by character map
		// Sort this out as late as we can
		if (Float.isNaN(object.pivotX) || Float.isNaN(object.pivotY)) {
			SpriterFile file = spriterData.folders.get(info.folderId).files.get(info.fileId);
			object.pivotX = file.pivotX;
			object.pivotY = file.pivotY;
		}
	}
}
