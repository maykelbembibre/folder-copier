package folder_copier.logic.models;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class that represents a collection of absolute paths.
 */
public class PathCollection {

	private final List<Path> filePaths = new ArrayList<>();
	private final List<Path> directoryPaths = new ArrayList<>();
	private final List<Path> allPaths = new ArrayList<>();
	
	/**
	 * Adds an absolute path to this file collection.
	 */
	public void addPath(Path absolutePath) {
		File file = absolutePath.toFile();
		if (file.isFile()) {
			this.filePaths.add(absolutePath);
		} else if (file.isDirectory()) {
			this.directoryPaths.add(absolutePath);
		}
		this.allPaths.add(absolutePath);
	}
	
	/**
	 * Adds the given paths to this path collection.
	 * @param absolutePaths The absolute paths.
	 */
	public void addPaths(Collection<Path> absolutePaths) {
		for (Path absolutePath : absolutePaths) {
			this.addPath(absolutePath);
		}
	}
	
	/**
	 * Returns whether this file collection is empty.
	 * @return Whether this file collection is empty.
	 */
	public boolean isEmpty() {
		return this.filePaths.isEmpty();
	}

	/**
	 * Returns the file paths.
	 * @return The file paths.
	 */
	public List<Path> getFilePaths() {
		return filePaths;
	}

	/**
	 * Returns the directory paths.
	 * @return The directory paths.
	 */
	public List<Path> getDirectoryPaths() {
		return directoryPaths;
	}
	
	/**
	 * Returns all paths both of files and directories, in the order
	 * they were added.
	 * @return All the paths.
	 */
	public List<Path> getAllPaths() {
		return this.allPaths;
	}
	
	@Override
	public String toString() {
		List<String> files = new ArrayList<>();
		for (Path path : this.filePaths) {
			files.add(path.toString());
		}
		List<String> directories = new ArrayList<>();
		for (Path path : this.directoryPaths) {
			directories.add(path.toString());
		}
		return "[Files: " + String.join(", ", files) + ", Directories: " + String.join(", ", directories) + "]";
	}
}
