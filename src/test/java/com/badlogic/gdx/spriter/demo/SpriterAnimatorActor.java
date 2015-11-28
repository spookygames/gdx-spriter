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

		if(disabled || animator == null)
			return;

		animator.update(delta);
	}

	@Override
	public void layout () {
		if (animator == null) return;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		validate();

		if(animator == null)
			return;

		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		float x = getX();
		float y = getY();
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		float rotation = getRotation();

		float formerX = animator.getX();
		float formerY = animator.getY();
		float formerScaleX = animator.getScaleX();
		float formerScaleY = animator.getScaleY();
		float formerAngle = animator.getAngle();

		animator.setX(formerX + x);
		animator.setY(formerY + y);
		animator.setScale(formerScaleX * scaleX, formerScaleY * scaleY);
		animator.setAngle(formerAngle + rotation);

		animator.draw(batch);

		animator.setX(formerX);
		animator.setY(formerY);
		animator.setScale(formerScaleX, formerScaleY);
		animator.setAngle(formerAngle);
	}

	@Override
	public void drawDebug(ShapeRenderer renderer) {
		if (!getDebug())
			return;

		super.drawDebug(renderer);

		if(animator == null)
			return;

		float x = getX();
		float y = getY();
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		float rotation = getRotation();

		float formerX = animator.getX();
		float formerY = animator.getY();
		float formerScaleX = animator.getScaleX();
		float formerScaleY = animator.getScaleY();
		float formerAngle = animator.getAngle();

		animator.setX(formerX + x);
		animator.setY(formerY + y);
		animator.setScale(formerScaleX * scaleX, formerScaleY * scaleY);
		animator.setAngle(formerAngle + rotation);

		renderer.set(ShapeType.Line);
		renderer.setColor(getStage().getDebugColor());

		// Draw position
		renderer.circle(animator.getX(), animator.getY(), 1f);
		animator.drawDebug(renderer);

		animator.setX(formerX);
		animator.setY(formerY);
		animator.setScale(formerScaleX, formerScaleY);
		animator.setAngle(formerAngle);
	}
}
