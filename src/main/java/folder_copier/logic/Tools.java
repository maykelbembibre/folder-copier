package folder_copier.logic;

import java.nio.file.Path;

import folder_copier.logic.models.FileCopyAction;
import folder_copier.logic.models.FileCopyResults;
import folder_copier.logic.models.PathCollection;
import folder_copier.ui.StringTools;

/**
 * General logic tools.
 */
public class Tools {

	public static void logFiles(PathCollection fileCollection, Logger logger) {
		if (fileCollection.isEmpty()) {
			logger.println("No files.");
		}
		for (Path path : fileCollection.getFilePaths()) {
			logger.println(path.toString());
		}
	}
	
	public static void logFilesAndDirectories(PathCollection fileCollection, Logger logger) {
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
	
    public static String printResults(FileCopyResults results) {
    	StringBuilder builder = new StringBuilder();
    	int count;
    	for (FileCopyAction value : FileCopyAction.values()) {
    		count = results.getNumberOfOccurrences(value);
    		builder.append(StringTools.printResult(value, count));
    	}
    	return builder.toString();
    }
    
    public static void logResults(FileCopyResults results, Logger logger) {
    	logResult(results, FileCopyAction.COPIED_WITH_NO_CONFLICT, logger);
    	logResult(results, FileCopyAction.OVERWRITTEN, logger);
    }
    
    private static void logResult(FileCopyResults results, FileCopyAction action, Logger logger) {
    	PathCollection affectedFiles;
		logger.println(StringTools.printResultTitle(action));
		affectedFiles = results.getAffectedFiles(action);
		Tools.logFiles(affectedFiles, logger);
		logger.println("");
    }
}
