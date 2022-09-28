package net.lintford.library.core.storage;

import java.io.File;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.debug.Debug;

public class AppStorage {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");

	/** USe working directory for app storage (System.getProperty("user.dir"))*/
	// https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
	public static boolean UseLocalDirectory = true;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static String createGameDataDirectory(String applicationName) {
		String lSaveFolder = null;

		if (UseLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FILE_SEPERATOR + "config" + FILE_SEPERATOR;
		} else {
			final var lOSName = System.getProperty("os.name").toLowerCase();
			if (lOSName.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + applicationName + FILE_SEPERATOR;
			} else if (lOSName.startsWith("linux") || lOSName.startsWith("mac") || lOSName.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + applicationName + FILE_SEPERATOR;
			}
		}

		final var lDirectory = new File(String.valueOf(lSaveFolder));
		if (!lDirectory.exists()) {
			lDirectory.mkdir();
		}

		Debug.debugManager().logger().i("Storage", "Creating game data directory at: " + lSaveFolder);
		return lSaveFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. The data folder will be created as Roaming\pApplicationName
	 */
	public static String getGameDataDirectory(String applicationName) {
		String lSaveFolder = null;

		if (UseLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FILE_SEPERATOR + "config" + FILE_SEPERATOR;
		} else {
			final var lOSName = System.getProperty("os.name").toLowerCase();
			if (lOSName.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + applicationName + FILE_SEPERATOR;
			} else if (lOSName.startsWith("linux") || lOSName.startsWith("mac") || lOSName.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + applicationName + FILE_SEPERATOR;
			}
		}

		return lSaveFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. Will use the APPLICATION_NAME from the ConstantsTable, if one has been defined.
	 */
	public static String getGameDataDirectory() {
		final String lApplicationName = ConstantsApp.getStringValueDef(ConstantsApp.CONSTANT_APP_NAME_TAG, "LintfordLib");
		return getGameDataDirectory(lApplicationName);
	}
}
