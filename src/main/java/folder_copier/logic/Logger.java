package folder_copier.logic;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that logs into a file.
 */
public class Logger implements Closeable {

	private static final int MAX_LOGS = 1000000;
	private static final DateFormat USER_FRIENDLY_DATE_FORMAT = new SimpleDateFormat("ss:mm:HH dd/MM/yyyy");
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	private final PrintWriter printWriter;
	
	/**
	 * Creates a new object that logs into a file.
	 * @parm name The name of the application.
	 * @throws IOException If the file can't be created.
	 */
	public Logger(String name) throws IOException {
		File file = this.getNewFile(name);
		FileWriter fileWriter = new FileWriter(file);
		this.printWriter = new PrintWriter(fileWriter);
		this.println(name + " execution on " + USER_FRIENDLY_DATE_FORMAT.format(new Date()) + ".");
		this.println("");
	}
	
	/**
	 * Prints some text into the file.
	 * @param text The text to print.
	 */
	public void print(String text) {
		this.printWriter.print(text);
	}
	
	/**
	 * Prints a whole line into the file.
	 * @param line The line to print.
	 */
	public void println(String line) {
		this.printWriter.println(line);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		if (this.printWriter != null) {
			this.printWriter.close();
		}
	}
	
	private static File getWorkingDirectory() {
		String userHomePath = System.getProperty("user.home");
		File userHomeDirectory = new File(userHomePath);
		if (!userHomeDirectory.isDirectory()) {
			throw new RuntimeException("Don't have access to the user home directory so can't log into files.");
		}
		File workingDir;
		File desktop = new File(userHomeDirectory, "Desktop");
		if (desktop.isDirectory()) {
			workingDir = desktop;
		} else {
			workingDir = userHomeDirectory;
		}
		return workingDir;
	}
	
	private File getNewFile(String name) {
		File workingDir = getWorkingDirectory();
		File file;
		Date now = new Date();
		int index = 1;
		String indexString;
		do {
			if (index > 1) {
				indexString = " (" + Integer.valueOf(index).toString() + ")";
			} else {
				indexString = "";
			}
			file = new File(workingDir, name + " " + DATE_TIME_FORMAT.format(now) + indexString + ".log");
			index++;
		} while (file.exists() && index <= MAX_LOGS);
		if (file.exists()) {
			file.delete();
		}
		return file;
	}
}
