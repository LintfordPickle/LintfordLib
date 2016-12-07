package net.ld.library.core.time;

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
	private boolean mShowFPSInConsole;

	// =============================================
	// Properties
	// =============================================

	public boolean showFPS(){
		return mShowFPSInConsole;
	}
	
	public void showFPS(boolean pNewValue){
		mShowFPSInConsole = pNewValue;
	}
	
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
			if (mShowFPSInConsole) {
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
