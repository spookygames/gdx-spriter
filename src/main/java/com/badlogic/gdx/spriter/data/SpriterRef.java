//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterRef extends SpriterElement {

	public int parentId = -1;
	public int timelineId;
	public int keyId;

	@Override
	public String toString() {
		return "SpriterRef [parentId=" + parentId + ", timelineId=" + timelineId + ", keyId=" + keyId + "]";
	}

}