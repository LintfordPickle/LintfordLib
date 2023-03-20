package net.lintford.library;

public class GameVersion {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int APP_VERSION_MAJ = 0;
	public static final int APP_VERSION_MIN = 1;
	public static final int APP_VERSION_BUILD = 1;
	public static final String APP_DATE = "032023";

	public static final int APP_ITERATION = 1;

	public static final String DELIMITOR = ".";

	/** Returns the game version, including major, minor and build number, delimited by a '.' */
	public static final String GAME_VERSION = getGameVersion();

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the game version as a string */
	private static String getGameVersion() {
		return APP_VERSION_MAJ + DELIMITOR + APP_VERSION_MIN + DELIMITOR + APP_VERSION_BUILD + DELIMITOR + APP_DATE;
	}

	/** Checks if the given version string matches the version of the game as defined in {@link GameVersion} */
	public static boolean checkVersion(String versionString) {
		int mj, mi, bu = 0;
		String buildDate = null;

		final var lParts = versionString.split(DELIMITOR);

		if (lParts.length != 4) {
			System.err.println("Not a valid app version (not enough parts)");
			return false;
		}

		mj = Integer.parseInt(lParts[0]); // Major
		mi = Integer.parseInt(lParts[1]); // Minor
		bu = Integer.parseInt(lParts[2]); // Hotfix
		buildDate = lParts[3]; // Date

		return APP_VERSION_MAJ == mj && APP_VERSION_MIN == mi && APP_VERSION_BUILD == bu && APP_DATE.equals(buildDate);
	}
}
