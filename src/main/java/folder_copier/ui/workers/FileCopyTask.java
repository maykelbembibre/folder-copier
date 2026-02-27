package folder_copier.ui.workers;

import java.awt.Component;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import folder_copier.logic.FileManager;
import folder_copier.logic.Tools;
import folder_copier.logic.exceptions.FileManagementException;
import folder_copier.logic.models.ConflictingFileOption;
import folder_copier.logic.models.FileCopyResult;
import folder_copier.ui.models.FileCounters;
import folder_copier.ui.models.Indicators;

/**
 * An asynchronous task that copies files in order from one
 * directory to another and updates the progress in the GUI.
 */
public class FileCopyTask extends ErrorAwareSwingWorker<Void, FileCounters> {

	private static final Logger LOGGER = LogManager.getLogger(FileCopyTask.class);

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private final File sourceDirectory;
	private final File destinationDirectory;
	private final JTextArea statusNote;
	private final Collection<Component> sensitiveComponents;
	private final Component stopButton;
	private final ConflictingFileOption conflictingFileOption;
	private final FileManager fileManager;
	private final boolean deleteOrphanInDestination;
	private final ProgressCalculator progressCalculator;
	private Indicators indicators;
	
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
    	this.fileManager = new FileManager(this.conflictingFileOption);
    	this.deleteOrphanInDestination = deleteOrphanInDestination;
    	if (deleteOrphanInDestination) {
			this.progressCalculator = new ProgressCalculator(75);
		} else {
			this.progressCalculator = new ProgressCalculator(100);
		}
	}

    /**
     * Main task. Executed in background thread. Note that this method is
     * executed only once.
     * @return <code>null</code>.
     */
    @Override
    public Void doInBackground() {
    	try {
    		Date now = new Date();
    		LOGGER.info("File copy task execution on " + DATE_FORMAT.format(now));
    		LOGGER.info("");
    		this.setProgress(0);
			FileManager.checkDirectories(sourceDirectory, destinationDirectory);
			this.indicators = new Indicators(
				this.countFilesRecursively(this.sourceDirectory), this.countFilesRecursively(this.destinationDirectory)
			);
			this.publishAll(0); // Show initial file counts to the user.
			this.copyRecursively(this.sourceDirectory, this.destinationDirectory);
			Tools.logResults(this.indicators.getFileCopyResults());
			if (this.deleteOrphanInDestination) {
				this.deleteRecursively(this.sourceDirectory, this.destinationDirectory);
				LOGGER.info("Files or folders that have been deleted from the destination folder:");
				Tools.logFilesAndDirectories(this.indicators.getDeletedFilesInDestination());
			}
			LOGGER.info("");
			LOGGER.info("");
		} catch (FileManagementException e) {
			this.publishError(e.getMessage());
		} catch (FileSystemException e) {
			this.publishError(e.getMessage());
		} catch (IOException e) {
			this.publishError(e.getMessage());
		} catch (Exception e) {
			String message = e.getMessage();
			String error;
			if (message == null || message.isEmpty()) {
				error = "Unknown error.";
			} else {
				error = message;
			}
			this.publishError(error);
			LOGGER.error("There's an error: ", e);
		}
        return null;
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
        	this.statusNote.setText(OutputText.createFinalStatusNoteText(
        		this.indicators.getDeletedFilesInDestination(), this.indicators.getFileCounters(), this.indicators.getFileCopyResults()
        	));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(List<FileCounters> chunks) {
    	FileCounters fileCounters = chunks.get(chunks.size() - 1);
    	this.statusNote.setText(OutputText.createOngoingStatusNoteText(fileCounters, this.getProgress()));
    }
    
    private void publishAll(int progress) {
    	this.publish(this.indicators.getFileCounters());
    	this.setProgress(progress);
    }
    
	private int countFilesRecursively(File directory) throws FileManagementException {
		int count = 0;
		Iterator<File> children = FileManager.getChildren(directory).iterator();
		File directoryChild;
    	while (!this.isCancelled() && children.hasNext()) {
    		directoryChild = children.next();
    		if (directoryChild.isFile()) {
    			count++;
    		} else {
    			count = count + countFilesRecursively(directoryChild);
    		}
    	}
		return count;
	}

	private List<Path> getFilesRecursively(File directory) throws FileManagementException {
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
	
	private final ProgressAdder PROGRESS_ADDER = new ProgressAdder() {
		@Override
		public void addRatio(BigDecimal ratio) {
			FileCopyTask.this.setProgress(progressCalculator.calculateCopyProgress(indicators.getFileCounters(), ratio));
		}
	};
	
	private FileCopyResult copyFileToDirectory(File sourceFile, File destinationDirectory) throws IOException {
		FileCopyResult result;
		if (this.indicators.getFileCounters().getNumberOfTotalFilesInSource() < 100) {
			result = this.fileManager.copyFileToDirectory(sourceFile, destinationDirectory, PROGRESS_ADDER);
		} else {
			result = this.fileManager.copyFileToDirectory(sourceFile, destinationDirectory);
		}
		return result;
	}
	
	private void copyRecursively(File srcSubdir, File dstSubdir) throws IOException, FileManagementException {
		Iterator<File> srcSubdirChildren = FileManager.getChildren(srcSubdir).iterator();
		File srcSubdirChild;
		while (!this.isCancelled() && srcSubdirChildren.hasNext()) {
    		srcSubdirChild = srcSubdirChildren.next();
    		if (srcSubdirChild.isFile()) {
    			FileCopyResult result = this.copyFileToDirectory(srcSubdirChild, dstSubdir);//this.fileManager.copyFileToDirectory(srcSubdirChild, dstSubdir);
    			this.indicators.addFileCopyResult(result);
    			this.publishAll(this.progressCalculator.calculateCopyProgress(this.indicators.getFileCounters()));
    		} else {
    			File dstSubdirChild = new File(dstSubdir, srcSubdirChild.getName());
				if (FileManager.createDirectoryIfNotExists(dstSubdirChild)) {
					this.copyRecursively(srcSubdirChild, dstSubdirChild);
				} else {
					this.indicators.addSkippedFiles(this.getFilesRecursively(srcSubdirChild));
					this.publishAll(this.progressCalculator.calculateCopyProgress(this.indicators.getFileCounters()));
				}
    		}
    	}
    }

	private void deleteRecursively(File srcSubdir, File dstSubdir) throws IOException, FileManagementException {
		Iterator<File> dstSubdirChildren = FileManager.getChildren(dstSubdir).iterator();
		File srcSubdirChild, dstSubdireChild;
		while (!this.isCancelled() && dstSubdirChildren.hasNext()) {
			dstSubdireChild = dstSubdirChildren.next();
			srcSubdirChild = new File(srcSubdir, dstSubdireChild.getName());
			if (dstSubdireChild.isFile()) {
				this.indicators.addProcessedFileInDestination();
				if (!srcSubdirChild.isFile()) {
					this.indicators.addDeletedFile(dstSubdireChild);
					dstSubdireChild.delete();
				}
				this.publishAll(this.progressCalculator.calculateDeleteProgress(this.indicators.getFileCounters()));
			} else {
				if (srcSubdirChild.isDirectory()) {
					this.deleteRecursively(srcSubdirChild, dstSubdireChild);
				} else {
					this.indicators.addDeletedDirectory(dstSubdireChild, this.getFilesRecursively(dstSubdireChild));
					FileUtils.deleteDirectory(dstSubdireChild);
					this.publishAll(this.progressCalculator.calculateDeleteProgress(this.indicators.getFileCounters()));
				}
			}
		}
	}
}