package net.lintford.library.core.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.lintford.library.core.debug.Debug;

public class FileUtils {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static StringBuilder StringBuilder = new StringBuilder();

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String NEW_LINE_CHARACTER = System.lineSeparator();

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private static void clearStringBuilder() {
		if (StringBuilder.length() == 0)
			return;

		StringBuilder.delete(0, StringBuilder.length() - 1);
	}

	public static String combinePath(String... fileParts) {
		clearStringBuilder();

		if (fileParts.length == 0)
			return null;

		StringBuilder.append(fileParts[0]);

		final var lPartCount = fileParts.length;
		for (int i = 1; i < lPartCount; i++) {
			StringBuilder.append(FILE_SEPARATOR);
			StringBuilder.append(fileParts[i]);
		}

		return StringBuilder.toString();

	}

	public static String loadString(String pPathName) {
		if (pPathName == null || pPathName.length() == 0) {
			return null;

		}

		if (pPathName.charAt(0) == '/') {
			return loadStringFromResource(pPathName);
		} else {
			return loadStringFromFile(pPathName);
		}

	}

	/** Loads a binary file as a string, from a file. */
	public static String loadStringFromFile(String pPathName) {
		clearStringBuilder();

		try {
			BufferedReader lReader = null;
			try {
				lReader = new BufferedReader(new FileReader(pPathName));
				var lBuffer = "";

				while ((lBuffer = lReader.readLine()) != null) {
					StringBuilder.append(lBuffer);
					StringBuilder.append(NEW_LINE_CHARACTER);
				}
			} finally {
				lReader.close();
			}

		} catch (IOException e) {
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), String.format("Error loading file %s", pPathName.toString()));
			Debug.debugManager().logger().e(FileUtils.class.getSimpleName(), e.getMessage());
			e.printStackTrace();

		}

		return StringBuilder.toString();
	}

	/** Loads a binary file as a string, from a resource in the jar file. */
	public static String loadStringFromResource(String pPathName) {
		clearStringBuilder();

		try {
			InputStream lInputStream = FileUtils.class.getResourceAsStream(pPathName);
			BufferedReader lReader = null;
			try {

				lReader = new BufferedReader(new InputStreamReader(lInputStream));
				var lBuffer = "";

				while ((lBuffer = lReader.readLine()) != null) {
					StringBuilder.append(lBuffer);
					StringBuilder.append(NEW_LINE_CHARACTER);
				}
			} finally {
				lReader.close();
			}
		} catch (IOException e) {
			System.err.println("Error loading text resource " + pPathName.toString());
			System.err.println(e.getMessage());
			e.printStackTrace();

		}

		return StringBuilder.toString();
	}

	/** Copies the source folder, and all its contents, to the destination folder. The destination folder is created if it doesn't already exist. */
	public static void copyFolder(File source, File destination) {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}

			final String files[] = source.list();

			for (String file : files) {
				final var srcFile = new File(source, file);
				final var destFile = new File(destination, file);

				copyFolder(srcFile, destFile);
			}

		} else {
			InputStream in = null;
			OutputStream out = null;

			try {
				in = new FileInputStream(source);
				out = new FileOutputStream(destination);

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

	public static void deleteFolder(File pSource) throws IOException {
		// Make sure that the folders and files being deleting belong to the
		if (!isChild(pSource.toPath(), AppStorage.getGameDataDirectory())) {
			throw new RuntimeException("Cannot delete from none GameStorage directory!");

		}

		if (pSource.isDirectory()) {
			for (File c : pSource.listFiles())
				deleteFolder(c);

		}

		if (!pSource.delete())
			throw new FileNotFoundException("Failed to delete file: " + pSource);

	}

	public static boolean isChild(Path child, String parentText) {
		Path parent = Paths.get(parentText).toAbsolutePath();

		return child.startsWith(parent);

	}

	public static String getFileExtension(String pFilepath) {
		final var lFile = new File(pFilepath);

		String name = lFile.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf);
	}

}