// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.loader;

import java.io.IOException;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.spriter.reader.SpriterReader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SpriterDataLoader extends SynchronousAssetLoader<SpriterData, SpriterDataLoader.SpriterDataParameter> {

	private SpriterData data = null;

	public SpriterDataLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public SpriterData load(AssetManager am, String fileName, FileHandle file, SpriterDataParameter param) {

		data.assetProvider = new SpriterDataLoaderAssetProvider(data, am, defineRootPath(file, param));

		SpriterData result = this.data;
		this.data = null;
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SpriterDataParameter param) {
		Array<AssetDescriptor> deps = null;

		SpriterDataFormat format = defineFormat(file, param);
		String rootPath = defineRootPath(file, param);

		try {

			SpriterReader reader = format.getReader();
			data = reader.load(file);

			deps = new Array<AssetDescriptor>();

			for (SpriterFolder fo : data.folders) {
				for (SpriterFile fi : fo.files) {
					switch (fi.type) {
					case Image:
						deps.add(new AssetDescriptor<Texture>(rootPath + fi.name, Texture.class));
						break;
					case Sound:
						deps.add(new AssetDescriptor<Sound>(rootPath + fi.name, Sound.class));
						break;
					}
				}
			}
		} catch (IOException ex) {
			throw new GdxRuntimeException("An error happened when loading Spriter data", ex);
		}

		return deps;
	}

	private SpriterDataFormat defineFormat(FileHandle file, SpriterDataParameter param) {
		if (param != null && param.format != null) {
			return param.format;
		} else {
			return SpriterDataFormat.defineFormat(file);
		}
	}

	private String defineRootPath(FileHandle file, SpriterDataParameter param) {
		if (param != null && param.rootFolder != null) {
			return param.rootFolder + "/";
		} else {
			return file.parent().path() + "/";
		}
	}

	/**
	 * Parameter to be passed to
	 * {@link AssetManager#load(String, Class, AssetLoaderParameters)} if
	 * additional configuration is necessary for the {@link SpriterData}.
	 */
	public static class SpriterDataParameter extends AssetLoaderParameters<SpriterData> {
		/** Root folder to find related assets. */
		public String rootFolder;
		/** Format of Spriter file */
		public SpriterDataFormat format;
	}

}
