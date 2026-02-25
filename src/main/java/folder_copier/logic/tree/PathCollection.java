package folder_copier.logic.tree;

import java.nio.file.Path;

/**
 * Class that represents a collection of absolute paths that start in a
 * specific directory.
 */
public class PathCollection extends PathNode {

	private final Path startDirectory;

	/**
	 * Creates a new path collection starting in the given directory.
	 * @param startDirectory The start directory.
	 */
	public PathCollection(Path startDirectory) {
		super(startDirectory.toString());
		this.startDirectory = startDirectory;
	}
	
	/**
	 * Adds an absolute path to this path collection.
	 */
	public void addPath(Path absolutePath) {
		if (this.startDirectory.getRoot().equals(absolutePath.getRoot())) {
			Path relativePath = this.startDirectory.relativize(absolutePath);
			super.addPath(relativePath);
		}
	}
}
