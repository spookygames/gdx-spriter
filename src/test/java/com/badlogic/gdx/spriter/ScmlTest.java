// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.spriter.data.SpriterData;
import com.badlogic.gdx.spriter.io.ScmlReader;
import com.badlogic.gdx.spriter.io.ScmlWriter;

public class ScmlTest {

	@Test
	public void readScml() throws IOException {
		for (String scml : SpriterTestData.scml) {

			Assert.assertNotNull("SCML file missing", getClass().getResource(scml));

			Reader r = new InputStreamReader(getClass().getResourceAsStream(scml));

			ScmlReader reader = new ScmlReader();

			SpriterData stuff = reader.load(r);

			Assert.assertNotNull(stuff);
		}
	}

	@Test
	public void checkScmlReadContent() throws IOException {
		String scml = SpriterTestData.letterbotSCML;
		SpriterData data = SpriterTestData.letterbotSCMLData;

		Reader r = new InputStreamReader(getClass().getResourceAsStream(scml));
		ScmlReader reader = new ScmlReader();
		SpriterData scmlData = reader.load(r);

		String ref = data.toString();
		String actual = scmlData.toString();
		Assert.assertEquals(ref, actual);
	}

	@Test
	public void writeScml() throws IOException {
		for (String scml : SpriterTestData.scml) {
			Assert.assertNotNull("SCML file missing", getClass().getResource(scml));
			
			ScmlReader reader = new ScmlReader();
			ScmlWriter writer = new ScmlWriter();

			SpriterData reference = reader.load(new InputStreamReader(getClass().getResourceAsStream(scml)));

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			writer.write(reference, output);
			
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

			SpriterData data = reader.load(new InputStreamReader(input));

			Assert.assertEquals(reference.toString(), data.toString());
		}
	}

	@Test
	public void checkScmlWriteContent() throws IOException {
		String scml = SpriterTestData.letterbotSCML;
		SpriterData data = SpriterTestData.letterbotSCMLData;

		ScmlWriter writer = new ScmlWriter();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		writer.write(data, stream);

		String output = stream.toString("UTF-8");

		String reference = SpriterTestUtils.readResourceContent(getClass().getResourceAsStream(scml), "UTF-8");

		SpriterTestUtils.assertContentEquals(reference, output);
	}
}
