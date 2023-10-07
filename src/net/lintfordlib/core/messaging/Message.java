package net.lintfordlib.core.messaging;

import net.lintfordlib.core.debug.Debug.DebugLogLevel;

public class Message {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected boolean mIsAssigned;
	protected String mTimestamp;
	protected String mTag;
	protected String mMessage;
	protected int mType;
	protected float mLifetime;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAssigned() {
		return mIsAssigned;
	}

	public String timestamp() {
		return mTimestamp;
	}

	public String tag() {
		return mTag;
	}

	public void tag(String newTag) {
		mTag = newTag;
	}

	public String message() {
		return mMessage;
	}

	public void message(String newMessage) {
		mMessage = newMessage;
	}

	public int type() {
		return mType;
	}

	public float lifetime() {
		return mLifetime;
	}

	public void lifetime(float lifeTime) {
		mLifetime = lifeTime;
	}

	public void reduceLifetime(float amountToReduceBy) {
		mLifetime -= amountToReduceBy;
	}

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
		mIsAssigned = false;
		mTag = "";
		mTimestamp = "";
		mMessage = "";

		mType = DebugLogLevel.off.logLevel;
	}

	public void setMessage(String tag, String message, String timestamp, int messageType) {
		mIsAssigned = true;

		mTag = tag;
		mTimestamp = timestamp;
		mMessage = message;
		mType = messageType;
		mLifetime = 0;
	}
}
