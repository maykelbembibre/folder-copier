package folder_copier.ui.workers;

import java.awt.Component;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

import folder_copier.logic.FileManager;
import folder_copier.logic.Logger;
import folder_copier.logic.Tools;
import folder_copier.logic.exceptions.FileManagementException;
import folder_copier.logic.models.ConflictingFileOption;
import folder_copier.logic.models.FileCopyAction;
import folder_copier.logic.models.FileCopyResult;
import folder_copier.logic.models.FileCopyResults;
import folder_copier.logic.models.PathCollection;
import folder_copier.ui.AppWindow;
import folder_copier.ui.models.FileCounters;

/**
 * An asynchronous task that copies files in order from one
 * directory to another and updates the progress in the GUI.
 */
public class FileCopyTask extends ErrorAwareSwingWorker<Void, FileCounters> {

	private final File sourceDirectory;
	private final File destinationDirectory;
	private final JTextArea statusNote;
	private final Collection<Component> sensitiveComponents;
	private final Component stopButton;
	private final ConflictingFileOption conflictingFileOption;
	private final int progressThreshold;
	private final FileManager fileManager;
	private final FileCopyResults fileCopyResults;
	private final PathCollection deletedFilesInDestination;
	private FileCounters fileCounters;
	
	/**
	 * Constructor.
	 * @param sourceDirectory The source directory.
	 * @param destinationDirectory The destination directory.
	 * @param statusNote The status note.
	 * @param sensitiveComponents The components that have to be enabled
	 * only when the task ends.
	 * @param stopButton The button that makes this task stop.
	 * @param conflictingFileOption What to do in case of file name conflicts.
	 * @param deleteOrphanInDestination Whether to delete files in the destination
	 * directory that don't exist in the source directory.
	 */
    public FileCopyTask(
    	File sourceDirectory, File destinationDirectory,
    	JTextArea statusNote, Collection<Component> sensitiveComponents,
    	Component stopButton, ConflictingFileOption conflictingFileOption, boolean deleteOrphanInDestination
    ) {
    	this.sourceDirectory = sourceDirectory;
    	this.destinationDirectory = destinationDirectory;
    	this.statusNote = statusNote;
    	this.sensitiveComponents = sensitiveComponents;
    	this.stopButton = stopButton;
    	this.conflictingFileOption = conflictingFileOption;
    	if (deleteOrphanInDestination) {
			this.progressThreshold = 75;
			this.deletedFilesInDestination = new PathCollection();
		} else {
			this.progressThreshold = 100;
			this.deletedFilesInDestination = null;
		}
    	this.fileManager = new FileManager(this.conflictingFileOption);
    	this.fileCopyResults = new FileCopyResults();
	}

    /**
     * Main task. Executed in background thread. Note that this method is
     * executed only once.
     * @return <code>null</code>.
     */
    @Override
    public Void doInBackground() {
    	Logger logger = null;
    	try {
    		this.setProgress(0);
    		logger = new Logger(AppWindow.APP_NAME);
			FileManager.checkDirectories(sourceDirectory, destinationDirectory);
			int totalFilesInDestination;
			if (this.deletedFilesInDestination != null) {
				totalFilesInDestination = this.countFilesRecursively(this.destinationDirectory);
			} else {
				totalFilesInDestination = 0;
			}
			this.fileCounters = new FileCounters(this.countFilesRecursively(this.sourceDirectory), totalFilesInDestination);
			this.publishAll(0); // Show initial file counts to the user.
			this.copyRecursively(this.sourceDirectory, this.destinationDirectory);
			Tools.logResults(this.fileCopyResults, logger);
			if (this.deletedFilesInDestination != null) {
				this.deleteRecursively(this.sourceDirectory, this.destinationDirectory);
				logger.println("Files or folders that have been deleted from the destination folder:");
				Tools.logFilesAndDirectories(deletedFilesInDestination, logger);
			}
		} catch (FileManagementException e) {
			this.publishError(e.getMessage());
		} catch (FileSystemException e) {
			this.publishError("It looks like an external storage device has been removed. The operation can't go on.");
		} catch (IOException e) {
			e.printStackTrace();
			this.publishError("OS file system error.");
		} finally {
			try {
				logger.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return null;
    }
    
    /**
     * Returns the counters for ongoing file operations.
     * @return The counters for ongoing file operations.
     */
	public FileCounters getFileCounters() {
		return fileCounters;
	}

	/**
	 * Executed in event dispatching thread. This method is called when the task
	 * finishes.
	 */
    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        for (Component component : this.sensitiveComponents) {
        	component.setEnabled(true);
        }
        
        // When a task ends, it can't be stopped anymore.
        this.stopButton.setEnabled(false);
        
        String error = this.getError();
        if (error != null) {
        	this.statusNote.setText("Error: " + error);
        } else if (this.isCancelled()) {
        	this.statusNote.setText("Task cancelled.");
        } else {
        	String deletedPart;
        	if (this.deletedFilesInDestination != null) {
        		deletedPart = "Files deleted from the destination folder: " + this.deletedFilesInDestination.getFilePaths().size() + ".";
        	} else {
        		deletedPart = "";
        	}
        	this.statusNote.setText(
        		"File copy has been completed.\n" +
        		"Total files in source folder: " + this.fileCounters.getNumberOfTotalFilesInSource() + ".\n" +
        		Tools.printResults(this.fileCopyResults) + deletedPart
        	);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(List<FileCounters> chunks) {
    	FileCounters fileCounters = chunks.get(chunks.size() - 1);
    	this.statusNote.setText(OutputText.createStatusNoteText(fileCounters, this.getProgress()));
    }
    
    private void publishAll(int progress) {
    	this.publish(this.fileCounters);
    	this.setProgress(progress);
    }
    
	private int countFilesRecursively(File directory) {
		Iterator<File> children = FileManager.getChildren(directory).iterator();
		File fromFolderChild;
		int count = 0;
    	while (!this.isCancelled() && children.hasNext()) {
    		fromFolderChild = children.next();
    		if (fromFolderChild.isFile()) {
    			count++;
    		} else {
    			count = count + countFilesRecursively(fromFolderChild);
    		}
    	}
		return count;
	}

	private List<Path> getFilesRecursively(File directory) {
		List<Path> result = new ArrayList<>();
		Iterator<File> children = FileManager.getChildren(directory).iterator();
		File directoryChild;
    	while (!this.isCancelled() && children.hasNext()) {
    		directoryChild = children.next();
    		if (directoryChild.isFile()) {
    			result.add(directoryChild.toPath());
    		} else {
    			result.addAll(getFilesRecursively(directoryChild));
    		}
    	}
		return result;
	}
	
	private int calculateCopyProgress() {
		return Math.min(
			this.fileCounters.getNumberOfProcessedFilesInSource() * this.progressThreshold / this.fileCounters.getNumberOfTotalFilesInSource(),
			this.progressThreshold
		);
	}
	
	private void copyRecursively(File sourceSubdirectory, File destinationSubdirectory) throws IOException, FileManagementException {
		Iterator<File> sourceSubdirectoryChildren = FileManager.getChildren(sourceSubdirectory).iterator();
		File sourceSubdirectoryChild;
		while (!this.isCancelled() && sourceSubdirectoryChildren.hasNext()) {
    		sourceSubdirectoryChild = sourceSubdirectoryChildren.next();
    		if (sourceSubdirectoryChild.isFile()) {
    			FileCopyResult result = this.fileManager.copyFileToDirectory(sourceSubdirectoryChild, destinationSubdirectory);
    			this.fileCopyResults.add(result);
    			this.fileCounters.addProcessedFilesInSource(1);
    			this.publishAll(this.calculateCopyProgress());
    		} else {
    			File destinationSubdirectoryChild = new File(destinationSubdirectory, sourceSubdirectoryChild.getName());
				if (FileManager.createDirectoryIfNotExists(destinationSubdirectoryChild)) {
					this.copyRecursively(sourceSubdirectoryChild, destinationSubdirectoryChild);
				} else {
					List<Path> files = this.getFilesRecursively(sourceSubdirectoryChild);
					this.fileCounters.addProcessedFilesInSource(files.size());
					for (Path file : files) {
						this.fileCopyResults.add(new FileCopyResult(file, FileCopyAction.SKIPPED));
					}
					this.publishAll(this.calculateCopyProgress());
				}
    		}
    	}
    }
	
	private int calculateDeleteOrphansProgress() {
		return Math.min(
			this.progressThreshold + this.fileCounters.getNumberOfProcessedFilesInDestination() *
			(100 - this.progressThreshold) / this.fileCounters.getNumberOfTotalFilesInDestination(),
			100
		);
	}
	
	private void deleteRecursively(File sourceSubirectory, File destinationSubdirectory) throws IOException {
		Iterator<File> destinationDirectoryChildren = FileManager.getChildren(destinationSubdirectory).iterator();
		File destinationSubdirectoryChild, sourceSubdirectoryChild;
		while (!this.isCancelled() && destinationDirectoryChildren.hasNext()) {
			destinationSubdirectoryChild = destinationDirectoryChildren.next();
			sourceSubdirectoryChild = new File(sourceSubirectory, destinationSubdirectoryChild.getName());
			if (destinationSubdirectoryChild.isFile()) {
				if (!sourceSubdirectoryChild.isFile()) {
					this.deletedFilesInDestination.addPath(destinationSubdirectoryChild.toPath());
					destinationSubdirectoryChild.delete();
				}
				this.fileCounters.addProcessedFilesInDestination(1);
				this.publishAll(calculateDeleteOrphansProgress());
			} else {
				if (sourceSubdirectoryChild.isFile() || !sourceSubdirectoryChild.exists()) {
					List<Path> files = this.getFilesRecursively(destinationSubdirectoryChild);
					this.fileCounters.addProcessedFilesInDestination(files.size());
					for (Path file : files) {
						this.deletedFilesInDestination.addPath(file);
					}
					this.deletedFilesInDestination.addPath(destinationSubdirectoryChild.toPath());
					FileUtils.deleteDirectory(destinationSubdirectoryChild);
					this.publishAll(calculateDeleteOrphansProgress());
				} else {
					this.deleteRecursively(sourceSubdirectoryChild, destinationSubdirectoryChild);
				}
			}
		}
	}
}