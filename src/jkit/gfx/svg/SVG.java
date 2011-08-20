/**
 * 
 */
package jkit.gfx.svg;

import java.awt.Graphics2D;
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
public class SVG extends AbstractGfx<SVGEvent> {

	public SVG(final int width, final int height) {
		super(width, height);
	}

	@Override
	protected GFXGraphics<SVGEvent> createGraphics(final Graphics2D gfx,
			final int width, final int height) {
		return new SVGGraphics(gfx, width, height);
	}

	public void write(final XMLStreamWriter out) throws XMLStreamException {
		if (!graphics.isDisposed()) {
			graphics.dispose();
		}
		event.getEvent().write(out);
	}

	private static final XMLOutputFactory FACTORY = XMLOutputFactory
			.newInstance();

	public static final String UTF8 = "UTF-8";

	public void write(final OutputStream out) throws XMLStreamException {
		final XMLStreamWriter w = FACTORY.createXMLStreamWriter(out, UTF8);
		w.writeStartDocument(UTF8, "1.0");
		write(w);
		w.writeEndDocument();
		w.close();
	}

}
