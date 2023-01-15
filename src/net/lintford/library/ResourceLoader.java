package net.lintford.library;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.time.TimeConstants;
import net.lintford.library.options.DisplayManager;

public abstract class ResourceLoader extends Thread {

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

	protected float mTextColorR = 1.f;
	protected float mTextColorG = 1.f;
	protected float mTextColorB = 1.f;

	private long mLastFrameTime;
	protected int mPeriodCount = 1;
	protected double frameDelta;
	private float mPeriodTimer;
	private float mPeriodFlashTimeMs = 400;
	private int mPeriodFlashCount = 3;
	private float minimumTimeToShowLogos = 4000; // ms

	private long mGlSyncObjectId = -1;
	private boolean _loadOnBackgroundThread = true;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setMinimumTimeToShowLogosMs(float newMinimumTime) {
		minimumTimeToShowLogos = newMinimumTime;
	}

	public void setPeriodFlash(float newPeriodFlashTimeMs, int numPeriods) {
		mPeriodFlashTimeMs = newPeriodFlashTimeMs;
		mPeriodFlashCount = numPeriods;
	}

	protected double getDelta() {
		final var lSystemTime = System.nanoTime();
		final var lDelta = ((lSystemTime - mLastFrameTime) / TimeConstants.NanoToMilli);
		mLastFrameTime = lSystemTime;

		return lDelta;
	}

	public String currentStatusMessage() {
		return mCurrentStatusMessage;
	}

	public void setTextColor(float r, float g, float b) {
		mTextColorR = MathHelper.clamp(r, 0.f, 1.f);
		mTextColorG = MathHelper.clamp(g, 0.f, 1.f);
		mTextColorB = MathHelper.clamp(b, 0.f, 1.f);
	}

	public void currentStatusMessage(String newStatusMessage) {
		// Not sure the lock is even needed here - one thread writes to the variable and the other
		// reads from it - but the contents of the string are prett much irrelevant.
		final var lResult = reentrantLock.tryLock();
		if (lResult) {
			try {
				mCurrentStatusMessage = newStatusMessage;
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

	public ResourceLoader(ResourceManager resourceManager, DisplayManager displayManager, boolean useBackgroundThread) {
		mResourceManager = resourceManager;
		mDisplayManager = displayManager;
		loadingThreadStarted = false;
		windowId = displayManager.windowID();

		_loadOnBackgroundThread = useBackgroundThread;

		setName("Background Resource Loader Thread");
		currentStatusMessage("Loading");
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadResourcesInBackground(LintfordCore core) {
		if (_loadOnBackgroundThread) {
			start();

			runLoadingScreenUntilFinish(core);

			unloadResources();
		} else {
			loadingThreadStarted = true;
			resourcesToLoadInBackground();
		}
	}

	// [Main]
	@Override
	public synchronized void start() {
		if (loadingThreadStarted)
			return;

		mGlSyncObjectId = GL32.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Called glFenceSync, objectId: " + mGlSyncObjectId);

		loadingThreadStarted = true;
		super.start();
	}

	// [Background]
	@Override
	public void run() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Thread run()");

		super.run();

		mDisplayManager.makeOffscreenContextCurrentOnThread();

		loadResourcesOnBackgroundThread();

	}

	public void runLoadingScreenUntilFinish(LintfordCore core) {
		while (loadingThreadStarted && hasLoadingFinished() == false) {
			onUpdate(core);

			onDraw(core);

			glfwSwapBuffers(windowId);
			glfwPollEvents();
		}

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Making main window context current again (after background loading)");
		mDisplayManager.makeContextCurrent(mDisplayManager.windowID());
	}

	// [Background]
	private void loadResourcesOnBackgroundThread() {
		final long lStartTime = System.currentTimeMillis();

		resourcesToLoadInBackground();

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

		// apparently we need to flush all GL commands on the off-thread

		GL11.glFlush();

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Calling glClientWaitSync on objectId: " + mGlSyncObjectId);
		GL32.glClientWaitSync(mGlSyncObjectId, 0, GL32.GL_TIMEOUT_IGNORED);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mSystemFont = resourceManager.fontManager().getCoreFont();
	}

	public void unloadResources() {
		mSystemFont = null;
	}

	protected void onUpdate(LintfordCore core) {
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

	protected void onDraw(LintfordCore core) {
		glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		drawMessages(core);
	}

	protected void drawMessages(LintfordCore core) {
		final var lHud = core.HUD();
		final var lHudBoundingBox = lHud.boundingRectangle();

		final var lLeft = lHudBoundingBox.left();
		final var lBottom = lHudBoundingBox.bottom();
		var message = currentStatusMessage() + " ";
		for (int i = 0; i < mPeriodCount; i++) {
			message += '.';
		}

		mSystemFont.begin(lHud);
		final var lTextColor = ColorConstants.getColor(mTextColorR, mTextColorG, mTextColorB);
		mSystemFont.drawText(message, lLeft + 5.f, lBottom - mSystemFont.fontHeight() - 5.f, -0.01f, lTextColor, 1.f);
		mSystemFont.end();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected abstract void resourcesToLoadInBackground();
}