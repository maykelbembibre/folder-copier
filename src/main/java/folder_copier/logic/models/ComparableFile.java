package folder_copier.logic.models;

import java.io.File;
import java.util.Comparator;
import java.util.Objects;

/**
 * A file that is comparable by its name according to a given string comparator.
 */
public class ComparableFile implements Comparable<ComparableFile> {

	private final Comparator<String> comparator;
	private final File file;
	private final String name;

	/**
	 * Creates a comparable file.
	 * @param comparator A string comparator. This cannot be <code>null</code>.
	 * @param file A file. This cannot be <code>null</code>.
	 */
	public ComparableFile(Comparator<String> comparator, File file) {
		Objects.requireNonNull(comparator);
		Objects.requireNonNull(file);
		this.comparator = comparator;
		this.file = file;
		this.name = file.getName();
	}

	/**
	 * Returns the file.
	 * @return The file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Compares the files by their name lexicographically. Directories always
	 * go before than files.
	 * @param o The other file.
	 */
	@Override
	public int compareTo(ComparableFile o) {
		int result;
		if (o == null) {
			result = 1;
		} else {
			if (this.file.isDirectory()) {
				if (o.file.isDirectory()) {
					result = this.comparator.compare(this.name, o.name);					
				} else {
					result = -1;
				}
			} else {
				if (o.file.isDirectory()) {
					result = 1;
				} else {
					result = this.comparator.compare(this.name, o.name);					
				}
			}
		}
		return result;
	}
}
