package folder_copier.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import folder_copier.logic.alphanumeric.AlphanumericComparator;
import folder_copier.logic.models.ComparableFile;

/**
 * Class that manages how files should be ordered.
 */
public class FileOrderManager {

	private static final Comparator<String> ALPHANUMERIC_COMPARATOR = new AlphanumericComparator();
	
	/**
	 * Gets the children of the given directory, ordered lexicographically.
	 * Directories always go first and files go later.
	 * @param directory A directory.
	 * @return The list of ordered children.
	 */
	public static List<File> getChildren(File directory) {
		List<ComparableFile> children = order(Arrays.asList(directory.listFiles()));
		List<File> result = new ArrayList<>();
		for (ComparableFile child : children) {
			result.add(child.getFile());
		}
		return result;
	}
	
	/**
	 * Orders the given files.
	 * @param files Files.
	 * @return The ordered list of files.
	 */
	private static List<ComparableFile> order(Iterable<File> files) {
		List<ComparableFile> result = new ArrayList<>();
		if (files != null) {
			for (File file : files) {
				if (file != null) {
					result.add(new ComparableFile(ALPHANUMERIC_COMPARATOR, file));
				}
			}
		}
		Collections.sort(result);
		return result;
	}
}
