/**
 * 
 */
package jkit.svg.event;

import java.awt.Image;

import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class ImageEvent extends SVGEvent {

	private final Image img;

	public ImageEvent(final Image img) {
		this.img = img;
	}

	@Override
	public void write(final XMLStreamWriter out) {
		// TODO Auto-generated method stub

	}

}
