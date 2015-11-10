//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Array;

public class SpriterFolder extends SpriterElement {

	public Array<SpriterFile> files = new Array<SpriterFile>();

	@Override
	public String toString() {
		return "SpriterFolder [files=" + files + ", id=" + id + ", name=" + name + "]";
	}

}
