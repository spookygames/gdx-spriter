//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Pool.Poolable;

public class SpriterSound extends SpriterElement implements Poolable {

	public SpriterFileInfo file;
	public boolean trigger = true;
	public float panning;
	public float volume = 1.0f;

	public void merge(SpriterSound sound) {
		file = sound.file;
		trigger = sound.trigger;
		panning = sound.panning;
		volume = sound.volume;
	}

	@Override
	public void reset() {
		file = null;
		trigger = true;
		panning = 0f;
		volume = 1.0f;
	}

	@Override
	public String toString() {
		return "SpriterSound [file=" + file + ", trigger=" + trigger + ", panning=" + panning + ", volume=" + volume + ", id=" + id + ", name=" + name + "]";
	}

}