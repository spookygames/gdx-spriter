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
