/**
 * 
 */
package jkit.svg.event;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class OvalEvent implements SVGEvent {

	private final int x;

	private final int y;

	private final int w;

	private final int h;

	private final boolean fill;

	public OvalEvent(final int x, final int y, final int w, final int h,
			final boolean fill) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.fill = fill;
	}

	@Override
	public void write(final XMLStreamWriter out) {
		// TODO Auto-generated method stub

	}

}
