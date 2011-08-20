/**
 * 
 */
package jkit.gfx;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 * @param <T>
 *            The generic event receiver type.
 * 
 */
public abstract class AbstractGfx<T extends GFXEvent> {

	protected final BufferedImage img;

	protected final GFXGraphics<T> graphics;

	protected final GFXEventReceiver<T> event;

	public AbstractGfx(final int width, final int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = createGraphics(img.createGraphics(), width, height);
		event = graphics.getReceiver();
	}

	protected abstract GFXGraphics<T> createGraphics(Graphics2D gfx, int width,
			int height);

	public Graphics2D getGraphics() {
		if (graphics.isDisposed()) {
			throw new IllegalStateException("graphics alread disposed");
		}
		return graphics;
	}

	public Image getResultingImage() {
		return img.getScaledInstance(img.getWidth(), img.getHeight(),
				Image.SCALE_FAST);
	}

}