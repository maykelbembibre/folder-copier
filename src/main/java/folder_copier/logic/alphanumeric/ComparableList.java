package folder_copier.logic.alphanumeric;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A list that can be compared to other lists.
 * @param <E> the type of elements in this list.
 */
public class ComparableList<T extends Comparable<T>> extends ArrayList<T> implements Comparable<List<T>> {

	private static final long serialVersionUID = -2795971504216385936L;

	/**
	 * Creates an empty comparable list.
	 */
	public ComparableList() {}
	
	/**
	 * Creates a comparable list from a collection.
	 * @param from A collection.
	 */
	public ComparableList(Collection<T> from) {
		super(from);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(List<T> o) {
		int result;
		if (o == null) {
			result = 1;
		} else {
			result = compareTo(this, o);
		}
		return result;
	}

	private T get(List<T> list, int index) {
		T result;
		if (index < list.size()) {
			result = list.get(index);
		} else {
			result = null;
		}
		return result;
	}
	
	private int compareTo(List<T> a, List<T> b) {
		int result = 0;
		int index = 0;
		T itemA, itemB;
		while ((index < a.size() || index < b.size()) && result == 0) {
			itemA = this.get(a, index);
			itemB = this.get(b, index);
			if (itemA == null) {
				if (itemB == null) {
					result = 0;
				} else {
					result = -1;
				}
			} else if (itemB == null) {
				result = 1;
			} else {
				result = itemA.compareTo(itemB);
			}
			index++;
		}
		return result;
	}
}
