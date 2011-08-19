/**
 * 
 */
package jkit.gfx;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 * @param <T>
 *            The event type.
 * 
 */
public abstract class GFXEventReceiver<T extends GFXEvent> implements
		Iterable<T> {

	private final List<T> events;

	public GFXEventReceiver() {
		events = new LinkedList<T>();
	}

	public final void addEvent(final T event) {
		events.add(event);
	}

	@Override
	public final Iterator<T> iterator() {
		return events.iterator();
	}

	public abstract T getEvent();

}
