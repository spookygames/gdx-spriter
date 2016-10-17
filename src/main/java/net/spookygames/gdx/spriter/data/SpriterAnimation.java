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
	public float interval = 100; // Looks like it has no real use

	@Override
	public String toString() {
		return "SpriterAnimation [length=" + length + ", looping=" + looping + ", mainline=" + mainline + ", timelines="
				+ timelines + ", eventlines=" + eventlines + ", soundlines=" + soundlines + ", meta=" + meta
				+ ", interval=" + interval + ", id=" + id + ", name=" + name + "]";
	}

}