package folder_copier.ui.workers;

import folder_copier.logic.Tools;
import folder_copier.logic.models.FileCopyResults;
import folder_copier.logic.models.PathCollection;
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
	public static String createOngoingStatusNoteText(FileCounters fileCounters, int progress) {
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
	
	/**
	 * Prints the final text for the status note for when the task has finished.
	 * @param deletedFilesInDestination The deleted files in the destination directory. <code>
	 * null</code> when this part hasn't been executed.
	 * @param fileCounters The file counters.
	 * @param fileCopyResults The file copy results.
	 * @return The text for the status note.
	 */
	public static String createFinalStatusNoteText(
		PathCollection deletedFilesInDestination, FileCounters fileCounters, FileCopyResults fileCopyResults
	) {
		String deletedPart;
    	if (deletedFilesInDestination != null) {
    		deletedPart = "Files deleted from the destination folder: " + deletedFilesInDestination.getFilePaths().size() + ".";
    	} else {
    		deletedPart = "";
    	}
    	return "File copy has been completed.\nTotal files in source folder: " + fileCounters.getNumberOfTotalFilesInSource() +
    	".\n" + Tools.printResults(fileCopyResults) + deletedPart;
	}
}
