// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

/**
 * The {@code FrameDataUpdateConfiguration} class specifies which part of a
 * {@link FrameData} should be updated during a call to
 * {@link SpriterAnimator#update(float)}. Any field that is not updated will be
 * cleared during update.
 * 
 * @see FrameData
 * @see SpriterAnimator
 * 
 * @author thorthur
 * 
 */
public class FrameDataUpdateConfiguration {

	/**
	 * Defines whether spatial data ({@link FrameData#spriteData},
	 * {@link FrameData#pointData} and {@link FrameData#boxData}) should be
	 * updated during a call to {@link SpriterAnimator#update(float)}.
	 */
	public boolean spatial = true;

	/**
	 * Defines whether {@link FrameData#animationTags},
	 * {@link FrameData#animationVars}, {@link FrameData#objectTags} and
	 * {@link FrameData#objectVars} should be updated during a call to
	 * {@link SpriterAnimator#update(float)}.
	 */
	public boolean tagsAndVariables = true;

	/**
	 * Defines whether {@link FrameData#events} should be updated during a call
	 * to {@link SpriterAnimator#update(float)}.
	 */
	public boolean events = true;

	/**
	 * Defines whether {@link FrameData#sounds} should be updated during a call
	 * to {@link SpriterAnimator#update(float)}.
	 */
	public boolean sounds = true;

	@Override
	public String toString() {
		return "FrameDataUpdateConfiguration [spatial=" + this.spatial + ", tagsAndVariables=" + this.tagsAndVariables + ", events=" + this.events + ", sounds=" + this.sounds + "]";
	}

}
