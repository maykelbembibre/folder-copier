package folder_copier.ui.j_panels;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import folder_copier.ui.AppWindow;
import folder_copier.ui.Tools;
import folder_copier.ui.models.Status;

/**
 * The status panel of the app, which should be located at the bottom
 * of the window.
 */
public class StatusJPanel extends JPanel {

	private static final long serialVersionUID = -3626156596714070873L;

	private static final ImageIcon TICK = Tools.createIcon("/images/tick16.png", "tick");
	private static final ImageIcon CROSS = Tools.createIcon("/images/cross16.png", "cross");
	private static final JLabel FOLDERS_EQUAL = new JLabel("The folders are equal.", TICK, JLabel.LEFT);
	private static final JLabel FOLDERS_NOT_EQUAL = new JLabel("The folders are not equal.", CROSS, JLabel.LEFT);	
	private static final JLabel FOLDERS_NOT_SELECTED = new JLabel("The folders are not selected.");
	
	private Status status;
	
	/**
	 * Creates a new status panel.
	 */
	public StatusJPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(0, AppWindow.GAP, AppWindow.GAP, AppWindow.GAP));
		this.add(FOLDERS_EQUAL);
		this.add(FOLDERS_NOT_EQUAL);
		this.add(FOLDERS_NOT_SELECTED);
		this.add(Box.createHorizontalGlue());
		this.setStatus(Status.FOLDERS_NOT_SELECTED);
	}
	
	/**
	 * Returns the status in this status panel.
	 * @return The status.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the status in this status panel.
	 * @param status The status to set.
	 */
	public void setStatus(Status status) {
		this.status = status;
		switch (status) {
		case FOLDERS_EQUAL:
			FOLDERS_EQUAL.setVisible(true);
			FOLDERS_NOT_EQUAL.setVisible(false);
			FOLDERS_NOT_SELECTED.setVisible(false);
			break;
		case FOLDERS_NOT_EQUAL:
			FOLDERS_EQUAL.setVisible(false);
			FOLDERS_NOT_EQUAL.setVisible(true);
			FOLDERS_NOT_SELECTED.setVisible(false);
			break;
		case FOLDERS_NOT_SELECTED:
		default:
			FOLDERS_EQUAL.setVisible(false);
			FOLDERS_NOT_EQUAL.setVisible(false);
			FOLDERS_NOT_SELECTED.setVisible(true);
			break;
		}
	}
}
