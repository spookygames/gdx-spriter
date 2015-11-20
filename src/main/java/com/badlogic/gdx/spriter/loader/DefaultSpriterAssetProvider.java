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

public class DefaultSpriterAssetProvider implements SpriterAssetProvider, Disposable {

	private final ObjectMap<SpriterFileInfo, Sprite> sprites = new ObjectMap<SpriterFileInfo, Sprite>();
	private final ObjectMap<SpriterFileInfo, Sound> sounds = new ObjectMap<SpriterFileInfo, Sound>();

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

	@Override
	public Sprite getSprite(SpriterFileInfo info) {
		return sprites.get(info);
	}

	@Override
	public Sound getSound(SpriterFileInfo info) {
		return sounds.get(info);
	}

	@Override
	public void dispose() {
		for (Sprite s : sprites.values())
			s.getTexture().dispose();
		for (Sound s : sounds.values())
			s.dispose();
	}
}
