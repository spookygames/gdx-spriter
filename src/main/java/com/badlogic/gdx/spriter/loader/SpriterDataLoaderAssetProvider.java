// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.spriter.data.SpriterAssetProvider;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFileInfo;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.utils.ObjectMap;

public class SpriterDataLoaderAssetProvider implements SpriterAssetProvider {

	private final ObjectMap<SpriterFileInfo, Sprite> sprites = new ObjectMap<SpriterFileInfo, Sprite>();
	private final ObjectMap<SpriterFileInfo, Sound> sounds = new ObjectMap<SpriterFileInfo, Sound>();
	private final ObjectMap<SpriterFileInfo, String> fileNames = new ObjectMap<SpriterFileInfo, String>();

	private final AssetManager assetManager;

	public SpriterDataLoaderAssetProvider(SpriterData data, AssetManager assetManager, String root) {
		super();
		this.assetManager = assetManager;

		for (SpriterFolder folder : data.folders) {
			for (SpriterFile file : folder.files) {
				SpriterFileInfo info = new SpriterFileInfo();
				info.folderId = folder.id;
				info.fileId = file.id;
				fileNames.put(info, root + file.name);
			}
		}
	}

	@Override
	public Sprite getSprite(SpriterFileInfo info) {
		Sprite sprite = sprites.get(info);
		if (sprite == null) {
			sprite = new Sprite(assetManager.get(fileNames.get(info), Texture.class));
			sprites.put(info, sprite);
		}
		return sprite;
	}

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
