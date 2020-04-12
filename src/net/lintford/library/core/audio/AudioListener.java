package net.lintford.library.core.audio;

import org.lwjgl.openal.AL10;

public class AudioListener {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mPositionX;
	private float mPositionY;
	private float mPositionZ;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float x() {
		return mPositionX;
	}

	public float y() {
		return mPositionY;
	}

	public float z() {
		return mPositionZ;
	}

	// --------------------------------------
	// Helper Methods
	// --------------------------------------

	public void setPosition(float pX, float pY) {
		setPosition(pX, pY, 0f);
	}

	public void setPosition(float pX, float pY, float pZ) {
		AL10.alListener3f(AL10.AL_POSITION, pX, pY, pZ);
	}
}
