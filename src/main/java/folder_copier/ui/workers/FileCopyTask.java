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
import folder_copier.ui.listeners.FileCopyPropertyChangeListener;

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
	private volatile int copiedFilesInSource;
	private volatile int totalFilesInSource;
	private volatile int processedFilesInDestination;
	private volatile int totalFilesInDestination;
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
			this.fileManagement = new FileManager(this.sourceDirectory, this.destinationDirectory, this.conflictingFileOption);
			this.totalFilesInSource = this.countFilesRecursively(this.sourceDirectory);
			this.copiedFilesInSource = 0;
			if (this.deleteOrphanInDestination) {
				this.totalFilesInDestination = this.countFilesRecursively(this.destinationDirectory);
			} else {
				this.totalFilesInDestination = 0;
			}
			this.processedFilesInDestination = 0;
			this.deletedFilesInDestination = 0;
			this.fileCopyResults = new FileCopyResults();
			this.statusNote.setText(
				FileCopyPropertyChangeListener.createStatusNoteText(
					this.copiedFilesInSource, this.totalFilesInSource,
					this.processedFilesInDestination, this.totalFilesInDestination,
					this.getProgress()
				)
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
     * Returns the total number of files.
     * @return The total number of files.
     */
    public int getTotalFilesInSource() {
		return totalFilesInSource;
	}

    /**
     * Returns the number of files that have already been copied.
     * @return The copied files.
     */
	public int getCopiedFilesInSource() {
		return copiedFilesInSource;
	}
	
	/**
	 * Returns the total number of files in the destination directory.
	 * @return The total number of files in the destination directory.
	 */
	public int getTotalFilesInDestination() {
		return totalFilesInDestination;
	}

	/**
	 * Returns the number of processed files in the destination directory.
	 * @return The number of processed files in the destination directory.
	 */
	public int getProcessedFilesInDestination() {
		return processedFilesInDestination;
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
        		"Total files in source folder: " + this.totalFilesInSource + ".\n" +
        		this.printResults() +
        		deletedPart
        	);
        }
    }
    
    private String printResult(FileCopyResult result) {
    	int count = this.fileCopyResults.get(result);
    	String message;
    	if (count > 0) {
    		String resultType;
    		switch (result) {
    		case FileCopyResult.COPIED_WITH_NO_CONFLICT:
    			resultType = "created";
    			break;
    		case FileCopyResult.SKIPPED:
    			resultType = "skipped";
    			break;
    		case FileCopyResult.OVERWRITTEN:
    			resultType = "overwritten";
    			break;
    		default:
    			resultType = null;
    			break;
    		}
    		if (resultType == null) {
    			message = "";
    		} else {
    			message = "Files " + resultType + ": " + count + ".\n";
    		}
    	} else {
    		message = "";
    	}
    	return message;
    }
    
    private String printResults() {
    	StringBuilder builder = new StringBuilder();
    	for (FileCopyResult value : FileCopyResult.values()) {
    		builder.append(printResult(value));
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
			this.copiedFilesInSource * this.copyFileProgressThreshold / this.totalFilesInSource,
			this.copyFileProgressThreshold
		);
	}
	
	private void copyRecursively(File fromDirectory, File toDirectory) throws IOException, FileManagementException {
		Iterator<File> fromDirectoryChildren = this.fileManagement.getChildren(fromDirectory).iterator();
		File fromDirectoryChild;
		while (!this.isCancelled() && fromDirectoryChildren.hasNext()) {
    		fromDirectoryChild = fromDirectoryChildren.next();
    		if (toDirectory.equals(fromDirectoryChild)) {
    			/*
    			 * This prevents a highly problematic loop error where an infinite directory
    			 * hierarchy is copied to toDirectory.
    			 */
    			throw new FileManagementException(
    				"A loop error has occurred because you've tried to copy the directory "
    				+ fromDirectoryChild.getAbsolutePath() + " into the directory "
    				+ toDirectory.getAbsolutePath()
    				+ ". Note that your request is absurd."
    			);
    		}
    		if (fromDirectoryChild.isFile()) {
    			FileCopyResult result = this.fileManagement.copyFileToDirectory(fromDirectoryChild, toDirectory);
    			this.fileCopyResults.add(result);
    			this.copiedFilesInSource++;
    			this.setProgress(this.calculateCopyProgress());
    		} else {
    			File toDirectoryChild = new File(toDirectory, fromDirectoryChild.getName());
    			boolean created;
    			if (toDirectoryChild.isFile()) {
    				created = false;
    			} else {
    				created = FileManager.createIfNotExists(toDirectoryChild);
    			}
				if (created) {
					this.copyRecursively(fromDirectoryChild, toDirectoryChild);
					FileManager.deleteIfEmpty(toDirectoryChild);
				} else {
					int fileCount = countFilesRecursively(fromDirectoryChild);
					this.copiedFilesInSource = this.copiedFilesInSource + fileCount;
					int i;
					for (i = 1;i <= fileCount; i++) {
						this.fileCopyResults.add(FileCopyResult.SKIPPED);
					}
					this.setProgress(this.calculateCopyProgress());
				}
    		}
    	}
    }
	
	private int calculateDeleteOrphansProgress() {
		return Math.min(
			this.copyFileProgressThreshold + this.processedFilesInDestination * (100 - this.copyFileProgressThreshold) / this.totalFilesInDestination,
			100
		);
	}
	
	private void deleteOrphansRecursively(File sourceDirectory, File destinationDirectory) throws IOException {
		Iterator<File> destinationDirectoryChildren = this.fileManagement.getChildren(destinationDirectory).iterator();
		File destinationDirectoryChild;
		File sourceDirectoryChild;
		while (!this.isCancelled() && destinationDirectoryChildren.hasNext()) {
			destinationDirectoryChild = destinationDirectoryChildren.next();
			sourceDirectoryChild = new File(sourceDirectory, destinationDirectoryChild.getName());
			if (destinationDirectoryChild.isFile()) {
				if (!sourceDirectoryChild.isFile()) {
					destinationDirectoryChild.delete();
					this.deletedFilesInDestination++;
				}
				this.processedFilesInDestination++;
				this.setProgress(calculateDeleteOrphansProgress());
			} else {
				if (sourceDirectoryChild.isFile() || !sourceDirectoryChild.exists()) {
					int fileCount = countFilesRecursively(destinationDirectoryChild);
					FileUtils.deleteDirectory(destinationDirectoryChild);
					this.processedFilesInDestination = this.processedFilesInDestination + fileCount;
					this.deletedFilesInDestination = this.deletedFilesInDestination + fileCount;
					this.setProgress(calculateDeleteOrphansProgress());
				} else {
					this.deleteOrphansRecursively(sourceDirectoryChild, destinationDirectoryChild);
					FileManager.deleteIfEmpty(sourceDirectoryChild);
				}
			}
		}
	}
    
    private void error(String error) {
    	this.cancel(true);
    	this.error = error;
    }
}