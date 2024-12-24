package net.lintfordlib.options;

import java.util.HashMap;
import java.util.Map;

import net.lintfordlib.options.reader.IniFile;

public class ResourcePathsConfig extends IniFile {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SECTION_NAME_SETTINGS = "Resource Path Config";

	public static final String LAST_WORKSPACE_PATHNAME = "LastWorkspaceLocation";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final Map<String, String> mPathKeyValuePairs = new HashMap<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Map<String, String> pathKeyValuePairs() {
		return mPathKeyValuePairs;
	}

	public String getKeyValue(String key) {
		return mPathKeyValuePairs.get(key);
	}

	public String getKeyValue(String key, String defaultValue) {
		final var result = mPathKeyValuePairs.get(key);
		if (result == null) {
			insertOrUpdateValue(key, defaultValue);
			return defaultValue;
		}

		return result;
	}

	/** Returns true if this map contains a mapping for the specifiedkey. More formally, returns true if and only if the mPathKeyValuePairs map contains a mapping for a key k such that Objects.equals(key, k). (There can beat most one such mapping.) */
	public boolean containsKeyValue(String key) {
		return mPathKeyValuePairs.containsKey(key);
	}

	public String insertOrUpdateValue(String key, String value) {
		return mPathKeyValuePairs.compute(key, (k, v) -> value);
	}

	/**
	 * If the specified key is not already associated with a value (or is mapped to {@code null}), then the value is inserted into the map.
	 */
	public String insertIfNotExists(String key, String value) {
		return mPathKeyValuePairs.computeIfAbsent(key, k -> value);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ResourcePathsConfig(String configFilename) {
		super(configFilename);

		loadConfig();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void loadConfig() {
		super.loadConfig();

		if (isEmpty()) {
			// ignore
		} else {
			getValues(SECTION_NAME_SETTINGS, mPathKeyValuePairs);
		}
	}

	@Override
	public void saveConfig() {
		clearEntries();

		setValues(SECTION_NAME_SETTINGS, mPathKeyValuePairs);

		super.saveConfig();
	}
}
