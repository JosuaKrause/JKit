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
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import jkit.svg.event.ReceiverEvent;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVGGraphics extends Graphics2D {

	private SVGEventReceiver receiver;

	public SVGGraphics(final SVGEventReceiver receiver) {
		this.receiver = receiver;
	}

	private SVGGraphics(final SVGGraphics copy, final SVGEventReceiver receiver) {
		this.receiver = receiver;
		// TODO:
	}

	// clipping

	@Override
	public void clip(final Shape s) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void clipRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Shape getClip() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Rectangle getClipBounds() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hit(final Rectangle rect, final Shape s,
			final boolean onStroke) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setClip(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setClip(final Shape clip) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// area operations

	@Override
	public void clearRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void copyArea(final int x, final int y, final int width,
			final int height, final int dx, final int dy) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// drawing

	@Override
	public void draw(final Shape s) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawOval(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPolyline(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// strings

	@Override
	public void drawString(final AttributedCharacterIterator iterator,
			final float x, final float y) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(final AttributedCharacterIterator iterator,
			final int x, final int y) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(final String str, final float x, final float y) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(final String str, final int x, final int y) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawGlyphVector(final GlyphVector g, final float x,
			final float y) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Font getFont() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFont(final Font font) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public FontMetrics getFontMetrics(final Font f) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// images

	@Override
	public void drawImage(final BufferedImage img, final BufferedImageOp op,
			final int x, final int y) {
		ensureReady();
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
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
		ensureReady();
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillArc(final int x, final int y, final int width,
			final int height, final int startAngle, final int arcAngle) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillOval(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillPolygon(final int[] xPoints, final int[] yPoints,
			final int nPoints) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillRect(final int x, final int y, final int width,
			final int height) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width,
			final int height, final int arcWidth, final int arcHeight) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// styles

	@Override
	public Color getBackground() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getColor() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Composite getComposite() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Paint getPaint() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Stroke getStroke() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBackground(final Color color) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setColor(final Color c) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setComposite(final Composite comp) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPaint(final Paint paint) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStroke(final Stroke s) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPaintMode() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setXORMode(final Color c1) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// transformation

	@Override
	public AffineTransform getTransform() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(final double theta) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(final double theta, final double x, final double y) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void scale(final double sx, final double sy) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTransform(final AffineTransform Tx) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void shear(final double shx, final double shy) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void transform(final AffineTransform Tx) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void translate(final double tx, final double ty) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void translate(final int x, final int y) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	// rendering hints

	@Override
	public void addRenderingHints(final Map<?, ?> hints) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getRenderingHint(final Key hintKey) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public RenderingHints getRenderingHints() {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRenderingHint(final Key hintKey, final Object hintValue) {
		ensureReady();
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRenderingHints(final Map<?, ?> hints) {
		ensureReady();
		throw new UnsupportedOperationException();
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

	public boolean isDisposed() {
		return receiver == null;
	}

	@Override
	public void dispose() {
		if (isDisposed()) {
			return;
		}
		// TODO
		receiver = null;
	}

}
