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

	public void setPosition(float positionX, float positionY) {
		setPosition(positionX, positionY, 0f);
	}

	public void setPosition(float positionX, float positionY, float positionZ) {
		AL10.alListener3f(AL10.AL_POSITION, positionX, positionY, positionZ);

		mPositionX = positionX;
		mPositionY = positionY;
		mPositionZ = positionZ;
	}

	public void setVelocity(float velocityX, float velocityY) {
		setVelocity(velocityX, velocityY, 0f);
	}

	public void setVelocity(float velocityX, float velocityY, float velocityZ) {
		AL10.alListener3f(AL10.AL_VELOCITY, velocityX, velocityY, velocityZ);

		mVelocityX = velocityX;
		mVelocityY = velocityY;
		mVelocityZ = velocityZ;
	}

}