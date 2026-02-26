package folder_copier.ui.j_panels;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import folder_copier.ui.AppWindow;
import folder_copier.ui.Tools;
import folder_copier.ui.j_components.ReadOnlyJTextArea;
import folder_copier.ui.j_components.buttons.StartJButton;
import folder_copier.ui.j_components.buttons.StopJButton;

public class FileCopyJPanel extends JPanel {

	private static final long serialVersionUID = -9050427655895759444L;

	public FileCopyJPanel(AppWindow appWindow) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JTextArea taskOutput = new ReadOnlyJTextArea();
		JButton stopButton = new StopJButton(appWindow);
		JButton startButton = new StartJButton(appWindow, progressBar, taskOutput, stopButton);
        
        JPanel horizontalPanel = new JPanel();
        BoxLayout horizontalLayout = new BoxLayout(horizontalPanel, BoxLayout.X_AXIS);
        horizontalPanel.setBorder(BorderFactory.createEmptyBorder(AppWindow.GAP, AppWindow.GAP, AppWindow.GAP, AppWindow.GAP));
        horizontalPanel.setLayout(horizontalLayout);
        horizontalPanel.add(startButton);
        horizontalPanel.add(Box.createRigidArea(new Dimension(AppWindow.GAP, 0)));
        horizontalPanel.add(stopButton);
        horizontalPanel.add(Box.createRigidArea(new Dimension(AppWindow.GAP, 0)));
        horizontalPanel.add(progressBar);
        
        this.add(horizontalPanel);
        JComponent scrollableTaskOutput = new JScrollPane(taskOutput);
        scrollableTaskOutput.setBorder(
        	BorderFactory.createCompoundBorder(
    			BorderFactory.createEmptyBorder(0, AppWindow.GAP, AppWindow.GAP, AppWindow.GAP),
        		scrollableTaskOutput.getBorder()
        	)
        );
        
        // Make the scroll pane want to be at least as big as the text area.
        Insets scrollableTaskOutputInsets = scrollableTaskOutput.getInsets();
        Dimension taskOutputPreferredSize = taskOutput.getPreferredSize();
        scrollableTaskOutput.setPreferredSize(Tools.add(taskOutputPreferredSize, scrollableTaskOutputInsets));
        
        this.add(scrollableTaskOutput);
	}
}
