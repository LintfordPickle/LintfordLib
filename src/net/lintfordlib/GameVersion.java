package net.lintfordlib;

import java.time.Year;

public class GameVersion {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static int APP_VERSION_MAJ = 0;
	private static int APP_VERSION_MIN = 1;
	private static int APP_VERSION_BUILD = 1;
	private static String APP_POSTFIX = "032023";

	public static final String Author = "LintfordPickle";
	public static final String Delimitor = ".";
	public static final String VersionYear = Year.now().toString();

	private static final String DELIMITOR = ".";

	/** Returns the game version, including major, minor and build number, delimited by a '.' */
	public static final String GAME_VERSION = getGameVersion();

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static void setGameVersion(int major, int minor, int build, String postFix) {
		APP_VERSION_MAJ = major;
		APP_VERSION_MIN = minor;
		APP_VERSION_BUILD = build;
		APP_POSTFIX = postFix;
	}

	/** Returns the game version as a string */
	private static String getGameVersion() {
		return APP_VERSION_MAJ + DELIMITOR + APP_VERSION_MIN + DELIMITOR + APP_VERSION_BUILD + DELIMITOR + APP_POSTFIX;
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

		return APP_VERSION_MAJ == mj && APP_VERSION_MIN == mi && APP_VERSION_BUILD == bu && APP_POSTFIX.equals(buildDate);
	}
}
