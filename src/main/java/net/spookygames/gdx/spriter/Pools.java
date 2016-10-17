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

package net.spookygames.gdx.spriter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

import net.spookygames.gdx.spriter.data.SpriterObject;
import net.spookygames.gdx.spriter.data.SpriterSound;
import net.spookygames.gdx.spriter.data.SpriterSpatial;
import net.spookygames.gdx.spriter.data.SpriterVarValue;

/**
 * The {@code Pools} class contains several {@link Pool} instances for various
 * objects used by {@link FrameData}.
 * 
 * @see FrameData
 * 
 * @author thorthur
 * 
 */
class Pools {

	static final Pool<SpriterObject> objects = new Pool<SpriterObject>() {
		@Override
		protected SpriterObject newObject() {
			return new SpriterObject();
		}
	};

	static final Pool<SpriterSpatial> spatials = new Pool<SpriterSpatial>() {
		@Override
		protected SpriterSpatial newObject() {
			return new SpriterSpatial();
		}
	};

	static final Pool<SpriterVarValue> varValues = new Pool<SpriterVarValue>() {
		@Override
		protected SpriterVarValue newObject() {
			return new SpriterVarValue();
		}
	};

	static final Pool<ObjectMap<String, SpriterVarValue>> varValuesMaps = new Pool<ObjectMap<String, SpriterVarValue>>() {
		@Override
		protected ObjectMap<String, SpriterVarValue> newObject() {
			return new ObjectMap<String, SpriterVarValue>();
		}
	};

	static final Pool<Array<String>> stringArrays = new Pool<Array<String>>() {
		@Override
		protected Array<String> newObject() {
			return new Array<String>();
		}
	};

	static final Pool<SpriterSound> sounds = new Pool<SpriterSound>() {
		@Override
		protected SpriterSound newObject() {
			return new SpriterSound();
		}
	};

}
