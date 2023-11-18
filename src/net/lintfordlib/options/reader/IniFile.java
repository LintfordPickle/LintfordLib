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
 * A class for reading in pKey / pValue pairs from a configuration (*.INI) file.
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

	public boolean getBoolean(String pSectionName, String pKey, boolean pDefaultValue) {
		Map<String, String> kv = mEntries.get(pSectionName);
		if (kv == null) {
			return pDefaultValue;
		}

		return Boolean.parseBoolean(kv.get(pKey));
	}

	public String getString(String pSectionName, String pKey, String pDefaultValue) {
		Map<String, String> kv = mEntries.get(pSectionName);
		if (kv == null) {
			return pDefaultValue;
		}
		return kv.get(pKey);
	}

	public int getInt(String pSectionName, String pKey, int pDefaultValue) {
		if (mEntries == null || mEntries.size() == 0 || !mEntries.containsKey(pSectionName))
			return pDefaultValue;

		try {
			Map<String, String> kv = mEntries.get(pSectionName);
			if (kv == null) {
				return pDefaultValue;

			}

			return Integer.parseInt(kv.get(pKey));

		} catch (NumberFormatException e) {
			return pDefaultValue;

		}
	}

	public long getLong(String pSectionName, String pKey, long pDefaultValue) {
		if (mEntries == null || mEntries.size() == 0 || !mEntries.containsKey(pSectionName))
			return pDefaultValue;

		try {
			Map<String, String> kv = mEntries.get(pSectionName);
			if (kv == null) {
				return pDefaultValue;

			}

			return Long.parseLong(kv.get(pKey));

		} catch (NumberFormatException e) {
			return pDefaultValue;

		}
	}

	public float getFloat(String pSectionName, String pKey, float pDefaultValue) {
		Map<String, String> kv = mEntries.get(pSectionName);
		if (kv == null) {
			return pDefaultValue;
		}
		return Float.parseFloat(kv.get(pKey));
	}

	public double getDouble(String pSectionName, String pKey, double pDefaultValue) {
		Map<String, String> kv = mEntries.get(pSectionName);
		if (kv == null) {
			return pDefaultValue;
		}
		return Double.parseDouble(kv.get(pKey));
	}

	public void setValue(String pSectionName, String pName, String pValue) {
		Map<String, String> pSection = mEntries.get(pSectionName);

		if (pSection == null) {
			pSection = new HashMap<>();
			mEntries.put(pSectionName, pSection);
		}

		pSection.put(pName, pValue);
	}

	public void setValue(String pSectionName, String pName, int pValue) {
		setValue(pSectionName, pName, String.valueOf(pValue));

	}

	public void setValue(String pSectionName, String pName, long pValue) {
		setValue(pSectionName, pName, String.valueOf(pValue));

	}

	public void setValue(String pSectionName, String pName, float pValue) {
		setValue(pSectionName, pName, String.valueOf(pValue));

	}

	public void setValue(String pSectionName, String pName, double pValue) {
		setValue(pSectionName, pName, String.valueOf(pValue));

	}

	public void setValue(String pSectionName, String pName, boolean pValue) {
		setValue(pSectionName, pName, String.valueOf(pValue));
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
		if (lConfigFile.getParentFile().exists() == false) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Creating config directory: " + lConfigFile.getParentFile());
			lConfigFile.mkdirs();
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
						String pKey = m.group(1).trim();
						String pValue = m.group(2).trim();
						Map<String, String> kv = mEntries.get(pSection);
						if (kv == null) {
							mEntries.put(pSection, kv = new HashMap<>());
						}
						kv.put(pKey, pValue);
					}
				}
			}
		}
	}

}
