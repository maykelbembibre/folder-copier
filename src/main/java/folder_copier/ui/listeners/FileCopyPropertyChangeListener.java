package folder_copier.ui.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

/**
 * Class that listens for changes in the progress property of a 
 * {@link javax.swing.SwingWorker SwingWorker} and updates a
 * progress bar.
 */
public class FileCopyPropertyChangeListener implements PropertyChangeListener {

	private final JProgressBar progressBar;
	
	/**
	 * Constructor.
	 * @param progressBar The progress bar.
	 * @param statusNote The status note.
	 * @param task The asynchronous task.
	 */
	public FileCopyPropertyChangeListener(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	/**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }
}
