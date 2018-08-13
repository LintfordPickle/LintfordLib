package net.lintford.library.core.debug;

import java.util.LinkedList;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.time.TimeSpan;

// TODO: DebugProfiler is currently pretty useless and not finished.
public class DebugProfiler extends AARectangle {

	private static final long serialVersionUID = 3330444673644866887L;

	public static final float Z_DEPTH = -0.1f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final Debug mDebugManager;

	private transient TextureBatch mSpriteBatch;
	private transient FontUnit mConsoleFont;
	private LineBatch mLineBatch;

	private boolean mIsOpen;
	private boolean mIsSimple;

	private long prevTimeDraw;
	private long prevTimeUp;
	private int deltaFrameCount;
	private int frameCount;
	private long timer;
	private float xMarker;
	private float mSimpleHeight = 25;
	private float mExtendedHeight = 60;
	private double deltaDraw;
	private final int HISTORY_COUNT = 50;
	private LinkedList<Float> mFPSHistory;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isOpen() {
		return mIsOpen;
	}

	public void isSimple(boolean pNewValue) {
		mIsSimple = pNewValue;
	}

	public boolean isSimple() {
		return mIsSimple;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugProfiler(final Debug pDebugManager) {
		mDebugManager = pDebugManager;

		if (!mDebugManager.debugManagerEnabled())
			return;

		mSpriteBatch = new TextureBatch();
		mLineBatch = new LineBatch();

		prevTimeDraw = System.nanoTime();
		prevTimeUp = System.nanoTime();

		mFPSHistory = new LinkedList<>();

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
		mLineBatch.loadGLContent(pResourceManager);
	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugProfiler unloading GL content");

		mSpriteBatch.unloadGLContent();
		mLineBatch.unloadGLContent();

	}

	public void handleInput(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F3)) {
			// three way open/close mech
			if (!mIsOpen) {
				mIsOpen = true;
			} else if (mIsSimple) {
				mIsSimple = false;
			} else if (!mIsSimple) {
				mIsOpen = false;
				mIsSimple = true;
			}

		}

	}

	public void update(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mIsOpen)
			return;

		final float lWindowWidth = pCore.config().display().windowSize().x;

		setWidth(lWindowWidth);
		setHeight(mSimpleHeight);

	}

	public void draw(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		long lTimeNow = System.nanoTime();
		deltaDraw = ((lTimeNow - prevTimeDraw) / TimeSpan.NanoToMilli);
		prevTimeDraw = lTimeNow;

		deltaFrameCount++;
		deltaDraw--;

		timer += pCore.time().elapseGameTimeMilli();
		if (timer > 1000) {
			frameCount = deltaFrameCount;
			deltaFrameCount = 0;
			timer -= 1000;

			if (!mIsSimple) {
				mFPSHistory.add((float) frameCount);
				if (mFPSHistory.size() > HISTORY_COUNT)
					mFPSHistory.remove();

				xMarker = 0;

			}

		}

		if (!mIsOpen)
			return;

		final float lH = h + (mIsSimple ? 0 : mExtendedHeight);

		mSpriteBatch.begin(pCore.HUD());
		mConsoleFont.begin(pCore.HUD());
		mLineBatch.begin(pCore.HUD());

		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 32, 0, 32, 32, x, y, w, lH, Z_DEPTH, 0f, 0f, 0f, 0.85f);

		final String frameTime = String.format(java.util.Locale.US, "%.2f", pCore.time().elapseGameTimeMilli());

		mConsoleFont.draw(frameCount + "FPS   " + frameTime + " ms", x + 5, y + 2, -0.1f, 0.9f, 0.21f, 0.12f, 1.0f, 1.0f, -1);

		if (!mIsSimple) {
			// Draw the outline
			final float lPadding = 10;
			final float lX = x;
			final float lY = y;
			final float lWidth = 400;

			final float lOR = 0.34f;
			final float lOG = 0.34f;
			final float lOB = 0.34f;

			final float lLR = 0.84f;
			final float lLG = 0.34f;
			final float lLB = 0.34f;

			mLineBatch.draw(lX + lPadding, lY + mSimpleHeight, x + lPadding, lY + h + mExtendedHeight - lPadding, -0.1f, lOR, lOG, lOB);
			mLineBatch.draw(lX + lPadding, lY + h + mExtendedHeight - lPadding, x + lPadding + lWidth, lY + h + mExtendedHeight - lPadding, -0.1f, lOR, lOG, lOB);
			mLineBatch.draw(lX + lPadding + lWidth, lY + mSimpleHeight, lX + lPadding + lWidth, lY + h + mExtendedHeight - lPadding, -0.1f, lOR, lOG, lOB);

			final float stepSizeX = lWidth / HISTORY_COUNT;
			final float pixelPerMilliSecond = stepSizeX / 1000f;
			xMarker += pCore.time().elapseGameTimeMilli() * pixelPerMilliSecond;

			if (mFPSHistory.size() > 0) {
				float lTY = mFPSHistory.get(0);
				float lOffsetX = (stepSizeX * HISTORY_COUNT + 2) - (mFPSHistory.size() * stepSizeX);

				for (int i = 1; i < mFPSHistory.size(); i++) {

					final float lFloorYPos = lY + h + mExtendedHeight + lPadding;

					mLineBatch.draw(lOffsetX + x + -xMarker + (i - 1) * stepSizeX, lFloorYPos - lTY, lOffsetX + x + -xMarker + i * stepSizeX, lFloorYPos - mFPSHistory.get(i), -0.1f, lLR, lLG, lLB);
					lTY = mFPSHistory.get(i);
				}

			}

		}

		mSpriteBatch.end();
		mConsoleFont.end();
		mLineBatch.end();

	}

}
