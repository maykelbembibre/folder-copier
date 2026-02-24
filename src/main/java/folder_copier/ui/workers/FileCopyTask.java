package folder_copier.ui.workers;

import java.awt.Component;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import folder_copier.logic.FileManager;
import folder_copier.logic.exceptions.FileManagementException;
import folder_copier.logic.models.ConflictingFileOption;
import folder_copier.logic.models.FileCopyResult;
import folder_copier.logic.models.FileCopyResults;
import folder_copier.ui.StringTools;
import folder_copier.ui.listeners.FileCopyPropertyChangeListener;
import folder_copier.ui.models.FileCounters;

/**
 * An asynchronous task that copies files in order from one
 * directory to another and updates the progress in the GUI.
 */
public class FileCopyTask extends SwingWorker<Void, Void> {

	private final File sourceDirectory;
	private final File destinationDirectory;
	private final JTextArea statusNote;
	private final Collection<Component> sensitiveComponents;
	private final Component stopButton;
	private final ConflictingFileOption conflictingFileOption;
	private final boolean deleteOrphanInDestination;
	private String error;
	private FileManager fileManagement;
	private int copyFileProgressThreshold;
	private FileCounters fileCounters;
	private FileCopyResults fileCopyResults;
	private int deletedFilesInDestination;
	
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
    	this.conflictingFileOption = conflictingFileOption;
    	this.deleteOrphanInDestination = deleteOrphanInDestination;
    	this.sourceDirectory = sourceDirectory;
    	this.destinationDirectory = destinationDirectory;
		this.statusNote = statusNote;
		this.sensitiveComponents = sensitiveComponents;
		this.stopButton = stopButton;
	}

    /**
     * Main task. Executed in background thread. Note that this method is
     * executed only once.
     * @return <code>null</code>.
     */
    @Override
    public Void doInBackground() {
    	try {
    		this.setProgress(0);
    		if (this.deleteOrphanInDestination) {
    			this.copyFileProgressThreshold = 75;
    		} else {
    			this.copyFileProgressThreshold = 100;
    		}
    		this.statusNote.setText("Calculating data...");
			this.fileManagement = new FileManager(this.conflictingFileOption);
			FileManager.checkDirectories(sourceDirectory, destinationDirectory);
			int totalFilesInDestination;
			if (this.deleteOrphanInDestination) {
				totalFilesInDestination = this.countFilesRecursively(this.destinationDirectory);
			} else {
				totalFilesInDestination = 0;
			}
			this.fileCounters = new FileCounters(this.countFilesRecursively(this.sourceDirectory), totalFilesInDestination);
			this.deletedFilesInDestination = 0;
			this.fileCopyResults = new FileCopyResults();
			this.statusNote.setText(
				FileCopyPropertyChangeListener.createStatusNoteText(this.fileCounters, this.getProgress())
			);
			this.copyRecursively(this.sourceDirectory, this.destinationDirectory);
			if (this.deleteOrphanInDestination) {
				this.deleteOrphansRecursively(this.sourceDirectory, this.destinationDirectory);
			}
		} catch (FileManagementException e) {
			this.error(e.getMessage());
		} catch (FileSystemException e) {
			this.error("It looks like an external storage device has been removed. The operation can't go on.");
		} catch (IOException e) {
			e.printStackTrace();
			this.error("OS file system error.");
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
        
        if (this.error != null) {
        	this.statusNote.setText("Error: " + this.error);
        } else if (this.isCancelled()) {
        	this.statusNote.setText("Task cancelled.");
        } else {
        	String deletedPart;
        	if (this.deleteOrphanInDestination) {
        		deletedPart = "Files deleted from the destination folder: " + this.deletedFilesInDestination + ".";
        	} else {
        		deletedPart = "";
        	}
        	this.statusNote.setText(
        		"File copy has been completed.\n" +
        		"Total files in source folder: " + this.fileCounters.getTotalFilesInSource() + ".\n" +
        		this.printResults() +
        		deletedPart
        	);
        }
    }
    
    private String printResults() {
    	StringBuilder builder = new StringBuilder();
    	int count;
    	for (FileCopyResult value : FileCopyResult.values()) {
    		count = this.fileCopyResults.get(value);
    		builder.append(StringTools.printResult(value, count));
    	}
    	return builder.toString();
    }
	
	private int countFilesRecursively(File directory) {
		Iterator<File> children = this.fileManagement.getChildren(directory).iterator();
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

	private int calculateCopyProgress() {
		return Math.min(
			this.fileCounters.getCopiedFilesInSource() * this.copyFileProgressThreshold / this.fileCounters.getTotalFilesInSource(),
			this.copyFileProgressThreshold
		);
	}
	
	private void copyRecursively(File sourceSubdirectory, File destinationSubdirectory) throws IOException, FileManagementException {
		Iterator<File> sourceSubdirectoryChildren = this.fileManagement.getChildren(sourceSubdirectory).iterator();
		File sourceSubdirectoryChild;
		while (!this.isCancelled() && sourceSubdirectoryChildren.hasNext()) {
    		sourceSubdirectoryChild = sourceSubdirectoryChildren.next();
    		if (sourceSubdirectoryChild.isFile()) {
    			FileCopyResult result = this.fileManagement.copyFileToDirectory(sourceSubdirectoryChild, destinationSubdirectory);
    			this.fileCopyResults.add(result);
    			this.fileCounters.addCopiedFilesInSource(1);
    			this.setProgress(this.calculateCopyProgress());
    		} else {
    			File destinationSubdirectoryChild = new File(destinationSubdirectory, sourceSubdirectoryChild.getName());
				if (FileManager.createDirectoryIfNotExists(destinationSubdirectoryChild)) {
					this.copyRecursively(sourceSubdirectoryChild, destinationSubdirectoryChild);
				} else {
					int fileCount = countFilesRecursively(sourceSubdirectoryChild);
					this.fileCounters.addCopiedFilesInSource(fileCount);
					int i;
					for (i = 1;i <= fileCount;i++) {
						this.fileCopyResults.add(FileCopyResult.SKIPPED);
					}
					this.setProgress(this.calculateCopyProgress());
				}
    		}
    	}
    }
	
	private int calculateDeleteOrphansProgress() {
		return Math.min(
			this.copyFileProgressThreshold +
			this.fileCounters.getProcessedFilesInDestination() *
			(100 - this.copyFileProgressThreshold) /
			this.fileCounters.getTotalFilesInDestination(),
			100
		);
	}
	
	private void deleteOrphansRecursively(File sourceSubirectory, File destinationSubdirectory) throws IOException {
		Iterator<File> destinationDirectoryChildren = this.fileManagement.getChildren(destinationSubdirectory).iterator();
		File destinationDirectoryChild;
		File sourceDirectoryChild;
		while (!this.isCancelled() && destinationDirectoryChildren.hasNext()) {
			destinationDirectoryChild = destinationDirectoryChildren.next();
			sourceDirectoryChild = new File(sourceSubirectory, destinationDirectoryChild.getName());
			if (destinationDirectoryChild.isFile()) {
				if (!sourceDirectoryChild.isFile()) {
					destinationDirectoryChild.delete();
					this.deletedFilesInDestination++;
				}
				this.fileCounters.addProcessedFilesInDestination(1);
				this.setProgress(calculateDeleteOrphansProgress());
			} else {
				if (sourceDirectoryChild.isFile() || !sourceDirectoryChild.exists()) {
					int fileCount = countFilesRecursively(destinationDirectoryChild);
					FileUtils.deleteDirectory(destinationDirectoryChild);
					this.fileCounters.addProcessedFilesInDestination(fileCount);
					this.deletedFilesInDestination += fileCount;
					this.setProgress(calculateDeleteOrphansProgress());
				} else {
					this.deleteOrphansRecursively(sourceDirectoryChild, destinationDirectoryChild);
				}
			}
		}
	}
    
    private void error(String error) {
    	this.cancel(true);
    	this.error = error;
    }
}