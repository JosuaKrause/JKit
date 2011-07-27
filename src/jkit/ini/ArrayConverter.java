package jkit.ini;

/**
 * An extension to the {@link Converter}-Interface for array conversions.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 * @param <T>
 *            The type of the object.
 * @see Converter
 */
public interface ArrayConverter<T> extends Converter<T> {

	/**
	 * Creates a new array of the needed type and the given length.
	 * 
	 * @param length
	 *            The length of the new array.
	 * @return The array.
	 */
	T[] array(int length);

	/**
	 * @return The default value. It is returned when the field is not set or
	 *         the Strings in the array cannot be converted.
	 */
	T[] defaultValue();

	/**
	 * @return The delimiter to separate array entries.
	 * @see IniReader#DEFAULT_DELIMITER
	 */
	String delimiter();

}
