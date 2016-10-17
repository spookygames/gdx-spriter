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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.spookygames.gdx.spriter.data.SpriterAnimation;
import net.spookygames.gdx.spriter.data.SpriterCharacterMap;

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