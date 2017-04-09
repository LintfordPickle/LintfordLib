package net.ld.library.core.camera;

import java.util.Random;

import net.ld.library.core.time.GameTime;

public class ShakeCamera extends Camera {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected Random mRandom = new Random();
	protected boolean mIsShaking;
	protected float mShakeMag;
	protected float mShakeDur;
	protected float mShakeTimer;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ShakeCamera(float pX, float pY, int pWidth, int pHeight) {
		super(pX, pY, pWidth, pHeight);
		// TODO Auto-generated constructor stub
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(GameTime pGameTime) {
		if (mIsShaking) {

			mShakeTimer += pGameTime.elapseGameTime();

			if (mShakeTimer > mShakeDur) {
				mIsShaking = false;
				mShakeTimer = mShakeDur;
			}

			// normal time
			float progress = mShakeTimer / mShakeDur;

			float lMagnitude = mShakeMag * (1f - (progress * progress));

			mOffsetPosition.x = mRandom.nextFloat() * lMagnitude;
			mOffsetPosition.y = mRandom.nextFloat() * lMagnitude;

		}

		super.update(pGameTime);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/**
	 * Shakes the camera for the specified amount of time (milliseconds) with
	 * the specified magnitude (pxs)
	 */
	public void shake(float pDuration, float pMagnitude) {
		if (pDuration <= 0)
			return;

		mIsShaking = true;
		mShakeDur = pDuration;
		mShakeMag = pMagnitude;

		mShakeTimer = 0;

	}

}
