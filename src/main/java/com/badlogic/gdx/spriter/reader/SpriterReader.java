// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.spriter.data.SpriterAnimation;
import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterEntity;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterFolder;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.data.SpriterObjectInfo;
import com.badlogic.gdx.spriter.data.SpriterTimeline;
import com.badlogic.gdx.spriter.data.SpriterTimelineKey;
import com.badlogic.gdx.spriter.data.SpriterVarDef;
import com.badlogic.gdx.spriter.data.SpriterVarline;
import com.badlogic.gdx.spriter.data.SpriterVarlineKey;
import com.badlogic.gdx.utils.Array;

public abstract class SpriterReader {

	public SpriterData load(String xml) throws IOException {
		return load(new StringReader(xml));
	}

	public SpriterData load(FileHandle file) throws IOException {
		return load(file.reader());
	}

	public SpriterData load(InputStream input) throws IOException {
		return load(new InputStreamReader(input));
	}

	public abstract SpriterData load(Reader reader) throws IOException;

	public void initializeData(SpriterData data) {
		for (SpriterEntity entity : data.entities) {
			entity.data = data;
			for (SpriterAnimation a : entity.animations) {
				a.entity = entity;
				
				// Initialize objects
				for (SpriterTimeline t : a.timelines)
					for (SpriterTimelineKey k : t.keys)
						initializeObject(k.objectInfo, data.folders);
				
				// Initialize vardefs
		        if (a.meta != null) {
		        	
			        for (SpriterVarline v : a.meta.varlines)
			        	initializeVarline(v, entity.variables.get(v.def));
			        
			        for(SpriterTimeline timeline : a.timelines)
			        	if(timeline.meta != null)
			        		for(SpriterVarline v : timeline.meta.varlines)
			        			for(SpriterObjectInfo o : entity.objectInfos)
			        				if(timeline.name.equals(o.name))
			        					initializeVarline(v, o.variables.get(v.def));
			        
		        }
			}
		}
	}

	private void initializeObject(SpriterObject o, Array<SpriterFolder> folders) {
		if (o == null)
			return;
		if (Float.isNaN(o.pivotX) || Float.isNaN(o.pivotY)) {
			SpriterFile file = folders.get(o.folderId).files.get(o.fileId);
			o.pivotX = file.pivotX;
			o.pivotY = file.pivotY;
		}
	}
	
	private void initializeVarline(SpriterVarline varline, SpriterVarDef varDef) {
        varDef.variableValue = varDef.type.buildVarValue(varDef.defaultValue);
        for (SpriterVarlineKey key : varline.keys)
        	key.variableValue = varDef.type.buildVarValue(key.value);
    }

}
