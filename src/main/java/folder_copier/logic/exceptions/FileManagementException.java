package folder_copier.logic.exceptions;

/**
 * Exception thrown if something goes wrong during file management
 * stuff.
 */
public class FileManagementException extends Exception {

	private static final long serialVersionUID = -6675300897398472754L;

	/**
	 * Creates an exception with a message.
	 * @param message The message.
	 */
	public FileManagementException(String message) {
		super(message);
	}
}
