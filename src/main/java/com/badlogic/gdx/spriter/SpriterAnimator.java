// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.spriter.data.SpriterAssetProvider;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
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

	private final Array<SpriterAnimationListener> listeners = new Array<SpriterAnimationListener>();

	private SpriterData spriterData;
	private SpriterEntity entity;
	private SpriterAnimation currentAnimation;
	private SpriterAnimation nextAnimation;
	private SpriterCharacterMap characterMap;
	private FrameData frameData;
	private FrameMetadata metaData;

	private String name;
	private float speed;
	private float length;
	private float time;
	private float x;
	private float y;

	private final ObjectMap<String, SpriterAnimation> animations = new ObjectMap<String, SpriterAnimation>();

	private float totalTransitionTime;
	private float transitionTime;
	private float factor;

	private SpriterAssetProvider assets;

	public SpriterAnimator(SpriterEntity spriterEntity) {
		entity = spriterEntity;
		spriterData = spriterEntity.data;
		assets = spriterData.assetProvider;

		for (SpriterAnimation animation : spriterEntity.animations)
			animations.put(animation.name, animation);

		speed = 1.0f;
		play(animations.keys().next());

		metaData = new FrameMetadata();
	}

	public float getProgress() {
		return time / length;
	}

	public void setProgress(float progress) {
		this.time = progress * length;
	}

	public void addAnimationListener(SpriterAnimationListener listener) {
		listeners.add(listener);
	}

	public boolean removeAnimationListener(SpriterAnimationListener listener) {
		return listeners.removeValue(listener, true);
	}

	public SpriterData getSpriterData() {
		return spriterData;
	}

	public SpriterEntity getEntity() {
		return entity;
	}

	public SpriterAnimation getCurrentAnimation() {
		return currentAnimation;
	}

	public SpriterAnimation getNextAnimation() {
		return nextAnimation;
	}

	public SpriterCharacterMap getCharacterMap() {
		return characterMap;
	}

	public void setCharacterMap(SpriterCharacterMap characterMap) {
		this.characterMap = characterMap;
	}

	public String getName() {
		return name;
	}

	public float getSpeed() {
		return speed;
	}

	public float getLength() {
		return length;
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

	public FrameMetadata getMetadata() {
		return metaData;
	}

	public float getTotalTransitionTime() {
		return totalTransitionTime;
	}

	public float getTransitionTime() {
		return transitionTime;
	}

	public float getFactor() {
		return factor;
	}

	public Iterable<String> getAnimations() {
		return animations.keys();
	}

	public void play(String name) {
		SpriterAnimation animation = animations.get(name);
		play(animation);
	}

	public void play(SpriterAnimation animation) {
		time = 0;

		currentAnimation = animation;
		name = animation.name;

		nextAnimation = null;
		length = currentAnimation.length;
	}

	public void transition(String name, float totalTransitionTime) {
		this.totalTransitionTime = totalTransitionTime;
		transitionTime = 0;
		nextAnimation = animations.get(name);
	}

	public void blend(String first, String second, float factor) {
		play(first);
		nextAnimation = animations.get(second);
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
				play(nextAnimation.name);
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

			for (SpriterAnimationListener listener : listeners)
				listener.onAnimationFinished(this, currentAnimation);
			
		} else if (time >= length) {
			
			if (currentAnimation.looping)
				time -= length;
			else
				time = length;

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
		float newPivotX = (sprite.getWidth() * object.pivotX);
		float newX = this.x + object.x - newPivotX;
		float newPivotY = (sprite.getHeight() * object.pivotY);
		float newY = this.y + object.y - newPivotY;

		float angle = object.angle;

		sprite.setX(newX);
		sprite.setY(newY);

		sprite.setOrigin(newPivotX, newPivotY);
		sprite.setRotation(angle);

		sprite.setColor(1f, 1f, 1f, object.alpha);
		sprite.setScale(object.scaleX, object.scaleY);
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
		if (characterMap != null)
			for (SpriterMapInstruction map : characterMap.maps)
				if (map.file.equals(file) && map.target.folderId >= 0 && map.target.fileId >= 0)
					return map.target;

		return file;
	}
}
