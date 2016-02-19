// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.spriter.data.SpriterAssetProvider;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * The {@code DefaultSpriterAssetProvider} class provides assets from Spriter
 * file infos by creating all of them during construction.
 * 
 * {@code DefaultSpriterAssetProvider} implements {@link Disposable} as it
 * handles creation of various disposable objects. So don't forget to call
 * {@link #dispose()} when you're done!
 * 
 * @see com.badlogic.gdx.spriter.data.SpriterAssetProvider
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
	 * @see
	 * com.badlogic.gdx.spriter.data.SpriterAssetProvider#getSprite(com.badlogic
	 * .gdx.spriter.data.SpriterFileInfo)
	 */
	@Override
	public Sprite getSprite(SpriterFileInfo info) {
		return sprites.get(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.badlogic.gdx.spriter.data.SpriterAssetProvider#getSound(com.badlogic
	 * .gdx.spriter.data.SpriterFileInfo)
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
