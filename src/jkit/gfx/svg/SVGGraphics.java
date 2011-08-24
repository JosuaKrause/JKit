/**
 * 
 */
package jkit.gfx.svg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;

import jkit.gfx.GFXGraphics;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVGGraphics extends GFXGraphics {

	private SVGWriter out;

	public SVGGraphics(final Graphics2D gfx, final SVGWriter out) {
		super(gfx);
		this.out = out;
	}

	@Override
	protected SVGGraphics copy(final Graphics2D gfx) {
		return new SVGGraphics(gfx, out);
	}

	private void ignore(final String type) {
		System.out.println(type);
	}

	private void writeMatrix(final StringBuilder sb, final double[] matrix) {
		boolean first = true;
		for (final double d : matrix) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			sb.append(d);
		}
	}

	private String getColorString(final Color c) {
		final String hex = "000000" + Integer.toHexString(c.getRGB());
		return "#" + hex.substring(hex.length() - 6);
	}

	private String getColorTransparency(final Color c) {
		return "" + (c.getAlpha() / 255.0);
	}

	@Override
	protected void doChangeEvent(final Change change) {
		switch (change) {
		case TRANSFORM: {
			final StringBuilder sb = new StringBuilder("matrix(");
			final double[] matrix = new double[6];
			gfx.getTransform().getMatrix(matrix);
			writeMatrix(sb, matrix);
			sb.append(')');
			transform = sb.toString();
			break;
		}
		case COLOR: {
			final Color c = gfx.getColor();
			color = getColorString(c);
			colorT = getColorTransparency(c);
			break;
		}
		case BACKGROUND: {
			final Color b = gfx.getBackground();
			background = getColorString(b);
			backgroundT = getColorTransparency(b);
			break;
		}
		default:
			// TODO
			ignore("ignored " + change);
			break;
		}
	}

	private String transform = "translate(0,0)";

	private void writeTransform(final SVGWriter out) {
		out.writeAttribute("transform", transform);
	}

	private String color = getColorString(Color.BLACK);

	private String colorT = "" + 1;

	private String background = getColorString(Color.WHITE);

	private String backgroundT = "" + 1;

	private void writeStyle(final SVGWriter out, final boolean fill) {
		final StringBuilder sb = new StringBuilder();
		if (fill) {
			sb.append("fill:");
			sb.append(background);
			sb.append(";stroke:none;fill-opacity:");
			sb.append(backgroundT);
			sb.append(';');
		} else {
			sb.append("fill:none;stroke:");
			sb.append(color);
			sb.append(";stroke-width:1.0;stroke-opacity:");
			sb.append(colorT);
			sb.append(';');
		}
		sb.append("fill-rule:");
		if (fill) {
			sb.append("evenodd");
		} else {
			sb.append("nonzero");
		}
		sb.append(';');
		if (sb.length() > 0) {
			out.writeAttribute("style", sb.toString());
		}
	}

	private void writeCoords(final StringBuilder sb, final double[] arr,
			int pairs) {
		int pos = 0;
		while (--pairs >= 0) {
			sb.append(' ');
			sb.append(arr[pos++]);
			sb.append(',');
			sb.append(arr[pos++]);
		}
	}

	private StringBuilder createD(final PathIterator path) {
		final StringBuilder sb = new StringBuilder();
		final double[] arr = new double[6];
		boolean first = true;
		while (!path.isDone()) {
			if (!first) {
				sb.append(' ');
			} else {
				first = false;
			}
			switch (path.currentSegment(arr)) {
			case PathIterator.SEG_MOVETO:
				sb.append('M');
				writeCoords(sb, arr, 1);
				break;
			case PathIterator.SEG_LINETO:
				sb.append('L');
				writeCoords(sb, arr, 1);
				break;
			case PathIterator.SEG_CUBICTO:
				sb.append('C');
				writeCoords(sb, arr, 3);
				break;
			case PathIterator.SEG_QUADTO:
				sb.append('Q');
				writeCoords(sb, arr, 2);
				break;
			case PathIterator.SEG_CLOSE:
				sb.append('z');
				break;
			default:
				throw new InternalError();
			}
			path.next();
		}
		return sb;
	}

	@Override
	protected void doPathEvent(final PathIterator path) {
		final String d = createD(path).toString();
		out.writeEmptyElement("path");
		out.writeAttribute("d", d);
		writeTransform(out);
		writeStyle(out, path.getWindingRule() == PathIterator.WIND_EVEN_ODD);
	}

	@Override
	protected void doSetClipEvent(final Shape s) {
		// TODO:
		ignore("set clip event");
	}

	@Override
	protected void doClearRectEvent(final Rectangle r) {
		// TODO:
		ignore("clear rect event");
	}

	@Override
	protected void doClipEvent(final Shape s) {
		// TODO:
		ignore("clip event");
	}

	@Override
	protected void doCopyAreaEvent(final Rectangle r, final int dx, final int dy) {
		// TODO:
		ignore("copy area event");
	}

	@Override
	protected void doImageEvent(final Image img, final double x, final double y) {
		// TODO:
		ignore("image event");
	}

	@Override
	protected void doModeEvent(final Color xorColor) {
		// TODO:
		ignore("mode event");
	}

	@Override
	public void dispose() {
		out = null;
		super.dispose();
	}

}
