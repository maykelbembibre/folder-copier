package folder_copier.ui;

import folder_copier.logic.models.FileCopyResult;

/**
 * General String tools.
 */
public class StringTools {
    
	/**
	 * Returns a string explaining the given file copy result and file count.
	 * @param result A file copy result.
	 * @param count The number of files that had that result.
	 * @return A string.
	 */
    public static String printResult(FileCopyResult result, int count) {
    	String message;
    	if (count > 0) {
    		String resultType;
    		switch (result) {
    		case FileCopyResult.COPIED_WITH_NO_CONFLICT:
    			resultType = "created";
    			break;
    		case FileCopyResult.SKIPPED:
    			resultType = "skipped";
    			break;
    		case FileCopyResult.OVERWRITTEN:
    			resultType = "overwritten";
    			break;
    		default:
    			resultType = null;
    			break;
    		}
    		if (resultType == null) {
    			message = "";
    		} else {
    			message = "Files " + resultType + ": " + count + ".\n";
    		}
    	} else {
    		message = "";
    	}
    	return message;
    }
}
