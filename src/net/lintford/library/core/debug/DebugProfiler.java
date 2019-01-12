package net.lintford.library.core.debug;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.options.DisplayManager;

// TODO: DebugProfiler is currently pretty useless and not finished.
public class DebugProfiler extends Rectangle {

	private static final long serialVersionUID = 3330444673644866887L;

	public static final float Z_DEPTH = -0.1f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final Debug mDebugManager;
	private double mLastUpdateElapsed;
	private double mLastDrawElapsed;

	// FIXME: Do not create a SpriteBatch instance just for the Profiler!
	private transient TextureBatch mSpriteBatch;
	private Texture mCoreUITexture;
	private transient FontUnit mConsoleFont;

	private boolean mIsOpen;

	StringBuilder mStringBuilder;

	private int deltaFrameCount;
	private int frameCount;
	private double timer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void isOpen(boolean pNewValue) {
		mIsOpen = pNewValue;
	}

	public boolean isOpen() {
		return mIsOpen;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugProfiler(final Debug pDebugManager) {
		mDebugManager = pDebugManager;

		if (!mDebugManager.debugManagerEnabled())
			return;

		mSpriteBatch = new TextureBatch();

		mStringBuilder = new StringBuilder();

		h = 20;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugProfiler loading GL content");

		mConsoleFont = pResourceManager.fontManager().systemFont();

		mSpriteBatch.loadGLContent(pResourceManager);
		mCoreUITexture = pResourceManager.textureManager().textureCore();

	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugProfiler unloading GL content");

		mSpriteBatch.unloadGLContent();

	}

	public void handleInput(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F3)) {
			mIsOpen = !mIsOpen;

		}

	}

	public void update(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled() || !mIsOpen)
			return;

		DisplayManager lDisplay = pCore.config().display();
		set(-lDisplay.windowWidth() * 0.5f, lDisplay.windowHeight() * 0.5f - height(), lDisplay.windowWidth(), height());

		mLastUpdateElapsed = pCore.time().elapseGameTimeMilli();

		deltaFrameCount++;

		timer += pCore.time().elapseGameTimeMilli();
		if (timer > 1000) {
			frameCount = deltaFrameCount;
			deltaFrameCount = 0;
			timer -= 1000;

		}

	}

	public void draw(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled() || !isOpen())
			return;

		if (!mIsOpen)
			return;

		mLastDrawElapsed = pCore.time().elapseGameTimeMilli();

		final float lH = h;

		mSpriteBatch.begin(pCore.HUD());
		mConsoleFont.begin(pCore.HUD());

		mSpriteBatch.draw(mCoreUITexture, 32, 0, 32, 32, x, y, w, lH, Z_DEPTH, 0f, 0f, 0f, 0.85f);

		final String lSpace = " ";
		final String lDelimiter = "|";
		String lIsFixed = (pCore.isFixedTimeStep() ? "f" : "v");
		String lIsRunningSlowly = (pCore.time().isGameRunningSlowly() ? "t" : "f");
		String lUElapsed = String.format(java.util.Locale.US, "%.2f", mLastUpdateElapsed);
		String lDElapsed = String.format(java.util.Locale.US, "%.2f", mLastDrawElapsed);
		String lTotalElapsed = "(" + String.format(java.util.Locale.US, "%.1f", pCore.time().totalGameTimeSeconds()) + "s)";

		if (mStringBuilder.length() > 0)
			mStringBuilder.delete(0, mStringBuilder.length());

		mStringBuilder.append(frameCount).append("fps").append(lSpace);
		mStringBuilder.append(lUElapsed).append("/").append(lDElapsed).append(lSpace);
		mStringBuilder.append(lTotalElapsed).append(lSpace);
		mStringBuilder.append(lIsFixed).append(lDelimiter).append(lIsRunningSlowly);

		mConsoleFont.draw(mStringBuilder.toString(), x + 5, y + 2, -0.1f, 0.9f, 0.21f, 0.12f, 1.0f, 0.7f, -1);

		mSpriteBatch.end();
		mConsoleFont.end();

	}

}
