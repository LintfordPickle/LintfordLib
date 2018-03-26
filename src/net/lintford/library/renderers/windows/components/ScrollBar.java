package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class ScrollBar extends AARectangle {

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

	public ScrollBar(IScrollBarArea pWindowBounds, AARectangle pContentBounds) {
		super(pContentBounds);

		mScrollBarArea = pWindowBounds;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		final float lMouseScreenSpaceX = pCore.HUD().getMouseWorldSpaceX();
		final float lMouseScreenSpaceY = pCore.HUD().getMouseWorldSpaceY();

		final float lMaxDiff = mScrollBarArea.contentArea().h - mScrollBarArea.windowArea().h;

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
		float lViewportHeight = mScrollBarArea.windowArea().h;
		float lContentHeight = Math.max(mScrollBarArea.windowArea().h, mScrollBarArea.contentArea().h);

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = lViewportHeight * lViewableRatio;

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		x = mScrollBarArea.windowArea().x + mScrollBarArea.windowArea().w - 20;
		y = mScrollBarArea.windowArea().y;
		w = 20;
		h = mScrollBarArea.windowArea().h;
		
	}

	public void draw(LintfordCore pCore, TextureBatch pSpriteBatch, float pZDepth) {
		pSpriteBatch.begin(pCore.HUD());

		// Scroll bar background
		pSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, pZDepth, 0.23f, 0.22f, 0.32f, 0.9f);

		// Render the actual scroll bar
		final float bx = mScrollBarArea.windowArea().x + mScrollBarArea.windowArea().w - 15;
		final float by = mScrollBarArea.windowArea().y - (mScrollBarArea.currentYPos() / mMarkerMoveMod);

		// Draw the marker bar
		pSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x + 9, y, 2, h, pZDepth, 1f, 1f, 1f, 1f);
		pSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, bx, by, 10, mMarkerBarHeight, pZDepth, 1f, 1f, 1f, 1f);

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
		return -mScrollBarArea.contentArea().h + h;
	}

}
