/**
 * 
 */
package jkit.gfx.svg;

import java.io.Closeable;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class SVGWriter implements Closeable {

	public static final String UTF8 = "UTF-8";

	private final XMLStreamWriter out;

	public SVGWriter(final XMLStreamWriter out) {
		this.out = out;
	}

	public void writeStart(final double width, final double height) {
		try {
			out.writeStartDocument(UTF8, "1.0");
			out.writeStartElement("svg");
			out.writeAttribute("xmlns:svg", "http://www.w3.org/2000/svg");
			out.writeAttribute("xmlns", "http://www.w3.org/2000/svg");
			out.writeAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
			out.writeAttribute("version", "1.0");
			out.writeAttribute("width", width + "px");
			out.writeAttribute("height", height + "px");
		} catch (final XMLStreamException e) {
			setErr(e);
		}
	}

	public void writeEnd() {
		try {
			out.writeEndElement();
			out.writeEndDocument();
		} catch (final XMLStreamException e) {
			setErr(e);
		}
	}

	public void writeEmptyElement(final String name) {
		try {
			out.writeEmptyElement(name);
		} catch (final XMLStreamException e) {
			setErr(e);
		}
	}

	public void writeAttribute(final String key, final String value) {
		try {
			out.writeAttribute(key, value);
		} catch (final XMLStreamException e) {
			setErr(e);
		}
	}

	private XMLStreamException err = null;

	private void setErr(final XMLStreamException e) {
		if (err != null) {
			return;
		}
		err = e;
	}

	public boolean hasException() {
		return err != null;
	}

	public XMLStreamException getException() {
		return err;
	}

	@Override
	public void close() throws IOException {
		try {
			out.close();
		} catch (final XMLStreamException e) {
			throw new IOException(e);
		}
	}

}
