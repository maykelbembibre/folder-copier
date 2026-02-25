package folder_copier.logic;

import java.nio.file.Path;

import folder_copier.logic.models.PathCollection;

/**
 * General logic tools.
 */
public class Tools {

	public static void logFiles(Logger logger, PathCollection fileCollection) {
		if (fileCollection.isEmpty()) {
			logger.println("No files.");
		}
		for (Path path : fileCollection.getFilePaths()) {
			logger.println(path.toString());
		}
	}
	
	public static void logFilesAndDirectories(Logger logger, PathCollection fileCollection) {
		if (fileCollection.isEmpty()) {
			logger.println("No files.");
		}
		for (Path path : fileCollection.getDirectoryPaths()) {
			logger.println(path.toString());
		}
		for (Path path : fileCollection.getFilePaths()) {
			logger.println(path.toString());
		}
	}
}
