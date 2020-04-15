package net.lintford.library.core.audio;

import org.lwjgl.openal.AL10;

public class AudioListener {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mPositionX;
	private float mPositionY;
	private float mPositionZ;

	private float mVelocityX;
	private float mVelocityY;
	private float mVelocityZ;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float positionX() {
		return mPositionX;
	}

	public float positionY() {
		return mPositionY;
	}

	public float positionZ() {
		return mPositionZ;
	}

	public float velocityX() {
		return mVelocityX;
	}

	public float velocityY() {
		return mVelocityY;
	}

	public float velocityZ() {
		return mVelocityZ;
	}

	// --------------------------------------
	// Helper Methods
	// --------------------------------------

	public void setPosition(float pX, float pY) {
		setPosition(pX, pY, 0f);
	}

	public void setPosition(float pX, float pY, float pZ) {
		AL10.alListener3f(AL10.AL_POSITION, pX, pY, pZ);

		mPositionX = pX;
		mPositionY = pY;
		mPositionZ = pZ;
	}

	public void setVelocity(float pX, float pY) {
		setVelocity(pX, pY, 0f);
	}

	public void setVelocity(float pX, float pY, float pZ) {
		AL10.alListener3f(AL10.AL_VELOCITY, pX, pY, pZ);

		mVelocityX = pX;
		mVelocityY = pY;
		mVelocityZ = pZ;
	}

}