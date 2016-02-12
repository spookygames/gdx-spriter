// Copyright (c) 2015 The original author or authors
//
// This software may be modified and distributed under the terms
// of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter;

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
