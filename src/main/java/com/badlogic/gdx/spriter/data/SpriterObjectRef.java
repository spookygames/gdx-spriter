//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterObjectRef extends SpriterRef implements Comparable<SpriterObjectRef> {

	public int zIndex;

	@Override
	public int compareTo(SpriterObjectRef o) {
		return zIndex - o.zIndex;
	}

	@Override
	public String toString() {
		return "SpriterObjectRef [zIndex=" + zIndex + ", parentId=" + parentId + ", timelineId=" + timelineId + ", keyId=" + keyId + ", id=" + id + ", name=" + name + "]";
	}
}