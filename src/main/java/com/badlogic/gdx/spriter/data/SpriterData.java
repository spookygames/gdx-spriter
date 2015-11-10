//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.data;

import com.badlogic.gdx.utils.Array;

public class SpriterData {

	public String scmlVersion;
	public String generator;
	public String generatorVersion;

	public Array<SpriterFolder> folders = new Array<SpriterFolder>();
	public Array<SpriterEntity> entities = new Array<SpriterEntity>();
	public Array<SpriterElement> tags = new Array<SpriterElement>();

	public transient SpriterAssetProvider assetProvider;

	@Override
	public String toString() {
		return "SpriterData [scmlVersion=" + scmlVersion + ", generator=" + generator + ", generatorVersion=" + generatorVersion + ", folders=" + folders + ", entities=" + entities + ", tags=" + tags + "]";
	}

}
