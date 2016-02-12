// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.spriter.io.ScmlReader;
import com.badlogic.gdx.spriter.io.SconReader;
import com.badlogic.gdx.spriter.io.SpriterReader;
import com.badlogic.gdx.utils.GdxRuntimeException;

public enum SpriterDataFormat {
	SCML {
		@Override
		SpriterReader getReader() {
			return new ScmlReader();
		}
	},
	SCON {
		@Override
		SpriterReader getReader() {
			return new SconReader();
		}
	};

	abstract SpriterReader getReader();

	static SpriterDataFormat defineFormat(FileHandle file) {
		String extension = file.extension();
		for (SpriterDataFormat format : values())
			if (format.name().equalsIgnoreCase(extension))
				return format;
		throw new GdxRuntimeException("Unable to define data format for Spriter file " + file.path());
	}
}
