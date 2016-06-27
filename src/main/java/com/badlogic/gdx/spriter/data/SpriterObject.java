//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

public class SpriterObject extends SpriterSpatial {

	public int animationId;
	public int entityId;
	public SpriterFileInfo file;
	public float pivotX = Float.NaN;
	public float pivotY = Float.NaN;
	public float t;

	public void merge(SpriterObject other) {
		super.merge(other);
		this.animationId = other.animationId;
		this.entityId = other.entityId;
		this.file = other.file == null ? null : new SpriterFileInfo(other.file);
		this.pivotX = other.pivotX;
		this.pivotY = other.pivotY;
		this.t = other.t;
	}

	@Override
	public void reset() {
		super.reset();
		animationId = 0;
		entityId = 0;
		file = null;
		pivotX = Float.NaN;
		pivotY = Float.NaN;
		t = 0f;
	}

	@Override
	public String toString() {
		return "SpriterObject [animationId=" + animationId + ", entityId=" + entityId + ", file=" + file + ", pivotX=" + pivotX + ", pivotY=" + pivotY + ", t=" + t + ", x=" + x + ", y=" + y + ", angle=" + angle + ", scaleX=" + scaleX + ", scaleY=" + scaleY + ", alpha=" + alpha + "]";
	}

}