package folder_copier.ui.j_components;

import java.awt.Insets;

import javax.swing.JTextArea;

/**
 * A read-only version of a {@link JTextArea}.
 */
public class ReadOnlyJTextArea extends JTextArea {

	private static final long serialVersionUID = 357272574720644231L;

	/**
	 * Constructor.
	 */
	public ReadOnlyJTextArea() {
		super(5, 20);
        this.setMargin(new Insets(5,5,5,5));
        this.setEditable(false);
        this.setFocusable(false);
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
	}
	
	/**
	 * Creates a new read-only {@link JTextArea} with the given text.
	 * @param text The text.
	 */
	public ReadOnlyJTextArea(String text) {
		this();
		this.setText(text);
	}
}
