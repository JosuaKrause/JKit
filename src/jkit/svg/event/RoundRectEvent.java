/**
 * 
 */
package jkit.svg.event;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class RoundRectEvent implements SVGEvent {

	private final int x;

	private final int y;

	private final int w;

	private final int h;

	private final int aw;

	private final int ah;

	private final boolean fill;

	public RoundRectEvent(final int x, final int y, final int w, final int h,
			final int aw, final int ah, final boolean fill) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.aw = aw;
		this.ah = ah;
		this.fill = fill;
	}

	@Override
	public void write(final XMLStreamWriter out) {
		// TODO Auto-generated method stub

	}

}
