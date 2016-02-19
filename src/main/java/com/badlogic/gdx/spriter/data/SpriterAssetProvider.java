//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Interface for providing assets (namely {@link Sprite}s and {@link Sound}s)
 * from Spriter file infos.
 * 
 * @see com.badlogic.gdx.spriter.loader.DefaultSpriterAssetProvider
 * @see com.badlogic.gdx.spriter.loader.SpriterDataLoaderAssetProvider
 * 
 * @author thorthur
 * 
 */
public interface SpriterAssetProvider {

	/**
	 * Get a {@link Sprite} from given file info.
	 * 
	 * @param file
	 *            File info to get sprite from
	 * @return The sprite contained in file.
	 */
	public Sprite getSprite(SpriterFileInfo file);

	/**
	 * Get a {@link Sound} from given file info.
	 * 
	 * @param file
	 *            File info to get sound from
	 * @return The sound contained in file.
	 */
	public Sound getSound(SpriterFileInfo file);

}
