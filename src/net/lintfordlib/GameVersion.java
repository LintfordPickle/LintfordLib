package net.lintfordlib;

import java.time.Year;

import net.lintfordlib.core.debug.Debug;

public class GameVersion {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static int appVersionMajor = 0;
	private static int appVersionMinor = 1;
	private static int appVersionBuild = 1;
	private static String appVersionPostFix = "032023";

	public static final String AUTHOR = "LintfordPickle";
	public static final String VERSIONYEAR = Year.now().toString();

	private static final String DELIMITOR = ".";

	/** Returns the game version, including major, minor and build number, delimited by a '.' */
	public static final String GAME_VERSION = getGameVersion();

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static void setGameVersion(int major, int minor, int build, String postFix) {
		appVersionMajor = major;
		appVersionMinor = minor;
		appVersionBuild = build;
		appVersionPostFix = postFix;
	}

	/** Returns the game version as a string */
	private static String getGameVersion() {
		return appVersionMajor + DELIMITOR + appVersionMinor + DELIMITOR + appVersionBuild + DELIMITOR + appVersionPostFix;
	}

	/** Checks if the given version string matches the version of the game as defined in {@link GameVersion} */
	public static boolean checkVersion(String versionString) {
		int mj = 0;
		int mi = 1;
		int bu = 0;
		String buildDate = null;

		final var lParts = versionString.split(DELIMITOR);

		if (lParts.length != 4) {
			Debug.debugManager().logger().e(GameVersion.class.getSimpleName(), "Not a valid app version (not enough parts)");
			return false;
		}

		mj = Integer.parseInt(lParts[0]); // Major
		mi = Integer.parseInt(lParts[1]); // Minor
		bu = Integer.parseInt(lParts[2]); // Hotfix
		buildDate = lParts[3]; // Date

		return appVersionMajor == mj && appVersionMinor == mi && appVersionBuild == bu && appVersionPostFix.equals(buildDate);
	}
}
