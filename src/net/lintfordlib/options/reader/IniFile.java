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

	private Pattern mSection = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
	private Pattern mKeyValue = Pattern.compile("\\s*([^=]*)=(.*)");

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

	public IniFile(String pConfigFilename) {
		mConfigFilename = pConfigFilename;

	}

	public void createNew(String pFilename) throws IOException {
		mConfigFilename = pFilename;
		File lFile = new File(pFilename);
		lFile.createNewFile();
	}

	public boolean getBoolean(String sectionName, String key, boolean pDefaultValue) {
		Map<String, String> kv = mEntries.get(sectionName);
		if (kv == null) {
			return pDefaultValue;
		}

		return Boolean.parseBoolean(kv.get(key));
	}

	public String getString(String sectionName, String key, String pDefaultValue) {
		Map<String, String> kv = mEntries.get(sectionName);
		if (kv == null) {
			return pDefaultValue;
		}
		return kv.get(key);
	}

	public int getInt(String sectionName, String key, int pDefaultValue) {
		if (mEntries == null || mEntries.size() == 0 || !mEntries.containsKey(sectionName))
			return pDefaultValue;

		try {
			Map<String, String> kv = mEntries.get(sectionName);
			if (kv == null) {
				return pDefaultValue;

			}

			return Integer.parseInt(kv.get(key));

		} catch (NumberFormatException e) {
			return pDefaultValue;

		}
	}

	public long getLong(String sectionName, String key, long pDefaultValue) {
		if (mEntries == null || mEntries.size() == 0 || !mEntries.containsKey(sectionName))
			return pDefaultValue;

		try {
			Map<String, String> kv = mEntries.get(sectionName);
			if (kv == null) {
				return pDefaultValue;

			}

			return Long.parseLong(kv.get(key));

		} catch (NumberFormatException e) {
			return pDefaultValue;

		}
	}

	public float getFloat(String sectionName, String key, float pDefaultValue) {
		Map<String, String> kv = mEntries.get(sectionName);
		if (kv == null) {
			return pDefaultValue;
		}
		return Float.parseFloat(kv.get(key));
	}

	public double getDouble(String sectionName, String key, double pDefaultValue) {
		Map<String, String> kv = mEntries.get(sectionName);
		if (kv == null) {
			return pDefaultValue;
		}
		return Double.parseDouble(kv.get(key));
	}

	public Map<String, String> getValues(String sectionName, Map<String, String> toFill) {
		toFill.putAll(mEntries.get(sectionName));
		return toFill;
	}

	public void setValues(String sectionName, Map<String, String> mavalue) {
		Map<String, String> lSection = mEntries.get(sectionName);
		if (lSection == null) {
			lSection = new HashMap<>();
			mEntries.put(sectionName, lSection);
		}

		lSection.putAll(mavalue);
	}

	public void setValue(String sectionName, String pName, String value) {
		Map<String, String> pSection = mEntries.get(sectionName);

		if (pSection == null) {
			pSection = new HashMap<>();
			mEntries.put(sectionName, pSection);
		}

		pSection.put(pName, value);
	}

	public void setValue(String sectionName, String pName, int value) {
		setValue(sectionName, pName, String.valueOf(value));

	}

	public void setValue(String sectionName, String pName, long value) {
		setValue(sectionName, pName, String.valueOf(value));

	}

	public void setValue(String sectionName, String pName, float value) {
		setValue(sectionName, pName, String.valueOf(value));

	}

	public void setValue(String sectionName, String pName, double value) {
		setValue(sectionName, pName, String.valueOf(value));

	}

	public void setValue(String sectionName, String pName, boolean value) {
		setValue(sectionName, pName, String.valueOf(value));
	}

	public void saveConfig() {
		if (mConfigFilename == null || mConfigFilename.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save the configuration file to " + mConfigFilename);
			return;
		}

		try {
			saveConfig(mConfigFilename);

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save the DisplayConfiguration file to " + mConfigFilename);
		}
	}

	public void saveConfig(String pFilename) throws IOException {
		final var lConfigFile = new File(pFilename);
		final var lParentDirectory = lConfigFile.getParentFile();
		if (lParentDirectory != null && lConfigFile.getParentFile().exists() == false) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Creating config directory: " + lParentDirectory.getAbsolutePath());
			lParentDirectory.mkdir();
		}

		try (BufferedWriter br = new BufferedWriter(new FileWriter(pFilename))) {

			for (Map.Entry<String, Map<String, String>> sectionEntry : mEntries.entrySet()) {
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

		File lFile = new File(path);
		if (!lFile.exists()) {
			createNew(path);
			return;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(lFile))) {
			String line;
			String pSection = null;
			while ((line = br.readLine()) != null) {
				Matcher m = mSection.matcher(line);
				if (m.matches()) {
					pSection = m.group(1).trim();
				} else if (pSection != null) {
					m = mKeyValue.matcher(line);
					if (m.matches()) {
						String key = m.group(1).trim();
						String value = m.group(2).trim();
						Map<String, String> kv = mEntries.get(pSection);
						if (kv == null) {
							mEntries.put(pSection, kv = new HashMap<>());
						}
						kv.put(key, value);
					}
				}
			}
		}
	}

}
