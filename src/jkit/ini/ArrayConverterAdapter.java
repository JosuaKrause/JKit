package jkit.ini;

import java.util.Arrays;

public abstract class ArrayConverterAdapter<T> implements ArrayConverter<T> {

	private final String delimiter;

	private final T[] defaultValue;

	private final T[] arrayHelper;

	public ArrayConverterAdapter(final T[] defaultValue, final char delimiter) {
		this(defaultValue, "" + delimiter);
	}

	public ArrayConverterAdapter(final T[] defaultValue) {
		this(defaultValue, IniReader.DEFAULT_DELIMITER);
	}

	public ArrayConverterAdapter(final T[] defaultValue, final String delimiter) {
		this.defaultValue = defaultValue;
		this.delimiter = delimiter;
		arrayHelper = Arrays.copyOf(defaultValue, 0);
	}

	@Override
	public T[] defaultValue() {
		return defaultValue;
	}

	@Override
	public T[] array(final int length) {
		return Arrays.copyOf(arrayHelper, length);
	}

	@Override
	public String delimiter() {
		return delimiter;
	}

}
