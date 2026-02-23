package folder_copier.ui.j_components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import folder_copier.logic.PropertyManager;

/**
 * A checkbox that saves in a properties file whether it's selected.
 */
public class PropertyBackedJCheckBox extends JCheckBox {

	private static final long serialVersionUID = 3042950309524838497L;
	
	/**
	 * Creates a checkbox that saves in a properties file whether it's
	 * selected.
	 * @param propertyManager The property manager.
	 * @param propertyKey The property key to save whether this checkbox
	 * is selected.
	 * @param title The title to show next to the checkbox.
	 */
	public PropertyBackedJCheckBox(PropertyManager propertyManager, String propertyKey, String title) {
		super(title);
		super.setSelected(propertyManager.getBooleanProperty(propertyKey));
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				propertyManager.setBooleanProperty(propertyKey, isSelected());
			}
		});
	}
}
