/**
 * 
 */
package jkit.svg.event;

import java.awt.geom.PathIterator;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class PathEvent extends SVGEvent {

	private final PathIterator path;

	private final boolean fill;

	public PathEvent(final PathIterator path, final boolean fill) {
		this.path = path;
		this.fill = fill;
	}

	@Override
	public void write(final XMLStreamWriter out) {
		// TODO Auto-generated method stub

	}

}
