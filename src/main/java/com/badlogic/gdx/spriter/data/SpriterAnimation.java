//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Array;

public class SpriterAnimation extends SpriterElement {

	public transient SpriterEntity entity;
	public float length;
	public boolean looping = true;
	public SpriterMainline mainline;
	public Array<SpriterTimeline> timelines = new Array<SpriterTimeline>();
	public Array<SpriterEventline> eventlines = new Array<SpriterEventline>();
	public Array<SpriterSoundline> soundlines = new Array<SpriterSoundline>();
	public SpriterMeta meta;
	public float interval = 100;	// Looks like it has no real use

	@Override
	public String toString() {
		return "SpriterAnimation [length=" + length + ", looping=" + looping + ", mainline=" + mainline + ", timelines=" + timelines + ", eventlines=" + eventlines + ", soundlines=" + soundlines + ", meta=" + meta + ", interval=" + interval + ", id=" + id + ", name=" + name + "]";
	}

}