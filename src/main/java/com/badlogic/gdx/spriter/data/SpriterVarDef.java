//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterVarDef extends SpriterElement {

	public SpriterVarType type = SpriterVarType.String;
	public String defaultValue;
	public SpriterVarValue variableValue = new SpriterVarValue();

	@Override
	public String toString() {
		return "SpriterVarDef [type=" + type + ", defaultValue=" + defaultValue + ", variableValue=" + variableValue + ", id=" + id + ", name=" + name + "]";
	}

}