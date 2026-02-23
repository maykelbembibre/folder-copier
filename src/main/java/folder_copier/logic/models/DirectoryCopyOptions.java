package folder_copier.logic.models;

/**
 * Class that represents the available directory copy options.
 */
public class DirectoryCopyOptions {

	/**
	 * What to do when a file with the same name already exists in the destination
	 * directory.
	 */
	private final ConflictingFileOption conflictingFileOption;
	
	/**
	 * Whether to delete destination files that don't exist in the source directory.
	 * If <code>true</code>, this will also delete empty directories remaining in
	 * the destination directory.
	 */
	private final boolean deleteDestinationFilesNotExistingInSourceDirectory;

	/**
	 * Creates an object containing the given directory copy options.
	 * @param conflictingFileOption what has to be done when a file
	 * with the same name already exists in the destination directory.
	 * @param deleteDestinationFilesNotExistingInSourceDirectory
	 * whether to delete destination files that don't exists in the
	 * source directory. If <code>true</code>, this will also delete empty
	 * directories remaining in the destination directory.
	 */
	public DirectoryCopyOptions(ConflictingFileOption conflictingFileOption,
			boolean deleteDestinationFilesNotExistingInSourceDirectory) {
		this.conflictingFileOption = conflictingFileOption;
		this.deleteDestinationFilesNotExistingInSourceDirectory = deleteDestinationFilesNotExistingInSourceDirectory;
	}

	/**
	 * Returns what has to be done when a file with the same name already
	 * exists in the destination directory.
	 * @return what has to be done when a file with the same name already
	 * exists in the destination directory.
	 */
	public ConflictingFileOption getConflictingFileOption() {
		return conflictingFileOption;
	}

	/**
	 * Returns whether to delete destination files that don't exists in the
	 * source directory. If <code>true</code>, this will also delete empty
	 * directories remaining in the destination directory.
	 * @return whether to delete destination files that don't exists in the
	 * source directory.
	 */
	public boolean isDeleteDestinationFilesNotExistingInSourceDirectory() {
		return deleteDestinationFilesNotExistingInSourceDirectory;
	}
}
