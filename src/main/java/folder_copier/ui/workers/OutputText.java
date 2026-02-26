package folder_copier.ui.workers;

import folder_copier.ui.models.FileCounters;

/**
 * Class that manages the output text of the file copy task.
 */
public class OutputText {
    
	/**
	 * Creates the status note text.
	 * @param fileCounters The counters for the ongoing file operations.
	 * @param progress Progress 0 - 100.
	 * @return The status note text.
	 */
	public static String createStatusNoteText(FileCounters fileCounters, int progress) {
		StringBuilder result = new StringBuilder();
		if (fileCounters.getNumberOfTotalFilesInSource() > 0) {
			result.append(
				"Copied " +
				fileCounters.getNumberOfProcessedFilesInSource() + "/" + fileCounters.getNumberOfTotalFilesInSource() +
				" files from source folder.\n"
			);
		}
		if (fileCounters.getNumberOfTotalFilesInDestination() > 0) {
			result.append(
				"Processed " +
				fileCounters.getNumberOfProcessedFilesInDestination() + "/" + fileCounters.getNumberOfTotalFilesInDestination() +
				" files in destination folder.\n"
			);
		}
		return result.toString();
	}
}
