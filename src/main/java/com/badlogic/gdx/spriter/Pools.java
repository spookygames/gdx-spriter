// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterSound;
import com.badlogic.gdx.spriter.data.SpriterSpatial;
import com.badlogic.gdx.spriter.data.SpriterVarValue;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

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
