package folder_copier.logic.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that aggregates file copy results.
 */
public class FileCopyResults {

	private final Map<FileCopyResult, Integer> results = new HashMap<>();
	
	/**
	 * Returns the number of occurrences of the given result.
	 * @param result The result.
	 * @return The number of occurrences.
	 */
	public int get(FileCopyResult result) {
		Integer count = this.results.get(result);
		if (count == null) {
			count = 0;
		}
		return count;
	}
	
	/**
	 * Adds an occurrence of a result.
	 * @param result The result.
	 */
	public void add(FileCopyResult result) {
		this.results.put(result, this.get(result) + 1);
	}
}
