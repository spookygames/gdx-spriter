//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public enum SpriterFileType {
	Image, Sound;

	public static SpriterFileType parse(String text) {
		if (text != null)
			for (SpriterFileType t : values())
				if (text.equalsIgnoreCase(t.name()))
					return t;
		return null;
	}
}