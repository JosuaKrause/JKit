/**
 * 
 */
package jkit.io.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class CSVReader {

	private static final String NL = System.getProperty("line.separator");

	private final class Context implements CSVContext {

		private final List<String> colNames;

		private final boolean hasRowNames;

		private int col;

		private int row;

		private String rowName;

		public Context(final boolean hasColNames, final boolean hasRowNames) {
			this.hasRowNames = hasRowNames;
			colNames = hasColNames ? new LinkedList<String>() : null;
			rowName = null;
			row = hasColNames ? -1 : 0;
			col = hasRowNames ? -1 : 0;
		}

		public void addColName(final String name) {
			colNames.add(name);
		}

		public void setRowName(final String rowName) {
			this.rowName = rowName;
		}

		public void nextCell() {
			++col;
		}

		public void nextRow() {
			++row;
			col = hasRowNames ? -1 : 0;
		}

		@Override
		public int col() {
			return col;
		}

		@Override
		public String colName() {
			if (col < 0) {
				return null;
			}
			return colNames != null ? colNames.get(col) : "" + col;
		}

		@Override
		public int row() {
			return row;
		}

		@Override
		public String rowName() {
			return rowName != null ? rowName : "" + row;
		}

		@Override
		public CSVReader reader() {
			return CSVReader.this;
		}

		@Override
		public String toString() {
			return "ctx[" + row + ":" + col + "](" + rowName() + ":"
					+ colName() + ")";
		}

	}

	private final char delimiter;

	private final char string;

	private boolean colTitle;

	private boolean rowTitle;

	private CSVHandler handler;

	public CSVReader() {
		this(';', '"');
	}

	public CSVReader(final char delimiter, final char string) {
		this(delimiter, string, false, false);
	}

	public CSVReader(final char delimiter, final char string,
			final boolean columnTitles, final boolean rowTitles) {
		this.delimiter = delimiter;
		this.string = string;
		rowTitle = rowTitles;
		colTitle = columnTitles;
		handler = null;
	}

	public void read(final Reader r) throws IOException {
		if (handler == null) {
			throw new IllegalStateException("handler needs to be set first");
		}
		final CSVHandler hnd = handler;
		final Context ctx = new Context(colTitle, rowTitle);
		hnd.start(ctx);
		char ignore = 0x0;
		char line = 0x0;
		boolean canString = true;
		boolean isString = false;
		boolean endString = false;
		boolean afterLn = false;
		StringBuilder current = null;
		int i;
		while ((i = r.read()) != -1) {
			if (current == null) {
				current = new StringBuilder();
			}
			final char c = (char) i;
			if (c == ignore && ignore != 0x0) {
				continue;
			}
			afterLn = false;
			if (line == 0x0 && (c == '\r' || c == '\n')) {
				line = c;
				ignore = (c == '\r') ? '\n' : '\r';
			}
			if (c == string) {
				if (!endString) {
					if (!isString) {
						if (canString) {
							isString = true;
							continue;
						}
					} else {
						endString = true;
						continue;
					}
				}
				endString = false;
			} else if (endString) {
				endString = false;
				isString = false;
			}
			if (c == delimiter && !isString) {
				handle(hnd, current.toString(), ctx);
				current = null;
				canString = true;
				continue;
			}
			canString = false;
			if (c == line) {
				if (isString) {
					current.append(NL);
				} else {
					handle(hnd, current.toString(), ctx);
					line(ctx);
					canString = true;
					current = null;
				}
				afterLn = true;
				continue;
			}
			current.append(c);
		}
		if (current != null && (current.length() > 0 || !afterLn)) {
			handle(hnd, current.toString(), ctx);
		}
		hnd.end(ctx);
	}

	private void handle(final CSVHandler hnd, final String content,
			final Context ctx) {
		switch (ctx.col()) {
		case -1:
			if (ctx.row() < 0) {
				break;
			}
			ctx.setRowName(content);
			hnd.rowTitle(ctx, content);
			break;
		case 0:
			if (ctx.row() >= 0) {
				hnd.row(ctx);
			}
			// no break
		default:
			if (ctx.row() < 0) {
				ctx.addColName(content);
				hnd.colTitle(ctx, content);
				break;
			}
			hnd.cell(ctx, content);
			break;
		}
		ctx.nextCell();
	}

	private void line(final Context ctx) {
		ctx.nextRow();
	}

	public void setHandler(final CSVHandler handler) {
		this.handler = handler;
	}

	public void setReadColTitles(final boolean colTitle) {
		this.colTitle = colTitle;
	}

	public boolean readColTitles() {
		return colTitle;
	}

	public void setReadRowTitles(final boolean rowTitle) {
		this.rowTitle = rowTitle;
	}

	public boolean readRowTitles() {
		return rowTitle;
	}

}
