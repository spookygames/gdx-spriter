//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Array;

public class SpriterMainlineKey extends SpriterKey {

	public Array<SpriterRef> boneRefs = new Array<SpriterRef>();
	public Array<SpriterObjectRef> objectRefs = new Array<SpriterObjectRef>();

	@Override
	public String toString() {
		return "SpriterMainlineKey [boneRefs=" + boneRefs + ", objectRefs=" + objectRefs + ", time=" + time + ", curveType=" + curveType + ", c1=" + c1 + ", c2=" + c2 + ", c3=" + c3 + ", c4=" + c4 + ", id=" + id + ", name=" + name + "]";
	}

}