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
import java.text.CharacterIterator;
import java.util.Map;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public abstract class GFXGraphics extends Graphics2D {

	protected Graphics2D gfx;

	public GFXGraphics(final Graphics2D gfx) {
		this.gfx = gfx;
	}

	private GFXGraphics copy(final GFXGraphics copy) {
		return copy((Graphics2D) copy.gfx.create());
	}

	protected abstract GFXGraphics copy(Graphics2D gfx);

	public static enum Change {
		FONT,

		BACKGROUND,

		COLOR,

		COMPOSITE,

		PAINT,

		TRANSFORM,

		RENDERING_HINTS,

		;
	}

	protected abstract void doChangeEvent(Change changes);

	// clipping

	protected abstract void doClipEvent(Shape s);

	protected abstract void doSetClipEvent(Shape s);

	@Override
	public void clip(final Shape s) {
		ensureReady();
		gfx.clip(s);
		doClipEvent(s);
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
		doSetClipEvent(clip);
	}

	// area operations

	protected abstract void doClearRectEvent(Rectangle r);

	protected abstract void doCopyAreaEvent(Rectangle r, int dx, int dy);

	@Override
	public void clearRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		gfx.clearRect(x, y, width, height);
		doClearRectEvent(new Rectangle(x, y, width, height));
	}

	@Override
	public void copyArea(final int x, final int y, final int width,
			final int height, final int dx, final int dy) {
		ensureReady();
		gfx.copyArea(x, y, width, height, dx, dy);
		doCopyAreaEvent(new Rectangle(x, y, width, height), dx, dy);
	}

	// drawing

	protected abstract void doPathEvent(PathIterator path, boolean fill);

	private void paintShape(final Shape s, final boolean fill) {
		ensureReady();
		if (fill) {
			gfx.draw(s);
		} else {
			gfx.fill(s);
		}
		final Shape n = gfx.getStroke().createStrokedShape(s);
		doPathEvent(n.getPathIterator(null), fill);
	}

	@Override
	public void draw(final Shape s) {
		paintShape(s, false);
	}

	@Override
	public void drawArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		paintShape(createArc(x, y, width, height, startAngle, arcAngle), false);
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		paintShape(new Line2D.Double(x1, y1, x2, y2), false);
	}

	@Override
	public void drawOval(final int x, final int y, final int width,
			final int height) {
		paintShape(createOval(x, y, width, height), false);
	}

	@Override
	public void drawRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		paintShape(createRoundRect(x, y, width, height, arcWidth, arcHeight),
				false);
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
		paintShape(createPolygon(xPoints, yPoints, nPoints, true), false);
	}

	@Override
	public void drawPolyline(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		paintShape(createPolygon(xPoints, yPoints, nPoints, false), false);
	}

	@Override
	public void fillPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		paintShape(createPolygon(xPoints, yPoints, nPoints, true), true);
	}

	// strings

	@Override
	public void drawString(final AttributedCharacterIterator it, final float x,
			final float y) {
		ensureReady();
		final Graphics2D g = (Graphics2D) gfx.create();
		g.translate(x, y);
		char c;
		while ((c = it.next()) != CharacterIterator.DONE) {
			System.out.println(c);
			throw new UnsupportedOperationException();
		}
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
		final GlyphVector gv = gfx.getFont().createGlyphVector(
				gfx.getFontRenderContext(), str);
		paintShape(gv.getOutline(x, y), true);
	}

	@Override
	public void drawString(final String str, final int x, final int y) {
		drawString(str, (float) x, (float) y);
	}

	@Override
	public void drawGlyphVector(final GlyphVector g, final float x,
			final float y) {
		paintShape(g.getOutline(x, y), true);
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
		doChangeEvent(FONT);
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

	protected abstract void doImageEvent(Image img, double x, double y);

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
		final boolean b = gfx.drawImage(img, x, y, observer);
		doImageEvent(img, x, y);
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
		paintShape(s, true);
	}

	@Override
	public void fillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		paintShape(createArc(x, y, width, height, startAngle, arcAngle), true);
	}

	private Shape createArc(final double x, final double y, final double w,
			final double h, final double sa, final double aa) {
		throw new UnsupportedOperationException();
	}

	private Shape createOval(final double l, final double t, final double w,
			final double h) {
		final GeneralPath gp = new GeneralPath();
		final double b = t + h;
		final double r = l + w;
		final double x = l + w * 0.5;
		final double y = t + h * 0.5;
		gp.moveTo(x, t);
		gp.quadTo(r, t, r, y);
		gp.quadTo(r, b, x, b);
		gp.quadTo(l, b, l, y);
		gp.quadTo(l, t, x, t);
		return gp;
	}

	private Shape createRoundRect(final double l, final double t,
			final double w, final double h, final double rx, final double ry) {
		final GeneralPath gp = new GeneralPath();
		final double b = t + h;
		final double r = l + w;
		final double l1 = l + rx;
		final double r1 = r - rx;
		final double t1 = t + ry;
		final double b1 = b - ry;
		gp.moveTo(r1, t);
		gp.quadTo(r, t, r, t1);
		gp.lineTo(r, b1);
		gp.quadTo(r, b, r1, b);
		gp.lineTo(l1, b);
		gp.quadTo(l, b, l, b1);
		gp.lineTo(l, t1);
		gp.quadTo(l, t, l1, t);
		gp.closePath();
		return gp;
	}

	@Override
	public void fillOval(final int x, final int y, final int width,
			final int height) {
		paintShape(createOval(x, y, width, height), true);
	}

	@Override
	public void fillRect(final int x, final int y, final int width,
			final int height) {
		paintShape(new Rectangle2D.Double(x, y, width, height), true);
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		paintShape(createRoundRect(x, y, width, height, arcWidth, arcHeight),
				true);
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
		doChangeEvent(BACKGROUND);
	}

	@Override
	public void setColor(final Color c) {
		ensureReady();
		gfx.setColor(c);
		doChangeEvent(COLOR);
	}

	@Override
	public void setComposite(final Composite comp) {
		ensureReady();
		gfx.setComposite(comp);
		doChangeEvent(COMPOSITE);
	}

	@Override
	public void setPaint(final Paint paint) {
		ensureReady();
		gfx.setPaint(paint);
		doChangeEvent(PAINT);
	}

	@Override
	public void setStroke(final Stroke s) {
		ensureReady();
		gfx.setStroke(s);
	}

	protected abstract void doModeEvent(Color xorColor);

	@Override
	public void setPaintMode() {
		ensureReady();
		gfx.setPaintMode();
		doModeEvent(null);
	}

	@Override
	public void setXORMode(final Color c1) {
		ensureReady();
		gfx.setXORMode(c1);
		doModeEvent(c1);
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
		doChangeEvent(TRANSFORM);
	}

	@Override
	public void shear(final double shx, final double shy) {
		transform(AffineTransform.getShearInstance(shx, shy));
	}

	@Override
	public void transform(final AffineTransform Tx) {
		ensureReady();
		gfx.transform(Tx);
		doChangeEvent(TRANSFORM);
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
		doChangeEvent(RENDERING_HINTS);
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
		doChangeEvent(RENDERING_HINTS);
	}

	@Override
	public void setRenderingHints(final Map<?, ?> hints) {
		ensureReady();
		gfx.setRenderingHints(hints);
		doChangeEvent(RENDERING_HINTS);
	}

	// maintenance

	@Override
	public Graphics create() {
		ensureReady();
		return copy(this);
	}

	private void ensureReady() {
		if (isDisposed()) {
			throw new IllegalStateException("context already disposed");
		}
	}

	public boolean isDisposed() {
		return gfx == null;
	}

	@Override
	public void dispose() {
		if (isDisposed()) {
			return;
		}
		gfx.dispose();
		gfx = null;
	}

}
