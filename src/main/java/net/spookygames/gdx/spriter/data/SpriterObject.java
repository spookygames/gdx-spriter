/**
 * Copyright (c) 2015-2016 Spooky Games
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgement in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package net.spookygames.gdx.spriter.data;

public class SpriterObject extends SpriterSpatial {

	public int animationId;
	public int entityId;
	public SpriterFileInfo file;
	public float pivotX = Float.NaN;
	public float pivotY = Float.NaN;
	public float t;

	public void fill(SpriterObject other) {
		super.fill(other);
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
		return "SpriterObject [animationId=" + animationId + ", entityId=" + entityId + ", file=" + file + ", pivotX="
				+ pivotX + ", pivotY=" + pivotY + ", t=" + t + ", x=" + x + ", y=" + y + ", angle=" + angle
				+ ", scaleX=" + scaleX + ", scaleY=" + scaleY + ", alpha=" + alpha + "]";
	}

}