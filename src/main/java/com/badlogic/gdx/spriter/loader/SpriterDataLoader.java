// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.spriter.io.SpriterReader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * The {@code SpriterDataLoader} class is a {@link SynchronousAssetLoader} to
 * load {@link SpriterData} instances.
 * 
 * Passing a {@link SpriterDataParameter} to
 * {@link AssetManager#load(String, Class, AssetLoaderParameters)} allows to
 * specify data format and root folder.
 * 
 * @author thorthur
 * 
 */
public class SpriterDataLoader extends SynchronousAssetLoader<SpriterData, SpriterDataLoader.SpriterDataParameter> {

	private SpriterData data = null;

	/**
	 * Initializes a new instance of {@code SpriterDataLoader} with given
	 * {@link FileHandleResolver}.
	 * 
	 * @param resolver
	 *            Resolver to use to resolve the file associated with the asset
	 *            name.
	 */
	public SpriterDataLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.badlogic.gdx.assets.loaders.SynchronousAssetLoader#load(com.badlogic
	 * .gdx.assets.AssetManager, java.lang.String,
	 * com.badlogic.gdx.files.FileHandle,
	 * com.badlogic.gdx.assets.AssetLoaderParameters)
	 */
	@Override
	public SpriterData load(AssetManager am, String fileName, FileHandle file, SpriterDataParameter param) {

		String rootPath = defineRootPath(file, param);
		data.assetProvider = new SpriterDataLoaderAssetProvider(data, am, rootPath, defineTextureAtlas(file, rootPath, param));

		SpriterData result = this.data;
		this.data = null;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.badlogic.gdx.assets.loaders.AssetLoader#getDependencies(java.lang
	 * .String, com.badlogic.gdx.files.FileHandle,
	 * com.badlogic.gdx.assets.AssetLoaderParameters)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SpriterDataParameter param) {
		Array<AssetDescriptor> deps = null;

		SpriterDataFormat format = defineFormat(file, param);
		String rootPath = defineRootPath(file, param);
		String atlas = defineTextureAtlas(file, rootPath, param);

		try {

			SpriterReader reader = format.getReader();
			data = reader.load(file);

			deps = new Array<AssetDescriptor>();

			// If atlas, load as TextureAtlas
			if (atlas != null) {
				deps.add(new AssetDescriptor<TextureAtlas>(atlas.startsWith(rootPath) ? atlas : (rootPath + atlas), TextureAtlas.class));
			}

			for (SpriterFolder fo : data.folders) {
				for (SpriterFile fi : fo.files) {
					switch (fi.type) {
					case Image:
						if (atlas == null) { // If no atlas, load as Textures
							deps.add(new AssetDescriptor<Texture>(rootPath + fi.name, Texture.class));
						}
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

	private String defineRootPath(FileHandle file, SpriterDataParameter param) {
		if (param != null && param.rootFolder != null) {
			return param.rootFolder + "/";
		} else {
			return file.parent().path() + "/";
		}
	}

	private SpriterDataFormat defineFormat(FileHandle file, SpriterDataParameter param) {
		if (param != null && param.format != null) {
			return param.format;
		} else {
			return SpriterDataFormat.defineFormat(file);
		}
	}

	private String defineTextureAtlas(FileHandle file, String rootFolder, SpriterDataParameter param) {
		if (param != null && param.textureAtlas != null) {
			return param.textureAtlas;
		} else {

			FileHandle[] possibleAtlasFiles = resolve(rootFolder).list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					int dotIndex = name.lastIndexOf('.');
					if (dotIndex != -1) {
						String extension = name.substring(dotIndex + 1);
						return extension.equalsIgnoreCase("atlas") || extension.equalsIgnoreCase("pack");
					} else {
						return false;
					}
				}
			});

			if (possibleAtlasFiles.length == 0) {
				// No atlas file here
				return null;
			} else {
				return possibleAtlasFiles[0].path();
			}
		}
	}

	/**
	 * Parameter to be passed to
	 * {@link AssetManager#load(String, Class, AssetLoaderParameters)} if
	 * additional configuration is necessary for the {@link SpriterData}.
	 */
	public static class SpriterDataParameter extends AssetLoaderParameters<SpriterData> {
		/**
		 * Optional root folder to find related assets. Defaults to Spriter
		 * file's parent folder.
		 */
		public String rootFolder = null;

		/**
		 * Optional format of Spriter file. Defaults to format identification
		 * based on Spriter file's extension.
		 */
		public SpriterDataFormat format = null;

		/**
		 * Optional texture atlas file. If a relative path is provided, it will
		 * be relative to rootFolder. Defaults to searching first file with
		 * .atlas extension in rootFolder and loading every texture individually
		 * (ie not using atlas) if none is found.
		 */
		public String textureAtlas = null;
	}

}
