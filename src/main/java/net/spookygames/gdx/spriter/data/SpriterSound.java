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

import com.badlogic.gdx.utils.Pool.Poolable;

public class SpriterSound extends SpriterElement implements Poolable {

	public SpriterFileInfo file;
	public boolean trigger = true;
	public float panning;
	public float volume = 1.0f;

	public void fill(SpriterSound sound) {
		id = sound.id;
		name = sound.name;
		file = sound.file;
		trigger = sound.trigger;
		panning = sound.panning;
		volume = sound.volume;
	}

	@Override
	public void reset() {
		id = 0;
		name = null;
		file = null;
		trigger = true;
		panning = 0f;
		volume = 1.0f;
	}

	@Override
	public String toString() {
		return "SpriterSound [file=" + file + ", trigger=" + trigger + ", panning=" + panning + ", volume=" + volume
				+ ", id=" + id + ", name=" + name + "]";
	}

}