//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Pool.Poolable;

public class SpriterSpatial implements Poolable {

	public float x;
	public float y;
	public float angle;
	public float scaleX = 1;
	public float scaleY = 1;
	public float alpha = 1;

	public void fill(SpriterSpatial other) {
		this.x = other.x;
		this.y = other.y;
		this.angle = other.angle;
		this.scaleX = other.scaleX;
		this.scaleY = other.scaleY;
		this.alpha = other.alpha;
	}

	@Override
	public void reset() {
		x = 0f;
		y = 0f;
		angle = 0f;
		scaleX = 1f;
		scaleY = 1f;
		alpha = 1f;
	}

	@Override
	public String toString() {
		return "SpriterSpatial [x=" + x + ", y=" + y + ", angle=" + angle + ", scaleX=" + scaleX + ", scaleY=" + scaleY + ", alpha=" + alpha + "]";
	}

}