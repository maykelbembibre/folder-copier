package folder_copier.logic.alphanumeric;
import java.util.Objects;

/**
 * A comparable list of numeric strings.
 */
public class AlphanumericStringList extends ComparableList<AlphanumericString> {

	private static final long serialVersionUID = -5634072556736886155L;

	/**
	 * Creates a comparable list of numeric strings by dividing up the given string
	 * into chunks.
	 * @param string A string.
	 */
	public AlphanumericStringList(String string) {
		Objects.requireNonNull(string);
		int pos = 0;
		char currentChar;
		StringBuilder token = null;
		Boolean isPreviousNumber = null;
		boolean isCurrentNumber;
		while (pos < string.length()) {
			currentChar = string.charAt(pos);
			isCurrentNumber = currentChar >= '0' && currentChar <= '9'; 
			if (token == null || isPreviousNumber == null) {
				token = new StringBuilder();
				token.append(currentChar);
			} else {
				if (isCurrentNumber != isPreviousNumber) {
					this.add(new AlphanumericString(token.toString()));
					token = new StringBuilder();
				}
				token.append(currentChar);
			}
			isPreviousNumber = isCurrentNumber;
			pos++;
		}
		if (!token.isEmpty()) {
			this.add(new AlphanumericString(token.toString()));
		}
	}
}
