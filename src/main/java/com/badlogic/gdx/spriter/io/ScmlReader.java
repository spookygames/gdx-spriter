//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * The {@code ScmlReader} class reads Spriter data from scml files. Acceptable
 * input can be of type {@link String}, {@link InputStream}, {@link Reader} or
 * {@link FileHandle}.
 * 
 * Encoding is set to system default.
 * 
 * @see SpriterReader
 * @see SconReader
 * 
 * @author thorthur
 * 
 */
public class ScmlReader extends SpriterReader {

	/**
	 * Get the file extension this Spriter reader would default to: scml.
	 * 
	 * @return The "scml" file extension
	 */
	@Override
	public String getExtension() {
		return "scml";
	}

	@Override
	ReaderBean parse(Reader reader) throws IOException {
		return new XmlReaderBean(new XmlReader().parse(reader));
	}

	private static class XmlReaderBean implements ReaderBean {

		private final Element xml;

		public XmlReaderBean(Element xml) {
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
		public Array<ReaderBean> getChildren() {
			return getChildrenByName("i");
		}

		@Override
		public Array<ReaderBean> getChildrenByName(String name) {
			Array<ReaderBean> elements = new Array<ReaderBean>();
			for (Element xmlElement : xml.getChildrenByName(name))
				elements.add(buildSubElement(xmlElement));
			return elements;
		}

		@Override
		public ReaderBean getChildByName(String name) {
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

		private XmlReaderBean buildSubElement(Element xml) {
			return xml == null ? null : new XmlReaderBean(xml);
		}
	}
}
