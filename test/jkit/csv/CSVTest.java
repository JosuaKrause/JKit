/**
 * 
 */
package jkit.csv;

import static jkit.csv.CSVTest.EventType.CELL;
import static jkit.csv.CSVTest.EventType.COL;
import static jkit.csv.CSVTest.EventType.END;
import static jkit.csv.CSVTest.EventType.ROW;
import static jkit.csv.CSVTest.EventType.START;

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

		public Event(final EventType type) {
			this(type, null);
		}

		public Event(final EventType type, final String content) {
			this.type = type;
			this.content = content;
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
			events.add(new Event(CELL, content));
		}

		@Override
		public void colTitle(final CSVContext ctx, final String title) {
			events.add(new Event(COL, title));
		}

		@Override
		public void row(final CSVContext ctx) {
			events.add(new Event(ROW, null));
		}

		@Override
		public void rowTitle(final CSVContext ctx, final String title) {
			events.add(new Event(ROW, title));
		}

		@Override
		public void start(final CSVContext ctx) {
			events.add(new Event(START, null));
		}

		@Override
		public void end(final CSVContext ctx) {
			events.add(new Event(END, null));
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

}
