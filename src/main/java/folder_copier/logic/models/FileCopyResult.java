package folder_copier.logic.models;

import java.nio.file.Path;

/**
 * The result of a file copy operation.
 */
public class FileCopyResult {

	private final Path affectedFilePath;
	private final FileCopyAction action;
	
	/**
	 * Creates a result of a file copy operation.
	 * @param affectedFilePath The affected file path.
	 * @param action The action that was taken with that file.
	 */
	public FileCopyResult(Path affectedFilePath, FileCopyAction action) {
		this.affectedFilePath = affectedFilePath;
		this.action = action;
	}
	
	/**
	 * Returns the affected file path.
	 * @return The affected file path.
	 */
	public Path getAffectedFilePath() {
		return affectedFilePath;
	}

	/**
	 * Returns the action that was taken with the affected file.
	 * @return The action that was taken with the affected file.
	 */
	public FileCopyAction getAction() {
		return action;
	}
}
