//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterObjectInfo extends SpriterVariableContainer {

	public SpriterObjectType objectType = SpriterObjectType.Sprite;
	public float width;
	public float height;
	public float pivotX;
	public float pivotY;

	@Override
	public String toString() {
		return "SpriterObjectInfo [objectType=" + objectType + ", width=" + width + ", height=" + height + ", pivotX=" + pivotX + ", pivotY=" + pivotY + ", variables=" + variables + ", id=" + id + ", name=" + name + "]";
	}

}