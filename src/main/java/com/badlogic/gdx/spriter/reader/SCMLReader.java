//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.reader;

import java.io.IOException;
import java.io.Reader;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class SCMLReader extends SpriterReader {

	@Override
	public String getExtension() {
		return "scml";
	}

	@Override
	public ReaderElement parse(Reader reader) throws IOException {
		return new XmlElement(new XmlReader().parse(reader));
	}

	private static class XmlElement implements ReaderElement {

		private final Element xml;

		public XmlElement(Element xml) {
			this.xml = xml;
		}

		@Override
		public String get(String name) {
			return xml.get(name);
		}

		@Override
		public String get(String name, String defaultValue) {
			return xml.get(name, defaultValue);
		}

		@Override
		public Array<ReaderElement> getChildren() {
			return getChildrenByName("i");
		}

		@Override
		public Array<ReaderElement> getChildrenByName(String name) {
			Array<ReaderElement> elements = new Array<ReaderElement>();
			for (Element xmlElement : xml.getChildrenByName(name))
				elements.add(buildSubElement(xmlElement));
			return elements;
		}

		@Override
		public ReaderElement getChildByName(String name) {
			return buildSubElement(xml.getChildByName(name));
		}

		@Override
		public int getInt(String name, int defaultValue) {
			return xml.getInt(name, defaultValue);
		}

		@Override
		public float getFloat(String name, float defaultValue) {
			return xml.getFloat(name, defaultValue);
		}

		@Override
		public boolean getBoolean(String name, boolean defaultValue) {
			return xml.getBoolean(name, defaultValue);
		}

		private XmlElement buildSubElement(Element xml) {
			return xml == null ? null : new XmlElement(xml);
		}
	}
}
