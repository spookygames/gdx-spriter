//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Array;

public class SpriterEntity extends SpriterVariableContainer {

	public transient SpriterData data;
	public Array<SpriterObjectInfo> objectInfos = new Array<SpriterObjectInfo>();
	public Array<SpriterCharacterMap> characterMaps = new Array<SpriterCharacterMap>();
	public Array<SpriterAnimation> animations = new Array<SpriterAnimation>();

	@Override
	public String toString() {
		return "SpriterEntity [objectInfos=" + objectInfos + ", characterMaps=" + characterMaps + ", animations=" + animations + ", variables=" + variables + ", id=" + id + ", name=" + name + "]";
	}

}