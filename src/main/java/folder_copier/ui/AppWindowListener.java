package folder_copier.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import folder_copier.ui.workers.FileCopyTask;

public class AppWindowListener implements WindowListener {

	private final AppWindow appWindow;
	
	public AppWindowListener(AppWindow appWindow) {
		this.appWindow = appWindow;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		FileCopyTask task = this.appWindow.getTask();
		if (task != null && !task.isDone()) {
			Tools.showErrorDialog(this.appWindow, "You may not close this application while a task is running.");
		} else {
			System.exit(0);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
