package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class ScrollBar extends Rectangle {

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

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		final float lMouseScreenSpaceX = pCore.HUD().getMouseWorldSpaceX();
		final float lMouseScreenSpaceY = pCore.HUD().getMouseWorldSpaceY();

		final float lMaxDiff = mScrollBarArea.fullContentArea().h - mScrollBarArea.contentDisplayArea().h;

		// First check that the left mouse button is down
		if (!pCore.input().mouseLeftClick()) {
			mClickActive = false; // Cannot be active if mouse not clicked
			return false;

		}

		// check the mouse is within bounds
		if (!mClickActive && !intersectsAA(lMouseScreenSpaceX, lMouseScreenSpaceY)) {
			return false;
		}

		// and check the mouse click isn't being handled elsewhere
		if (!pCore.input().tryAquireLeftClickOwnership(hashCode()))
			return false;

		if (!mClickActive) {
			mClickActive = true;
			mLastMouseYPos = lMouseScreenSpaceY;
		}

		// Scrolling
		if (mClickActive) {
			if (lMaxDiff > 0) {
				float lDiffY = lMouseScreenSpaceY - mLastMouseYPos;
				mScrollBarArea.RelCurrentYPos(-lDiffY * mMarkerMoveMod);

				if (mScrollBarArea.currentYPos() < -lMaxDiff)
					mScrollBarArea.AbsCurrentYPos(-lMaxDiff);
				if (mScrollBarArea.currentYPos() > 0)
					mScrollBarArea.AbsCurrentYPos(0);

				mLastMouseYPos = lMouseScreenSpaceY;

				return true;
			}

		} else {
			mClickActive = false;

		}

		return true;
	}

	public void update(LintfordCore pCore) {
		float lViewportHeight = mScrollBarArea.contentDisplayArea().h;
		float lContentHeight = mScrollBarArea.fullContentArea().h;

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = lViewportHeight * lViewableRatio;

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		x = mScrollBarArea.contentDisplayArea().x + mScrollBarArea.contentDisplayArea().w - 20;
		y = mScrollBarArea.contentDisplayArea().y;
		w = 20;
		h = mScrollBarArea.contentDisplayArea().h;

	}

	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, float pZDepth) {
		pTextureBatch.begin(pCore.HUD());

		// Scroll bar background
		// pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, pZDepth, 0.13f, 0.12f, 0.22f, 0.9f);

		// Render the actual scroll bar
		final float by = mScrollBarArea.contentDisplayArea().y - (mScrollBarArea.currentYPos() / mMarkerMoveMod);

		// Draw the marker bar
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x + 9, y, 2, h, pZDepth, 1f, 1f, 1f, 1f);

		// Draw the moving bar
		float lR = mClickActive ? 0.5f : 1f;
		float lG = mClickActive ? 0.5f : 1f;
		float lB = mClickActive ? 0.5f : 1f;

		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x + 5, by, 10, mMarkerBarHeight, pZDepth, lR, lG, lB, 1f);

		pTextureBatch.end();

	}

	public void resetBarTop() {

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

}
