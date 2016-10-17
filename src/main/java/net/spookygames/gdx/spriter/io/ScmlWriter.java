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
