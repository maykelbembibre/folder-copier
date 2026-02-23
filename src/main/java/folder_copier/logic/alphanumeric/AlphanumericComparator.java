package folder_copier.logic.alphanumeric;
import java.util.Comparator;

/**
 * String comparator that compares strings both lexicographically and numerically.
 */
public class AlphanumericComparator implements Comparator<String> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(String o1, String o2) {
		return new AlphanumericStringList(o1).compareTo(new AlphanumericStringList(o2));
	}
}
