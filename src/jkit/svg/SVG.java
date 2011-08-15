/**
 * 
 */
package jkit.svg;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.xml.stream.XMLStreamWriter;

import jkit.svg.event.ReceiverEvent;
import jkit.svg.event.SVGEvent;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVG {

	private final BufferedImage img;

	private final SVGGraphics graphics;

	private final SVGEvent event;

	public SVG(final int width, final int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final ReceiverEvent event = new ReceiverEvent();
		graphics = new SVGGraphics(event, img.createGraphics());
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

	public Image getResultingImage() {
		return img.getScaledInstance(img.getWidth(), img.getHeight(),
				Image.SCALE_FAST);
	}

}
