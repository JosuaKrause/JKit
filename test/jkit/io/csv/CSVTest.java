/**
 * 
 */
package jkit.io.csv;

import static jkit.io.csv.CSVTest.EventType.CELL;
import static jkit.io.csv.CSVTest.EventType.COL;
import static jkit.io.csv.CSVTest.EventType.END;
import static jkit.io.csv.CSVTest.EventType.ROW;
import static jkit.io.csv.CSVTest.EventType.START;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class CSVTest {

	public static enum EventType {
		START, END, ROW, COL, CELL;
	}

	private static final class Event {

		private final EventType type;

		private final String content;

		private final String row;

		private final String col;

		public Event(final EventType type) {
			this(type, null);
		}

		public Event(final EventType type, final String content) {
			this(type, content, null, null);
		}

		public Event(final EventType type, final String content,
				final String row, final String col) {
			this.type = type;
			this.content = content;
			this.row = row;
			this.col = col;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Event)) {
				return false;
			}
			final Event e = (Event) obj;
			if (e.type != type) {
				return false;
			}
			if (row != null && e.row != null && !row.equals(e.row)) {
				return false;
			}
			if (col != null && e.col != null && !col.equals(e.col)) {
				return false;
			}
			if (content == null) {
				return e.content == null;
			}
			return content.equals(e.content);
		}

		@Override
		public int hashCode() {
			return 31 * type.hashCode()
					+ (content == null ? 1234 : content.hashCode());
		}

		@Override
		public String toString() {
			return type + (content != null ? "(\"" + content + "\")" : "");
		}

	}

	private final class TestHandler implements CSVHandler {

		private final List<Event> events;

		public TestHandler() {
			events = new ArrayList<Event>();
		}

		public void test(final Event[] es) {
			for (int i = 0; i < es.length; ++i) {
				if (events.size() <= i) {
					final StringBuilder res = new StringBuilder(
							"missing events: ");
					for (int j = i; j < es.length; ++j) {
						if (j != i) {
							res.append(", ");
						}
						res.append(es[j]);
					}
					throw new IllegalStateException(res.toString());
				}
				if (!es[i].equals(events.get(i))) {
					throw new IllegalStateException("expected " + es[i]
							+ " got " + events.get(i));
				}
			}
		}

		@Override
		public void cell(final CSVContext ctx, final String content) {
			events.add(new Event(CELL, content, ctx.rowName(), ctx.colName()));
		}

		@Override
		public void colTitle(final CSVContext ctx, final String title) {
			events.add(new Event(COL, title, ctx.rowName(), ctx.colName()));
		}

		@Override
		public void row(final CSVContext ctx) {
			events.add(new Event(ROW, null, ctx.rowName(), ctx.colName()));
		}

		@Override
		public void rowTitle(final CSVContext ctx, final String title) {
			events.add(new Event(ROW, title, ctx.rowName(), ctx.colName()));
		}

		@Override
		public void start(final CSVContext ctx) {
			events.add(new Event(START, null, ctx.rowName(), ctx.colName()));
		}

		@Override
		public void end(final CSVContext ctx) {
			events.add(new Event(END, null, ctx.rowName(), ctx.colName()));
		}

	}

	private static final String NL = System.getProperty("line.separator");

	private static final String strTest0 = "hallo;\"abc\"; buh ;\r\nbello;;"
			+ "\"ab\"\"cd\";\"wu\r\nff\"\r\ngrr"
			+ "rh;\"te;st\"\rblubb\nblubb;;";

	private static final Event[] evTest0 = new Event[] { new Event(START),
			new Event(ROW), new Event(CELL, "hallo"), new Event(CELL, "abc"),
			new Event(CELL, " buh "), new Event(CELL, ""), new Event(ROW),
			new Event(CELL, "bello"), new Event(CELL, ""),
			new Event(CELL, "ab\"cd"), new Event(CELL, "wu" + NL + "ff"),
			new Event(ROW), new Event(CELL, "grrrh"), new Event(CELL, "te;st"),
			new Event(ROW), new Event(CELL, "blubbblubb"), new Event(CELL, ""),
			new Event(END) };

	private static final String strTest1r = "abc;def;ghi\rjkl;mno;pqr\rstu;vwx;yz_";

	private static final String strTest1rn = "abc;def;ghi\r\njkl;mno;pqr\r\nstu;vwx;yz_\r\n";

	private static final String strTest1n = "abc;def;ghi\njkl;mno;pqr\nstu;vwx;yz_\n";

	private static final Event[] evTest1 = new Event[] { new Event(START),
			new Event(ROW), new Event(CELL, "abc"), new Event(CELL, "def"),
			new Event(CELL, "ghi"), new Event(ROW), new Event(CELL, "jkl"),
			new Event(CELL, "mno"), new Event(CELL, "pqr"), new Event(ROW),
			new Event(CELL, "stu"), new Event(CELL, "vwx"),
			new Event(CELL, "yz_"), new Event(END) };

	private static final String strTest2 = "a\"b;c\"d;e\"f;\"gh\"";

	private static final String strTest3 = "-;c1;\"c2\";c3\nr1;1;2;3\n\"r2\";4;5;\"6\"\n\"r3\";7;8;9\n";

	private static final Event[] evTest3r = new Event[] { new Event(START),
			new Event(ROW, "-"), new Event(CELL, "c1"), new Event(CELL, "c2"),
			new Event(CELL, "c3"), new Event(ROW, "r1"),
			new Event(CELL, "1", "r1", null), new Event(CELL, "2", "r1", null),
			new Event(CELL, "3", "r1", null), new Event(ROW, "r2"),
			new Event(CELL, "4", "r2", null), new Event(CELL, "5", "r2", null),
			new Event(CELL, "6", "r2", null), new Event(ROW, "r3"),
			new Event(CELL, "7", "r3", null), new Event(CELL, "8", "r3", null),
			new Event(CELL, "9", "r3", null), new Event(END) };

	private static final Event[] evTest3c = new Event[] { new Event(START),
			new Event(COL, "-"), new Event(COL, "c1"), new Event(COL, "c2"),
			new Event(COL, "c3"), new Event(ROW),
			new Event(CELL, "r1", null, "c1"),
			new Event(CELL, "1", null, "c2"), new Event(CELL, "2"),
			new Event(CELL, "3", null, "c3"), new Event(ROW),
			new Event(CELL, "r2", null, "c1"),
			new Event(CELL, "4", null, "c2"), new Event(CELL, "5"),
			new Event(CELL, "6", null, "c3"), new Event(ROW),
			new Event(CELL, "r3", null, "c1"),
			new Event(CELL, "7", null, "c2"), new Event(CELL, "8"),
			new Event(CELL, "9", null, "c3"), new Event(END) };

	private static final Event[] evTest3rc = new Event[] { new Event(START),
			new Event(COL, "c1"), new Event(COL, "c2"), new Event(COL, "c3"),
			new Event(ROW, "r1"), new Event(CELL, "1", "r1", "c1"),
			new Event(CELL, "2", "r1", "c2"), new Event(CELL, "3", "r2", "c3"),
			new Event(ROW, "r2"), new Event(CELL, "4", "r2", "c1"),
			new Event(CELL, "5", "r2", "c2"), new Event(CELL, "6", "r2", "c3"),
			new Event(ROW, "r3"), new Event(CELL, "7", "r3", "c1"),
			new Event(CELL, "8", "r3", "c2"), new Event(CELL, "9", "r3", "c3"),
			new Event(END) };

	private static final Event[] evTest2 = new Event[] { new Event(START),
			new Event(ROW), new Event(CELL, "a\"b"), new Event(CELL, "c\"d"),
			new Event(CELL, "e\"f"), new Event(CELL, "gh"), new Event(END) };

	private void doTest(final CSVReader reader, final String in,
			final Event[] valid) throws Exception {
		final TestHandler th = new TestHandler();
		reader.setHandler(th);
		reader.read(new StringReader(in));
		th.test(valid);
	}

	@Test
	public void test0() throws Exception {
		doTest(new CSVReader(), strTest0, evTest0);
	}

	@Test
	public void test1() throws Exception {
		doTest(new CSVReader(), strTest1r, evTest1);
		doTest(new CSVReader(), strTest1rn, evTest1);
		doTest(new CSVReader(), strTest1n, evTest1);
	}

	@Test
	public void test2() throws Exception {
		doTest(new CSVReader(), strTest2, evTest2);
	}

	public void test3() throws Exception {
		final CSVReader csv = new CSVReader();
		csv.setReadRowTitles(true);
		doTest(csv, strTest3, evTest3r);
		csv.setReadColTitles(true);
		doTest(csv, strTest3, evTest3rc);
		csv.setReadRowTitles(false);
		doTest(csv, strTest3, evTest3c);
	}

}
