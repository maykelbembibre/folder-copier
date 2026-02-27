package folder_copier.logic;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import folder_copier.logic.models.FileCopyAction;
import folder_copier.logic.models.FileCopyResults;
import folder_copier.logic.models.PathCollection;
import folder_copier.ui.StringTools;

/**
 * General logic tools.
 */
public class Tools {

	private static final Logger LOGGER = LogManager.getLogger(Tools.class);
	
	public static void logFiles(PathCollection fileCollection) {
		if (fileCollection.isEmpty()) {
			LOGGER.info("No files.");
		}
		for (Path path : fileCollection.getFilePaths()) {
			LOGGER.info(path.toString());
		}
	}
	
	public static void logFilesAndDirectories(PathCollection fileCollection) {
		if (fileCollection.isEmpty()) {
			LOGGER.info("No files.");
		}
		for (Path path : fileCollection.getAllPaths()) {
			LOGGER.info(path.toString());
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
    
    public static void logResults(FileCopyResults results) {
    	logResult(results, FileCopyAction.COPIED_WITH_NO_CONFLICT);
    	logResult(results, FileCopyAction.OVERWRITTEN);
    	logResult(results, FileCopyAction.OVERWRITTEN_SUSPECTED_CORRUPTION);
    }
    
    private static void logResult(FileCopyResults results, FileCopyAction action) {
    	PathCollection affectedFiles;
    	LOGGER.info(StringTools.printResultTitle(action));
		affectedFiles = results.getAffectedFiles(action);
		Tools.logFiles(affectedFiles);
		LOGGER.info("");
    }
}
