package jkit.ini;

/**
 * Provides a generic enum converter. The enums to create can be set via the
 * constructor.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 * @param <T>
 *            The enum type.
 */
public class EnumConverter<T extends Enum<T>> implements Converter<T> {

	/**
	 * The enum type to create.
	 */
	private final Class<T> enumType;

	/**
	 * Creates an enum converter for the given type.
	 * 
	 * @param enumType
	 *            The enumeration type.
	 */
	public EnumConverter(final Class<T> enumType) {
		this.enumType = enumType;
	}

	@Override
	public T convert(final String s) {
		return Enum.valueOf(enumType, s);
	}

}
