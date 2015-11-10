// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.spriter.IntPairMap;
import com.badlogic.gdx.spriter.data.SpriterAssetProvider;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFolder;

public class SpriterDataLoaderAssetProvider implements SpriterAssetProvider {

	private final IntPairMap<Sprite> sprites = new IntPairMap<Sprite>();
	private final IntPairMap<Sound> sounds = new IntPairMap<Sound>();
	private final IntPairMap<String> fileNames = new IntPairMap<String>();

	private final AssetManager assetManager;

	public SpriterDataLoaderAssetProvider(SpriterData data, AssetManager assetManager, String root) {
		super();
		this.assetManager = assetManager;

		for (SpriterFolder folder : data.folders)
			for (SpriterFile file : folder.files)
				fileNames.put(folder.id, file.id, root + file.name);
	}

	@Override
	public Sprite getSprite(int folderId, int fileId) {
		Sprite sprite = sprites.get(folderId, fileId);
		if (sprite == null) {
			sprite = new Sprite(assetManager.get(fileNames.get(folderId, fileId), Texture.class));
			sprites.put(folderId, fileId, sprite);
		}
		return sprite;
	}

	@Override
	public Sound getSound(int folderId, int fileId) {
		Sound sound = sounds.get(folderId, fileId);
		if (sound == null) {
			sound = assetManager.get(fileNames.get(folderId, fileId), Sound.class);
			sounds.put(folderId, fileId, sound);
		}
		return sound;
	}

}
