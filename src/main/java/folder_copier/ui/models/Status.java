package folder_copier.ui.models;

/**
 * Represents the status of the two folders selected in the app
 * window.
 */
public enum Status {

	/**
	 * The folders have the same name.
	 */
	FOLDERS_EQUAL,
	
	/**
	 * At least one of the folders is not selected.
	 */
	FOLDERS_NOT_SELECTED,
	
	/**
	 * The folders don't have the same name.
	 */
	FOLDERS_NOT_EQUAL
}
