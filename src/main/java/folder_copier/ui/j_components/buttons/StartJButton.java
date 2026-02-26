package folder_copier.ui.j_components.buttons;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import folder_copier.ui.AppWindow;
import folder_copier.ui.Tools;
import folder_copier.ui.j_components.FileJTextField;
import folder_copier.ui.listeners.FileCopyPropertyChangeListener;
import folder_copier.ui.models.Status;
import folder_copier.ui.workers.FileCopyTask;

/**
 * The start button.
 */
public class StartJButton extends JButton {

	private static final long serialVersionUID = -6805354002802955071L;

	/**
	 * Creates a button that starts a task.
	 * @param appWindow The root app window.
	 * @param progressBar The progress bar for the task that is going to start.
	 * @param taskOutput The text area to output the progress of the task that is going to start.
	 * @param stopButton The stop button for the task that is going to start.
	 */
	public StartJButton(AppWindow appWindow, JProgressBar progressBar, JTextArea taskOutput, JButton stopButton) {
		super("Copy files");
		FileJTextField sourceFileTextField = appWindow.getSourceFileTextField();
		FileJTextField destinationFileTextField = appWindow.getDestinationFileTextField();
		Collection<Component> sensitiveComponents = new ArrayList<>(Arrays.asList(
			this, sourceFileTextField, destinationFileTextField,
			appWindow.getSourceFileChooseButton(), appWindow.getDestinationFileChooseButton(),
			appWindow.getDeleteOrphanInDestinationJCheckBox()
		));
		sensitiveComponents.addAll(appWindow.getFileCopyOptionRadios());
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean goOn;
				if (appWindow.getStatus() == Status.FOLDERS_NOT_EQUAL) {
					goOn = Tools.showConfirmationDialog(appWindow, "The selected folders have different names. Copying the files can lead to unexpected results or even undesired data loss. Are you sure you want to go on?");
				} else {
					goOn = true;
				}
				if (goOn) {
					progressBar.setValue(0);
			    	taskOutput.setText("");
			    	Tools.setEnabled(sensitiveComponents, false);
			        
			    	File sourceDirectory = sourceFileTextField.getSelectedFile();
					File destinationDirectory = destinationFileTextField.getSelectedFile();
			        //Instances of javax.swing.SwingWorker are not reusuable, so
			        //we create new instances as needed.
			    	FileCopyTask task = new FileCopyTask(
			        	sourceDirectory, destinationDirectory, taskOutput, sensitiveComponents, stopButton,
			        	appWindow.getConflictingFileOption(), appWindow.deleteOrphanInDestination()
			        );
			        PropertyChangeListener propertyChangeListener = new FileCopyPropertyChangeListener(
		            	progressBar, taskOutput, task
		            );
			        task.addPropertyChangeListener(propertyChangeListener);
			        appWindow.setTask(task);
			        taskOutput.setText("Calculating data...");
			        task.execute();
			        
			        // After a task starts, the user has the possibility of stopping it.
			        stopButton.setEnabled(true);
				}
			}
		});
	}
}
