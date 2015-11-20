//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;

public interface SpriterAssetProvider {

	public Sprite getSprite(SpriterFileInfo file);

	public Sound getSound(SpriterFileInfo file);

}
