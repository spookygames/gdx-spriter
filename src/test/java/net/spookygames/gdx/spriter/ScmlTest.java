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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Test;

import net.spookygames.gdx.spriter.data.SpriterData;
import net.spookygames.gdx.spriter.io.ScmlReader;
import net.spookygames.gdx.spriter.io.ScmlWriter;

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
