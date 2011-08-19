/**
 * 
 */
package jkit.gfx.svg;

import java.awt.Graphics2D;

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
	protected GFXGraphics<SVGEvent> createGraphics(final Graphics2D gfx) {
		return new SVGGraphics(gfx);
	}

	public void write(final XMLStreamWriter out) {
		if (!graphics.isDisposed()) {
			graphics.dispose();
		}
		event.getEvent().write(out);
	}

}
