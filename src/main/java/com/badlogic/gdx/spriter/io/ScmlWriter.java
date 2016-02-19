//Copyright (c) 2015 The original author or authors
//
//This software may be modified and distributed under the terms
//of the zlib license.  See the LICENSE file for details.

package com.badlogic.gdx.spriter.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlWriter;

/**
 * The {@code ScmlWriter} class writes Spriter data in scml format to output of
 * type {@link OutputStream}, {@link Writer} or {@link FileHandle}.
 * 
 * Encoding is set to UTF-8.
 * 
 * @see SpriterWriter
 * 
 * @author thorthur
 * 
 */
public class ScmlWriter extends SpriterWriter {

	/**
	 * Get the file extension this Spriter writer would default to: scml.
	 * 
	 * @return The "scml" file extension
	 */
	@Override
	public String getExtension() {
		return "scml";
	}

	@Override
	WriterBean wrap(Writer writer) throws IOException {
		return new XmlWriterElement(new XmlWriter(writer));
	}

	private static class XmlWriterElement implements WriterBean {

		private final XmlWriter xml;

		public XmlWriterElement(XmlWriter xml) throws IOException {
			this.xml = xml;

			// XML header
			this.xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		}

		@Override
		public void element(String name) throws IOException {
			xml.element(name);
		}

		@Override
		public void array(String name) throws IOException {
			xml.element(name);
		}

		@Override
		public void subElement() throws IOException {
			xml.element("i");
		}

		@Override
		public void attribute(String name, Object value) throws IOException {
			xml.attribute(name, value);
		}

		@Override
		public void pop() throws IOException {
			xml.pop();
		}

		@Override
		public void close() throws IOException {
			xml.close();
		}
	}
}
