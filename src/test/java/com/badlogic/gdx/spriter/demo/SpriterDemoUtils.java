// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class SpriterDemoUtils {

	public static void debug(Actor table) {
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
		FileHandle root = Gdx.files.local(".");
		findFiles(files, root, acceptedExtensions);
		return files;
	}

	static void findFiles(Array<FileHandle> files, FileHandle fileHandle, String[] acceptedExtensions) {
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

	static String getFileExtension(String name) {
		String extension = "";
		int i = name.lastIndexOf('.');
		if (i > 0) {
			extension = name.substring(i + 1);
		}
		return extension;
	}
}
