// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterCharacterMap;

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