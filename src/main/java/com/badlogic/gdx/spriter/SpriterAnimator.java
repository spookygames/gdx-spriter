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
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterMapInstruction;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterObjectInfo;
import com.badlogic.gdx.spriter.data.SpriterSound;
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

	private float x = 0f;
	private float y = 0f;
	private float pivotX = 0f;
	private float pivotY = 0f;
	private float scaleX = 1.0f;
	private float scaleY = 1.0f;
	private float angle = 0f;
	private float alpha = 1.0f;

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
		for(SpriterCharacterMap map : entity.characterMaps) {
			if(characterMapName.equals(map.name)) {
				addCharacterMap(map);
				break;
			}
		}
	}

	public void addCharacterMap(SpriterCharacterMap characterMap) {
		if(characterMap == null || this.characterMaps.contains(characterMap, true))
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
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
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
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
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
		for (SpriterObject info : frameData.spriteData)
			drawObject(batch, assets.getSprite(applyCharacterMap(info.file)), info);

		for (SpriterSound info : metaData.sounds)
			playSound(assets.getSound(applyCharacterMap(info.file)), info);

		if (renderer != null) {
			for (SpriterObject info : frameData.pointData)
				drawPoint(renderer, info);

			for (Entry<SpriterObject> entry : frameData.boxData)
				drawBox(renderer, entity.objectInfos.get(entry.key), entry.value);
		}

		for (String eventName : metaData.events)
			dispatchEvent(eventName);
	}

	protected void drawObject(Batch batch, Sprite sprite, SpriterObject object) {
		
		float pivotX = this.pivotX + (sprite.getWidth() * object.pivotX);
		float x = this.x + object.x - pivotX;
		
		float pivotY = this.pivotY + (sprite.getHeight() * object.pivotY);
		float y = this.y + object.y - pivotY;

		float angle = this.angle + object.angle;
		
		float scaleX = this.scaleX * object.scaleX;
		float scaleY = this.scaleY * object.scaleY;
		
		float alpha = this.alpha * object.alpha;

		sprite.setX(x);
		sprite.setY(y);
		sprite.setOrigin(pivotX, pivotY);
		sprite.setRotation(angle);
		sprite.setAlpha(alpha);
		sprite.setScale(scaleX, scaleY);
		
		sprite.draw(batch);
	}

	protected void playSound(Sound sound, SpriterSound info) {
		sound.play(info.volume, 1.0f, info.panning);
	}

	protected void drawPoint(ShapeRenderer shapeRenderer, SpriterObject info) {
		shapeRenderer.circle(this.x + info.x, this.y + info.y, Math.max(info.scaleX, info.scaleY));
	}

	protected void drawBox(ShapeRenderer shapeRenderer, SpriterObjectInfo objInfo, SpriterObject info) {
		shapeRenderer.rect(this.x + info.x, this.y + info.y, objInfo.width, objInfo.height);
	}

	protected void dispatchEvent(String eventName) {
		for (SpriterAnimationListener listener : listeners)
			listener.onEventTriggered(this, eventName);
	}

	private SpriterFileInfo applyCharacterMap(SpriterFileInfo file) {
		// Check values from character map
		if (characterMaps.size > 0) {
			for(int i = characterMaps.size - 1 ; i >= 0 ; i--) {
				SpriterCharacterMap characterMap = characterMaps.get(i);
				for (SpriterMapInstruction map : characterMap.maps) {
					if (map.file.equals(file) && map.target.folderId >= 0 && map.target.fileId >= 0)
						return map.target;
				}
			}
		}

		return file;
	}
}
