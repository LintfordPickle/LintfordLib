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
	public static final float ARROW_SIZE = 16;

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
	private boolean mScrollbarEnabled;

	private float mScrollPosition;
	private float mScrollAcceleration;
	private float mScrollVelocity;

	private float mHeaderOffset;
	private float mFooterOffset;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float scrollAcceleration() {
		return mScrollAcceleration;
	}

	public void scrollAbsAcceleration(float pNewAbsAcceleration) {
		mScrollAcceleration = pNewAbsAcceleration;
	}

	public void scrollRelAcceleration(float pNewRelAcceleration) {
		mScrollAcceleration += pNewRelAcceleration;
	}

	public boolean scrollBarEnabled() {
		return mScrollbarEnabled;
	}

	public void scrollBarEnabled(boolean pNewValue) {
		mScrollbarEnabled = pNewValue;
	}

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
		final var lDoWeAlreadyHaveTheMouse = pCore.input().mouse().isMouseLeftClickOwnerAssigned(hashCode()) && pCore.input().mouse().isMouseLeftButtonDown();
		final var lCanAcquireMouse = lDoWeAlreadyHaveTheMouse || lMouseInWindowCoords && lLeftMouseButtonDown && pCore.input().mouse().tryAcquireMouseLeftClick(hashCode());

		mScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

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

		if (mClickActive)
			constrainScrollBarPosition(pCore.HUD().getMouseWorldSpaceY());

		return true;
	}

	private void constrainScrollBarPosition(float pNewPositionSS) {
		final float lMouseScreenSpaceY = pNewPositionSS;
		final float lMaxDiff = mScrollBarArea.fullContentArea().h() - mScrollBarArea.contentDisplayArea().h();

		if (lMaxDiff > 0) {
			float lDiffY = lMouseScreenSpaceY - mLastMouseYPos;
			RelCurrentYPos(-lDiffY * mMarkerMoveMod);

			if (mScrollPosition < -lMaxDiff)
				AbsCurrentYPos(-lMaxDiff);
			if (mScrollPosition > 0)
				AbsCurrentYPos(0);

			mLastMouseYPos = lMouseScreenSpaceY;
		}
	}

	public void update(LintfordCore pCore) {
		updateMovement(pCore);
		updateBar(pCore);
	}

	private void updateMovement(LintfordCore pCore) {
		final var lContent = mScrollBarArea.fullContentArea();
		if (mScrollbarEnabled) {
			final float lDeltaTime = (float) pCore.appTime().elapsedTimeMilli() / 1000f;
			float lScrollSpeedFactor = mScrollPosition;

			mScrollVelocity += mScrollAcceleration;
			lScrollSpeedFactor += mScrollVelocity * lDeltaTime;
			mScrollVelocity *= 0.85f;
			mScrollAcceleration = 0.0f;
			mScrollPosition = lScrollSpeedFactor;

			// Constrain
			if (mScrollPosition > 0)
				mScrollPosition = 0;
			if (mScrollPosition < -(lContent.h() - this.h + mHeaderOffset + mFooterOffset)) {
				mScrollPosition = -(lContent.h() - this.h + mHeaderOffset + mFooterOffset);
			}
		}
	}

	private void updateBar(LintfordCore pCore) {
		mMouseTimer -= pCore.appTime().elapsedTimeMilli();

		float lViewportHeight = mScrollBarArea.contentDisplayArea().h();
		float lContentHeight = mScrollBarArea.fullContentArea().h();

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = (lViewportHeight * lViewableRatio) - ARROW_SIZE * 2;

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight - ARROW_SIZE * 2;
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
		final float by = ARROW_SIZE + mScrollBarArea.contentDisplayArea().y() - (mScrollPosition / mMarkerMoveMod);

		// Draw the background bar
		var lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x + 7, y + 16, 2, h - 32, pZDepth, lWhiteColorWithAlpha);

		// Draw the moving bar
		final float lColorMod = mClickActive ? 0.35f : 0.55f;
		final var lBarColor = ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor.r * .8f, ColorConstants.TertiaryColor.g * .8f, ColorConstants.TertiaryColor.b * .8f, mScrollBarAlpha, lColorMod);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, by, 16, mMarkerBarHeight, pZDepth, lBarColor);

		lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_UP, x, y + 3, ARROW_SIZE, ARROW_SIZE, pZDepth, lWhiteColorWithAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_DOWN, x, y + h - ARROW_SIZE - 3, ARROW_SIZE, ARROW_SIZE, pZDepth - 0.01f, lWhiteColorWithAlpha);
	}

	public void resetBarTop() {
		AbsCurrentYPos(0);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean isAtBottomPosition() {
		float ny = getScrollYBottomPosition();
		boolean lResult = mScrollPosition == ny;
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

	public float currentYPos() {
		return mScrollPosition;
	}

	public void RelCurrentYPos(float pAmt) {
		mScrollPosition += pAmt;
	}

	public void AbsCurrentYPos(float pValue) {
		mScrollPosition = pValue;
	}

}
