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

package net.spookygames.gdx.spriter.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * The {@code SconReader} class lets you read Spriter data from scon files.
 * Acceptable input can be of type {@link String}, {@link InputStream},
 * {@link Reader} or {@link FileHandle}.
 * 
 * Encoding is set to system default.
 * 
 * @see SpriterReader
 * @see ScmlReader
 * 
 * @author thorthur
 * 
 */
public class SconReader extends SpriterReader {

	/**
	 * Get the file extension this Spriter reader would default to: scon.
	 * 
	 * @return The "scon" file extension
	 */
	@Override
	public String getExtension() {
		return "scon";
	}

	@Override
	ReaderBean parse(Reader reader) throws IOException {
		return new JsonReaderBean(new JsonReader().parse(reader));
	}

	private static class JsonReaderBean implements ReaderBean {

		private final JsonValue json;

		public JsonReaderBean(JsonValue json) {
			this.json = json;
		}

		@Override
		public String get(String name) {
			return json.getString(name);
		}

		@Override
		public String get(String name, String defaultValue) {
			return json.getString(name, defaultValue);
		}

		@Override
		public Array<ReaderBean> getChildren() {
			Array<ReaderBean> elements = new Array<ReaderBean>();

			for (JsonValue jsonElement : json)
				elements.add(buildSubElement(jsonElement));

			return elements;
		}

		@Override
		public Array<ReaderBean> getChildrenByName(String name) {
			Array<ReaderBean> elements = new Array<ReaderBean>();

			JsonValue jsonCollection = json.get(name);
			if (jsonCollection != null)
				for (JsonValue jsonElement : json.get(name))
					elements.add(buildSubElement(jsonElement));

			return elements;
		}

		@Override
		public ReaderBean getChildByName(String name) {
			return buildSubElement(json.get(name));
		}

		@Override
		public int getInt(String name, int defaultValue) {
			return json.getInt(name, defaultValue);
		}

		@Override
		public float getFloat(String name, float defaultValue) {
			return json.getFloat(name, defaultValue);
		}

		@Override
		public boolean getBoolean(String name, boolean defaultValue) {
			return json.getBoolean(name, defaultValue);
		}

		private JsonReaderBean buildSubElement(JsonValue json) {
			return json == null ? null : new JsonReaderBean(json);
		}

	}
}
