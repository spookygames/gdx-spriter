//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterMapInstruction {

	public SpriterFileInfo file;
	public SpriterFileInfo target;

	@Override
	public String toString() {
		return "SpriterMapInstruction [file=" + file + ", target=" + target + "]";
	}

}