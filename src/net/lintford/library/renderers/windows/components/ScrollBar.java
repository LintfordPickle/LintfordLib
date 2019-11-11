package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.IProcessMouseInput;

public class ScrollBar extends Rectangle implements IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 1303829783855348106L;

	public static final float BAR_WIDTH = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient boolean mClickActive;
	private transient float mLastMouseYPos;

	private transient IScrollBarArea mScrollBarArea;
	private transient float mMarkerBarHeight;
	private transient float mMarkerMoveMod;

	private float mWindowRightOffset;

	private float mMouseTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float windowRightOffset() {
		return mWindowRightOffset;
	}

	public void windowRightOffset(float pNewValue) {
		mWindowRightOffset = pNewValue;
	}

	public boolean clickAction() {
		return mClickActive;
	}

	public boolean areaNeedsScrolling() {
		float lViewportHeight = mScrollBarArea.contentDisplayArea().h;
		float lContentHeight = Math.max(mScrollBarArea.contentDisplayArea().h, mScrollBarArea.fullContentArea().h);

		return lContentHeight > lViewportHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScrollBar(IScrollBarArea pWindowBounds, Rectangle pContentBounds) {
		super(pContentBounds);

		mScrollBarArea = pWindowBounds;
		mWindowRightOffset = -25;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		final float lMouseScreenSpaceX = pCore.HUD().getMouseWorldSpaceX();
		final float lMouseScreenSpaceY = pCore.HUD().getMouseWorldSpaceY();

		final var lMouseInWindowCoords = intersectsAA(pCore.input().mouse().mouseWindowCoords());
		final var lCanAcquireLeftMouse = pCore.input().mouse().isMouseLeftButtonDown();

		// If left mouse isn't pressed and the mouse isn't within the window, then the scrollbar cannot be active
		if (!lMouseInWindowCoords && !lCanAcquireLeftMouse) {
			mClickActive = false;
			return false;
		}

		// First check that the left mouse button is down
		if (lMouseInWindowCoords && lCanAcquireLeftMouse) {
			mClickActive = false; // Cannot be active if mouse not clicked
			return false;

		}

		// check the mouse is within bounds
		if (!mClickActive && !intersectsAA(lMouseScreenSpaceX, lMouseScreenSpaceY)) {
			return false;
		}

		// and check the mouse click isn't being handled elsewhere
		if (!pCore.input().mouse().tryAcquireMouseOwnership(hashCode()))
			return false;

		if (!mClickActive) {
			mClickActive = true;
			mLastMouseYPos = lMouseScreenSpaceY;
		}

		// Scrolling
		constrainScrollBarPosition(lMouseScreenSpaceY);

		return true;
	}

	private void constrainScrollBarPosition(float pNewPositionSS) {
		final float lMouseScreenSpaceY = pNewPositionSS;

		final float lMaxDiff = mScrollBarArea.fullContentArea().h - mScrollBarArea.contentDisplayArea().h;

		if (lMaxDiff > 0) {
			float lDiffY = lMouseScreenSpaceY - mLastMouseYPos;
			mScrollBarArea.RelCurrentYPos(-lDiffY * mMarkerMoveMod);

			if (mScrollBarArea.currentYPos() < -lMaxDiff)
				mScrollBarArea.AbsCurrentYPos(-lMaxDiff);
			if (mScrollBarArea.currentYPos() > 0)
				mScrollBarArea.AbsCurrentYPos(0);

			mLastMouseYPos = lMouseScreenSpaceY;

		}

	}

	public void update(LintfordCore pCore) {
		mMouseTimer -= pCore.time().elapseGameTimeMilli();

		float lViewportHeight = mScrollBarArea.contentDisplayArea().h;
		float lContentHeight = mScrollBarArea.fullContentArea().h;

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = lViewportHeight * lViewableRatio;

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		x = mScrollBarArea.contentDisplayArea().x + mScrollBarArea.contentDisplayArea().w + mWindowRightOffset;
		y = mScrollBarArea.contentDisplayArea().y;
		w = BAR_WIDTH;
		h = mScrollBarArea.contentDisplayArea().h;

	}

	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, float pZDepth) {
		pTextureBatch.begin(pCore.HUD());

		// Scroll bar background
		// pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y, w, h, pZDepth, 0.13f, 1.0f, 0.22f, 0.9f);

		// Render the actual scroll bar
		final float by = mScrollBarArea.contentDisplayArea().y - (mScrollBarArea.currentYPos() / mMarkerMoveMod);

		// Draw the marker bar
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x + 9, y, 2, h, pZDepth, 1f, 1f, 1f, 1f);

		// Draw the moving bar
		float lR = mClickActive ? 0.4f : 0.5f;
		float lG = mClickActive ? 0.4f : 0.5f;
		float lB = mClickActive ? 0.4f : 0.5f;

		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x + 5, by, 10, mMarkerBarHeight, pZDepth, lR, lG, lB, 1f);

		pTextureBatch.end();

	}

	public void resetBarTop() {
		mScrollBarArea.AbsCurrentYPos(0);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean isAtBottomPosition() {
		float oy = mScrollBarArea.currentYPos();
		float ny = getScrollYBottomPosition();
		boolean lResult = oy == ny;

		return lResult;

	}

	public float getScrollYTopPosition() {
		return 0;
	}

	public float getScrollYBottomPosition() {
		return -mScrollBarArea.fullContentArea().h + h;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;

	}

}
