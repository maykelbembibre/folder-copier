package folder_copier.logic.models;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that aggregates file copy results.
 */
public class FileCopyResults {

	private final Map<FileCopyAction, PathCollection> results = new HashMap<>();

	/**
	 * Returns all the files that have been affected by the given
	 * file copy action.
	 * @param action The file copy action.
	 * @return The affected files as a path collection.
	 */
	public PathCollection getAffectedFiles(FileCopyAction action) {
		PathCollection result = this.results.get(action);
		if (result == null) {
			result = new PathCollection();
		}
		return result;
	}
	
	/**
	 * Returns the number of occurrences of the given result.
	 * @param result The result.
	 * @return The number of occurrences.
	 */
	public int getNumberOfOccurrences(FileCopyAction action) {
		PathCollection pathCollection = this.getAffectedFiles(action);
		return pathCollection.getFilePaths().size();
	}
	
	/**
	 * Adds an occurrence of a result.
	 * @param result The result.
	 */
	public void add(FileCopyResult result) {
		FileCopyAction action = result.getAction();
		PathCollection pathCollection = this.results.get(action);
		if (pathCollection == null) {
			pathCollection = new PathCollection();
			this.results.put(action, pathCollection);
		}
		pathCollection.addPath(result.getAffectedFilePath());
	}
	
	/**
	 * Adds many instances of the same action for all the given files.
	 * @param files The files.
	 * @param result The action.
	 */
	public void addMany(Collection<Path> files, FileCopyAction action) {
		for (Path file : files) {
			this.add(new FileCopyResult(file, action));
		}
	}
}
