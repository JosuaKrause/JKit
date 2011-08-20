/**
 * 
 */
package jkit.gfx;

import static jkit.gfx.GFXGraphics.Change.BACKGROUND;
import static jkit.gfx.GFXGraphics.Change.COLOR;
import static jkit.gfx.GFXGraphics.Change.COMPOSITE;
import static jkit.gfx.GFXGraphics.Change.FONT;
import static jkit.gfx.GFXGraphics.Change.PAINT;
import static jkit.gfx.GFXGraphics.Change.RENDERING_HINTS;
import static jkit.gfx.GFXGraphics.Change.STROKE;
import static jkit.gfx.GFXGraphics.Change.TRANSFORM;

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
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 * @param <T>
 *            The event type.
 * 
 */
public abstract class GFXGraphics<T extends GFXEvent> extends Graphics2D {

	private final Graphics2D gfx;

	private GFXEventReceiver<T> receiver;

	public GFXGraphics(final Graphics2D gfx, final GFXEventReceiver<T> receiver) {
		this.receiver = receiver;
		this.gfx = gfx;
	}

	public GFXGraphics(final Graphics2D gfx, final int width, final int height) {
		receiver = createReceiverEvent(width, height, false);
		this.gfx = gfx;
	}

	public GFXEventReceiver<T> getReceiver() {
		ensureReady();
		return receiver;
	}

	private GFXGraphics<T> copy(final GFXEventReceiver<T> receiver,
			final GFXGraphics<T> copy) {
		return copy(receiver, (Graphics2D) copy.gfx.create());
	}

	protected abstract GFXEventReceiver<T> createReceiverEvent(int width,
			int height, boolean inner);

	protected abstract GFXGraphics<T> copy(GFXEventReceiver<T> receiver,
			Graphics2D gfx);

	private void e(final T event) {
		e(event, gfx);
	}

	private void e(final T event, final Graphics2D gfx) {
		event.setGraphics(gfx);
		receiver.addEvent(event);
	}

	public static enum Change {
		FONT,

		BACKGROUND,

		COLOR,

		COMPOSITE,

		PAINT,

		STROKE,

		TRANSFORM,

		RENDERING_HINTS,

		;
	}

	protected abstract T createChangeEvent(Change changes);

	// clipping

	protected abstract T createClipEvent(Shape s);

	protected abstract T createSetClipEvent(Shape s);

	@Override
	public void clip(final Shape s) {
		ensureReady();
		gfx.clip(s);
		e(createClipEvent(s));
	}

	@Override
	public void clipRect(final int x, final int y, final int width,
			final int height) {
		clip(new Rectangle2D.Double(x, y, width, height));
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
		setClip(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void setClip(final Shape clip) {
		ensureReady();
		gfx.setClip(clip);
		e(createSetClipEvent(clip));
	}

	// area operations

	protected abstract T createClearRectEvent(Rectangle r);

	protected abstract T createCopyAreaEvent(Rectangle r, int dx, int dy);

	@Override
	public void clearRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.clearRect(x, y, width, height);
		e(createClearRectEvent(new Rectangle(x, y, width, height)));
	}

	@Override
	public void copyArea(final int x, final int y, final int width,
			final int height, final int dx, final int dy) {
		ensureReady();
		gfx.copyArea(x, y, width, height, dx, dy);
		e(createCopyAreaEvent(new Rectangle(x, y, width, height), dx, dy));
	}

	// drawing

	protected abstract T createPathEvent(PathIterator path, boolean fill);

	protected abstract T createArcEvent(final int x, final int y,
			final int width, final int height, final int startAngle,
			final int arcAngle, boolean fill);

	protected abstract T createOvalEvent(final int x, final int y,
			final int width, final int height, boolean fill);

	protected abstract T createRoundRectEvent(final int x, final int y,
			final int width, final int height, final int arcWidth,
			final int arcHeight, boolean fill);

	protected abstract T createLineEvent(Line2D line);

	protected abstract T createRectangleEvent(final int x, final int y,
			final int width, final int height);

	@Override
	public void draw(final Shape s) {
		ensureReady();
		gfx.draw(s);
		e(createPathEvent(s.getPathIterator(null), false));
	}

	@Override
	public void drawArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		ensureReady();
		gfx.drawArc(x, y, width, height, startAngle, arcAngle);
		e(createArcEvent(x, y, width, height, startAngle, arcAngle, false));
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		ensureReady();
		gfx.drawLine(x1, y1, x2, y2);
		e(createLineEvent(new Line2D.Double(x1, y1, x2, y2)));
	}

	@Override
	public void drawOval(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.drawOval(x, y, width, height);
		e(createOvalEvent(x, y, width, height, false));
	}

	@Override
	public void drawRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		ensureReady();
		gfx.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		e(createRoundRectEvent(x, y, width, height, arcWidth, arcHeight, false));
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

	protected abstract T createStringEvent(AttributedCharacterIterator iterator);

	protected abstract T createStringEvent(String string);

	@Override
	public void drawString(final AttributedCharacterIterator iterator,
			final float x, final float y) {
		ensureReady();
		final Graphics2D g = (Graphics2D) gfx.create();
		g.translate(x, y);
		g.drawString(iterator, 0, 0);
		e(createStringEvent(iterator), g);
		g.dispose();
	}

	@Override
	public void drawString(final AttributedCharacterIterator iterator,
			final int x, final int y) {
		drawString(iterator, (float) x, (float) y);
	}

	@Override
	public void drawString(final String str, final float x, final float y) {
		ensureReady();
		final Graphics2D g = (Graphics2D) gfx.create();
		g.translate(x, y);
		g.drawString(str, 0, 0);
		e(createStringEvent(str), g);
		g.dispose();
	}

	@Override
	public void drawString(final String str, final int x, final int y) {
		drawString(str, (float) x, (float) y);
	}

	@Override
	public void drawGlyphVector(final GlyphVector g, final float x,
			final float y) {
		fill(g.getOutline(x, y));
	}

	@Override
	public Font getFont() {
		ensureReady();
		return gfx.getFont();
	}

	@Override
	public void setFont(final Font font) {
		ensureReady();
		gfx.setFont(font);
		e(createChangeEvent(FONT));
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

	protected abstract T createImageEvent(Image img);

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
		e(createImageEvent(img), g);
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
		e(createPathEvent(s.getPathIterator(null), true));
	}

	@Override
	public void fillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		ensureReady();
		gfx.fillArc(x, y, width, height, startAngle, arcAngle);
		e(createArcEvent(x, y, width, height, startAngle, arcAngle, true));
	}

	@Override
	public void fillOval(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.fillOval(x, y, width, height);
		e(createOvalEvent(x, y, width, height, true));
	}

	@Override
	public void fillRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.fillRect(x, y, width, height);
		e(createRectangleEvent(x, y, width, height));
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		ensureReady();
		gfx.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		e(createRoundRectEvent(x, y, width, height, arcWidth, arcHeight, true));
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
		gfx.setBackground(color);
		e(createChangeEvent(BACKGROUND));
	}

	@Override
	public void setColor(final Color c) {
		ensureReady();
		gfx.setColor(c);
		e(createChangeEvent(COLOR));
	}

	@Override
	public void setComposite(final Composite comp) {
		ensureReady();
		gfx.setComposite(comp);
		e(createChangeEvent(COMPOSITE));
	}

	@Override
	public void setPaint(final Paint paint) {
		ensureReady();
		gfx.setPaint(paint);
		e(createChangeEvent(PAINT));
	}

	@Override
	public void setStroke(final Stroke s) {
		ensureReady();
		gfx.setStroke(s);
		e(createChangeEvent(STROKE));
	}

	protected abstract T createModeEvent(Color xorColor);

	@Override
	public void setPaintMode() {
		ensureReady();
		gfx.setPaintMode();
		e(createModeEvent(null));
	}

	@Override
	public void setXORMode(final Color c1) {
		ensureReady();
		gfx.setXORMode(c1);
		e(createModeEvent(c1));
	}

	// transformation

	@Override
	public AffineTransform getTransform() {
		ensureReady();
		return gfx.getTransform();
	}

	@Override
	public void rotate(final double theta) {
		transform(AffineTransform.getRotateInstance(theta));
	}

	@Override
	public void rotate(final double theta, final double x, final double y) {
		transform(AffineTransform.getRotateInstance(theta, x, y));
	}

	@Override
	public void scale(final double sx, final double sy) {
		transform(AffineTransform.getScaleInstance(sx, sy));
	}

	@Override
	public void setTransform(final AffineTransform Tx) {
		ensureReady();
		gfx.setTransform(Tx);
		e(createChangeEvent(TRANSFORM));
	}

	@Override
	public void shear(final double shx, final double shy) {
		transform(AffineTransform.getShearInstance(shx, shy));
	}

	@Override
	public void transform(final AffineTransform Tx) {
		ensureReady();
		gfx.transform(Tx);
		e(createChangeEvent(TRANSFORM));
	}

	@Override
	public void translate(final double tx, final double ty) {
		transform(AffineTransform.getTranslateInstance(tx, ty));
	}

	@Override
	public void translate(final int x, final int y) {
		translate((double) x, (double) y);
	}

	// rendering hints

	@Override
	public void addRenderingHints(final Map<?, ?> hints) {
		ensureReady();
		gfx.addRenderingHints(hints);
		e(createChangeEvent(RENDERING_HINTS));
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
		gfx.setRenderingHint(hintKey, hintValue);
		e(createChangeEvent(RENDERING_HINTS));
	}

	@Override
	public void setRenderingHints(final Map<?, ?> hints) {
		ensureReady();
		gfx.setRenderingHints(hints);
		e(createChangeEvent(RENDERING_HINTS));
	}

	// maintenance

	@Override
	public Graphics create() {
		ensureReady();
		final GFXEventReceiver<T> event = createReceiverEvent(-1, -1, true);
		receiver.addEvent(event.getEvent());
		return copy(event, this);
	}

	private void ensureReady() {
		if (isDisposed()) {
			throw new IllegalStateException("context already disposed");
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
