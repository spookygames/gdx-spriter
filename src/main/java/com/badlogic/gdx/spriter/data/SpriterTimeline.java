//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Array;

public class SpriterTimeline extends SpriterElement {

	public SpriterObjectType objectType = SpriterObjectType.Sprite;
	public int objectId;
	public Array<SpriterTimelineKey> keys = new Array<SpriterTimelineKey>();
	public SpriterMeta meta;

	@Override
	public String toString() {
		return "SpriterTimeline [objectType=" + objectType + ", objectId=" + objectId + ", keys=" + keys + ", meta=" + meta + ", id=" + id + ", name=" + name + "]";
	}

}