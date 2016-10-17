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

import net.spookygames.gdx.spriter.data.SpriterAnimation;
import net.spookygames.gdx.spriter.data.SpriterCharacterMap;

/**
 * Adapter class for {@link SpriterAnimationListener}.
 * 
 * @author thorthur
 * 
 */
public class SpriterAnimationAdapter implements SpriterAnimationListener {

	@Override
	public void onAnimationFinished(SpriterAnimator animator, SpriterAnimation animation) {
	}

	@Override
	public void onEventTriggered(SpriterAnimator animator, String event) {
	}

	@Override
	public void onAnimationChanged(SpriterAnimator animator, SpriterAnimation former, SpriterAnimation newer) {
	}

	@Override
	public void onCharacterMapAdded(SpriterAnimator animator, SpriterCharacterMap characterMap) {
	}

	@Override
	public void onCharacterMapRemoved(SpriterAnimator animator, SpriterCharacterMap characterMap) {
	}
}