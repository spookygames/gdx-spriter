//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterVarValue {

	public SpriterVarType type = SpriterVarType.String;
	public String stringValue = "";
	public float floatValue = Float.MIN_VALUE;
	public int intValue = Integer.MIN_VALUE;

	@Override
	public String toString() {
		return "SpriterVarValue [type=" + type + ", stringValue=" + stringValue + ", floatValue=" + floatValue + ", intValue=" + intValue + "]";
	}

}