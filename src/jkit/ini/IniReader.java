package jkit.ini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import jkit.convert.ArrayConverter;
import jkit.convert.Converter;

/**
 * Reads out an INI file and presents its information as a simple name value
 * pair.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class IniReader {

	/**
	 * The name of the utf8 charset.
	 */
	public static final String UTF8_STR = "UTF-8";

	/**
	 * The name of an entry.
	 * 
	 * @author Joschi <josua.krause@googlemail.com>
	 * 
	 */
	private static class Entry {
		/* the area name */
		final String area;

		/* the actual name */
		final String name;

		/**
		 * Creates a new Entry.
		 * 
		 * @param area
		 *            The area name.
		 * @param name
		 *            The actual name.
		 */
		Entry(final String area, final String name) {
			this.name = name;
			this.area = area;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Entry)) {
				return false;
			}
			final Entry e = (Entry) obj;
			return e.name.equals(name) && e.area.equals(area);
		}

		@Override
		public int hashCode() {
			return area.hashCode() * 31 + name.hashCode();
		}
	}

	/**
	 * Creates an INI reader from a specific file.
	 * 
	 * @param f
	 *            The file to interpret as INI.
	 * @return The newly created {@link IniReader}.
	 * @throws FileNotFoundException
	 *             If this file was not found.
	 */
	public static IniReader createIniReader(final File f)
			throws FileNotFoundException {
		final IniReader r = new IniReader(new Scanner(f));
		r.setFile(f);
		return r;
	}

	/**
	 * Guarantees that an IniReader for a given file is created even when the
	 * file does not exist.
	 * 
	 * @param f
	 *            The file.
	 * @param autoLearn
	 *            Whether the IniReader should learn from default values. This
	 *            option can be hazardous regarding fields that are interpreted
	 *            in multiple ways.
	 * 
	 * @return The reader for the file.
	 * 
	 * @see #setAutoLearn(boolean)
	 * @see #createIniReader()
	 * @see #createIniReader(File)
	 */
	public static IniReader createFailProofIniReader(final File f,
			final boolean autoLearn) {
		IniReader r;
		try {
			r = createIniReader(f);
		} catch (final FileNotFoundException e) {
			r = createIniReader();
			r.setFile(f);
		}
		r.setAutoLearn(autoLearn);
		return r;
	}

	/**
	 * Creates an empty INI reader.
	 * 
	 * @return The newly created {@link IniReader}.
	 */
	public static IniReader createIniReader() {
		return new IniReader();
	}

	/**
	 * Writes the content of this IniReader to the associated file.
	 * 
	 * @throws IllegalStateException
	 *             When there is no associated file.
	 * @throws IOException
	 *             When there is a problem with the associated file.
	 * 
	 * @see #writeIni(PrintWriter)
	 */
	public void writeIni() throws IllegalStateException, IOException {
		final File file = this.file;
		if (file == null) {
			throw new IllegalStateException("no associated file");
		}
		final PrintWriter pw = new PrintWriter(file, UTF8_STR);
		writeIni(pw);
		pw.close();
	}

	/**
	 * Writes the information represented from the IniReader to the given
	 * {@link PrintWriter}. Note that any comments present in the original INI
	 * file are lost.
	 * 
	 * @param pw
	 *            The output writer.
	 */
	public void writeIni(final PrintWriter pw) {
		parse();
		final Comparator<Entry> cmp = new Comparator<Entry>() {
			@Override
			public int compare(final Entry o1, final Entry o2) {
				final int cmp = o1.area.compareTo(o2.area);
				if (cmp != 0) {
					return cmp;
				}
				return o1.name.compareTo(o2.name);
			}
		};
		// gather areas
		final Map<Entry, String> map = entries;
		final Map<String, SortedSet<Entry>> areas = new HashMap<String, SortedSet<Entry>>();
		for (final Entry e : map.keySet()) {
			final String area = e.area;
			if (!areas.containsKey(area)) {
				areas.put(area, new TreeSet<Entry>(cmp));
			}
			areas.get(area).add(e);
		}
		// write output
		final Object[] objAreas = areas.keySet().toArray();
		Arrays.sort(objAreas);
		for (final Object area : objAreas) {
			// relying on the toString() method of String
			pw.println("[" + area + "]");
			for (final Entry e : areas.get(area)) {
				pw.print(e.name);
				pw.print('=');
				String line = map.get(e);
				if (line.contains("#")) {
					line = line.replace("#", "##");
				}
				pw.println(line);
			}
			pw.println();
		}
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param value
	 *            The value.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect.
	 */
	public void set(final String area, final String name, final String value) {
		if (area.contains("]")) {
			throw new IllegalArgumentException("invalud area name: " + area);
		}
		if (name.contains("=")) {
			throw new IllegalArgumentException("invalid name: " + name);
		}
		entries.put(new Entry(area.trim(), name.trim()), value.trim());
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param value
	 *            The integer value.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect.
	 */
	public void setInteger(final String area, final String name, final int value) {
		set(area, name, "" + value);
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param value
	 *            The long value.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect.
	 */
	public void setLong(final String area, final String name, final long value) {
		set(area, name, "" + value);
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param value
	 *            The boolean value.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect.
	 */
	public void setBoolean(final String area, final String name,
			final boolean value) {
		set(area, name, "" + value);
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param value
	 *            The float value.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect.
	 */
	public void setFloat(final String area, final String name, final float value) {
		set(area, name, "" + value);
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param value
	 *            The double value.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect.
	 */
	public void setDouble(final String area, final String name,
			final double value) {
		set(area, name, "" + value);
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param obj
	 *            The object.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect.
	 */
	public void setObject(final String area, final String name, final Object obj) {
		set(area, name, obj.toString());
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param arr
	 *            The array content -- note that all elements get trimmed.
	 * @param delimiter
	 *            The delimiter to use.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect or when the array
	 *             contained the delimiter.
	 */
	public void setArray(final String area, final String name,
			final Object[] arr, final String delimiter) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final Object obj : arr) {
			if (first) {
				first = false;
			} else {
				sb.append(delimiter);
			}
			final String str = obj.toString().trim();
			if (str.contains(delimiter)) {
				throw new IllegalArgumentException("\"" + str
						+ "\" contains the delimiter \"" + delimiter + "\"");
			}
			sb.append(str);
		}
		set(area, name, sb.toString());
	}

	/**
	 * Sets a field in the INI file.
	 * 
	 * Note that all Strings will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param arr
	 *            The array content -- note that all elements get trimmed. The
	 *            default delimiter is used.
	 * @throws IllegalArgumentException
	 *             When the area or the name is incorrect or when the array
	 *             contained the default delimiter.
	 * 
	 * @see #DEFAULT_DELIMITER
	 */
	public void setArray(final String area, final String name,
			final Object[] arr) {
		setArray(area, name, arr, DEFAULT_DELIMITER);
	}

	/**
	 * Returns the associated value to the given name.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return The value or an empty String if it was not set.
	 */
	public String get(final String area, final String name) {
		return get(area, name, "");
	}

	/**
	 * Returns the associated value to the given name or the given default value
	 * if the requested value does not exist.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param defaultValue
	 *            The default value.
	 * @return The value or the default value if it was not set.
	 */
	public String get(final String area, final String name,
			final String defaultValue) {
		parse();
		final String res = entries.get(new Entry(area, name));
		if (autoLearn && res == null) {
			set(area, name, defaultValue);
		}
		return res == null ? defaultValue : res;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return If the given name has an associated value.
	 */
	public boolean has(final String area, final String name) {
		parse();
		return entries.containsKey(new Entry(area, name));
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return If the given name has a numerical value.
	 */
	public boolean hasInteger(final String area, final String name) {
		if (!has(area, name)) {
			return false;
		}
		final Integer i = getInteger0(area, name);
		return i != null;
	}

	/* the integer wormhole */
	private Integer getInteger0(final String area, final String name) {
		final String res = get(area, name);
		Integer i = null;
		try {
			i = Integer.parseInt(res);
		} catch (final NumberFormatException e) {
			// ignore malformed numbers
		}
		return i;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return The value of the given name interpreted as number or 0 if it
	 *         could not be interpreted as Integer.
	 */
	public int getInteger(final String area, final String name) {
		return getInteger(area, name, 0);
	}

	/**
	 * Returns the associated value to the given name or the given default value
	 * if the requested value does not exist.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param defaultValue
	 *            The default value.
	 * @return The integer value or the default value if it was not set.
	 */
	public int getInteger(final String area, final String name,
			final int defaultValue) {
		final Integer i = getInteger0(area, name);
		if (autoLearn && i == null) {
			setInteger(area, name, defaultValue);
		}
		return i == null ? defaultValue : i;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return If the given name can be interpreted as a long number.
	 */
	public boolean hasLong(final String area, final String name) {
		if (!has(area, name)) {
			return false;
		}
		final Long l = getLong0(area, name);
		return l != null;
	}

	/* the long wormhole */
	private Long getLong0(final String area, final String name) {
		final String res = get(area, name);
		Long l = null;
		try {
			l = Long.parseLong(res);
		} catch (final NumberFormatException e) {
			// ignore malformed numbers
		}
		return l;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return The long interpretation of the value from the given name or
	 *         <code>0L</code> if the value could not be interpreted in such a
	 *         way.
	 */
	public long getLong(final String area, final String name) {
		return getLong(area, name, 0L);
	}

	/**
	 * Returns the associated value to the given name or the given default value
	 * if the requested value does not exist.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param defaultValue
	 *            The default value.
	 * @return The long value or the default value if it was not set.
	 */
	public long getLong(final String area, final String name,
			final long defaultValue) {
		final Long l = getLong0(area, name);
		if (autoLearn && l == null) {
			setLong(area, name, defaultValue);
		}
		return l == null ? defaultValue : l;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return If the given name has a small floating decimal value.
	 */
	public boolean hasFloat(final String area, final String name) {
		if (!has(area, name)) {
			return false;
		}
		final Float f = getFloat0(area, name);
		return f != null;
	}

	/* the float wormhole */
	private Float getFloat0(final String area, final String name) {
		final String res = get(area, name);
		Float f = null;
		try {
			f = Float.parseFloat(res);
		} catch (final NumberFormatException e) {
			// ignore malformed numbers
		}
		return f;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return The value of the given name interpreted as small floating decimal
	 *         or {@code 0.0f} if it could not be interpreted as Float.
	 */
	public float getFloat(final String area, final String name) {
		return getFloat(area, name, 0f);
	}

	/**
	 * Returns the associated value to the given name or the given default value
	 * if the requested value does not exist.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param defaultValue
	 *            The default value.
	 * @return The float value or the default value if it was not set.
	 */
	public float getFloat(final String area, final String name,
			final float defaultValue) {
		final Float f = getFloat0(area, name);
		if (autoLearn && f == null) {
			setFloat(area, name, defaultValue);
		}
		return f == null ? defaultValue : f;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return If the given name has a floating decimal value.
	 */
	public boolean hasDouble(final String area, final String name) {
		if (!has(area, name)) {
			return false;
		}
		final Double d = getDouble0(area, name);
		return d != null;
	}

	/* the double wormhole */
	private Double getDouble0(final String area, final String name) {
		final String res = get(area, name);
		Double d = null;
		try {
			d = Double.parseDouble(res);
		} catch (final NumberFormatException e) {
			// ignore malformed numbers
		}
		return d;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return The value of the given name interpreted as floating decimal or
	 *         {@code 0.0} if it could not be interpreted as Double.
	 */
	public double getDouble(final String area, final String name) {
		return getDouble(area, name, 0.0);
	}

	/**
	 * Returns the associated value to the given name or the given default value
	 * if the requested value does not exist.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param defaultValue
	 *            The default value.
	 * @return The double value or the default value if it was not set.
	 */
	public double getDouble(final String area, final String name,
			final double defaultValue) {
		final Double d = getDouble0(area, name);
		if (autoLearn && d == null) {
			setDouble(area, name, defaultValue);
		}
		return d == null ? defaultValue : d;
	}

	/**
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return Interprets the value of the given name as boolean. There are four
	 *         steps. If the value does not exist return <code>false</code>. If
	 *         it exists and it can be interpreted as a number return
	 *         <code>true</code> if the number is not 0. If the value can not be
	 *         interpreted as number test whether it is "true" or "false" and
	 *         return this as result. If none of the preceding steps come to an
	 *         result return <code>true</code> if the String is not empty.
	 */
	public boolean getBoolean(final String area, final String name) {
		if (hasLong(area, name)) {
			return getLong(area, name) == 0;
		}
		if (hasDouble(area, name)) {
			return getDouble(area, name) == 0.0;
		}
		final String res = get(area, name);
		if (res.equals("false")) {
			return false;
		}
		if (autoLearn && res.isEmpty()) {
			setBoolean(area, name, false);
		}
		return !res.isEmpty();
	}

	/**
	 * Converts the String content of the field in an Object.
	 * 
	 * @param <T>
	 *            The type of the Object.
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param converter
	 *            The converter to convert the String into the Object.
	 * @return The object or <code>null</code> if the conversion has failed or
	 *         the field was empty.
	 * 
	 * @see Converter
	 */
	public <T> T getObject(final String area, final String name,
			final Converter<T> converter) {
		return getObject0(area, name, converter);
	}

	/**
	 * Converts the String content of the field in an Object.
	 * 
	 * @param <T>
	 *            The type of the Object.
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param converter
	 *            The converter to convert the String into the Object.
	 * @param defaultValue
	 *            The String to convert if the original conversion failed or the
	 *            field was empty.
	 * @return The object.
	 * 
	 * @see Converter
	 */
	public <T> T getObject(final String area, final String name,
			final Converter<T> converter, final String defaultValue) {
		final T res = getObject(area, name, converter);
		if (autoLearn && res == null) {
			setObject(area, name, defaultValue);
		}
		return res != null ? res : converter.convert(defaultValue);
	}

	/**
	 * Converts the String content of the field in an Object.
	 * 
	 * @param <T>
	 *            The type of the Object.
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param converter
	 *            The converter to convert the String into the Object.
	 * @param defaultValue
	 *            The Object to return if the original conversion failed or the
	 *            field was empty.
	 * @return The object.
	 * 
	 * @see Converter
	 */
	public <T> T getObject(final String area, final String name,
			final Converter<T> converter, final T defaultValue) {
		final T res = getObject(area, name, converter);
		if (autoLearn && res == null) {
			setObject(area, name, defaultValue);
		}
		return res != null ? res : defaultValue;
	}

	/* The object worm hole */
	private <T> T getObject0(final String area, final String name,
			final Converter<T> converter) {
		if (!has(area, name)) {
			return null;
		}
		final String str = get(area, name);
		return converter.convert(str);
	}

	/**
	 * Tests whether the String at the given field can be converted via the
	 * given converter.
	 * 
	 * @param <T>
	 *            The type of the conversion.
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param converter
	 *            The converter to test the String for.
	 * @return Whether the String can be converted.
	 * 
	 * @see Converter
	 */
	public <T> boolean hasObject(final String area, final String name,
			final Converter<T> converter) {
		return getObject0(area, name, converter) != null;
	}

	/**
	 * Returns a field interpreted as an array of arbitrary objects. The
	 * original String is split by the delimiter and the results are converted
	 * via the given converter.
	 * 
	 * @param <T>
	 *            The component type of the resulting array.
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param converter
	 *            The converter for the array.
	 * @return The resulting array.
	 * 
	 * @see ArrayConverter
	 */
	public <T> T[] getArray(final String area, final String name,
			final ArrayConverter<T> converter) {
		if (!has(area, name)) {
			return malformedArray(area, name, converter);
		}
		final String[] strings = getArray(area, name, converter.delimiter());
		int i = strings.length;
		final T[] res = converter.array(i);
		while (--i >= 0) {
			final T cur = converter.convert(strings[i]);
			if (cur == null) {
				return malformedArray(area, name, converter);
			}
			res[i] = cur;
		}
		return res;
	}

	// if the error could not be converted
	private <T> T[] malformedArray(final String area, final String name,
			final ArrayConverter<T> converter) {
		final T[] defaultValue = converter.defaultValue();
		if (autoLearn) {
			setArray(area, name, defaultValue, converter.delimiter());
		}
		return defaultValue;
	}

	/**
	 * Returns a field interpreted as an array. The original String is split by
	 * the delimiter and the results are the resulting Strings. The result will
	 * not contain the delimiter. Note that the Strings of the result will be
	 * trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param delimiter
	 *            The delimiter used to determine fields of the array.
	 * @param defaultValue
	 *            The default value, if the given field does not exist.
	 * @return The resulting array.
	 */
	public String[] getArray(final String area, final String name,
			final String delimiter, final String[] defaultValue) {
		if (!has(area, name)) {
			if (autoLearn) {
				setArray(area, name, defaultValue, delimiter);
			}
			return defaultValue;
		}
		final String content = get(area, name);
		final String[] arr = content.split(delimiter);
		int i = arr.length;
		final String[] res = new String[i];
		while (--i >= 0) {
			res[i] = arr[i].trim();
		}
		return res;
	}

	/**
	 * The default delimiter for the {@link #getArray(String, String)} method.
	 * 
	 * @see #getArray(String, String)
	 * @see #getArray(String, String, String[])
	 */
	public static final String DEFAULT_DELIMITER = ",";

	/**
	 * The empty String array.
	 */
	private static final String[] EMPTY_ARR = new String[0];

	/**
	 * Returns a field interpreted as an array. The original String is split by
	 * the delimiter and the results are the resulting Strings. The result will
	 * not contain the delimiter. Note that the Strings of the result will be
	 * trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param delimiter
	 *            The delimiter used to determine fields of the array.
	 * @return The resulting array or the empty array if the field was not
	 *         present.
	 */
	public String[] getArray(final String area, final String name,
			final String delimiter) {
		return getArray(area, name, delimiter, EMPTY_ARR);
	}

	/**
	 * Returns a field interpreted as an array. The original String is split by
	 * the default delimiter and the results are the resulting Strings. The
	 * result will not contain the default delimiter. Note that the Strings of
	 * the result will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @param defaultValue
	 *            The default value, if the given field does not exist.
	 * @return The resulting array.
	 * 
	 * @see #DEFAULT_DELIMITER
	 */
	public String[] getArray(final String area, final String name,
			final String[] defaultValue) {
		return getArray(area, name, DEFAULT_DELIMITER, defaultValue);
	}

	/**
	 * Returns a field interpreted as an array. The original String is split by
	 * the default delimiter and the results are the resulting Strings. The
	 * result will not contain the default delimiter. Note that the Strings of
	 * the result will be trimmed.
	 * 
	 * @param area
	 *            The area name. The name in [box brackets].
	 * @param name
	 *            The actual name of the value.
	 * @return The resulting array or the empty array if the field was not
	 *         present.
	 * 
	 * @see #DEFAULT_DELIMITER
	 */
	public String[] getArray(final String area, final String name) {
		return getArray(area, name, EMPTY_ARR);
	}

	/* the map of the entries */
	private final Map<Entry, String> entries;

	/* whether to automatically learn from default values */
	private boolean autoLearn;

	/* the associated file */
	private File file;

	/* the INI scanner */
	private Scanner scanner;

	/* the current area or null if finished scanning */
	private String area;

	/* private constructor */
	private IniReader(final Scanner s) {
		entries = new HashMap<Entry, String>();
		autoLearn = false;
		scanner = s;
		area = "";
		file = null;
	}

	/* private default constructor */
	private IniReader() {
		entries = new HashMap<Entry, String>();
		autoLearn = false;
		scanner = null;
		area = null;
		file = null;
	}

	/**
	 * @param autoLearn
	 *            Whether the IniReader should learn from default values. This
	 *            option can be hazardous regarding fields that are interpreted
	 *            in multiple ways.
	 * 
	 *            <p>
	 *            <code>
	 * 				IniReader ini = IniReader.createIniReader();<br />
	 * 				ini.set("foo", "bar", "baz");<br />
	 * 				ini.getLong("foo", "bar", 15);<br />
	 * 				ini.get("foo", "bar"); // returns 15 instead of "baz"!<br />
	 * 			  </code>
	 *            </p>
	 */
	public void setAutoLearn(final boolean autoLearn) {
		this.autoLearn = autoLearn;
	}

	/**
	 * Sets the associated file.
	 * 
	 * @param file
	 *            The file.
	 */
	public void setFile(final File file) {
		this.file = file;
	}

	/* parses the input file */
	private void parse() {
		if (scanner == null) {
			return;
		}
		while (scanner.hasNextLine()) {
			interpret(scanner.nextLine());
		}
		scanner.close();
		scanner = null;
		area = null;
	}

	/* uncomments a line and trims its endings */
	private String uncommentAndTrim(final String line) {
		int cur = 0;
		while (cur < line.length()) {
			final int pos = line.indexOf('#', cur);
			if (pos < 0) {
				return line;
			}
			final int doublepos = line.indexOf("##", cur);
			if (pos < doublepos || doublepos < 0) {
				return line.substring(0, pos);
			}
			cur = doublepos + 2;
		}
		return line;
	}

	/* interprets a line */
	private void interpret(String line) {
		line = uncommentAndTrim(line).replace("##", "#");
		if (line.startsWith("[")) {
			final int end = line.indexOf(']');
			if (end < 0) {
				throw new IllegalArgumentException("no area definition: "
						+ line);
			}
			area = line.substring(1, end).trim();
			return;
		}
		if (line.isEmpty()) {
			return;
		}
		final String[] eq = line.split("=", 2);
		if (eq.length == 1) {
			return;
		}
		entries.put(new Entry(area, eq[0].trim()), eq[1].trim());
	}

}
