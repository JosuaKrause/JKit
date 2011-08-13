/**
 * 
 */
package jkit.svg.event;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class LineEvent implements SVGEvent {

	private final int x1;

	private final int y1;

	private final int x2;

	private final int y2;

	public LineEvent(final int x1, final int y1, final int x2, final int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public void write(final XMLStreamWriter out) {
		// TODO Auto-generated method stub

	}

}
