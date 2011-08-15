/**
 * 
 */
package jkit.svg.event;

import java.awt.Graphics2D;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public abstract class SVGEvent {

	protected Graphics2D gfx;

	public SVGEvent() {
		gfx = null;
	}

	public final void setGraphics(final Graphics2D gfx) {
		this.gfx = (Graphics2D) gfx.create();
	}

	public abstract void write(XMLStreamWriter out);

	public final void dispose() {
		gfx.dispose();
	}

}
