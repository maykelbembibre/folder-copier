package folder_copier.ui.panels;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import folder_copier.ui.AppWindow;
import folder_copier.ui.Tools;

public class StatusJPanel extends JPanel {

	private static final long serialVersionUID = -3626156596714070873L;

	private static final ImageIcon TICK = Tools.createIcon("/images/tick16.png", "tick");
	private static final ImageIcon CROSS = Tools.createIcon("/images/cross16.png", "cross");
	
	public StatusJPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(0, AppWindow.GAP, AppWindow.GAP, AppWindow.GAP));
		JLabel statusLabel = new JLabel("This is a test label.", TICK, JLabel.LEFT);
		this.add(statusLabel);
		this.add(Box.createHorizontalGlue());
	}
}
