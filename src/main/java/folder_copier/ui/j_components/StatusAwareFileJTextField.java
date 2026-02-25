package folder_copier.ui.j_components;

import javax.swing.JTextField;

import folder_copier.logic.PropertyManager;
import folder_copier.ui.AppWindow;

/**
 * A {@link FileJTextField} that updates its app window status whenever
 * the text changes.
 */
public class StatusAwareFileJTextField extends FileJTextField {

	private static final long serialVersionUID = -6713096680218488439L;
	private final AppWindow appWindow;
	
	/**
	 * Creates a new {@link JTextField} whose content is the absolute path of
	 * a file and is backed by a properties file.
	 * @param propertyManager The properties file manager instance.
	 * @param propertyKey The key of the property where the value of this instance
	 * will be saved.
	 */
	public StatusAwareFileJTextField(AppWindow appWindow, PropertyManager propertyManager, String propertyKey) {
		super(propertyManager, propertyKey);
		this.appWindow = appWindow;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doAfterCaretUpdate() {
		appWindow.updateStatus();
	}
}
