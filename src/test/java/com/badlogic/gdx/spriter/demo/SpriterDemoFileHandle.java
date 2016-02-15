// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.demo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

class SpriterDemoFileHandle extends FileHandle {

	final AssetManager manager;
	
	private String displayString;

	public SpriterDemoFileHandle(FileHandle handle, AssetManager manager) {
		super(handle.file(), handle.type());
		
		this.manager = manager;

		String[] parts = path().split("/");
		int count = parts.length;
		this.displayString = count > 1 ?
				parts[count - 2] + "/" + parts[count - 1] :
				parts[count - 1];
	}

	@Override
	public String toString() {
		return displayString;
	}
}
