//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public enum SpriterObjectType {
	Sprite, Bone, Box, Point, Sound, Entity, Variable;

	public static SpriterObjectType parse(String text) {
		if (text != null)
			for (SpriterObjectType t : values())
				if (text.equalsIgnoreCase(t.name()))
					return t;
		return null;
	}
}