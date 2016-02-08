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

import com.badlogic.gdx.spriter.data.SpriterData;
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
	public void checkReadContent() throws IOException {
		String scml = SpriterTestData.letterbotSCML;
		SpriterData data = SpriterTestData.letterbotSCMLData;

		Reader r = new InputStreamReader(getClass().getResourceAsStream(scml));
		SCMLReader reader = new SCMLReader();
		SpriterData scmlData = reader.load(r);

		String ref = data.toString();
		String actual = scmlData.toString();
		Assert.assertEquals(ref, actual);
	}
}
