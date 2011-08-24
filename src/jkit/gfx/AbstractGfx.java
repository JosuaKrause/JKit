/**
 * 
 */
package jkit.gfx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 * 
 */
public abstract class AbstractGfx {

	protected final BufferedImage img;

	protected GFXGraphics graphics;

	public AbstractGfx(final int width, final int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = null;
	}

	protected abstract GFXGraphics createGraphics(Graphics2D gfx);

	public Graphics2D getGraphics() {
		if (graphics == null) {
			graphics = createGraphics(img.createGraphics());
		}
		if (graphics.isDisposed()) {
			throw new IllegalStateException("graphics already disposed");
		}
		return graphics;
	}

	public BufferedImage getResultingImage() {
		final BufferedImage res = new BufferedImage(img.getWidth(), img
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics g = res.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return res;
	}

}