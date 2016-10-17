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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.spookygames.gdx.spriter.io.ScmlReader;
import net.spookygames.gdx.spriter.io.SconReader;
import net.spookygames.gdx.spriter.io.SpriterReader;

/**
 * The {@code SpriterDataFormat} enum defines available format for Spriter
 * files.
 * 
 * @see net.spookygames.gdx.spriter.io.ScmlReader
 * @see net.spookygames.gdx.spriter.io.ScmlWriter
 * @see net.spookygames.gdx.spriter.io.SconReader
 * 
 * @author thorthur
 * 
 */
public enum SpriterDataFormat {
	/**
	 * The scml format is derived from XML.
	 */
	SCML {
		@Override
		SpriterReader getReader() {
			return new ScmlReader();
		}
	},
	/**
	 * The scon format is derived from json.
	 */
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
