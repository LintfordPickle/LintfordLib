package net.lintford.library;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.options.DisplayManager;

public abstract class GameResourceLoader extends Thread {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected ResourceManager mResourceManager;
	private boolean loadingThreadStarted;
	private DisplayManager mDisplayManager;
	private long windowId;

	protected FontUnit mSystemFont;
	protected final ReentrantLock reentrantLock = new ReentrantLock();
	private String mCurrentStatusMessage;

	private long mLastFrameTime;
	private int mPeriodCount = 1;
	protected double frameDelta;
	private float mPeriodTimer;
	private float mPeriodFlashTimeMs = 400;
	private int mPeriodFlashCount = 3;
	private float minimumTimeToShowLogos = 4000; // ms

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setMinimumTimeToShowLogosMs(float pNewMinimumTime) {
		minimumTimeToShowLogos = pNewMinimumTime;
	}

	public void setPeriodFlash(float pNewPeriodFlashTimeMs, int pNumPeriods) {
		mPeriodFlashTimeMs = pNewPeriodFlashTimeMs;
		mPeriodFlashCount = pNumPeriods;
	}

	protected double getDelta() {
		long time = System.nanoTime();
		double lDelta = ((time - mLastFrameTime) / TimeSpan.NanoToMilli);
		mLastFrameTime = time;

		return lDelta;
	}

	public String currentStatusMessage() {
		return mCurrentStatusMessage;
	}

	public void currentStatusMessage(String pNewStatusMessage) {
		// Not sure the lock is even needed here - one thread writes to the variable and the other
		// reads from it - but the contents of the string are prett much irrelevant.
		final var lResult = reentrantLock.tryLock();
		if (lResult) {
			try {
				mCurrentStatusMessage = pNewStatusMessage;
			} finally {
				reentrantLock.unlock();
			}
		}
	}

	public boolean hasLoadingStarted() {
		return loadingThreadStarted;
	}

	public boolean hasLoadingFinished() {
		return loadingThreadStarted && (isAlive() == false);
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameResourceLoader(ResourceManager pResourceManager, DisplayManager pDisplayManager) {
		mResourceManager = pResourceManager;
		mDisplayManager = pDisplayManager;
		loadingThreadStarted = false;
		windowId = pDisplayManager.windowID();
		setName("Background Resource Loader Thread");
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadResourcesInBackground(LintfordCore pCore) {
		start();
		runLoadingScreenUntilFinish(pCore);
		unloadResources();
	}

	@Override
	public synchronized void start() {
		if (loadingThreadStarted)
			return;
		loadingThreadStarted = true;
		super.start();
	}

	@Override
	public void run() {
		super.run();

		mDisplayManager.makeOffscreenContextCurrentOnThread();

		loadResourcesOnBackgroundThread();
	}

	public void runLoadingScreenUntilFinish(LintfordCore pCore) {
		while (loadingThreadStarted && hasLoadingFinished() == false) {
			onUpdate(pCore);

			onDraw(pCore);

			glfwSwapBuffers(windowId);
			glfwPollEvents();
		}
	}

	private void loadResourcesOnBackgroundThread() {
		final long lStartTime = System.currentTimeMillis();

		currentStatusMessage("loading textures files");
		loadGameTextures(mResourceManager);

		currentStatusMessage("loading Audio files");
		loadGameAudioFiles(mResourceManager);

		currentStatusMessage("loading PObjects files");
		loadGamePObjectFiles(mResourceManager);

		currentStatusMessage("loading Spritesheets files");
		loadGameSpritesheetFiles(mResourceManager);

		currentStatusMessage("loading Spritegraphs files");
		loadGameSpriteGraphs(mResourceManager);

		final long elapsedMillis = System.currentTimeMillis() - lStartTime;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading assets took: " + elapsedMillis + "ms");

		final var lTimeRemainingMs = minimumTimeToShowLogos - elapsedMillis;
		if (lTimeRemainingMs > 0) {
			try {
				Thread.sleep((long) lTimeRemainingMs);
			} catch (InterruptedException e) {

			}
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Waiting for logos : " + lTimeRemainingMs + "ms");
		}
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		mSystemFont = pResourceManager.fontManager().getCoreFont();
	}

	public void unloadResources() {
		mSystemFont = null;
	}

	protected void onUpdate(LintfordCore pCore) {
		frameDelta = getDelta();
		mPeriodTimer += frameDelta;
		if (mPeriodTimer > mPeriodFlashTimeMs) {
			mPeriodTimer = 0;
			mPeriodCount++;
			if (mPeriodCount > mPeriodFlashCount) {
				mPeriodCount = 0;
			}
		}
	}

	protected void onDraw(LintfordCore pCore) {
		final var lHud = pCore.HUD();
		final var lHudBoundingBox = lHud.boundingRectangle();

		glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		final var lLeft = lHudBoundingBox.left();
		final var lBottom = lHudBoundingBox.bottom();
		var message = currentStatusMessage() + " ";
		for (int i = 0; i < mPeriodCount; i++) {
			message += '.';
		}

		mSystemFont.begin(lHud);
		mSystemFont.drawText(message, lLeft + 5.f, lBottom - mSystemFont.fontHeight() - 5.f, -0.01f, 1.f);
		mSystemFont.end();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected void loadGameTextures(ResourceManager pResourceManager) {
	}

	protected void loadGameAudioFiles(ResourceManager pResourceManager) {
	}

	protected void loadGameSpritesheetFiles(ResourceManager pResourceManager) {
	}

	protected void loadGameSpriteGraphs(ResourceManager pResourceManager) {
	}

	protected void loadGamePObjectFiles(ResourceManager pResourceManager) {
	}

}
