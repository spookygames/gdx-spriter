//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Array;

public class SpriterTaglineKey extends SpriterKey {

	public Array<SpriterTag> tags = new Array<SpriterTag>();

	@Override
	public String toString() {
		return "SpriterTaglineKey [tags=" + tags + ", time=" + time + ", curveType=" + curveType + ", c1=" + c1 + ", c2=" + c2 + ", c3=" + c3 + ", c4=" + c4 + ", id=" + id + ", name=" + name + "]";
	}

}