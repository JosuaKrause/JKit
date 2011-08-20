/**
 * 
 */
package jkit.gfx.svg;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jkit.gfx.GFXEvent;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public abstract class SVGEvent extends GFXEvent {

	public abstract void write(XMLStreamWriter out) throws XMLStreamException;

}
