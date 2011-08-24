/**
 * 
 */
package jkit.gfx.svg;

import java.awt.Graphics2D;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jkit.gfx.AbstractGfx;
import jkit.gfx.GFXGraphics;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVG extends AbstractGfx implements Closeable {

	protected SVGWriter out;

	public SVG(final XMLStreamWriter out, final int width, final int height) {
		super(width, height);
		this.out = new SVGWriter(out);
		this.out.writeStart(width, height);
	}

	@Override
	protected GFXGraphics createGraphics(final Graphics2D gfx) {
		return new SVGGraphics(gfx, out);
	}

	@Override
	public void close() throws IOException {
		if (out == null) {
			return;
		}
		if (graphics != null && !graphics.isDisposed()) {
			graphics.dispose();
		}
		out.writeEnd();
		if (out.hasException()) {
			throw new IOException(out.getException());
		}
		out.close();
		out = null;
	}

	private static XMLOutputFactory XML_FACTORY = null;

	public static final SVG createInstance(final OutputStream out,
			final int width, final int height) throws XMLStreamException {
		if (XML_FACTORY == null) {
			XML_FACTORY = XMLOutputFactory.newInstance();
		}
		return new SVG(XML_FACTORY.createXMLStreamWriter(out), width, height);
	}

}
