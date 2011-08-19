/**
 * 
 */
package jkit.gfx.svg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.text.AttributedCharacterIterator;

import jkit.gfx.GFXEventReceiver;
import jkit.gfx.GFXGraphics;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVGGraphics extends GFXGraphics<SVGEvent> {

	public SVGGraphics(final Graphics2D gfx) {
		super(gfx);
	}

	@Override
	protected SVGGraphics copy(final GFXEventReceiver<SVGEvent> receiver,
			final Graphics2D gfx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createArcEvent(final int x, final int y,
			final int width, final int height, final int startAngle,
			final int arcAngle, final boolean fill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createChangeEvent(final Change changes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createClearRectEvent(final Rectangle r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createClipEvent(final Shape s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createCopyAreaEvent(final Rectangle r, final int dx,
			final int dy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createImageEvent(final Image img) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createLineEvent(final Line2D line) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createModeEvent(final Color xorColor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createOvalEvent(final int x, final int y,
			final int width, final int height, final boolean fill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createPathEvent(final PathIterator path,
			final boolean fill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GFXEventReceiver<SVGEvent> createReceiverEvent(final boolean inner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createRectangleEvent(final int x, final int y,
			final int width, final int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createRoundRectEvent(final int x, final int y,
			final int width, final int height, final int arcWidth,
			final int arcHeight, final boolean fill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createSetClipEvent(final Shape s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createStringEvent(
			final AttributedCharacterIterator iterator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SVGEvent createStringEvent(final String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
