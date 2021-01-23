package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
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
		float lViewportHeight = mScrollBarArea.contentDisplayArea().h();
		float lContentHeight = Math.max(mScrollBarArea.contentDisplayArea().h(), mScrollBarArea.fullContentArea().h());

		return lContentHeight > lViewportHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScrollBar(IScrollBarArea pWindowBounds, Rectangle pContentBounds) {
		super(pContentBounds);

		mScrollBarArea = pWindowBounds;
		mWindowRightOffset = -25;

		set(0, 0, BAR_WIDTH, 0);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		final var lMouseInWindowCoords = intersectsAA(pCore.HUD().getMouseCameraSpace());
		final var lLeftMouseButtonDown = pCore.input().mouse().isMouseLeftButtonDown();
		final var lCanAcquireMouse = lMouseInWindowCoords && lLeftMouseButtonDown && pCore.input().mouse().tryAcquireMouseLeftClick(hashCode());

		if (!mClickActive && !lCanAcquireMouse) {
			return false;

		}

		if (mClickActive && !lLeftMouseButtonDown) {
			mClickActive = false;

			return false;

		}

		if (!pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			return false;

		}

		if (!mClickActive && lCanAcquireMouse) {
			mClickActive = true;
			mLastMouseYPos = pCore.HUD().getMouseWorldSpaceY();

		}

		constrainScrollBarPosition(pCore.HUD().getMouseWorldSpaceY());

		return true;

	}

	private void constrainScrollBarPosition(float pNewPositionSS) {
		final float lMouseScreenSpaceY = pNewPositionSS;

		final float lMaxDiff = mScrollBarArea.fullContentArea().h() - mScrollBarArea.contentDisplayArea().h();

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
		mMouseTimer -= pCore.appTime().elapsedTimeMilli();

		float lViewportHeight = mScrollBarArea.contentDisplayArea().h();
		float lContentHeight = mScrollBarArea.fullContentArea().h();

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = lViewportHeight * lViewableRatio;

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		final float lX = mScrollBarArea.contentDisplayArea().x() + mScrollBarArea.contentDisplayArea().w() - BAR_WIDTH;
		final float lY = mScrollBarArea.contentDisplayArea().y();
		final float lW = BAR_WIDTH;
		final float lH = mScrollBarArea.contentDisplayArea().h();
		set(lX, lY, lW, lH);

	}

	public void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, float pZDepth) {
		// Scroll bar background
		final var lColor = ColorConstants.getBlackWithAlpha(.5f);
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y, w, h, pZDepth, lColor);

		if (mMarkerMoveMod == 0.f) {
			return;
		}

		// Render the actual scroll bar
		final float by = mScrollBarArea.contentDisplayArea().y() - (mScrollBarArea.currentYPos() / mMarkerMoveMod);

		// Draw the marker bar
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x + 9, y, 2, h, pZDepth, ColorConstants.WHITE);

		// Draw the moving bar
		final float lColorMod = mClickActive ? 0.4f : 0.5f;
		final var lBarColor = ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor.r, ColorConstants.TertiaryColor.g, ColorConstants.TertiaryColor.b, 1.f, lColorMod);
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x + 5, by, 10, mMarkerBarHeight, pZDepth, lBarColor);

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
		return -mScrollBarArea.fullContentArea().h() + h;
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
