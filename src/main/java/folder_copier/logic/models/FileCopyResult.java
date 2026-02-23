package folder_copier.logic.models;

/**
 * This enumeration represents the possible results that can happen when
 * copying a file from the source directory to the destination directory.
 */
public enum FileCopyResult {

	/**
	 * The file was copied because not a file with the same name existed
	 * in the destination directory.
	 */
	COPIED_WITH_NO_CONFLICT(true),
	
	/**
	 * The file was skipped because a file with the same name already existed
	 * in the destination directory and the user has selected to skip these
	 * files, or because a file with the same name already existed in the
	 * destination directory but the source file didn't have a newer modification
	 * date. This result also happens if a directory exists in the destination
	 * directory with the same name as the file. This is very rare because
	 * directories shouldn't be called like files and have extensions.
	 */
	SKIPPED(false),
	
	/**
	 * The file was overwritten because of user preference, because a file
	 * with the same name and different size existed in the destination
	 * directory or because a file with the same name and older modification
	 * date existed in the destination directory.
	 */
	OVERWRITTEN(true);
	
	private boolean copy;
	
	private FileCopyResult(boolean copy) {
		this.copy = copy;
	}
	
	public boolean copy() {
		return this.copy;
	}
}
