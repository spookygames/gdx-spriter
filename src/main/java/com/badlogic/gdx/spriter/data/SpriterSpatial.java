//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterSpatial {

	public float x;
	public float y;
	public float angle;
	public float scaleX = 1;
	public float scaleY = 1;
	public float alpha = 1;

	@Override
	public String toString() {
		return "SpriterSpatial [x=" + x + ", y=" + y + ", angle=" + angle + ", scaleX=" + scaleX + ", scaleY=" + scaleY + ", alpha=" + alpha + "]";
	}

}