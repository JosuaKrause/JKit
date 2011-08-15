/**
 * 
 */
package jkit.svg;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import jkit.svg.event.ArcEvent;
import jkit.svg.event.ImageEvent;
import jkit.svg.event.LineEvent;
import jkit.svg.event.OvalEvent;
import jkit.svg.event.PathEvent;
import jkit.svg.event.ReceiverEvent;
import jkit.svg.event.RectangleEvent;
import jkit.svg.event.RoundRectEvent;
import jkit.svg.event.SVGEvent;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVGGraphics extends Graphics2D {

	public static boolean ERROR_ON_IGNORE = false;

	private final Graphics2D gfx;

	private SVGEventReceiver receiver;

	public SVGGraphics(final SVGEventReceiver receiver, final Graphics2D gfx) {
		this.receiver = receiver;
		this.gfx = gfx;
	}

	private SVGGraphics(final SVGGraphics copy, final SVGEventReceiver receiver) {
		this.receiver = receiver;
		gfx = (Graphics2D) copy.gfx.create();
	}

	private void e(final SVGEvent event) {
		e(event, gfx);
	}

	private void e(final SVGEvent event, final Graphics2D gfx) {
		event.setGraphics(gfx);
		receiver.addEvent(event);
	}

	// clipping

	@Override
	public void clip(final Shape s) {
		ensureReady();
		mayBeIgnored();
		gfx.clip(s);
	}

	@Override
	public void clipRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		mayBeIgnored();
		gfx.clipRect(x, y, width, height);
	}

	@Override
	public Shape getClip() {
		ensureReady();
		return gfx.getClip();
	}

	@Override
	public Rectangle getClipBounds() {
		ensureReady();
		return gfx.getClipBounds();
	}

	@Override
	public boolean hit(final Rectangle rect, final Shape s,
			final boolean onStroke) {
		ensureReady();
		return gfx.hit(rect, s, onStroke);
	}

	@Override
	public void setClip(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		mayBeIgnored();
		gfx.setClip(x, y, width, height);
	}

	@Override
	public void setClip(final Shape clip) {
		ensureReady();
		mayBeIgnored();
		gfx.setClip(clip);
	}

	// area operations

	@Override
	public void clearRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		mayBeIgnored();
		gfx.clearRect(x, y, width, height);
	}

	@Override
	public void copyArea(final int x, final int y, final int width,
			final int height, final int dx, final int dy) {
		ensureReady();
		mayBeIgnored();
		gfx.copyArea(x, y, width, height, dx, dy);
	}

	// drawing

	@Override
	public void draw(final Shape s) {
		ensureReady();
		gfx.draw(s);
		e(new PathEvent(s.getPathIterator(null), false));
	}

	@Override
	public void drawArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		ensureReady();
		gfx.drawArc(x, y, width, height, startAngle, arcAngle);
		e(new ArcEvent(x, y, width, height, startAngle, arcAngle, false));
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		ensureReady();
		gfx.drawLine(x1, y1, x2, y2);
		e(new LineEvent(x1, y1, x2, y2));
	}

	@Override
	public void drawOval(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.drawOval(x, y, width, height);
		e(new OvalEvent(x, y, width, height, false));
	}

	@Override
	public void drawRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		ensureReady();
		gfx.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		e(new RoundRectEvent(x, y, width, height, arcWidth, arcHeight, false));
	}

	// polygons

	private Shape createPolygon(final int[] x, final int[] y, final int n,
			final boolean closed) {
		final GeneralPath path = new GeneralPath();
		if (n == 0) {
			return path;
		}
		path.moveTo(x[0], y[0]);
		for (int i = 1; i <= n; ++i) {
			path.lineTo(x[i], y[i]);
		}
		if (closed) {
			path.closePath();
		}
		return path;
	}

	@Override
	public void drawPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		draw(createPolygon(xPoints, yPoints, nPoints, true));
	}

	@Override
	public void drawPolyline(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		draw(createPolygon(xPoints, yPoints, nPoints, false));
	}

	@Override
	public void fillPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		fill(createPolygon(xPoints, yPoints, nPoints, true));
	}

	// strings

	@Override
	public void drawString(final AttributedCharacterIterator iterator,
			final float x, final float y) {
		ensureReady();
		mayBeIgnored();
		gfx.drawString(iterator, x, y);
	}

	@Override
	public void drawString(final AttributedCharacterIterator iterator,
			final int x, final int y) {
		drawString(iterator, (float) x, (float) y);
	}

	@Override
	public void drawString(final String str, final float x, final float y) {
		ensureReady();
		mayBeIgnored();
		gfx.drawString(str, x, y);
	}

	@Override
	public void drawString(final String str, final int x, final int y) {
		drawString(str, (float) x, (float) y);
	}

	@Override
	public void drawGlyphVector(final GlyphVector g, final float x,
			final float y) {
		draw(g.getOutline(x, y));
	}

	@Override
	public Font getFont() {
		ensureReady();
		return gfx.getFont();
	}

	@Override
	public void setFont(final Font font) {
		ensureReady();
		mayBeIgnored();
		gfx.setFont(font);
	}

	@Override
	public FontMetrics getFontMetrics(final Font f) {
		ensureReady();
		return gfx.getFontMetrics(f);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		ensureReady();
		return gfx.getFontRenderContext();
	}

	// images

	@Override
	public void drawImage(final BufferedImage img, final BufferedImageOp op,
			final int x, final int y) {
		final BufferedImage dest = op.createCompatibleDestImage(img, img
				.getColorModel());
		op.filter(img, dest);
		drawImage(dest, x, y, null);
	}

	@Override
	public boolean drawImage(final Image img, final AffineTransform xform,
			final ImageObserver obs) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(final Image img, final int dx1, final int dy1,
			final int dx2, final int dy2, final int sx1, final int sy1,
			final int sx2, final int sy2, final Color bgcolor,
			final ImageObserver observer) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(final Image img, final int dx1, final int dy1,
			final int dx2, final int dy2, final int sx1, final int sy1,
			final int sx2, final int sy2, final ImageObserver observer) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(final Image img, final int x, final int y,
			final Color bgcolor, final ImageObserver observer) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(final Image img, final int x, final int y,
			final ImageObserver observer) {
		ensureReady();
		final Graphics2D g = (Graphics2D) gfx.create();
		g.translate(x, y);
		final boolean b = g.drawImage(img, 0, 0, observer);
		e(new ImageEvent(img), g);
		g.dispose();
		return b;
	}

	@Override
	public boolean drawImage(final Image img, final int x, final int y,
			final int width, final int height, final Color bgcolor,
			final ImageObserver observer) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(final Image img, final int x, final int y,
			final int width, final int height, final ImageObserver observer) {
		return drawImage(img.getScaledInstance(width, height,
				Image.SCALE_SMOOTH), x, y, observer);
	}

	@Override
	public void drawRenderableImage(final RenderableImage img,
			final AffineTransform xform) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawRenderedImage(final RenderedImage img,
			final AffineTransform xform) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// filling

	@Override
	public void fill(final Shape s) {
		ensureReady();
		gfx.fill(s);
		e(new PathEvent(s.getPathIterator(null), true));
	}

	@Override
	public void fillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		ensureReady();
		gfx.fillArc(x, y, width, height, startAngle, arcAngle);
		e(new ArcEvent(x, y, width, height, startAngle, arcAngle, true));
	}

	@Override
	public void fillOval(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.fillOval(x, y, width, height);
		e(new OvalEvent(x, y, width, height, true));
	}

	@Override
	public void fillRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.fillRect(x, y, width, height);
		e(new RectangleEvent(x, y, width, height));
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		ensureReady();
		gfx.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		e(new RoundRectEvent(x, y, width, height, arcWidth, arcHeight, true));
	}

	// styles

	@Override
	public Color getBackground() {
		ensureReady();
		return gfx.getBackground();
	}

	@Override
	public Color getColor() {
		ensureReady();
		return gfx.getColor();
	}

	@Override
	public Composite getComposite() {
		ensureReady();
		return gfx.getComposite();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		ensureReady();
		return gfx.getDeviceConfiguration();
	}

	@Override
	public Paint getPaint() {
		ensureReady();
		return gfx.getPaint();
	}

	@Override
	public Stroke getStroke() {
		ensureReady();
		return gfx.getStroke();
	}

	@Override
	public void setBackground(final Color color) {
		ensureReady();
		mayBeIgnored();
		gfx.setBackground(color);
	}

	@Override
	public void setColor(final Color c) {
		ensureReady();
		mayBeIgnored();
		gfx.setColor(c);
	}

	@Override
	public void setComposite(final Composite comp) {
		ensureReady();
		mayBeIgnored();
		gfx.setComposite(comp);
	}

	@Override
	public void setPaint(final Paint paint) {
		ensureReady();
		mayBeIgnored();
		gfx.setPaint(paint);
	}

	@Override
	public void setStroke(final Stroke s) {
		ensureReady();
		mayBeIgnored();
		gfx.setStroke(s);
	}

	@Override
	public void setPaintMode() {
		ensureReady();
		mayBeIgnored();
		gfx.setPaintMode();
	}

	@Override
	public void setXORMode(final Color c1) {
		ensureReady();
		mayBeIgnored();
		gfx.setXORMode(c1);
	}

	// transformation

	@Override
	public AffineTransform getTransform() {
		ensureReady();
		return gfx.getTransform();
	}

	@Override
	public void rotate(final double theta) {
		ensureReady();
		mayBeIgnored();
		gfx.rotate(theta);
	}

	@Override
	public void rotate(final double theta, final double x, final double y) {
		ensureReady();
		mayBeIgnored();
		gfx.rotate(theta, x, y);
	}

	@Override
	public void scale(final double sx, final double sy) {
		ensureReady();
		mayBeIgnored();
		gfx.scale(sx, sy);
	}

	@Override
	public void setTransform(final AffineTransform Tx) {
		ensureReady();
		mayBeIgnored();
		gfx.setTransform(Tx);
	}

	@Override
	public void shear(final double shx, final double shy) {
		ensureReady();
		mayBeIgnored();
		gfx.shear(shx, shy);
	}

	@Override
	public void transform(final AffineTransform Tx) {
		ensureReady();
		mayBeIgnored();
		gfx.transform(Tx);
	}

	@Override
	public void translate(final double tx, final double ty) {
		ensureReady();
		mayBeIgnored();
		gfx.translate(tx, ty);
	}

	@Override
	public void translate(final int x, final int y) {
		translate((double) x, (double) y);
	}

	// rendering hints

	@Override
	public void addRenderingHints(final Map<?, ?> hints) {
		ensureReady();
		mayBeIgnored();
		gfx.addRenderingHints(hints);
	}

	@Override
	public Object getRenderingHint(final Key hintKey) {
		ensureReady();
		return gfx.getRenderingHint(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		ensureReady();
		return gfx.getRenderingHints();
	}

	@Override
	public void setRenderingHint(final Key hintKey, final Object hintValue) {
		ensureReady();
		mayBeIgnored();
		gfx.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(final Map<?, ?> hints) {
		ensureReady();
		mayBeIgnored();
		gfx.setRenderingHints(hints);
	}

	// maintenance

	@Override
	public Graphics create() {
		ensureReady();
		final ReceiverEvent event = new ReceiverEvent();
		receiver.addEvent(event);
		return new SVGGraphics(this, event);
	}

	private void ensureReady() {
		if (isDisposed()) {
			throw new IllegalStateException("context already disposed");
		}
	}

	private void mayBeIgnored() {
		if (ERROR_ON_IGNORE) {
			throw new UnsupportedOperationException();
		}
	}

	public boolean isDisposed() {
		return receiver == null;
	}

	@Override
	public void dispose() {
		if (isDisposed()) {
			return;
		}
		gfx.dispose();
		receiver = null;
	}

}
