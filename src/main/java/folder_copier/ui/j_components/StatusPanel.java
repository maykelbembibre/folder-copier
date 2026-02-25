package folder_copier.ui.j_components;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import folder_copier.ui.AppWindow;

public class StatusPanel extends JPanel {

	private static final long serialVersionUID = -3626156596714070873L;

	private static final ImageIcon TICK = createImageIcon("/images/tick16.png", "tick");
	private static final ImageIcon CROSS = createImageIcon("/images/cross16.png", "cross");
	
	public StatusPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(0, AppWindow.GAP, AppWindow.GAP, AppWindow.GAP));
		JLabel statusLabel = new JLabel("This is a test label.", TICK, JLabel.LEFT);
		this.add(statusLabel);
		this.add(Box.createHorizontalGlue());
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private static ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = StatusPanel.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
}
