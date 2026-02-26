package folder_copier.ui.models;

/**
 * Class providing counters for ongoing file operations.
 */
public class FileCounters {
	
	private volatile int processedFilesInSource;
	private final int totalFilesInSource;
	private volatile int processedFilesInDestination;
	private final int totalFilesInDestination;
	
	/**
	 * Creates an object containing counters for ongoing file operations.
	 * @param totalFilesInSource The total number of files in the source
	 * directory.
	 * @param totalFilesInDestination The total number of files in the
	 * destination directory.
	 */
	public FileCounters(int totalFilesInSource, int totalFilesInDestination) {
		this.totalFilesInSource = totalFilesInSource;
		this.totalFilesInDestination = totalFilesInDestination;
	}

	/**
	 * Returns the number of files in the source directory that have
	 * already been processed.
	 * @return The number of files in the source directory that have
	 * already been processed.
	 */
	public int getNumberOfProcessedFilesInSource() {
		return processedFilesInSource;
	}

	/**
	 * Adds to the number of files in the source directory that have
	 * already been processed.
	 * @param filesToAdd The number of files to add.
	 */
	public void addProcessedFilesInSource(int filesToAdd) {
		this.processedFilesInSource += filesToAdd;
	}

	/**
	 * Returns the total number of files in the source directory.
	 * @return The total number of files in the source directory.
	 */
	public int getNumberOfTotalFilesInSource() {
		return totalFilesInSource;
	}
	
	/**
	 * Returns the number of files in the destination directory that
	 * have already been processed.
	 * @return The number of files in the destination directory that
	 * have already been processed.
	 */
	public int getNumberOfProcessedFilesInDestination() {
		return processedFilesInDestination;
	}

	/**
	 * Adds to the number of files in the destination directory that
	 * have already been processed.
	 * @param filesToAdd The number of files to add.
	 */
	public void addProcessedFilesInDestination(int filesToAdd) {
		this.processedFilesInDestination += filesToAdd;
	}

	/**
	 * Returns the total number of files in the destination directory.
	 * @return The total number of files in the destination directory.
	 */
	public int getNumberOfTotalFilesInDestination() {
		return totalFilesInDestination;
	}
}
