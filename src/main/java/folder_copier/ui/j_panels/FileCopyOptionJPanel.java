package folder_copier.ui.j_panels;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import folder_copier.logic.PropertyManager;
import folder_copier.logic.models.ConflictingFileOption;
import folder_copier.ui.j_components.PropertyBackedButtonGroup;

/**
 * A {@link JPanel} with file copy options.
 */
public class FileCopyOptionJPanel extends JPanel {

	private static final long serialVersionUID = 1503627171176547121L;

	private final JRadioButton overwriteIfNewerButton;
	private final JRadioButton skipButton;
	private final JRadioButton overwriteButton;
	
	/**
	 * Creates a {@link JPanel} with file copy options.
	 * @param propertyManager The property manager to save the selected
	 * option.
	 */
	public FileCopyOptionJPanel(PropertyManager propertyManager) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel subfolderSelectionJLabel = new JLabel("In case a file with the same name exists");
		subfolderSelectionJLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(subfolderSelectionJLabel);
		
		this.overwriteIfNewerButton = new JRadioButton("Overwrite if newer");
		this.overwriteIfNewerButton.setSelected(true);
	    this.skipButton = new JRadioButton("Skip");
	    this.overwriteButton = new JRadioButton("Overwrite");
	    
	    //Group the radio buttons.
	    ButtonGroup group = new PropertyBackedButtonGroup(propertyManager, "button-group.file-copy-option");
	    group.add(overwriteIfNewerButton);
	    group.add(skipButton);
	    group.add(overwriteButton);
	    
	    //Put the radio buttons in a column.
        this.add(overwriteIfNewerButton);
        this.add(skipButton);
        this.add(overwriteButton);
	}
	
	/**
	 * Returns the option that is currently selected in the UI.
	 * @return The option that is currently selected in the UI.
	 */
	public ConflictingFileOption getConflictingFileOption() {
		ConflictingFileOption result;
		if (this.overwriteIfNewerButton.isSelected()) {
			result = ConflictingFileOption.OVERWRITE_IF_NEWER;
		} else if (this.overwriteButton.isSelected()) {
			result = ConflictingFileOption.OVERWRITE;
		} else {
			result = ConflictingFileOption.SKIP;
		}
		return result;
	}
	
	public Collection<JRadioButton> getAllButtons() {
		return Arrays.asList(this.overwriteIfNewerButton, this.skipButton, this.overwriteButton);
	}
}
