package net.ld.library.core.time;

/**
 * The {@link GameTime} class is used to track the progression of time throughout the lifetime of the application. It tracks and recorded the frame elapsed time as well as the total game time.
 */
public class GameTime {

	// -------------------------------------
	// Variables
	// -------------------------------------

	private long mLastFrame;
	private double mTotalGameTime;
	private double mElapsedGameTime;

	// -------------------------------------
	// Properties
	// -------------------------------------

	/**
	 * @return The total game time in milliseconds.
	 */
	public double totalGameTime() {
		return mTotalGameTime;

	}

	/**
	 * @return The elapsed game time since the last frame in milliseconds.
	 */
	public double elapseGameTime() {
		return mElapsedGameTime;

	}

	// -------------------------------------
	// Constructor
	// -------------------------------------

	/** public constructor. */
	public GameTime() {
		getDelta(); // needs to be called once at least

	}

	// -------------------------------------
	// Core-Methods
	// -------------------------------------

	/**
	 * update called once per frame. It records the amount of time elapsed since the last time update was called.
	 */
	public void update() {
		mElapsedGameTime = getDelta();
		while (mElapsedGameTime > 100f) {
			mElapsedGameTime -= 100f;
		}

		mTotalGameTime += mElapsedGameTime;

	}

	// -------------------------------------
	// Methods
	// -------------------------------------

	/**
	 * Returns the delta time (in milliseconds) since the last time this method was called.
	 */
	private double getDelta() {
		long time = System.nanoTime();
		double lDelta = ((time - mLastFrame) / TimeSpan.NanoToMilli);
		mLastFrame = time;

		return lDelta;
	}

	public void resetElapsed() {
		mElapsedGameTime = getDelta();

	}

}