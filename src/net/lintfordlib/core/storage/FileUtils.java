package net.lintfordlib.core.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.lintfordlib.core.debug.Debug;

public class FileUtils {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final StringBuilder mStringBuilder = new StringBuilder();

	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private FileUtils() {

	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private static void clearStringBuilder() {
		if (mStringBuilder.length() == 0)
			return;

		mStringBuilder.delete(0, mStringBuilder.length() - 1);
	}

	public static String combinePath(String... fileParts) {
		clearStringBuilder();

		if (fileParts.length == 0)
			return null;

		mStringBuilder.append(fileParts[0]);

		final var lPartCount = fileParts.length;
		for (int i = 1; i < lPartCount; i++) {
			mStringBuilder.append(FILE_SEPERATOR);
			mStringBuilder.append(fileParts[i]);
		}

		return mStringBuilder.toString();
	}

	public static String loadString(String resourcepath) {
		if (resourcepath == null || resourcepath.length() == 0) {
			return null;
		}

		if (resourcepath.charAt(0) == '/') {
			return loadStringFromResource(resourcepath);
		} else {
			final var lFile = new File(resourcepath);
			if (!lFile.exists()) {
				Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Could not load string from file '" + resourcepath + "'. The file does not exist!");
				return null;
			}

			return loadStringFromFile(resourcepath);
		}
	}

	/** Loads a binary file from disk and return its contents as a string. */
	public static String loadStringFromFile(String filepath) {
		clearStringBuilder();

		try (final var reader = new BufferedReader(new FileReader(filepath))) {
			var lBuffer = "";

			while ((lBuffer = reader.readLine()) != null) {
				mStringBuilder.append(lBuffer);
				mStringBuilder.append(LINE_SEPERATOR);
			}

		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error loading text resource (File not found): " + filepath);
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error loading text resource (IOException): " + filepath);
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		}

		return mStringBuilder.toString();
	}

	/** Loads a binary file from within the jar and returns its contents as a string. */
	public static String loadStringFromResource(String resourceLocation) {
		clearStringBuilder();

		try {
			final var lInputStream = FileUtils.class.getResourceAsStream(resourceLocation);

			try (final var reader = new BufferedReader(new InputStreamReader(lInputStream))) {
				var lBuffer = "";

				while ((lBuffer = reader.readLine()) != null) {
					mStringBuilder.append(lBuffer);
					mStringBuilder.append(LINE_SEPERATOR);
				}
			}

		} catch (IOException e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error loading text resource: " + resourceLocation);
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		}

		return mStringBuilder.toString();
	}

	/** Copies the source folder, and all its contents, to the destination folder. The destination folder is created if it doesn't already exist. */
	public static void copyFolder(File sourceFile, File destFile) {
		if (sourceFile.isDirectory()) {
			if (!destFile.exists()) {
				destFile.mkdirs();
			}

			final String[] files = sourceFile.list();

			for (String file : files) {
				copyFolder(new File(sourceFile, file), new File(destFile, file));
			}

		} else {
			try (InputStream in = new FileInputStream(sourceFile)) {
				writeToFile(sourceFile, in);
			} catch (Exception e) {
				Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error copying file: " + sourceFile.getPath());
				Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
			}
		}
	}

	private static void writeToFile(File destFile, InputStream streamToWrite) {
		try (OutputStream out = new FileOutputStream(destFile)) {
			byte[] buffer = new byte[1024];

			int length;
			while ((length = streamToWrite.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

		} catch (Exception e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error writing to file: " + destFile.getPath());
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		}
	}

	public static boolean copyFile(File oldAssetFile, File newAssetFile) {
		if (oldAssetFile == null || !oldAssetFile.exists())
			return false;

		if (newAssetFile == null)
			return false;

		if (!newAssetFile.exists()) {
			try {
				newAssetFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try (var in = new BufferedInputStream(new FileInputStream(oldAssetFile)); var out = new BufferedOutputStream(new FileOutputStream(newAssetFile))) {
			final var buffer = new byte[1024];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
				out.flush();
			}

			return true;

		} catch (IOException e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error copying file from " + oldAssetFile.getPath() + " to " + newAssetFile.getPath());
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		} catch (Exception e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Unexpected error while copying file: " + oldAssetFile.getPath() + " to " + newAssetFile.getPath());
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		}

		return false;
	}

	/* deletes the given file or folder, includes all files and subdirectories. Note the file or folder specified must be contained within the GameStorage directory */
	public static void deleteFolder(File fileToDelete) throws IOException {
		// Make sure that the folders and files being deleting belongs within the app storage folder
		if (!isChild(fileToDelete.toPath(), AppStorage.getGameDataDirectory())) {
			throw new RuntimeException("Cannot delete from none GameStorage directory!");
		}

		if (fileToDelete.isDirectory()) {
			for (File lFile : fileToDelete.listFiles())
				deleteFolder(lFile);
		}

		if (!fileToDelete.delete())
			throw new FileNotFoundException("Failed to delete file: " + fileToDelete);
	}

	public static boolean isChild(Path child, String parentText) {
		Path parent = Paths.get(parentText).toAbsolutePath();

		return child.startsWith(parent);
	}

	public static String getFileExtension(String filepath) {
		final var lFile = new File(filepath);

		final var lFilename = lFile.getName();
		int lastIndexOf = lFilename.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return "";
		}
		return lFilename.substring(lastIndexOf);
	}

	public static void deleteFile(File file) throws IOException {
		if (file.isDirectory()) {
			for (final var lFileInDirectory : file.listFiles()) {
				deleteFile(lFileInDirectory);
			}
		}

		if (!file.delete())
			throw new FileNotFoundException("Failed to delete file: " + file);

	}

	public static String cleanFilename(String filename) {
		// Replaces whitespace with an underscores
		return filename.replaceAll("\\s+", "");
	}

	public static String separatorsToSystem(String filePath) {
		final var lFile = new File(filePath);
		return lFile.getPath();
	}

	public static Path makeRelative(String path1, String path2) {
		final var pathAbsolute = Paths.get(path1);
		final var pathBase = Paths.get(path2);

		return pathBase.relativize(pathAbsolute);
	}

	// --------------------------------------

	public static List<File> getListOfFilesInDirectory(String directory) {
		// accepts all files
		return getListOfFilesInDirectory(directory, (dir, name) -> true);
	}

	public static List<File> getListOfFilesInDirectory(String directory, String extension) {
		return getListOfFilesInDirectory(directory, (dir, name) -> name.endsWith(extension));
	}

	public static List<File> getListOfFilesInDirectory(String directory, FilenameFilter filter) {
		final var lDirectory = new File(directory);
		final var lListOfFiles = lDirectory.listFiles(filter);

		if (lListOfFiles == null)
			return new ArrayList<>();

		return Arrays.asList(lListOfFiles);
	}

	public static List<File> getListOfFilesInDirectorySortedByDate(String directory, String extension) {
		return getListOfFilesInDirectorySortedByDate(directory, (dir, name) -> name.endsWith(extension));
	}

	public static List<File> getListOfFilesInDirectorySortedByDate(String directory, FilenameFilter filter) {
		final var lDirectory = new File(directory);
		final var lFileList = lDirectory.listFiles(filter);

		Arrays.sort(lFileList, (file1, file2) -> Long.compare(file2.lastModified(), file1.lastModified()));

		return Arrays.asList(lFileList);
	}

	public List<File> getListOfFilesInResourceDirectory(String resourceDirectory, String subDirectory, String extType) {
		final var path = Paths.get(resourceDirectory, subDirectory);

		final var lScenesDirectory = path.toFile();
		final var lSubDirectoryList = lScenesDirectory.listFiles((dir, name) -> new File(dir, name).isDirectory());

		final List<File> lAllHeaderFiles = new ArrayList<>();

		if (lSubDirectoryList == null)
			return lAllHeaderFiles;

		for (var subDir : lSubDirectoryList) {
			final var lFilesInSubDir = FileUtils.getListOfFilesInDirectory(subDir.getPath(), extType);
			lAllHeaderFiles.addAll(lFilesInSubDir);
		}

		return lAllHeaderFiles;
	}

}