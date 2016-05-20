// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.spriter.data.SpriterAssetProvider;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterFileType;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * The {@code SpriterDataLoaderAssetProvider} class provides assets from Spriter
 * file infos by querying them from a given {@link AssetManager}.
 * 
 * As such, it thus only acts as an association table between a file info from
 * Spriter (folder id + file id) and a proper file name in the
 * {@link AssetManager}.
 * 
 * {@code SpriterDataLoaderAssetProvider} does not directly handle creation of
 * the disposable objects. Don't forget however to call
 * {@link AssetManager#dispose()} on the manager when you're done!
 * 
 * @see com.badlogic.gdx.spriter.data.SpriterAssetProvider
 * @see DefaultSpriterAssetProvider
 * 
 * @author thorthur
 * 
 */
public class SpriterDataLoaderAssetProvider implements SpriterAssetProvider {

	private final ObjectMap<SpriterFileInfo, Sprite> sprites = new ObjectMap<SpriterFileInfo, Sprite>();
	private final ObjectMap<SpriterFileInfo, Sound> sounds = new ObjectMap<SpriterFileInfo, Sound>();
	private final ObjectMap<SpriterFileInfo, String> fileNames = new ObjectMap<SpriterFileInfo, String>();

	private final AssetManager assetManager;
	private final String textureAtlas;

	/**
	 * Initializes a new {@code SpriterDataLoaderAssetProvider} from given
	 * Spriter data, {@link AssetManager} and root asset folder. Build sprites
	 * from individual {@link Texture} instance. In order to benefit from
	 * {@link TextureAtlas} use other constructor.
	 * 
	 * @param data
	 *            Spriter data to provide assets to
	 * @param assetManager
	 *            AssetManager containing the assets
	 * @param root
	 *            Base folder where all assets may be found
	 */
	public SpriterDataLoaderAssetProvider(SpriterData data, AssetManager assetManager, String root) {
		this(data, assetManager, root, null);
	}

	/**
	 * Initializes a new {@code SpriterDataLoaderAssetProvider} from given
	 * Spriter data, {@link AssetManager}, root asset folder and
	 * {@link TextureAtlas}. If the TextureAtlas provided is null, will build
	 * sprites from individual {@link Texture} instances which end up less
	 * performant.
	 * 
	 * @param data
	 *            Spriter data to provide assets to
	 * @param assetManager
	 *            AssetManager containing the assets
	 * @param root
	 *            Base folder where all assets may be found
	 * @param textureAtlas
	 *            Path to the TextureAtlas used to build sprites, will build
	 *            them from individual textures if set to null
	 */
	public SpriterDataLoaderAssetProvider(SpriterData data, AssetManager assetManager, String root, String textureAtlas) {
		super();
		this.assetManager = assetManager;
		this.textureAtlas = textureAtlas;

		for (SpriterFolder folder : data.folders) {
			for (SpriterFile file : folder.files) {
				SpriterFileInfo info = new SpriterFileInfo();
				info.folderId = folder.id;
				info.fileId = file.id;
				String fileName = textureAtlas == null || file.type != SpriterFileType.Image ? root + file.name : file.name.substring(0, file.name.lastIndexOf('.'));
				fileNames.put(info, fileName);
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
		Sprite sprite = sprites.get(info);
		if (sprite == null) {

			String fileName = fileNames.get(info);

			if (textureAtlas == null) {
				sprite = new Sprite(assetManager.get(fileName, Texture.class));
			} else {
				sprite = new Sprite(assetManager.get(textureAtlas, TextureAtlas.class).findRegion(fileName));
			}

			sprites.put(info, sprite);
		}
		return sprite;
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
		Sound sound = sounds.get(info);
		if (sound == null) {
			sound = assetManager.get(fileNames.get(info), Sound.class);
			sounds.put(info, sound);
		}
		return sound;
	}

}
