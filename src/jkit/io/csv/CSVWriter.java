package jkit.io.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVWriter implements Closeable {

	private PrintWriter out;

	private final char delimiter;

	private final String string;

	private final String check;

	private boolean first;

	public CSVWriter(final PrintWriter out) {
		this(out, ';', '"');
	}

	public CSVWriter(final PrintWriter out, final char delimiter,
			final char string) {
		this.out = out;
		this.delimiter = delimiter;
		this.string = "" + string;
		check = "\r\n" + delimiter + string;
		first = true;
	}

	public void writeCell(final String content) {
		ensureOpen();
		if (first) {
			first = false;
		} else {
			out.print(delimiter);
		}
		out.print(sanitize(content));
	}

	private static boolean hasChars(final String haystack, final String chars) {
		for (int i = 0; i < chars.length(); ++i) {
			if (haystack.indexOf(chars.charAt(i)) >= 0) {
				return true;
			}
		}
		return false;
	}

	private String sanitize(final String content) {
		if (!hasChars(content, check)) {
			return content;
		}
		return string + content.replace(string, string + string) + string;
	}

	public void writeRow() {
		ensureOpen();
		out.println();
		first = true;
	}

	private void ensureOpen() {
		if (out == null) {
			throw new IllegalStateException("already closed");
		}
	}

	@Override
	public void close() throws IOException {
		if (out != null) {
			if (!first) {
				out.println();
			}
			out.close();
			out = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

}
