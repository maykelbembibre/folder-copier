package folder_copier.ui.models;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

import folder_copier.logic.models.FileCopyAction;
import folder_copier.logic.models.FileCopyResult;
import folder_copier.logic.models.FileCopyResults;
import folder_copier.logic.models.PathCollection;

/**
 * Class that contains the indicators that show the number of things that
 * a file copy task has to do and the things it has done so far.
 */
public class Indicators {

	private final FileCounters fileCounters;
	private final FileCopyResults fileCopyResults;
	private final PathCollection deletedFilesInDestination;
	
	/**
	 * Creates a new indicators instance.
	 * @param totalFilesInSource The total number of files in the source directory.
	 * @param totalFilesInDestination The total number of files in the destination directory.
	 */
	public Indicators(int totalFilesInSource, int totalFilesInDestination) {
		this.fileCounters = new FileCounters(totalFilesInSource, totalFilesInDestination);
		this.fileCopyResults = new FileCopyResults();
		this.deletedFilesInDestination = new PathCollection();
	}
	
	/**
	 * Returns the file counters.
	 * @return The file counters.
	 */
	public FileCounters getFileCounters() {
		return fileCounters;
	}

	/**
	 * Returns the file copy results.
	 * @return The file copy results.
	 */
	public FileCopyResults getFileCopyResults() {
		return fileCopyResults;
	}

	/**
	 * Returns the paths of the files and possibly directories that have
	 * been deleted in the destination directory.
	 * @return The paths of the deleted files and directories.
	 */
	public PathCollection getDeletedFilesInDestination() {
		return deletedFilesInDestination;
	}
	
	/**
	 * Adds to this instance the result of the copy of a single file.
	 * @param result The result.
	 */
	public void addFileCopyResult(FileCopyResult result) {
		this.fileCounters.addProcessedFilesInSource(1);
		this.fileCopyResults.add(result);
	}
	
	/**
	 * Adds to this instance a series of file paths that have been skipped.
	 * @param skippedFilePaths Skipped file paths.
	 */
	public void addSkippedFiles(Collection<Path> skippedFilePaths) {
		this.fileCounters.addProcessedFilesInSource(skippedFilePaths.size());
		this.fileCopyResults.addMany(skippedFilePaths, FileCopyAction.SKIPPED);
	}
	
	/**
	 * Adds a deleted file to this instance.
	 * @param deletedFile The deleted file.
	 */
	public void addDeletedFile(File deletedFile) {
		this.fileCounters.addProcessedFilesInDestination(1);
		if (!deletedFile.isFile()) {
			this.deletedFilesInDestination.addPath(deletedFile.toPath());
		}
	}
	
	/**
	 * Adds a deleted directory to this instance.
	 * @param deletedDirectory The deleted directory.
	 * @param deletedDirectoryFiles The files of the deleted directory
	 * (not including directories, only plain files).
	 */
	public void addDeletedDirectory(File deletedDirectory, Collection<Path> deletedDirectoryFiles) {
		this.fileCounters.addProcessedFilesInDestination(deletedDirectoryFiles.size());
		this.deletedFilesInDestination.addPaths(deletedDirectoryFiles);
		this.deletedFilesInDestination.addPath(deletedDirectory.toPath());
	}
}
