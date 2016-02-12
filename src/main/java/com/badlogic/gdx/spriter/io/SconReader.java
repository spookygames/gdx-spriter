//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.io;

import java.io.IOException;
import java.io.Reader;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class SconReader extends SpriterReader {

	@Override
	public String getExtension() {
		return "scon";
	}

	@Override
	public ReaderBean parse(Reader reader) throws IOException {
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
