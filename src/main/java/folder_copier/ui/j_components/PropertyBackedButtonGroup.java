package folder_copier.ui.j_components;

import java.util.Iterator;
import java.util.Optional;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

import folder_copier.logic.PropertyManager;

/**
 * Button group that saves the selected button in a properties file.
 */
public class PropertyBackedButtonGroup extends ButtonGroup {

	private static final long serialVersionUID = 3042950309524838497L;
	
	private final PropertyManager propertyManager;
	private final String propertyKey;
	
	/**
	 * Creates a button group that saves the selected button in a properties
	 * file.
	 * @param propertyManager The property manager.
	 * @param propertyKey The key for saving the selected button in the properties
	 * file.
	 */
	public PropertyBackedButtonGroup(PropertyManager propertyManager, String propertyKey) {
		this.propertyManager = propertyManager;
		this.propertyKey = propertyKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void add(AbstractButton b) {
    	Optional<Integer> optionalSelectedIndex = this.propertyManager.getIntegerProperty(this.propertyKey);
    	if (optionalSelectedIndex.isPresent()) {
    		Integer selectedIndex = optionalSelectedIndex.get();
    		b.setSelected(selectedIndex == this.buttons.size());
    	}
    	super.add(b);
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelected(ButtonModel m, boolean b) {
		if (b && m != null && m != this.getSelection()) {
			int index = 0;
			Iterator<AbstractButton> buttons = this.buttons.iterator();
			AbstractButton button;
			boolean set = false;
			while (buttons.hasNext() && !set) {
				button = buttons.next();
				if (m == button.getModel()) {
					this.propertyManager.setIntegerProperty(this.propertyKey, index);
					set = true;
				}
				index++;
			}
		}
		super.setSelected(m, b);
	}
}
