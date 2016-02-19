// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.spriter.data.SpriterAnimation;

/**
 * Interface for listening to Spriter animation completion and events.
 * 
 * @see SpriterAnimator#draw(Batch batch, ShapeRenderer renderer)
 * 
 * @author thorthur
 * 
 */
public interface SpriterAnimationListener {

	/**
	 * Triggered once a {@link SpriterAnimation} played by a
	 * {@link SpriterAnimator} reaches end.
	 * 
	 * @param animator
	 *            Animator playing the animation
	 * @param animation
	 *            Animation reaching its end
	 */
	public void onAnimationFinished(SpriterAnimator animator, SpriterAnimation animation);

	/**
	 * Triggered once an event happens in a {@link SpriterAnimation} played by a
	 * {@link SpriterAnimator}.
	 * 
	 * @param animator
	 *            Animator playing the animation
	 * @param event
	 *            Event happening
	 */
	public void onEventTriggered(SpriterAnimator animator, String event);

}