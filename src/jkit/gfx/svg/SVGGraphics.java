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
					System.out.println("ignored " + change);
					break;
				}
			}
		};
	}

	private String transform = "translate(0,0)";

	private void writeTransform(final XMLStreamWriter out)
			throws XMLStreamException {
		if (transform != null) {
			out.writeAttribute("transform", transform);
		}
	}

	private String color = getColorString(Color.BLACK);

	private String colorT = "" + 1;

	private String background = getColorString(Color.WHITE);

	private String backgroundT = "" + 1;

	private void writeStyle(final XMLStreamWriter out, final boolean fill)
			throws XMLStreamException {
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
				writeStyle(out, fill);
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
	protected SVGEvent createSetClipEvent(final Shape s) {
		// TODO:
		return ignore("set clip event");
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
	protected SVGEvent createModeEvent(final Color xorColor) {
		// TODO:
		return ignore("mode event");
	}

}
