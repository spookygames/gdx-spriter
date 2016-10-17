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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

class SpriterDemoFileHandle extends FileHandle {

	final AssetManager manager;

	private String displayString;

	public SpriterDemoFileHandle(FileHandle handle, AssetManager manager) {
		super(handle.file());

		this.manager = manager;

		String[] parts = path().split("/");
		int count = parts.length;
		this.displayString = count > 1 ? parts[count - 2] + "/" + parts[count - 1] : parts[count - 1];
	}

	@Override
	public String toString() {
		return displayString;
	}
}
