package net.lintford.library.core.storage;

import net.lintford.library.ConstantsTable;

public class AppStorage {

	public static final String FILE_SEPERATOR = System.getProperty("file.separator");

	/**
	 * Returns a platform dependant folder which can be used for saving application data.
	 */
	public static String getGameDataDirectory() {
		
		final String lApplicationName = ConstantsTable.getStringValueDef("APPLICATION_NAME", "LintfordLib");
		
		String lSaveFolder = null;
		String os = System.getProperty("os.name").toLowerCase();

		if (os.startsWith("win")) {
			lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + lApplicationName + FILE_SEPERATOR;

		} else if (os.startsWith("linux") || os.startsWith("mac") || os.startsWith("darwin")) {
			lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + lApplicationName + FILE_SEPERATOR;

		}

		return lSaveFolder;
	}
	
}
