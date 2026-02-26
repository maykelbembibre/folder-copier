package folder_copier.ui.workers;

import folder_copier.ui.models.FileCounters;

/**
 * Class that calculates the progress of an ongoing file copy task.
 */
public class ProgressCalculator {

	private final int progressThreshold;

	/**
	 * Creates the progress calculator.
	 * @param progressThreshold The threshold for the progress when the file
	 * copying part of the task ends and the deletion of files in the
	 * destination folder that aren't in the source folder begins.
	 */
	public ProgressCalculator(int progressThreshold) {
		this.progressThreshold = progressThreshold;
	}
	
	/**
	 * Calculates the progress of the file copy part.
	 * @param fileCounters The file counters.
	 * @return The progress.
	 */
	public int calculateCopyProgress(FileCounters fileCounters) {
		int result;
		int divisor = fileCounters.getNumberOfTotalFilesInSource();
		if (divisor > 0) {
			result = Math.min(
				fileCounters.getNumberOfProcessedFilesInSource() * this.progressThreshold / divisor,
				this.progressThreshold
			);
		} else {
			result = this.progressThreshold;
		}
		return result;
	}
	
	/**
	 * Calculates the progress of the file deletion part.
	 * @param fileCounters The file counters.
	 * @return The progress.
	 */
	public int calculateDeleteProgress(FileCounters fileCounters) {
		int result;
		int divisor = fileCounters.getNumberOfTotalFilesInDestination();
		if (divisor > 0) {
			result = Math.min(
				this.progressThreshold + fileCounters.getNumberOfProcessedFilesInDestination() *
				(100 - this.progressThreshold) / divisor,
				100
			);
		} else {
			result = 100;
		}
		return result;
	}
}
