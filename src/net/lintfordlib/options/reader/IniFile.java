package net.lintfordlib.options.reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

/**
 * A class for reading in key / value pairs from a configuration (*.INI) file.
 */
public class IniFile {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mConfigFilename;

	private static final Pattern mSection = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
	private static final Pattern mKeyValue = Pattern.compile("\\s*([^=]*)=(.*)");

	private Map<String, Map<String, String>> mEntries = new HashMap<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isEmpty() {
		return mEntries.size() == 0;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clearEntries() {
		if (mEntries == null)
			return;

		mEntries.clear();
	}

	public IniFile(String configFilename) {
		mConfigFilename = configFilename;

	}

	public void createNew(String filename) throws IOException {
		mConfigFilename = filename;
		final var lFile = new File(filename);

		try {
			lFile.createNewFile();

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create a new configuration file at '" + filename + "'.");
		}
	}

	public boolean getBoolean(String sectionName, String key, boolean defaultValue) {
		final var kv = mEntries.get(sectionName);
		if (kv == null)
			return defaultValue;

		return Boolean.parseBoolean(kv.get(key));
	}

	public String getString(String sectionName, String key, String defaultValue) {
		final var kv = mEntries.get(sectionName);
		if (kv == null)
			return defaultValue;

		return kv.get(key);
	}

	public int getInt(String sectionName, String key, int defaultValue) {
		if (mEntries == null || mEntries.size() == 0 || !mEntries.containsKey(sectionName))
			return defaultValue;

		try {
			final var kv = mEntries.get(sectionName);
			if (kv == null)
				return defaultValue;

			return Integer.parseInt(kv.get(key));

		} catch (NumberFormatException e) {
			return defaultValue;

		}
	}

	public long getLong(String sectionName, String key, long defaultValue) {
		if (mEntries == null || mEntries.size() == 0 || !mEntries.containsKey(sectionName))
			return defaultValue;

		try {
			final var kv = mEntries.get(sectionName);
			if (kv == null)
				return defaultValue;

			return Long.parseLong(kv.get(key));

		} catch (NumberFormatException e) {
			return defaultValue;

		}
	}

	public float getFloat(String sectionName, String key, float defaultValue) {
		final var kv = mEntries.get(sectionName);
		if (kv == null)
			return defaultValue;

		return Float.parseFloat(kv.get(key));
	}

	public double getDouble(String sectionName, String key, double defaultValue) {
		final var kv = mEntries.get(sectionName);
		if (kv == null)
			return defaultValue;

		return Double.parseDouble(kv.get(key));
	}

	public Map<String, String> getValues(String sectionName, Map<String, String> toFill) {
		toFill.putAll(mEntries.get(sectionName));
		return toFill;
	}

	public void setValues(String sectionName, Map<String, String> mavalue) {
		var lSection = mEntries.get(sectionName);
		if (lSection == null) {
			lSection = new HashMap<>();
			mEntries.put(sectionName, lSection);
		}

		lSection.putAll(mavalue);
	}

	public void setValue(String sectionName, String name, String value) {
		var section = mEntries.get(sectionName);

		if (section == null) {
			section = new HashMap<>();
			mEntries.put(sectionName, section);
		}

		section.put(name, value);
	}

	public void setValue(String sectionName, String name, int value) {
		setValue(sectionName, name, String.valueOf(value));

	}

	public void setValue(String sectionName, String name, long value) {
		setValue(sectionName, name, String.valueOf(value));

	}

	public void setValue(String sectionName, String name, float value) {
		setValue(sectionName, name, String.valueOf(value));

	}

	public void setValue(String sectionName, String name, double value) {
		setValue(sectionName, name, String.valueOf(value));

	}

	public void setValue(String sectionName, String name, boolean value) {
		setValue(sectionName, name, String.valueOf(value));
	}

	public void saveConfig() {
		if (mConfigFilename == null || mConfigFilename.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save the configuration file to " + mConfigFilename);
			return;
		}

		try {
			saveConfig(mConfigFilename);

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save the configuration file to " + mConfigFilename);
		}
	}

	public void saveConfig(String filename) throws IOException {
		final var lConfigFile = new File(filename);
		final var lParentDirectory = lConfigFile.getParentFile();
		if (lParentDirectory != null && lConfigFile.getParentFile().exists() == false) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Creating config directory: " + lParentDirectory.getAbsolutePath());
			lParentDirectory.mkdir();
		}

		try (final var br = new BufferedWriter(new FileWriter(filename))) {
			for (final var sectionEntry : mEntries.entrySet()) {
				String lSectionName = sectionEntry.getKey();
				br.write("[" + lSectionName + "]" + FileUtils.LINE_SEPERATOR);
				for (Map.Entry<String, String> lineEntry : sectionEntry.getValue().entrySet()) {
					br.write(lineEntry.getKey() + "=" + lineEntry.getValue() + FileUtils.LINE_SEPERATOR);

				}
			}
		}
	}

	public void loadConfig() {
		if (mConfigFilename == null || mConfigFilename.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "No configuration file defined.");
			return;
		}

		try {
			loadConfig(mConfigFilename);

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to load DisplayConfig from file " + mConfigFilename);

		}

	}

	public void loadConfig(String path) throws IOException {

		final var file = new File(path);
		if (!file.exists()) {
			createNew(path);
			return;
		}

		try (final var br = new BufferedReader(new FileReader(file))) {
			String line;
			String section = null;
			while ((line = br.readLine()) != null) {
				Matcher m = mSection.matcher(line);
				if (m.matches()) {
					section = m.group(1).trim();
				} else if (section != null) {
					m = mKeyValue.matcher(line);
					if (m.matches()) {
						final var key = m.group(1).trim();
						final var value = m.group(2).trim();
						var kv = mEntries.get(section);

						if (kv == null)
							mEntries.put(section, kv = new HashMap<>());

						kv.put(key, value);
					}
				}
			}
		}
	}

}
