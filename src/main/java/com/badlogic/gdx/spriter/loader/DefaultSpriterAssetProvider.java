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
import com.badlogic.gdx.spriter.IntPairMap;
import com.badlogic.gdx.spriter.data.SpriterAssetProvider;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.utils.Disposable;

public class DefaultSpriterAssetProvider implements SpriterAssetProvider, Disposable {

	private final IntPairMap<Sprite> sprites = new IntPairMap<Sprite>();
	private final IntPairMap<Sound> sounds = new IntPairMap<Sound>();

	public DefaultSpriterAssetProvider(SpriterData data, String root) {
		super();

		for (SpriterFolder folder : data.folders) {
			for (SpriterFile file : folder.files) {
				FileHandle handle = Gdx.files.internal(root + file.name);
				switch (file.type) {
				case Image:
					sprites.put(folder.id, file.id, new Sprite(new Texture(handle)));
					break;
				case Sound:
					sounds.put(folder.id, file.id, Gdx.audio.newSound(handle));
					break;
				}
			}
		}

	}

	@Override
	public Sprite getSprite(int folderId, int fileId) {
		return sprites.get(folderId, fileId);
	}

	@Override
	public Sound getSound(int folderId, int fileId) {
		return sounds.get(folderId, fileId);
	}

	@Override
	public void dispose() {
		for (Sprite s : sprites.values())
			s.getTexture().dispose();
		for (Sound s : sounds.values())
			s.dispose();
	}
}
