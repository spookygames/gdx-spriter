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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;

public class SpriterTestUtils {

	public static String readResourceContent(InputStream resource, String encoding) {
		Reader r = null;
		String content = null;

		char[] buffer = new char[1024];
		StringBuilder out = new StringBuilder();
		try {
			Reader in = new InputStreamReader(resource, encoding);
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
			content = out.toString();
		} catch (IOException ex) {
		} finally {
			if (r != null)
				try {
					r.close();
				} catch (IOException e) {
				}
		}

		return content;

	}

	public static void assertContentEquals(String expected, String actual) {
		Assert.assertEquals(cleanContent(expected), cleanContent(actual));
	}

	private static String cleanContent(String content) {
		return content.replaceAll("\r", "").replaceAll("\t", "    ");
	}

}
