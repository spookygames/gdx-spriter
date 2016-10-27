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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.spookygames.gdx.spriter.data.SpriterData;
import net.spookygames.gdx.spriter.data.SpriterFile;
import net.spookygames.gdx.spriter.data.SpriterFolder;
import net.spookygames.gdx.spriter.io.SpriterReader;

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

	private String rootPath = null;
	private String atlas = null;
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

		String rootPath = this.rootPath;
		String atlas = this.atlas;
		SpriterData result = this.data;

		result.assetProvider = new SpriterDataLoaderAssetProvider(data, am, rootPath, atlas);

		this.rootPath = null;
		this.atlas = null;
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
		rootPath = defineRootPath(file, param);
		atlas = defineTextureAtlas(file, rootPath, param);

		try {

			SpriterReader reader = format.getReader();
			data = reader.load(file);

			deps = new Array<AssetDescriptor>();

			// If atlas, load as TextureAtlas
			if (atlas != null) {
				deps.add(new AssetDescriptor<TextureAtlas>(rootPath + atlas, TextureAtlas.class));
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
		String root;
		if (param != null && param.rootFolder != null) {
			root = param.rootFolder;
		} else {
			root = file.parent().path();
		}
		if (!root.endsWith("/"))
			root += "/";
		return root;
	}

	private SpriterDataFormat defineFormat(FileHandle file, SpriterDataParameter param) {
		SpriterDataFormat format;
		if (param != null && param.format != null) {
			format = param.format;
		} else {
			format = SpriterDataFormat.defineFormat(file);
		}
		return format;
	}

	private String defineTextureAtlas(FileHandle file, String rootFolder, SpriterDataParameter param) {
		String atlas = null;
		if (param != null && param.textureAtlas != null) {
			atlas = param.textureAtlas;
		} else {
			String baseName = file.nameWithoutExtension();
			String[] possibleAtlasNames = { baseName + ".atlas", baseName + ".pack" };
			for (int i = 0, n = possibleAtlasNames.length; i < n; i++) {
				FileHandle possibleAtlasFile = resolve(rootFolder + possibleAtlasNames[i]);
				if (possibleAtlasFile.exists()) { // Atlas file found!
					atlas = possibleAtlasFile.path();
					break;
				}
			}
		}
		if (atlas != null && atlas.startsWith(rootFolder))
			atlas = atlas.substring(rootFolder.length());
		return atlas;
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
		 * Optional texture atlas file. May or may not start with rootFolder.
		 * Defaults to searching first file with same name as Spriter file with
		 * .pack or .atlas extension in rootFolder and loading every texture
		 * individually (ie not using atlas) if none is found.
		 */
		public String textureAtlas = null;
	}

}
