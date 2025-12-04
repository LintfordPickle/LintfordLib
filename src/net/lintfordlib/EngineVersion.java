package net.lintfordlib;

public class EngineVersion {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static int appVersionApi = 0;
	private static String appVersionDate = "20251202";
	private static int appVersionPatch = 0;

	public static final String AUTHOR = "LintfordPickle";

	private static final String DELIMITOR = ".";

	/** Returns the game version, including major, minor and build number, delimited by a '.' */
	public static final String ENGINE_VERSION = getEngineVersion();

	// --------------------------------------
	// Ctor
	// --------------------------------------

	private EngineVersion() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the game version as a string */
	private static String _engineVersion;

	private static String getEngineVersion() {
		if (_engineVersion == null) {
			_engineVersion = appVersionApi + DELIMITOR + appVersionDate + DELIMITOR + appVersionPatch;
		}

		return _engineVersion;
	}

	public static void main(String[] args) {
		System.out.println(EngineVersion.ENGINE_VERSION);
	}
}
