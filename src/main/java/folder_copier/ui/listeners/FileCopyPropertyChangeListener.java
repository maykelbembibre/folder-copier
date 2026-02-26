package folder_copier.ui.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import folder_copier.ui.models.FileCounters;
import folder_copier.ui.workers.FileCopyTask;

/**
 * An object of this class must listen for changes in a
 * {@link FileCopyTask}.
 */
public class FileCopyPropertyChangeListener implements PropertyChangeListener {

	private final JProgressBar progressBar;
	private final JTextArea statusNote;
	private final FileCopyTask task;
	
	/**
	 * Constructor.
	 * @param progressBar The progress bar.
	 * @param statusNote The status note.
	 * @param task The asynchronous task.
	 */
	public FileCopyPropertyChangeListener(
		JProgressBar progressBar, JTextArea statusNote, FileCopyTask task
	) {
		this.progressBar = progressBar;
		this.statusNote = statusNote;
		this.task = task;
	}

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
		result.append("Completed " + progress + "% of task.");
		return result.toString();
	}
	
	/**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            statusNote.setText(createStatusNoteText(task.getFileCounters(), task.getProgress()));
        } 
    }
}
