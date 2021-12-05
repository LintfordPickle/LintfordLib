package net.lintford.library.core.messaging;

import net.lintford.library.core.debug.Debug.DebugLogLevel;

public class Message {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public boolean isAssigned;
	public String timestamp;
	public String tag;
	public String message;
	public int type;
	public float lifetime;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Message() {
		reset();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {
		isAssigned = false;
		tag = "";
		timestamp = "";
		message = "";

		type = DebugLogLevel.off.logLevel;

	}

	public void setMessage(String pTag, String pMessage, String pTimestamp, int pLevelType) {
		isAssigned = true;

		tag = pTag;
		timestamp = pTimestamp;
		message = pMessage;
		type = pLevelType;
		lifetime = 0;
	}
}
