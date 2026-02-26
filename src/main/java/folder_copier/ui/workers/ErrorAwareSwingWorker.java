package folder_copier.ui.workers;

import javax.swing.SwingWorker;

/**
 * A {@link SwingWorker} that controls errors.
 * 
 * @param <T> the result type returned by this {@code SwingWorker's}
 *        {@code doInBackground} and {@code get} methods
 * @param <V> the type used for carrying out intermediate results by this
 *        {@code SwingWorker's} {@code publish} and {@code process} methods
 */
public abstract class ErrorAwareSwingWorker<T, V> extends SwingWorker<T, V> {

	/**
	 * Any error produced during the execution of this task.
	 */
	private String error;

	/**
	 * Returns any error produced during the execution of this task.
	 * @return Any error produced during the execution of this task.
	 */
    protected String getError() {
    	return this.error;
    }
	
    /**
     * Publishes the given error and cancels the task.
     * @param error The error.
     */
    protected void publishError(String error) {
    	this.cancel(true);
    	this.error = error;
    }
}
