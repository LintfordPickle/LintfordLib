package net.ld.library.renderers.windows.components;

import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;

public class ScrollBar extends Rectangle {

	public static final float BAR_WIDTH = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mClickActive;
	private float mLastMouseYPos;

	IScrollBarArea mScrollBarArea;
	private float mMarkerBarHeight;
	private float mMarkerMoveMod;

	public boolean clickAction() {
		return mClickActive;
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

	public boolean handleInput(InputState pInputState) {
		final float lMouseScreenSpaceX = pInputState.mouseWindowCoords().x;
		final float lMouseScreenSpaceY = pInputState.mouseWindowCoords().y;

		final float lMaxDiff = mScrollBarArea.contentArea().height - mScrollBarArea.windowArea().height;

		// First check the mouse is within bounds
		if (!pInputState.mouseLeftClick()) {
			mClickActive = false; // Cannot be active if mouse not clicked
			return false;

		}

		if (!mClickActive && !intersects(lMouseScreenSpaceX, lMouseScreenSpaceY)) {
			return false;
		}

		if (!pInputState.tryAquireLeftClickOwnership(hashCode()))
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

	public void update(GameTime pGameTime) {
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

	public void draw(RenderState pRenderState, TextureBatch pSpriteBatch) {
		pSpriteBatch.begin(pRenderState.hudCamera());

		// Scroll bar background
		pSpriteBatch.draw(0, 96, 32, 32, x, y, 1f, width, height, 1f, 0.23f, 0.22f, 0.32f, 0.9f, TextureManager.CORE_TEXTURE);

		// Render the actual scroll bar
		final float bx = mScrollBarArea.windowArea().x + mScrollBarArea.windowArea().width - 15;
		final float by = mScrollBarArea.windowArea().y - (mScrollBarArea.currentYPos() / mMarkerMoveMod);

		// Draw the marker bar
		pSpriteBatch.draw(0, 96, 32, 32, x + 9, y, 1f, 2, height, 1.0f, 1f, 1f, 1f, 1f, TextureManager.CORE_TEXTURE);
		pSpriteBatch.draw(0, 96, 32, 32, bx, by, 1f, 10, mMarkerBarHeight, 1.0f, 1f, 1f, 1f, 1f, TextureManager.CORE_TEXTURE);

		pSpriteBatch.end();

	}

	public void resetBarTop() {
		// TODO Auto-generated method stub

	}

}
