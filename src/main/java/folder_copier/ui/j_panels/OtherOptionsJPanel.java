package folder_copier.ui.j_panels;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The panel with other file options.
 */
public class OtherOptionsJPanel extends JPanel {

	private static final long serialVersionUID = 8294137259109878501L;

	/**
	 * Creates a panel with other file options.
	 * @param deleteOrphanInDestinationJCheckBox The delete orphan in destination
	 * checkbox.
	 */
	public OtherOptionsJPanel(JCheckBox deleteOrphanInDestinationJCheckBox) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new JLabel("Other options"));
		this.add(deleteOrphanInDestinationJCheckBox);
	}
}
