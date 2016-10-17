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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Test;

import net.spookygames.gdx.spriter.data.SpriterData;
import net.spookygames.gdx.spriter.io.SconReader;

public class SconTest {

	@Test
	public void readScon() throws IOException {
		for (String scon : SpriterTestData.scon) {

			Assert.assertNotNull("SCON file missing", getClass().getResource(scon));

			Reader r = new InputStreamReader(getClass().getResourceAsStream(scon));

			SconReader reader = new SconReader();

			SpriterData stuff = reader.load(r);

			Assert.assertNotNull(stuff);
		}
	}

	@Test
	public void checkSconReadContent() throws IOException {
		String scon = SpriterTestData.letterbotSCON;
		SpriterData data = SpriterTestData.letterbotSCONData;

		Reader r = new InputStreamReader(getClass().getResourceAsStream(scon));
		SconReader reader = new SconReader();
		SpriterData sconData = reader.load(r);

		String ref = data.toString();
		String actual = sconData.toString();
		Assert.assertEquals(ref, actual);
	}
}
