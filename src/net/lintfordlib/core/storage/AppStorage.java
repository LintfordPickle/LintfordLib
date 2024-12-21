package net.lintfordlib.core.storage;

import java.io.File;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;

public class AppStorage {

	private AppStorage() {

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** Use working directory for app storage (System.getProperty("user.dir")) */
	// https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
	public static boolean useLocalDirectory = true;

	public static final String DATA_FOLDER_NAME = "data";
	public static final String CONFIG_FOLDER_NAME = "config";

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static String createGameDirectories(String applicationName) {
		final var lDataFolder = getGameDataDirectory(applicationName);
		final var lConfigFolder = getGameConfigDirectory(applicationName);

		final var lConfigDirectory = new File(String.valueOf(lConfigFolder));
		if (!lConfigDirectory.exists()) {
			Debug.debugManager().logger().i(AppStorage.class.getSimpleName(), "Creating game config directory at: " + lConfigFolder);
			lConfigDirectory.mkdir();
		}

		final var lDataDirectory = new File(String.valueOf(lDataFolder));
		if (!lDataDirectory.exists()) {
			Debug.debugManager().logger().i(AppStorage.class.getSimpleName(), "Creating game data directory at: " + lDataFolder);
			lDataDirectory.mkdir();
		}

		return lConfigFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. The data folder will be created as Roaming\pApplicationName
	 */
	public static String getGameDataDirectory(String applicationName) {
		String lDataFolder = null;

		if (useLocalDirectory) {
			lDataFolder = System.getProperty("user.dir") + FileUtils.FILE_SEPERATOR + DATA_FOLDER_NAME + FileUtils.FILE_SEPERATOR;
		} else {
			final var lOSName = System.getProperty("os.name").toLowerCase();
			if (lOSName.startsWith("win")) {
				lDataFolder = System.getenv("AppData") + FileUtils.FILE_SEPERATOR + applicationName + FileUtils.FILE_SEPERATOR + DATA_FOLDER_NAME + FileUtils.FILE_SEPERATOR;
			} else if (lOSName.startsWith("linux") || lOSName.startsWith("mac") || lOSName.startsWith("darwin")) {
				lDataFolder = System.getProperty("user.home") + FileUtils.FILE_SEPERATOR + "." + applicationName + FileUtils.FILE_SEPERATOR + DATA_FOLDER_NAME + FileUtils.FILE_SEPERATOR;
			}
		}

		return lDataFolder;
	}

	/**
	 * Returns a platform dependant folder which can be used for saving application data. The data folder will be created as Roaming\pApplicationName
	 */
	public static String getGameConfigDirectory(String applicationName) {
		String lSaveFolder = null;

		if (useLocalDirectory) {
			lSaveFolder = System.getProperty("user.dir") + FileUtils.FILE_SEPERATOR + CONFIG_FOLDER_NAME + FileUtils.FILE_SEPERATOR;
		} else {
			final var lOSName = System.getProperty("os.name").toLowerCase();
			if (lOSName.startsWith("win")) {
				lSaveFolder = System.getenv("AppData") + FileUtils.FILE_SEPERATOR + applicationName + FileUtils.FILE_SEPERATOR + CONFIG_FOLDER_NAME + FileUtils.FILE_SEPERATOR;
			} else if (lOSName.startsWith("linux") || lOSName.startsWith("mac") || lOSName.startsWith("darwin")) {
				lSaveFolder = System.getProperty("user.home") + FileUtils.FILE_SEPERATOR + "." + applicationName + FileUtils.FILE_SEPERATOR + CONFIG_FOLDER_NAME + FileUtils.FILE_SEPERATOR;
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

	/**
	 * Returns a platform dependant folder which can be used for saving application configuration data. Will use the APPLICATION_NAME from the ConstantsTable, if one has been defined.
	 */
	public static String getGameConfigDirectory() {
		final String lApplicationName = ConstantsApp.getStringValueDef(ConstantsApp.CONSTANT_APP_NAME_TAG, "LintfordLib");
		return getGameConfigDirectory(lApplicationName);
	}
}
