package net.ld.library.core.time;

import net.ld.library.AppConstants;

public class GameTime {

	// =============================================
	// Variables
	// =============================================

	private long mLastFrame;
	private double mTotalGameTime;
	private double mElapsedGameTime;
	private int mFPSCounter;
	private int mFPS;
	private float mLastFPSTimer;

	// =============================================
	// Properties
	// =============================================

	public int fps() {
		return mFPS;
	}

	/**
	 * @return The total game time in seconds.
	 */
	public double totalGameTime() {
		return mTotalGameTime / 1000.0f;
	}

	/**
	 * @return The elapsed game time since the last frame in milliseconds.
	 */
	public double elapseGameTime() {
		return mElapsedGameTime;
	}

	// =============================================
	// Constructor
	// =============================================

	public GameTime() {
		getDelta(); // needs to be called once at least
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void update() {

		mElapsedGameTime = getDelta();
		mTotalGameTime += mElapsedGameTime;

		if (mTotalGameTime - mLastFPSTimer > 1000) {
			mLastFPSTimer += 1000;
			mFPS = mFPSCounter;
			if (AppConstants.SHOW_FPS) {
				System.out.println("FPS: " + mFPS);
			}

			mFPSCounter = 0;
		}
		mFPSCounter++;

	}

	// =============================================
	// Methods
	// =============================================

	private double getDelta() {
		long time = System.nanoTime();
		double lDelta = ((time - mLastFrame) / TimeSpan.NanoToMilli);
		mLastFrame = time;

		return lDelta;
	}
}
