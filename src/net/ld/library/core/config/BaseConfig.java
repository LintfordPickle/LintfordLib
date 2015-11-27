package net.ld.library.core.config;

import java.io.IOException;

public class BaseConfig {

	// =============================================
	// Variables
	// =============================================

	protected final String CONFIG_FILENAME;
	protected IniFile mConfigFile;

	// =============================================
	// Constructor
	// =============================================

	public BaseConfig(String pConfigFilename) {
		CONFIG_FILENAME = pConfigFilename;

	}

	// =============================================
	// Methods
	// =============================================

	public void loadConfig() {
		try {
			mConfigFile = new IniFile(CONFIG_FILENAME);

		}
		catch (IOException e) {
			e.printStackTrace();
			// TODO: Need to revert to default in this case
		}
	}

	public void saveConfig() {
		try {
			mConfigFile.saveConfig(CONFIG_FILENAME);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
