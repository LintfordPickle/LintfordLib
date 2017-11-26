package net.lintford.library.core.storage;

public class GameStorage {

	public static final String APPLICATION_NAME = System.getProperty("program.name");
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");

	/**
	 * Returns a platform dependant folder which can be used for saving application data.
	 */
	public static String getGameDataDirectory() {
		String lSaveFolder = null;
		String os = System.getProperty("os.name").toLowerCase();

		if (os.startsWith("win")) {
			lSaveFolder = System.getenv("AppData") + FILE_SEPERATOR + APPLICATION_NAME + FILE_SEPERATOR;

		} else if (os.startsWith("linux") || os.startsWith("mac") || os.startsWith("darwin")) {
			lSaveFolder = System.getProperty("user.home") + FILE_SEPERATOR + "." + APPLICATION_NAME + FILE_SEPERATOR;

		}

		return lSaveFolder;
	}
	
}
