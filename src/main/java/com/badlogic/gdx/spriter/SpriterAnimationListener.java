// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;

/**
 * Interface for listening to various Spriter animator events.
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
	 * @see SpriterAnimator#update(float)
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
	 * @see SpriterAnimator#draw(Batch batch, ShapeRenderer renderer)
	 */
	public void onEventTriggered(SpriterAnimator animator, String event);

	/**
	 * Triggered once the current {@link SpriterAnimation} played by a
	 * {@link SpriterAnimator} is changed.
	 * 
	 * @param animator
	 *            Animator playing the animation
	 * @param formerAnimation
	 *            Animation previously being played (may be null)
	 * @param newAnimation
	 *            Next animation to play (may be null)
	 * @see SpriterAnimator#play(SpriterAnimation)
	 */
	public void onAnimationChanged(SpriterAnimator animator, SpriterAnimation formerAnimation,
			SpriterAnimation newAnimation);

	/**
	 * Triggered once a {@link SpriterCharacterMap} is added to a
	 * {@link SpriterAnimator}.
	 * 
	 * @param animator
	 *            Animator subject to the change
	 * @param characterMap
	 *            Character map added
	 * @see SpriterAnimator#addCharacterMap(SpriterCharacterMap)
	 */
	public void onCharacterMapAdded(SpriterAnimator animator, SpriterCharacterMap characterMap);

	/**
	 * Triggered once a {@link SpriterCharacterMap} is removed from a
	 * {@link SpriterAnimator}.
	 * 
	 * @param animator
	 *            Animator subject to the change
	 * @param characterMap
	 *            Character map removed
	 * @see SpriterAnimator#removeCharacterMap(SpriterCharacterMap)
	 */
	public void onCharacterMapRemoved(SpriterAnimator animator, SpriterCharacterMap characterMap);

}