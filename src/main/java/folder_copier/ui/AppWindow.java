package folder_copier.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import folder_copier.logic.PropertyManager;
import folder_copier.logic.models.ConflictingFileOption;
import folder_copier.ui.j_components.DirectoryChooser;
import folder_copier.ui.j_components.FileCopyOptionJPanel;
import folder_copier.ui.j_components.FileCopyPanel;
import folder_copier.ui.j_components.FileJTextField;
import folder_copier.ui.j_components.HorizontalSeparator;
import folder_copier.ui.j_components.PropertyBackedJCheckBox;
import folder_copier.ui.j_components.StatusPanel;
import folder_copier.ui.workers.FileCopyTask;

/**
 * The window containing all the GUI for this application.
 */
public class AppWindow extends JFrame {
	
	private static final long serialVersionUID = 2516782550252442192L;

	public static final String APP_NAME = "Folder copier";
	public final static int GAP = 10;
	
	private final PropertyManager properties;
	private final JPanel contentPane;
	private final JFileChooser directoryChooser = new DirectoryChooser();
	private final FileJTextField sourceFileTextField;
	private final FileJTextField destinationFileTextField;
	private final JCheckBox deleteOrphanInDestinationJCheckBox;
	private JButton sourceFileChooseButton;
	private JButton destinationFileChooseButton;
	private FileCopyTask task;
	private FileCopyOptionJPanel fileCopyOptionPanel;
	
	/**
	 * Creates the window.
	 */
	public AppWindow() {
		this.properties = new PropertyManager("folder-copier");
		this.sourceFileTextField = createJFileTextField("text.field.source.directory");
		this.destinationFileTextField = createJFileTextField("text.field.destination.directory");
		
		this.deleteOrphanInDestinationJCheckBox = new PropertyBackedJCheckBox(
			this.properties, "checkbox.delete-orphan", "Delete orphan files in destination"
		);
		
		//Set up the window.
	    this.setTitle(APP_NAME);
	    this.setMinimumSize(new Dimension(450, 350));
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	    //Create the menu bar.
	    JMenuBar menuBar = new JMenuBar();
	    menuBar.setOpaque(true);
	
	    //Create a panel.
	    this.contentPane = new JPanel(new BorderLayout());
	    this.setContentPane(contentPane);
	    
	    //Set the menu bar.
	    this.setJMenuBar(menuBar);
	    
	    this.drawContentPane();
	
	    //Display the window.
	    this.pack();
	    this.setVisible(true);
	}
	
	public FileCopyTask getTask() {
		return task;
	}

	public void setTask(FileCopyTask task) {
		this.task = task;
	}

	public FileJTextField getSourceFileTextField() {
		return sourceFileTextField;
	}

	public FileJTextField getDestinationFileTextField() {
		return destinationFileTextField;
	}

	public JButton getSourceFileChooseButton() {
		return sourceFileChooseButton;
	}

	public JButton getDestinationFileChooseButton() {
		return destinationFileChooseButton;
	}
	
	public Collection<JRadioButton> getFileCopyOptionRadios() {
		return this.fileCopyOptionPanel.getAllButtons();
	}
	
	public JCheckBox getDeleteOrphanInDestinationJCheckBox() {
		return deleteOrphanInDestinationJCheckBox;
	}

	/**
	 * Returns the file copy option that is currently selected in
	 * the UI.
	 * @return The file copy option that is currently selected in
	 * the UI.
	 */
	public ConflictingFileOption getConflictingFileOption() {
		return this.fileCopyOptionPanel.getConflictingFileOption();
	}
	
	/**
	 * Returns whether the option to delete files in the destination
	 * directory that don't exist in the source directory is
	 * selected.
	 * @return <code>true</code> or <code>false</code>.
	 */
	public boolean deleteOrphanInDestination() {
		return this.deleteOrphanInDestinationJCheckBox.isSelected();
	}
	
	private static void adjustTextField(JTextField jTextField) {
		jTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, jTextField.getPreferredSize().height));
	}
	
	private FileJTextField createJFileTextField(String propertyKey) {
		FileJTextField jFileTextField = new FileJTextField(this.properties, propertyKey);
		adjustTextField(jFileTextField);
		return jFileTextField;
	}
	
	private JButton addFolderSelectionComponents(
		Container verticalPanel, FileJTextField fileTextField, String label
	) {
		JPanel folderSelectionPanel = new JPanel();
		folderSelectionPanel.setLayout(new BoxLayout(folderSelectionPanel, BoxLayout.Y_AXIS));
		folderSelectionPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
		folderSelectionPanel.add(new JLabel(label));
		folderSelectionPanel.add(Box.createVerticalStrut(GAP));
		JPanel horizontalPanel = new JPanel();
	    BoxLayout horizontalLayout = new BoxLayout(horizontalPanel, BoxLayout.X_AXIS);
		horizontalPanel.setLayout(horizontalLayout);
	    
	    /*
	     * Necessary so the horizontal panel and the labels are aligned
	     * the same way horizontally.
	     */
	    horizontalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	    
		horizontalPanel.add(fileTextField);
		horizontalPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));
		JButton selectDirectoryButton = new JButton("Select folder");
		selectDirectoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = AppWindow.this.directoryChooser.showOpenDialog(verticalPanel);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File directory = AppWindow.this.directoryChooser.getSelectedFile();
		            fileTextField.setSelectedFile(directory);
		        }
			}});
		horizontalPanel.add(selectDirectoryButton);
		folderSelectionPanel.add(horizontalPanel);
		verticalPanel.add(folderSelectionPanel);
		verticalPanel.add(new HorizontalSeparator());
		return selectDirectoryButton;
	}
	
	private void drawContentPane() {
		JPanel verticalPanel = new JPanel();
		verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
		this.sourceFileChooseButton = addFolderSelectionComponents(
			verticalPanel, this.sourceFileTextField, "Source folder"
		);
		this.destinationFileChooseButton = addFolderSelectionComponents(
			verticalPanel, this.destinationFileTextField, "Destination folder"
		);
		
		this.fileCopyOptionPanel = new FileCopyOptionJPanel(this.properties);
		// Necessary so the components in the vertical panel align horizontally the same way.
		this.fileCopyOptionPanel.setAlignmentX(LEFT_ALIGNMENT);
		this.fileCopyOptionPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
		verticalPanel.add(this.fileCopyOptionPanel);
		verticalPanel.add(new HorizontalSeparator());
		
		JPanel otherOptionsJPanel = new JPanel();
		otherOptionsJPanel.setLayout(new BoxLayout(otherOptionsJPanel, BoxLayout.Y_AXIS));
		otherOptionsJPanel.add(new JLabel("Other options"));
		otherOptionsJPanel.add(this.deleteOrphanInDestinationJCheckBox);
		otherOptionsJPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
		verticalPanel.add(otherOptionsJPanel);
        verticalPanel.add(new HorizontalSeparator());
		
		JPanel fileCopyPanel = new FileCopyPanel(this);
		// Necessary so the components in the vertical panel align horizontally the same way.
		fileCopyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		verticalPanel.add(fileCopyPanel);
		this.contentPane.add(verticalPanel, BorderLayout.CENTER);
		
		StatusPanel statusPanel = new StatusPanel();
		this.contentPane.add(statusPanel, BorderLayout.SOUTH);
	}
}
