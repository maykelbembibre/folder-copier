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
import folder_copier.logic.models.FileCopyAction;
import folder_copier.logic.models.FileCopyResult;

/**
 * Logic class for doing the file management operations.
 */
public class FileManager {

	private final ConflictingFileOption option;
	
	/**
	 * Creates a file management object.
	 * @param option Options on how to deal with file name conflicts. Must not be <code>
	 * null</code>.
	 */
	public FileManager(ConflictingFileOption option) {
		Objects.requireNonNull(option);
		this.option = option;
	}
	
	/**
	 * Checks that the source and destination directories are correct or throws an
	 * exception if they are not.
	 * @param sourceDirectory The source directory.
	 * @param destinationDirectory The destination directory.
	 * @throws FileManagementException If the source or destination directories are
	 * not correct.
	 */
	public static void checkDirectories(File sourceDirectory, File destinationDirectory) throws FileManagementException {
		if (sourceDirectory == null) {
			throw new FileManagementException("You must select a source folder.");
		} else if (!sourceDirectory.isDirectory()) {
			throw new FileManagementException("The source folder is not valid.");
		}
		if (destinationDirectory == null) {
			throw new FileManagementException("You must select a destination folder.");
		} else {
			destinationDirectory.mkdirs();
			if (!destinationDirectory.isDirectory()) {
				throw new FileManagementException("The destination folder is not valid.");
			}
		}
		if (sourceDirectory.getAbsolutePath().equals(destinationDirectory.getAbsolutePath())) {
			throw new FileManagementException("You cannot copy files to the same folder.");
		}
		checkForDirectoryLoop(sourceDirectory, destinationDirectory);
	}
	
	/**
	 * Gets the children already filtered by file extension and ordered
	 * lexicographically.
	 * @param directory A directory.
	 * @return The list of filtered and ordered children.
	 */
	public static List<File> getChildren(File directory) {
		return FileOrderManager.getChildren(directory);
	}
	
	/**
	 * Copies the given file to the given directory. Note that if a directory
	 * already exists in the destination directory with the same name as the
	 * file to copy, the file will be skipped. 
	 * @param file The file to copy.
	 * @param destinationDirectory The destination directory to copy the
	 * file to.
	 * @throws IOException If something goes wrong.
	 */
	public FileCopyResult copyFileToDirectory(File file, File destinationDirectory) throws IOException {
		Path sourceFilePath = file.toPath();
		File destinationFile = new File(destinationDirectory, file.getName());
		Path destinationFilePath = destinationFile.toPath();
		FileCopyAction action;
		if (destinationFile.isDirectory()) {
			action = FileCopyAction.SKIPPED;
		} else {
			if (destinationFile.exists()) {
				long sourceSize = getSize(sourceFilePath);
				long destinationSize = getSize(destinationFilePath);
				if (sourceSize > destinationSize) {
					action = FileCopyAction.OVERWRITTEN;
				} else {
					switch (this.option) {
					case ConflictingFileOption.SKIP:
						action = FileCopyAction.SKIPPED;
						break;
					case ConflictingFileOption.OVERWRITE:
						action = FileCopyAction.OVERWRITTEN;
						break;
					case ConflictingFileOption.OVERWRITE_IF_NEWER:
						if (file.lastModified() > destinationFile.lastModified()) {
							action = FileCopyAction.OVERWRITTEN;
						} else {
							action = FileCopyAction.SKIPPED;
						}
						break;
					default:
						throw new IOException("Conflicting file option " + this.option + " is not valid.");
					}
				}
			} else {
				action = FileCopyAction.COPIED_WITH_NO_CONFLICT;
			}
		}
		if (action.copy()) {
			Files.copy(sourceFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
		}
		return new FileCopyResult(destinationFilePath, action);
	}
	
	/**
	 * Creates the directory if it doesn't exist.
	 * 
	 * @param directory A directory. Note that if this directory is really a file, a
	 *                  directory with the same name can't be created so this method
	 *                  won't create anything.
	 * @return <code>true</code> if the directory has been created or if it already
	 *         existed. <code>False</code> if the directory couldn't be created
	 *         (i.e. file system permission denial or the given directory is in fact
	 *         a file and thus cannot be created as a directory).
	 */
	public static boolean createDirectoryIfNotExists(File directory) {
		boolean result;
		if (directory.isFile()) {
			result = false;
		} else if (directory.isDirectory()) {
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
	public static void deleteDirectoryIfEmpty(File directory) {
		File[] files = directory.listFiles();
		if (files != null && files.length < 1) {
			directory.delete();
		}
	}
	
	/**
	 * This prevents a highly problematic loop error where an infinite directory
	 * Hierarchy is copied to destinationSubdirectory.
	 * @param sourceDirectory the source directory.
	 * @param destinationDirectory the destination directory.
	 * @throws FileManagementException If the destination subdirectory is
	 * contained in the source subdirectory, that is, the user is trying
	 * to copy a directory to a child directory of itself.
	 */
	private static void checkForDirectoryLoop(File sourceDirectory, File destinationDirectory)
			throws FileManagementException {
		Path sourceDirectoryPath = sourceDirectory.toPath();
		Path destinationDirectoryPath = destinationDirectory.toPath();
		if (destinationDirectoryPath.startsWith(sourceDirectoryPath)) {
			throw new FileManagementException(
				"A loop error has occurred because you've tried to copy the contents of the directory "
				+ sourceDirectory.getAbsolutePath() + " into the directory "
				+ destinationDirectory.getAbsolutePath()
				+ ". Note that your request is absurd."
			);
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
