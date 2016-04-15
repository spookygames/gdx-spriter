// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.spriter.SpriterAnimator;

public class SpriterAnimatorActor extends Widget implements Disableable {

	private SpriterAnimator animator = null;
	private boolean disabled = false;

	public SpriterAnimatorActor() {
		this(null);
	}

	public SpriterAnimatorActor(SpriterAnimator animator) {
		super();
		this.animator = animator;
	}

	public SpriterAnimator getAnimator() {
		return animator;
	}

	public void setAnimator(SpriterAnimator animator) {
		this.animator = animator;
	}

	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (animator == null)
			return;

		animator.update(disabled ? 0f : delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();

		if (animator == null)
			return;

		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		float x = getX();
		float y = getY();
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		float rotation = getRotation();

		float animationX = animator.getX();
		float animationY = animator.getY();
		float animationScaleX = animator.getScaleX();
		float animationScaleY = animator.getScaleY();
		float animationAngle = animator.getAngle();

		animator.setX(animationX + x);
		animator.setY(animationY + y);
		animator.setScale(animationScaleX * scaleX, animationScaleY * scaleY);
		animator.setAngle(animationAngle + rotation);

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

		float x = getX();
		float y = getY();
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		float rotation = getRotation();

		float animationX = animator.getX();
		float animationY = animator.getY();
		float animationScaleX = animator.getScaleX();
		float animationScaleY = animator.getScaleY();
		float animationAngle = animator.getAngle();

		animator.setX(animationX + x);
		animator.setY(animationY + y);
		animator.setScale(animationScaleX * scaleX, animationScaleY * scaleY);
		animator.setAngle(animationAngle + rotation);

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
}
