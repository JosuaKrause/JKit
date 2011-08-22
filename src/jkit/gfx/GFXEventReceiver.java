/**
 * 
 */
package jkit.gfx;

import java.util.ArrayList;
import java.util.Iterator;
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

	protected final int width;

	protected final int height;

	public GFXEventReceiver(final int width, final int height) {
		events = new ArrayList<T>();
		this.width = width;
		this.height = height;
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
