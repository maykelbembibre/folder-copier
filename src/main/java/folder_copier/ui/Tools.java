package folder_copier.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;

import folder_copier.ui.panels.StatusJPanel;

/**
 * General UI tools.
 */
public class Tools {

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
}
