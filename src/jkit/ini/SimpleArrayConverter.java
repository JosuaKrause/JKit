package jkit.ini;


public class SimpleArrayConverter<T> extends ArrayConverterAdapter<T> {

	private final Converter<T> converter;

	public SimpleArrayConverter(final Converter<T> converter,
			final T[] defaultValue, final char delimiter) {
		this(converter, defaultValue, "" + delimiter);
	}

	public SimpleArrayConverter(final Converter<T> converter,
			final T[] defaultValue) {
		this(converter, defaultValue, IniReader.DEFAULT_DELIMITER);
	}

	public SimpleArrayConverter(final Converter<T> converter,
			final T[] defaultValue, final String delimiter) {
		super(defaultValue, delimiter);
		this.converter = converter;
	}

	@Override
	public T convert(final String s) {
		return converter.convert(s);
	}

}
