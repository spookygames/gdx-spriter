// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.demo;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class SpriterDemoUtils {

	static void debug(Actor table) {
		if (table instanceof Group) {
			for (Actor widget : ((Group) table).getChildren()) {
				debug(widget);
			}
			if (table instanceof Table) {
				((Table) table).debug();
			}
		}
	}

	static Array<FileHandle> findFiles(String[] acceptedExtensions) {
		Array<FileHandle> files = new Array<FileHandle>();
		try {
			URL resource = SpriterDemoUtils.class.getResource("/");
			if (resource != null) {
				FileHandle root = new FileHandle(new File(resource.toURI()));
				findFiles(files, root, acceptedExtensions);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return files;
	}

	static void findFiles(Array<FileHandle> files, FileHandle fileHandle,
			String[] acceptedExtensions) {
		if (fileHandle.isDirectory()) {
			for (FileHandle child : fileHandle.list())
				findFiles(files, child, acceptedExtensions);
		} else {
			String extension = fileHandle.extension();
			for (String acceptedExtension : acceptedExtensions) {
				if (acceptedExtension.equalsIgnoreCase(extension)) {
					files.add(fileHandle);
					break;
				}
			}
		}
	}

	static String removeStart(String str, String remove) {
		if (str == null || str.length() == 0 || remove == null
				|| remove.length() == 0) {
			return str;
		}
		if (str.startsWith(remove)) {
			return str.substring(remove.length());
		}
		return str;
	}

	static String getFileExtension(String name) {
		String extension = "";
		int i = name.lastIndexOf('.');
		if (i > 0) {
			extension = name.substring(i + 1);
		}
		return extension;
	}
}
