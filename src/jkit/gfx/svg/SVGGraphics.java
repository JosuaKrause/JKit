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
import java.text.CharacterIterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jkit.gfx.GFXEventReceiver;
import jkit.gfx.GFXGraphics;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVGGraphics extends GFXGraphics<SVGEvent> {

	public SVGGraphics(final Graphics2D gfx, final int width, final int height) {
		super(gfx, width, height);
	}

	public SVGGraphics(final Graphics2D gfx,
			final GFXEventReceiver<SVGEvent> receiver) {
		super(gfx, receiver);
	}

	@Override
	protected SVGGraphics copy(final GFXEventReceiver<SVGEvent> receiver,
			final Graphics2D gfx) {
		return new SVGGraphics(gfx, receiver);
	}

	private SVGEvent ignore(final String str) {
		return new SVGEvent() {

			@Override
			public void write(final XMLStreamWriter out)
					throws XMLStreamException {
				System.out.println("ignore " + str);
			}

		};
	}

	@Override
	protected SVGEvent createArcEvent(final int x, final int y,
			final int width, final int height, final int startAngle,
			final int arcAngle, final boolean fill) {
		// TODO:
		return ignore("arc event");
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

	@Override
	protected SVGEvent createChangeEvent(final Change change) {
		return new SVGEvent() {

			@Override
			public void write(final XMLStreamWriter out)
					throws XMLStreamException {
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
				case COLOR:
					color = getColorString(gfx.getColor());
					break;
				case BACKGROUND:
					background = getColorString(gfx.getBackground());
					break;
				case PAINT: {
					// TODO:
					final Color c = gfx.getColor();
					if (c != null) {
						color = getColorString(c);
					}
					final Color b = gfx.getBackground();
					if (b != null) {
						background = getColorString(b);
					}
					break;
				}
				default:
					// TODO
					System.out.println("ignored " + change);
					break;
				}
			}
		};
	}

	private String transform = null;

	private void writeTransform(final XMLStreamWriter out)
			throws XMLStreamException {
		if (transform != null) {
			out.writeAttribute("transform", transform);
		}
	}

	private String color = null;

	private String background = null;

	private void writeStyle(final XMLStreamWriter out, final boolean fill)
			throws XMLStreamException {
		writeStyle(out, fill, Integer.MIN_VALUE);
	}

	private void writeStyle(final XMLStreamWriter out, final boolean fill,
			final int rule) throws XMLStreamException {
		final StringBuilder sb = new StringBuilder();
		if (fill) {
			if (background != null) {
				sb.append("fill:");
				sb.append(background);
				sb.append(";stroke:none;fill-opacity:1;");
			}
		} else {
			if (color != null) {
				sb.append("fill:none;stroke:");
				sb.append(color);
				sb.append(";stroke-width:1.0;stroke-opacity:1;");
			}
		}
		if (rule != Integer.MIN_VALUE) {
			sb.append("fill-rule:");
			switch (rule) {
			case PathIterator.WIND_EVEN_ODD:
				sb.append("evenodd");
				break;
			case PathIterator.WIND_NON_ZERO:
				sb.append("nonzero");
				break;
			default:
				throw new InternalError("illegal fill rule");
			}
			sb.append(';');
		}
		if (sb.length() > 0) {
			out.writeAttribute("style", sb.toString());
		}
	}

	private void writeClip(final XMLStreamWriter out) throws XMLStreamException {
		// TODO
	}

	@Override
	protected SVGEvent createClearRectEvent(final Rectangle r) {
		// TODO:
		return ignore("clear rect event");
	}

	@Override
	protected SVGEvent createClipEvent(final Shape s) {
		// TODO:
		return ignore("clip event");
	}

	@Override
	protected SVGEvent createCopyAreaEvent(final Rectangle r, final int dx,
			final int dy) {
		// TODO:
		return ignore("copy area event");
	}

	@Override
	protected SVGEvent createImageEvent(final Image img) {
		// TODO:
		return ignore("image event");
	}

	@Override
	protected SVGEvent createLineEvent(final Line2D line) {
		return createPathEvent(line.getPathIterator(null), false);
	}

	@Override
	protected SVGEvent createModeEvent(final Color xorColor) {
		// TODO:
		return ignore("mode event");
	}

	@Override
	protected SVGEvent createOvalEvent(final int x, final int y,
			final int width, final int height, final boolean fill) {
		// TODO:
		return ignore("oval event");
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
			case PathIterator.SEG_CLOSE:
				sb.append('z');
				break;
			default:
				// TODO:
				System.out.println("ignored path segment "
						+ path.currentSegment(arr));
				break;
			}
			path.next();
		}
		return sb;
	}

	@Override
	protected SVGEvent createPathEvent(final PathIterator path,
			final boolean fill) {
		return new SVGEvent() {

			private String d = null;

			private String d() {
				if (d == null) {
					d = createD(path).toString();
				}
				return d;
			}

			@Override
			public void write(final XMLStreamWriter out)
					throws XMLStreamException {
				out.writeEmptyElement("path");
				out.writeAttribute("d", d());
				writeTransform(out);
				writeStyle(out, fill, path.getWindingRule());
			}

		};
	}

	@Override
	protected GFXEventReceiver<SVGEvent> createReceiverEvent(final int w,
			final int h, final boolean inner) {
		return new GFXEventReceiver<SVGEvent>(w, h) {

			@Override
			public SVGEvent getEvent() {
				final Iterable<SVGEvent> events = this;
				return new SVGEvent() {

					@Override
					public void write(final XMLStreamWriter out)
							throws XMLStreamException {
						if (inner) {
							out.writeStartElement("g");
							writeClip(out);
							writeTransform(out);
						} else {
							out.writeStartElement("svg");
							out.writeAttribute("xmlns:svg",
									"http://www.w3.org/2000/svg");
							out.writeAttribute("xmlns",
									"http://www.w3.org/2000/svg");
							out.writeAttribute("xmlns:xlink",
									"http://www.w3.org/1999/xlink");
							out.writeAttribute("version", "1.0");
							if (width > 0) {
								out.writeAttribute("width", width + "px");
							}
							if (height > 0) {
								out.writeAttribute("height", height + "px");
							}
						}
						for (final SVGEvent e : events) {
							e.write(out);
						}
						out.writeEndElement();
					}
				};
			}

		};
	}

	@Override
	protected SVGEvent createRectangleEvent(final int x, final int y,
			final int width, final int height) {
		return createRoundRectEvent(x, y, width, height, 0, 0, true);
	}

	@Override
	protected SVGEvent createRoundRectEvent(final int x, final int y,
			final int width, final int height, final int arcWidth,
			final int arcHeight, final boolean fill) {
		return new SVGEvent() {

			@Override
			public void write(final XMLStreamWriter out)
					throws XMLStreamException {
				out.writeEmptyElement("rect");
				out.writeAttribute("x", "" + x);
				out.writeAttribute("y", "" + y);
				out.writeAttribute("width", "" + width);
				out.writeAttribute("height", "" + height);
				if (arcWidth > 0) {
					out.writeAttribute("rx", "" + arcWidth);
				}
				if (arcHeight > 0) {
					out.writeAttribute("ry", "" + arcHeight);
				}
				writeTransform(out);
				writeStyle(out, fill);
			}

		};
	}

	@Override
	protected SVGEvent createSetClipEvent(final Shape s) {
		// TODO:
		return ignore("set clip event");
	}

	@Override
	protected SVGEvent createStringEvent(
			final AttributedCharacterIterator iterator) {
		// TODO:
		final StringBuilder sb = new StringBuilder();
		char c;
		while ((c = iterator.next()) != CharacterIterator.DONE) {
			sb.append(c);
		}
		return createStringEvent(sb.toString());
	}

	@Override
	protected SVGEvent createStringEvent(final String string) {
		return new SVGEvent() {

			@Override
			public void write(final XMLStreamWriter out)
					throws XMLStreamException {
				out.writeStartElement("text");
				out.writeCharacters(string);
				out.writeEndElement();
			}
		};
	}

}
