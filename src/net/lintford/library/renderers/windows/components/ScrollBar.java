package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIRectangle;

public class ScrollBar extends UIRectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScrollBar(IScrollBarArea pWindowBounds, UIRectangle pContentBounds) {
		super(pContentBounds);

		mScrollBarArea = pWindowBounds;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		final float lMouseScreenSpaceX = pCore.HUD().getMouseWorldSpaceX();
		final float lMouseScreenSpaceY = pCore.HUD().getMouseWorldSpaceY();

		final float lMaxDiff = mScrollBarArea.contentArea().height - mScrollBarArea.windowArea().height;

		// First check the mouse is within bounds
		if (!pCore.input().mouseLeftClick()) {
			mClickActive = false; // Cannot be active if mouse not clicked
			return false;

		}

		if (!mClickActive && !intersects(lMouseScreenSpaceX, lMouseScreenSpaceY)) {
			return false;
		}

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
		float lViewportHeight = mScrollBarArea.windowArea().height;
		float lContentHeight = mScrollBarArea.contentArea().height;

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = lViewportHeight * lViewableRatio;

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		x = mScrollBarArea.windowArea().x + mScrollBarArea.windowArea().width - 20;
		y = mScrollBarArea.windowArea().y;
		width = 20;
		height = mScrollBarArea.windowArea().height;
	}

	public void draw(LintfordCore pCore, TextureBatch pSpriteBatch, float pZDepth) {
		pSpriteBatch.begin(pCore.HUD());

		// Scroll bar background
		pSpriteBatch.draw(0, 0, 32, 32, x, y, pZDepth, width, height, 1f, 0.23f, 0.22f, 0.32f, 0.9f, TextureManager.TEXTURE_CORE_UI);

		// Render the actual scroll bar
		final float bx = mScrollBarArea.windowArea().x + mScrollBarArea.windowArea().width - 15;
		final float by = mScrollBarArea.windowArea().y - (mScrollBarArea.currentYPos() / mMarkerMoveMod);

		// Draw the marker bar
		pSpriteBatch.draw(0, 0, 32, 32, x + 9, y, pZDepth, 2, height, 1.0f, 1f, 1f, 1f, 1f, TextureManager.TEXTURE_CORE_UI);
		pSpriteBatch.draw(0, 0, 32, 32, bx, by, pZDepth, 10, mMarkerBarHeight, 1.0f, 1f, 1f, 1f, 1f, TextureManager.TEXTURE_CORE_UI);

		pSpriteBatch.end();

	}

	public void resetBarTop() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float getScrollYTopPosition() {
		return 0;
	}

	public float getScrollYBottomPosition() {
		return -mScrollBarArea.contentArea().height + height;
	}

}
