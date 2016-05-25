// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;
import com.badlogic.gdx.utils.Scaling;

public class SpriterAnimatorActor extends Widget implements Disableable, SpriterAnimationListener {

	private SpriterAnimator animator = null;
	private boolean disabled = false;
	private int align = Align.center;
	private Scaling scaling = Scaling.fit;
	private float imageX, imageY, imageWidth, imageHeight;
	private float prefWidth, prefHeight;
	
	public SpriterAnimatorActor() {
		this(null);
	}

	public SpriterAnimatorActor(SpriterAnimator animator) {
		super();
		setAnimator(animator);
	}

	public SpriterAnimator getAnimator() {
		return animator;
	}

	public void setAnimator(SpriterAnimator animator) {
		if (this.animator == animator)
			return;
		
		float oldPrefWidth = getPrefWidth();

		if(this.animator != null)
			this.animator.removeAnimationListener(this);
		
		this.animator = animator;
		
		if(this.animator != null) {
			this.animator.addAnimationListener(this);
			this.animator.update(0f);
		}

		invalidate();
		if (oldPrefWidth != getPrefWidth()) invalidateHierarchy();
	}

	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
	}
	
	public int getAlign() {
		return this.align;
	}

	public void setAlign(int align) {
		this.align = align;
	}
	
	public Scaling getScaling() {
		return this.scaling;
	}

	public void setScaling(Scaling scaling) {
		this.scaling = scaling;
	}

	public float getMinWidth() {
		return 0;
	}

	public float getMinHeight() {
		return 0;
	}

	public float getPrefWidth() {
		validate();
		return prefWidth;
	}

	public float getPrefHeight() {
		validate();
		return prefHeight;
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (animator == null)
			return;
		
		animator.update(disabled ? 0f : delta);
	}

	@Override
	public void layout() {
		if (animator == null)
			return;

		// Compute bounding box
		Rectangle bounds = animator.computeBoundingBox();
		
		prefWidth = bounds.width;
		prefHeight = bounds.height;
		float width = getWidth();
		float height = getHeight();

		Vector2 size = scaling.apply(prefWidth, prefHeight, width, height);
		imageWidth = size.x;
		imageHeight = size.y;

		if ((align & Align.left) != 0)
			imageX = bounds.x;
		else if ((align & Align.right) != 0)
			imageX = width - bounds.width - bounds.x;
		else
			imageX = width / 2 - bounds.x - bounds.width / 2;

		if ((align & Align.top) != 0)
			imageY = height - bounds.height - bounds.y;
		else if ((align & Align.bottom) != 0)
			imageY = bounds.y;
		else
			imageY = height / 2 - bounds.y - bounds.height / 2;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();

		if (animator == null)
			return;

		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		float animationX = animator.getX();
		float animationY = animator.getY();
		float animationScaleX = animator.getScaleX();
		float animationScaleY = animator.getScaleY();
		float animationAngle = animator.getAngle();

		animator.setX(getX() + imageX);
		animator.setY(getY() + imageY);
		animator.setScaleX(getScaleX() * imageWidth / getPrefWidth());
		animator.setScaleY(getScaleY() * imageHeight / getPrefHeight());
		animator.setAngle(getRotation());

		// Update here again to take offsets into account
		animator.update(0f);
		animator.draw(batch);

		animator.setX(animationX);
		animator.setY(animationY);
		animator.setScale(animationScaleX, animationScaleY);
		animator.setAngle(animationAngle);
	}

	@Override
	public void drawDebug(ShapeRenderer renderer) {
		if (!getDebug())
			return;

		super.drawDebug(renderer);
		
		if (animator == null)
			return;

		renderer.set(ShapeType.Line);
		renderer.setColor(getStage().getDebugColor());

		float animationX = animator.getX();
		float animationY = animator.getY();
		float animationScaleX = animator.getScaleX();
		float animationScaleY = animator.getScaleY();
		float animationAngle = animator.getAngle();

		animator.setX(getX() + imageX);
		animator.setY(getY() + imageY);
		animator.setScaleX(getScaleX() * imageWidth / getPrefWidth());
		animator.setScaleY(getScaleY() * imageHeight / getPrefHeight());
		animator.setAngle(getRotation());
		
		// Update here again to take offsets into account
		animator.update(0f);

		// Draw position
		renderer.circle(animator.getX(), animator.getY(), 1f);
		animator.drawDebug(renderer);

		animator.setX(animationX);
		animator.setY(animationY);
		animator.setScale(animationScaleX, animationScaleY);
		animator.setAngle(animationAngle);
	}

	@Override
	public void onAnimationFinished(SpriterAnimator animator, SpriterAnimation animation) {
	}

	@Override
	public void onEventTriggered(SpriterAnimator animator, String event) {
	}

	@Override
	public void onAnimationChanged(SpriterAnimator animator, SpriterAnimation formerAnimation, SpriterAnimation newAnimation) {
		float oldPrefWidth = getPrefWidth();
		invalidate();
		this.animator.update(0f);
		if (oldPrefWidth != getPrefWidth()) invalidateHierarchy();
	}

	@Override
	public void onCharacterMapAdded(SpriterAnimator animator, SpriterCharacterMap characterMap) {
	}

	@Override
	public void onCharacterMapRemoved(SpriterAnimator animator, SpriterCharacterMap characterMap) {
	}
}
