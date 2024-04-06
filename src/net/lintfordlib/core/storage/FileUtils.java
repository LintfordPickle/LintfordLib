package net.lintfordlib.core.storage;

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
import java.util.Comparator;
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
			if (lFile.exists() == false) {
				Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Could not load string from file '" + resourcepath + "'. The file does not exist!");
				return null;
			}

			return loadStringFromFile(resourcepath);
		}
	}

	/** Loads a binary file from disk and return its contents as a string. */
	public static String loadStringFromFile(String filepath) {
		clearStringBuilder();

		try {
			BufferedReader lReader = null;
			try {
				lReader = new BufferedReader(new FileReader(filepath));
				var lBuffer = "";

				while ((lBuffer = lReader.readLine()) != null) {
					mStringBuilder.append(lBuffer);
					mStringBuilder.append(LINE_SEPERATOR);
				}
			} finally {
				if (lReader != null) {
					lReader.close();
				}
			}

		} catch (IOException e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error loading text resource " + String.format("Error loading file %s", filepath.toString()));
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		}

		return mStringBuilder.toString();
	}

	/** Loads a binary file from within the jar and returns its contents as a string. */
	public static String loadStringFromResource(String resourceLocation) {
		clearStringBuilder();

		try {
			InputStream lInputStream = FileUtils.class.getResourceAsStream(resourceLocation);
			BufferedReader lReader = null;
			try {
				lReader = new BufferedReader(new InputStreamReader(lInputStream));
				var lBuffer = "";

				while ((lBuffer = lReader.readLine()) != null) {
					mStringBuilder.append(lBuffer);
					mStringBuilder.append(LINE_SEPERATOR);
				}
			} finally {
				if (lReader != null) {
					lReader.close();
				}
			}
		} catch (IOException e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), "Error loading text resource " + resourceLocation.toString());
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
		}

		return mStringBuilder.toString();
	}

	/** Copies the source folder, and all its contents, to the destination folder. The destination folder is created if it doesn't already exist. */
	public static void copyFolder(File sourceFolder, File destinationFolder) {
		if (sourceFolder.isDirectory()) {
			if (!destinationFolder.exists()) {
				destinationFolder.mkdirs();
			}

			final String files[] = sourceFolder.list();

			for (String file : files) {
				final var srcFile = new File(sourceFolder, file);
				final var destFile = new File(destinationFolder, file);

				copyFolder(srcFile, destFile);
			}

		} else {
			InputStream in = null;
			OutputStream out = null;

			try {
				in = new FileInputStream(sourceFolder);
				out = new FileOutputStream(destinationFolder);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			} catch (Exception e) {
				try {
					if (in != null)
						in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					if (out != null)
						out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
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
		if (!file.delete()) {
			throw new FileNotFoundException("Failed to delete file: " + file);
		}
	}

	public static String cleanFilename(String filename) {
		// Replaces whitespace with an underscores
		return filename.replaceAll("\\s+", "");
	}

	// --------------------------------------

	public static List<File> getListOfFileInDirectory(String directory) {
		return getListOfFileInDirectory(directory, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return true; // accepts all files
			}
		});
	}

	public static List<File> getListOfFileInDirectory(String directory, String extension) {
		return getListOfFileInDirectory(directory, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(extension);
			}
		});
	}

	public static List<File> getListOfFileInDirectory(String directory, FilenameFilter filter) {
		final var lDirectory = new File(directory);
		final var lListOfFiles = lDirectory.listFiles(filter);

		if (lListOfFiles == null) {
			return new ArrayList<File>();
		}

		return Arrays.asList(lListOfFiles);
	}

	public static List<File> getListOfFilesInDirectorySortedByDate(String directory, String extension) {
		return getListOfFilesInDirectorySortedByDate(directory, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(extension);
			}
		});
	}

	public static List<File> getListOfFilesInDirectorySortedByDate(String directory, FilenameFilter filter) {
		final var lDirectory = new File(directory);
		final var lFileList = lDirectory.listFiles(filter);

		Arrays.sort(lFileList, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			}
		});

		return Arrays.asList(lFileList);
	}
}