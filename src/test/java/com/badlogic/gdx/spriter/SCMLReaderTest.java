// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.data.SpriterFile;
import com.badlogic.gdx.spriter.data.SpriterObject;
import com.badlogic.gdx.spriter.reader.SCMLReader;

public class SCMLReaderTest {

	@Test
	public void readSCML() throws IOException {
		for (String scml : SpriterTestData.scml) {

			Assert.assertNotNull("SCML file missing", getClass().getResource(scml));

			Reader r = new InputStreamReader(getClass().getResourceAsStream(scml));

			SCMLReader reader = new SCMLReader();
			
			SpriterData stuff = reader.load(r);
			
			Assert.assertNotNull(stuff);
		}
	}
	
    @Test
    public void cleanData() {
    	SpriterData data = SpriterTestData.data1;
    	
    	SCMLReader reader = new SCMLReader();

    	reader.cleanData(data);

    	SpriterObject object = data.entities.first().animations.first().timelines.first().keys.first().objectInfo;
    	SpriterFile file = data.folders.get(object.folderId).files.get(object.fileId);
    	Assert.assertEquals(file.pivotX, object.pivotX, 0.01d);
    	Assert.assertEquals(file.pivotY, object.pivotY, 0.01d);
    }
    
    @Test
    public void checkReadContent() throws IOException {
    	String scml = SpriterTestData.letterbotSCML;
    	SpriterData data = SpriterTestData.letterbotSCMLData;
    	
		Reader r = new InputStreamReader(getClass().getResourceAsStream(scml));
		SCMLReader reader = new SCMLReader();
		SpriterData scmlData = reader.load(r);

		ReflectionAssert.assertReflectionEquals(data, scmlData);
    }
}
