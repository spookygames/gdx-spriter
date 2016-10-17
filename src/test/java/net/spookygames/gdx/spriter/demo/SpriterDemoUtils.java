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

package net.spookygames.gdx.spriter.demo;

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
		FileHandle root = Gdx.files.internal(".");
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
