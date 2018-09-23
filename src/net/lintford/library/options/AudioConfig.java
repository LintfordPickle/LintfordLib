package net.lintford.library.options;

import net.lintford.library.GameInfo;
import net.lintford.library.options.reader.IniFile;

public class AudioConfig extends IniFile {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioConfig(GameInfo pGameInfo, String pConfigFilename) {
		super(pConfigFilename);

		loadConfig();

		// if no file previously existed, the underlying config is empty, so we need to set some defaults
		if (isEmpty()) {

		}

	}

}
