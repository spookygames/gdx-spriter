//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterKey extends SpriterElement {

	public float time = 0;
	public SpriterCurveType curveType = SpriterCurveType.Linear;
	public float c1;
	public float c2;
	public float c3;
	public float c4;

	@Override
	public String toString() {
		return "SpriterKey [time=" + time + ", curveType=" + curveType + ", c1=" + c1 + ", c2=" + c2 + ", c3=" + c3 + ", c4=" + c4 + "]";
	}
}