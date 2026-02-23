package folder_copier.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import folder_copier.logic.exceptions.FileManagementException;
import folder_copier.logic.models.ConflictingFileOption;
import folder_copier.logic.models.FileCopyResult;

/**
 * Logic class for doing the file management operations.
 */
public class FileManager {

	private final ConflictingFileOption option;
	private final FileOrderManager fileOrderManager;
	
	/**
	 * Creates a file management object.
	 * @param sourceDirectory The source directory.
	 * @param destinationDirectory The destination directory.
	 * @param option Options on how to deal with file name conflicts. Must not be <code>
	 * null</code>.
	 * @throws FileManagementException If something goes wrong.
	 */
	public FileManager(File sourceDirectory, File destinationDirectory, ConflictingFileOption option) throws FileManagementException {
		if (sourceDirectory == null) {
			throw new FileManagementException("You must select a source folder.");
		} else if (!sourceDirectory.isDirectory()) {
			throw new FileManagementException("The source folder is not valid.");
		}
		if (destinationDirectory == null) {
			throw new FileManagementException("You must select a destination folder.");
		} else if (!destinationDirectory.isDirectory()) {
			throw new FileManagementException("The destination folder is not valid.");
		}
		if (sourceDirectory.getAbsolutePath().equals(destinationDirectory.getAbsolutePath())) {
			throw new FileManagementException("You cannot copy files to the same folder.");
		}
		Objects.requireNonNull(option);
		this.option = option;
		this.fileOrderManager = new FileOrderManager();
	}
	
	/**
	 * Gets the children already filtered by file extension and ordered
	 * lexicographically.
	 * @param directory A directory.
	 * @return The list of filtered and ordered children.
	 */
	public List<File> getChildren(File directory) {
		return this.fileOrderManager.getChildren(directory);
	}
	
	/**
	 * Copies the given file to the given directory.
	 * @param sourceFile A file.
	 * @param destinationDirectory A directory.
	 * @throws IOException If something goes wrong.
	 */
	public FileCopyResult copyFileToDirectory(File sourceFile, File destinationDirectory) throws IOException {
		Path sourceFilePath = sourceFile.toPath();
		File destinationFile = new File(destinationDirectory, sourceFile.getName());
		Path destinationFilePath = destinationFile.toPath();
		FileCopyResult result;
		if (destinationFile.isDirectory()) {
			result = FileCopyResult.SKIPPED;
		} else {
			if (destinationFile.exists()) {
				long sourceSize = getSize(sourceFilePath);
				long destinationSize = getSize(destinationFilePath);
				if (sourceSize > destinationSize) {
					result = FileCopyResult.OVERWRITTEN;
				} else {
					switch (this.option) {
					case ConflictingFileOption.SKIP:
						result = FileCopyResult.SKIPPED;
						break;
					case ConflictingFileOption.OVERWRITE:
						result = FileCopyResult.OVERWRITTEN;
						break;
					case ConflictingFileOption.OVERWRITE_IF_NEWER:
						if (sourceFile.lastModified() > destinationFile.lastModified()) {
							result = FileCopyResult.OVERWRITTEN;
						} else {
							result = FileCopyResult.SKIPPED;
						}
						break;
					default:
						throw new IOException("Conflicting file option " + this.option + " is not valid.");
					}
				}
			} else {
				result = FileCopyResult.COPIED_WITH_NO_CONFLICT;
			}
		}
		if (result.copy()) {
			Files.copy(sourceFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
		}
		return result;
	}
	
	/**
	 * Creates the directory if it doesn't exist.
	 * @param directory A directory.
	 * @return Whether the directory was created or it already existed.
	 */
	public static boolean createIfNotExists(File directory) {
		boolean result;
		if (directory.exists()) {
			result = true;
		} else {
			result = directory.mkdir();
		}
		return result;
	}
	
	/**
	 * Deletes the directory if it is empty.
	 * @param directory A directory.
	 */
	public static void deleteIfEmpty(File directory) {
		File[] files = directory.listFiles();
		if (files != null && files.length < 1) {
			directory.delete();
		}
	}
	
	private static long getSize(Path filePath) throws IOException {
		long result;
		if (Files.exists(filePath)) {
			result = Files.size(filePath);
		} else {
			result = 0;
		}
		return result;
	}
}
