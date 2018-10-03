package net.lintford.library.core.storage;

import java.io.File;

import net.lintford.library.ConstantsTable;

public class AppStorage {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static String createGameDataDirectory(String pApplicationName) {
		String lSaveFolder = null;
		String os = System.getProperty("os.name").toLowerCase();

		if (os.startsWith("win")) {
			lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + pApplicationName + FILE_SEPERATOR;

		} else if (os.startsWith("linux") || os.startsWith("mac") || os.startsWith("darwin")) {
			lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + pApplicationName + FILE_SEPERATOR;

		}

		File directory = new File(String.valueOf(lSaveFolder));
		if (!directory.exists()) {
			directory.mkdir();
			
		}

		return lSaveFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. The data folder will be created as Roaming\pApplicationName
	 */
	public static String getGameDataDirectory(String pApplicationName) {

		String lSaveFolder = null;
		String os = System.getProperty("os.name").toLowerCase();

		if (os.startsWith("win")) {
			lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + pApplicationName + FILE_SEPERATOR;

		} else if (os.startsWith("linux") || os.startsWith("mac") || os.startsWith("darwin")) {
			lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + pApplicationName + FILE_SEPERATOR;

		}

		return lSaveFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. Will use the APPLICATION_NAME from the ConstantsTable, if one has been defined.
	 */
	public static String getGameDataDirectory() {
		final String lApplicationName = ConstantsTable.getStringValueDef("APPLICATION_NAME", "LintfordLib");

		return getGameDataDirectory(lApplicationName);

	}

}
