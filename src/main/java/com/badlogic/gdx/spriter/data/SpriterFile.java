//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterFile extends SpriterElement {

	public SpriterFileType type = SpriterFileType.Image;
	public float pivotX = 0f;
	public float pivotY = 1f;
	public int width;
	public int height;

	@Override
	public String toString() {
		return "SpriterFile [type=" + type + ", pivotX=" + pivotX + ", pivotY=" + pivotY + ", width=" + width + ", height=" + height + ", id=" + id + ", name=" + name + "]";
	}

}