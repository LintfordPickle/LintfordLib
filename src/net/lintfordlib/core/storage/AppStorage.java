package net.lintfordlib.core.storage;

import java.io.File;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;

public class AppStorage {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** Use working directory for app storage (System.getProperty("user.dir")) */
	// https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
	public static boolean useLocalDirectory = true;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static String createGameDataDirectory(String applicationName) {
		String lSaveFolder = null;

		if (useLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FileUtils.FILE_SEPERATOR + "config" + FileUtils.FILE_SEPERATOR;
		} else {
			final var lOSName = System.getProperty("os.name").toLowerCase();
			if (lOSName.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FileUtils.FILE_SEPERATOR + applicationName + FileUtils.FILE_SEPERATOR;
			} else if (lOSName.startsWith("linux") || lOSName.startsWith("mac") || lOSName.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FileUtils.FILE_SEPERATOR + "." + applicationName + FileUtils.FILE_SEPERATOR;
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

		if (useLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FileUtils.FILE_SEPERATOR + "Data" + FileUtils.FILE_SEPERATOR;
		} else {
			final var lOSName = System.getProperty("os.name").toLowerCase();
			if (lOSName.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FileUtils.FILE_SEPERATOR + applicationName + FileUtils.FILE_SEPERATOR;
			} else if (lOSName.startsWith("linux") || lOSName.startsWith("mac") || lOSName.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FileUtils.FILE_SEPERATOR + "." + applicationName + FileUtils.FILE_SEPERATOR;
			}
		}

		return lSaveFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. The data folder will be created as Roaming\pApplicationName
	 */
	public static String getGameCOnfigDirectory(String applicationName) {
		String lSaveFolder = null;

		if (useLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FileUtils.FILE_SEPERATOR + "config" + FileUtils.FILE_SEPERATOR;
		} else {
			final var lOSName = System.getProperty("os.name").toLowerCase();
			if (lOSName.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FileUtils.FILE_SEPERATOR + applicationName + FileUtils.FILE_SEPERATOR;
			} else if (lOSName.startsWith("linux") || lOSName.startsWith("mac") || lOSName.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FileUtils.FILE_SEPERATOR + "." + applicationName + FileUtils.FILE_SEPERATOR;
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
