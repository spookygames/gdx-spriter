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

package net.spookygames.gdx.spriter.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import net.spookygames.gdx.spriter.data.SpriterAssetProvider;
import net.spookygames.gdx.spriter.data.SpriterData;
import net.spookygames.gdx.spriter.data.SpriterFile;
import net.spookygames.gdx.spriter.data.SpriterFileInfo;
import net.spookygames.gdx.spriter.data.SpriterFolder;

/**
 * The {@code DefaultSpriterAssetProvider} class provides assets from Spriter
 * file infos by creating all of them during construction.
 * 
 * {@code DefaultSpriterAssetProvider} implements {@link Disposable} as it
 * handles creation of various disposable objects. So don't forget to call
 * {@link #dispose()} when you're done!
 * 
 * @see net.spookygames.gdx.spriter.data.SpriterAssetProvider
 * @see SpriterDataLoaderAssetProvider
 * @see com.badlogic.gdx.utils.Disposable
 * 
 * @author thorthur
 * 
 */
public class DefaultSpriterAssetProvider implements SpriterAssetProvider, Disposable {

	private final ObjectMap<SpriterFileInfo, Sprite> sprites = new ObjectMap<SpriterFileInfo, Sprite>();
	private final ObjectMap<SpriterFileInfo, Sound> sounds = new ObjectMap<SpriterFileInfo, Sound>();

	/**
	 * Initializes a new {@code DefaultSpriterAssetProvider} from given Spriter
	 * data and root asset folder.
	 * 
	 * @param data
	 *            Spriter data to provide assets to
	 * @param root
	 *            Base folder where all assets could be found
	 */
	public DefaultSpriterAssetProvider(SpriterData data, String root) {
		super();

		for (SpriterFolder folder : data.folders) {
			for (SpriterFile file : folder.files) {
				SpriterFileInfo info = new SpriterFileInfo();
				info.folderId = folder.id;
				info.fileId = file.id;
				FileHandle handle = Gdx.files.internal(root + file.name);
				switch (file.type) {
				case Image:
					sprites.put(info, new Sprite(new Texture(handle)));
					break;
				case Sound:
					sounds.put(info, Gdx.audio.newSound(handle));
					break;
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.spookygames.gdx.spriter.data.SpriterAssetProvider#getSprite(com.
	 * badlogic .gdx.spriter.data.SpriterFileInfo)
	 */
	@Override
	public Sprite getSprite(SpriterFileInfo info) {
		return sprites.get(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.spookygames.gdx.spriter.data.SpriterAssetProvider#getSound(com.
	 * badlogic .gdx.spriter.data.SpriterFileInfo)
	 */
	@Override
	public Sound getSound(SpriterFileInfo info) {
		return sounds.get(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.utils.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		for (Sprite s : sprites.values())
			s.getTexture().dispose();
		for (Sound s : sounds.values())
			s.dispose();
	}
}
