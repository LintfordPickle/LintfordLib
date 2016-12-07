package net.ld.library.core.config;

import java.io.IOException;

import net.ld.library.GameInfo;

public class BaseConfig {

	// =============================================
	// Variables
	// =============================================

	protected GameInfo mGameInfo;
	protected IniFile mConfigFile;

	// =============================================
	// Constructor
	// =============================================

	public BaseConfig(GameInfo pGameInfo) {
		mGameInfo = pGameInfo;

	}

	// =============================================
	// Methods
	// =============================================

	public void loadConfig() {
		try {
			mConfigFile = new IniFile(mGameInfo.configFileLocation());

		} catch (IOException e) {
			e.printStackTrace();
			// TODO: Need to revert to default in this case
		}
	}

	public void saveConfig() {
		try {
			mConfigFile.saveConfig(mGameInfo.configFileLocation());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
