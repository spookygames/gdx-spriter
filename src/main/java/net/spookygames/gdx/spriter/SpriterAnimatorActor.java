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

package net.spookygames.gdx.spriter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Scaling;

import net.spookygames.gdx.spriter.data.SpriterAnimation;
import net.spookygames.gdx.spriter.data.SpriterCharacterMap;

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

		if (this.animator != null)
			this.animator.removeAnimationListener(this);

		this.animator = animator;

		if (this.animator != null) {
			this.animator.addAnimationListener(this);
			this.animator.update(0f);
		}

		invalidate();
		if (oldPrefWidth != getPrefWidth())
			invalidateHierarchy();
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
		Rectangle bounds = animator.getBoundingBox();

		prefWidth = bounds.width;
		prefHeight = bounds.height;
		float width = getWidth();
		float height = getHeight();

		Vector2 size = scaling.apply(prefWidth, prefHeight, width, height);
		imageWidth = size.x;
		imageHeight = size.y;

		float xOffset = bounds.x * imageWidth / prefWidth;
		float yOffset = bounds.y * imageHeight / prefHeight;

		if ((align & Align.left) != 0)
			imageX = xOffset;
		else if ((align & Align.right) != 0)
			imageX = width - imageWidth - xOffset;
		else
			imageX = width / 2 - xOffset - imageWidth / 2;

		if ((align & Align.top) != 0)
			imageY = height - imageHeight - yOffset;
		else if ((align & Align.bottom) != 0)
			imageY = yOffset;
		else
			imageY = height / 2 - yOffset - imageHeight / 2;
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

		// Draw animator
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

		// Draw points and boxes
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
	public void onAnimationChanged(SpriterAnimator animator, SpriterAnimation formerAnimation,
			SpriterAnimation newAnimation) {
		float oldPrefWidth = getPrefWidth();
		invalidate();
		this.animator.update(0f);
		if (oldPrefWidth != getPrefWidth())
			invalidateHierarchy();
	}

	@Override
	public void onCharacterMapAdded(SpriterAnimator animator, SpriterCharacterMap characterMap) {
	}

	@Override
	public void onCharacterMapRemoved(SpriterAnimator animator, SpriterCharacterMap characterMap) {
	}
	
	@Override
	public String toString() {
		if(animator == null)
			return super.toString();
		else
			return super.toString() + " -- " + animator.getName();
	}
}
