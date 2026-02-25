package folder_copier.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.net.URL;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import folder_copier.ui.j_components.ReadOnlyJTextArea;
import folder_copier.ui.panels.StatusJPanel;

/**
 * General UI tools.
 */
public class Tools {

	private static final Color TRANSPARENT = new Color(255, 255, 255, 0);

	/**
	 * Adds the given dimension and insets.
	 * @param dimension A dimension.
	 * @param insets The insets.
	 * @return The result of adding the parameters together.
	 */
	public static Dimension add(Dimension dimension, Insets insets) {
		return new Dimension(
			dimension.width + insets.left + insets.right,
			dimension.height + insets.top + insets.bottom
		);
	}
	
	/**
	 * Creates an icon.
	 * @param path The path in the resources folder of this app.
	 * Must begin with slash /.
	 * @param description The description.
	 * @return The icon.
	 */
	public static ImageIcon createIcon(String path, String description) {
		URL imgURL = StatusJPanel.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	
	/**
	 * Enables or disables the given components.
	 * @param components Components.
	 * @param enabled Whether to enable them or not.
	 */
	public static void setEnabled(Collection<Component> components, boolean enabled) {
		for (Component component : components) {
    		component.setEnabled(enabled);
    	}
	}
	
	/**
	 * Shows a confirmation dialog.
	 * @param parentComponent The parent component.
	 * @param question The question to ask the user.
	 * @return Whether the user has confirmed or not.
	 */
	public static boolean showConfirmationDialog(Component parentComponent, String question) {
		Object[] options = {"Yes", "No"};
		JTextArea textArea = new ReadOnlyJTextArea(question);
		textArea.setBackground(TRANSPARENT);
		int n = JOptionPane.showOptionDialog(
			parentComponent, textArea, "Confirmation",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]
		);
		return n == 0;
	}
}
