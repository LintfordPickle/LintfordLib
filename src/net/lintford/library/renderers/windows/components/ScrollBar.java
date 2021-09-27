package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.MathHelper;

public class ScrollBar extends Rectangle implements IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 1303829783855348106L;

	public static final float BAR_WIDTH = 24;

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
	private float mScrollBarAlpha;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void scrollBarAlpha(float pScrollbarAlpha) {
		mScrollBarAlpha = (float) MathHelper.clamp(pScrollbarAlpha, 0.f, 1.f);
	}

	public float scrollBarAlpha() {
		return mScrollBarAlpha;
	}

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

		if (pCore.HUD().getMouseCameraSpace().x > x + w - 64)
			return false;

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
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight - 32;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		final float lX = mScrollBarArea.contentDisplayArea().x() + mScrollBarArea.contentDisplayArea().w() - BAR_WIDTH;
		final float lY = mScrollBarArea.contentDisplayArea().y();
		final float lW = BAR_WIDTH;
		final float lH = mScrollBarArea.contentDisplayArea().h();
		set(lX, lY, lW, lH);

	}

	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, float pZDepth) {
		// Scroll bar background
		mScrollBarAlpha = 1.0f;
		if (mMarkerMoveMod == 0.f) {
			return;
		}

		// Render the actual scroll bar
		final float by = 16 + mScrollBarArea.contentDisplayArea().y() - (mScrollBarArea.currentYPos() / mMarkerMoveMod);

		// Draw the background bar
		var lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x + 7, y + 16, 2, h - 32, pZDepth, lWhiteColorWithAlpha);

		// Draw the moving bar
		final float lColorMod = mClickActive ? 0.4f : 0.5f;
		final var lBarColor = ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor.r * .8f, ColorConstants.TertiaryColor.g * .8f, ColorConstants.TertiaryColor.b * .8f, mScrollBarAlpha, lColorMod);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, by, 16, mMarkerBarHeight, pZDepth, lBarColor);
		lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_UP, x, y + 3, 16, 16, pZDepth, lWhiteColorWithAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_DOWN, x, y + h - 16 - 3, 16, 16, pZDepth - 0.01f, lWhiteColorWithAlpha);
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
