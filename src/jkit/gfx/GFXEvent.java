/**
 * 
 */
package jkit.gfx;

import java.awt.Graphics2D;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public abstract class GFXEvent {

	protected Graphics2D gfx;

	public GFXEvent() {
		gfx = null;
	}

	public final void setGraphics(final Graphics2D gfx) {
		this.gfx = (Graphics2D) gfx.create();
	}

	public final void dispose() {
		gfx.dispose();
	}

}
