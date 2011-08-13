/**
 * 
 */
package jkit.svg;

import java.awt.Graphics2D;

import javax.xml.stream.XMLStreamWriter;

import jkit.svg.event.ReceiverEvent;
import jkit.svg.event.SVGEvent;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVG {

	private final SVGGraphics graphics;

	private final SVGEvent event;

	public SVG() {
		final ReceiverEvent event = new ReceiverEvent();
		graphics = new SVGGraphics(event);
		this.event = event;
	}

	public Graphics2D getGraphics() {
		if (graphics.isDisposed()) {
			throw new IllegalStateException("graphics alread disposed");
		}
		return graphics;
	}

	public void write(final XMLStreamWriter out) {
		if (!graphics.isDisposed()) {
			graphics.dispose();
		}
		event.write(out);
	}

}
