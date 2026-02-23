package folder_copier.logic.models;

/**
 * This enumeration represents the options that you have when trying to copy
 * a file from one location to another and a file with the same name already
 * exists in the destination location.
 */
public enum ConflictingFileOption {

	/**
	 * The destination file will be overwritten if the modification timestamp of
	 * the source file is newer.
	 */
	OVERWRITE_IF_NEWER,
	
	/**
	 * Nothing will be done with the destination file. The source file won't be
	 * copied over.
	 */
	SKIP,
	
	/**
	 * The destination file will always be overwritten with the source file.
	 */
	OVERWRITE
}
