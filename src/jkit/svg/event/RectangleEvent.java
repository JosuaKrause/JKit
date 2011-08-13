/**
 * 
 */
package jkit.svg.event;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class RectangleEvent implements SVGEvent {

	private final int x;

	private final int y;

	private final int w;

	private final int h;

	public RectangleEvent(final int x, final int y, final int w, final int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public void write(final XMLStreamWriter out) {
		// TODO Auto-generated method stub

	}

}
