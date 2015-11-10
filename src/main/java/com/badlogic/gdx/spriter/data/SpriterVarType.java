//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public enum SpriterVarType {
	String, Int, Float;

	public static SpriterVarType parse(String text) {
		if (text != null)
			for (SpriterVarType t : values())
				if (text.equalsIgnoreCase(t.name()))
					return t;
		return null;
	}
}