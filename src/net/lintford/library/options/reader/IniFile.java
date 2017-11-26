package net.lintford.library.options.reader;

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

/** A class for reading in key / value pairs from an ini file. Taken from: http://stackoverflow.com/questions/190629/what-is-the-easiest-way-to-parse-an-ini-file-in-java */
public class IniFile {

	private Pattern mSection = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
	private Pattern mKeyValue = Pattern.compile("\\s*([^=]*)=(.*)");
	private Map<String, Map<String, String>> mEntries = new HashMap<>();

	public IniFile(String path) throws IOException {
		load(path);
	}

	public void load(String path) throws IOException {
		
		File lFile = new File(path);
		if(!lFile.exists()) {
			createNew(path);
			return;
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(lFile))) {
			String line;
			String section = null;
			while ((line = br.readLine()) != null) {
				Matcher m = mSection.matcher(line);
				if (m.matches()) {
					section = m.group(1).trim();
				} else if (section != null) {
					m = mKeyValue.matcher(line);
					if (m.matches()) {
						String key = m.group(1).trim();
						String value = m.group(2).trim();
						Map<String, String> kv = mEntries.get(section);
						if (kv == null) {
							mEntries.put(section, kv = new HashMap<>());
						}
						kv.put(key, value);
					}
				}
			}
		}
	}

	public void createNew(String path) throws IOException {
		File lFile = new File(path);
		lFile.createNewFile();
	}
	
	public String getString(String section, String key, String defaultvalue) {
		Map<String, String> kv = mEntries.get(section);
		if (kv == null) {
			return defaultvalue;
		}
		return kv.get(key);
	}

	public int getInt(String section, String key, int defaultvalue) {
		Map<String, String> kv = mEntries.get(section);
		if (kv == null) {
			return defaultvalue;
		}
		return Integer.parseInt(kv.get(key));
	}

	public float getFloat(String section, String key, float defaultvalue) {
		Map<String, String> kv = mEntries.get(section);
		if (kv == null) {
			return defaultvalue;
		}
		return Float.parseFloat(kv.get(key));
	}

	public double getDouble(String section, String key, double defaultvalue) {
		Map<String, String> kv = mEntries.get(section);
		if (kv == null) {
			return defaultvalue;
		}
		return Double.parseDouble(kv.get(key));
	}
	
	public void saveConfig(String path) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter(path))) {
			// TODO: Still need to write out the config file
		}
	}

}
