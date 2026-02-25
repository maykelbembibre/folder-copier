package folder_copier.logic.models;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a collection of absolute paths that start in a
 * specific directory.
 */
public class PathCollection {

	private final List<Path> filePaths = new ArrayList<>();
	private final List<Path> directoryPaths = new ArrayList<>();
	
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
