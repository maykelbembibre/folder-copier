package folder_copier.logic.alphanumeric;
import java.util.Objects;

/**
 * A string that can be compared both lexicographically and
 * numerically.
 */
public class AlphanumericString implements Comparable<AlphanumericString> {

	private final String string;

	/**
	 * Creates a new numeric string from a plain Java {@link String}.
	 * @param string The plain Java {@link String}. It must not be <code>null</code>.
	 */
	public AlphanumericString(String string) {
		Objects.requireNonNull(string);
		this.string = string;
	}

	/**
	 * If this numeric string an the other represent numbers, they are compared as numbers.
	 * Otherwise, they are compared lexicographically.
	 * @param o The other numeric string.
	 * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(AlphanumericString o) {
		int result;
		if (o == null) {
			result = 1;
		} else {
			result = this.compareTo(this.string, o.string);
		}
		return result;
	}
	
	/**
	 * Returns the plain Java {@link String} of this numeric string.
	 * @return The plain Java {@link String}.
	 */
	@Override
	public String toString() {
		return this.string;
	}
	
	private int compareTo(String a, String b) {
		int result;
		try {
			Integer aInteger = Integer.valueOf(a);
			Integer bInteger = Integer.valueOf(b);
			result = aInteger.compareTo(bInteger);
		} catch (NumberFormatException e) {
			result = a.compareToIgnoreCase(b);
		}
		return result;
	}
}
