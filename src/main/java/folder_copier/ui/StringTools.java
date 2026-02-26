package folder_copier.ui;

import folder_copier.logic.models.FileCopyAction;

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
    public static String printResult(FileCopyAction result, int count) {
    	String message;
    	if (count > 0) {
    		String resultType;
    		switch (result) {
    		case FileCopyAction.COPIED_WITH_NO_CONFLICT:
    			resultType = "created";
    			break;
    		case FileCopyAction.SKIPPED:
    			resultType = "skipped";
    			break;
    		case FileCopyAction.OVERWRITTEN:
    			resultType = "overwritten";
    			break;
    		default:
    			resultType = null;
    			break;
    		}
    		if (resultType == null) {
    			message = "";
    		} else {
    			message = "  of which " + resultType + ": " + count + ".\n";
    		}
    	} else {
    		message = "";
    	}
    	return message;
    }
    
    /**
     * Prints a title suitable for the given file copy action.
     * @param action A file copy action.
     * @return The title.
     */
    public static String printResultTitle(FileCopyAction action) {
    	String title;
		String resultType;
		switch (action) {
		case FileCopyAction.COPIED_WITH_NO_CONFLICT:
			resultType = "created";
			break;
		case FileCopyAction.SKIPPED:
			resultType = "skipped";
			break;
		case FileCopyAction.OVERWRITTEN:
			resultType = "overwritten";
			break;
		default:
			resultType = null;
			break;
		}
		if (resultType == null) {
			title = "";
		} else {
			title = "Files that have been " + resultType + ":";
		}
    	return title;
    }
}
