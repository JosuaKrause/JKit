/**
 * 
 */
package jkit.svg.event;

import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import jkit.svg.SVGEventReceiver;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class ReceiverEvent implements SVGEvent, SVGEventReceiver {

	private final List<SVGEvent> events;

	public ReceiverEvent() {
		events = new LinkedList<SVGEvent>();
	}

	@Override
	public void addEvent(final SVGEvent event) {
		events.add(event);
	}

	@Override
	public void write(final XMLStreamWriter out) {
		for (final SVGEvent event : events) {
			event.write(out);
		}
	}

}
