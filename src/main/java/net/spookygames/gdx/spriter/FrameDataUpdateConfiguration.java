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
		return "FrameDataUpdateConfiguration [spatial=" + this.spatial + ", tagsAndVariables=" + this.tagsAndVariables
				+ ", events=" + this.events + ", sounds=" + this.sounds + "]";
	}

}
