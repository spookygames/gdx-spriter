//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.reader;

import java.io.IOException;
import java.io.Reader;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class SCONReader extends SpriterReader {

	@Override
	public String getExtension() {
		return "scon";
	}

	@Override
	public ReaderElement parse(Reader reader) throws IOException {
		return new JsonElement(new JsonReader().parse(reader));
	}

	private static class JsonElement implements ReaderElement {

		private final JsonValue json;

		public JsonElement(JsonValue json) {
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
		public Array<ReaderElement> getChildren() {
			Array<ReaderElement> elements = new Array<ReaderElement>();

			for (JsonValue jsonElement : json)
				elements.add(buildSubElement(jsonElement));

			return elements;
		}

		@Override
		public Array<ReaderElement> getChildrenByName(String name) {
			Array<ReaderElement> elements = new Array<ReaderElement>();

			JsonValue jsonCollection = json.get(name);
			if (jsonCollection != null)
				for (JsonValue jsonElement : json.get(name))
					elements.add(buildSubElement(jsonElement));

			return elements;
		}

		@Override
		public ReaderElement getChildByName(String name) {
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

		private JsonElement buildSubElement(JsonValue json) {
			return json == null ? null : new JsonElement(json);
		}
	}

}
