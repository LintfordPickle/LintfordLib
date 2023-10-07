package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.IInputClickedFocusTracker;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

public class ScrollBarHorizontal extends Rectangle implements IInputProcessor, IInputClickedFocusTracker {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 1303829783855348106L;

	public static final float BAR_WIDTH = 24;
	public static final float ARROW_SIZE = 16;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsActive;
	private transient boolean mClickActive;
	private transient float mLastMouseXPos;
	private transient IScrollBarArea mScrollBarArea;
	private transient float mMarkerBarHeight;
	private transient float mMarkerMoveMod;
	private float mWindowRightOffset;
	private float mMouseTimer;
	private float mScrollBarAlpha;
	private boolean mScrollbarAutoHide;
	private boolean mScrollbarEnabled;
	private float mScrollPosition;
	private float mScrollAcceleration;
	private float mScrollVelocity;
	private float mHeaderOffset;
	private float mFooterOffset;
	private boolean mInputHandledInCoreFrame;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean autoHide() {
		return mScrollbarAutoHide;
	}

	public void autoHide(boolean autoHideEnabled) {
		mScrollbarAutoHide = autoHideEnabled;
	}

	@Override
	public void resetInputHandledInCoreFrameFlag() {
		mInputHandledInCoreFrame = false;
	}

	@Override
	public boolean inputHandledInCoreFrame() {
		return mInputHandledInCoreFrame;
	}

	@Override
	public int parentScreenHash() {
		return mScrollBarArea != null ? mScrollBarArea.parentScreenHash() : -1;
	}

	public void isActive(boolean pIsActive) {
		mIsActive = pIsActive;
	}

	public boolean isActive() {
		return mIsActive;
	}

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
		return mIsActive && mScrollbarEnabled;
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
		float lViewportHeight = mScrollBarArea.contentDisplayArea().height();
		float lContentHeight = Math.max(mScrollBarArea.contentDisplayArea().height(), mScrollBarArea.fullContentArea().height());

		return lContentHeight > lViewportHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScrollBarHorizontal(IScrollBarArea pWindowBounds, Rectangle pContentBounds) {
		super(pContentBounds);

		mScrollBarArea = pWindowBounds;
		mWindowRightOffset = -25;
		mIsActive = true;
		mScrollbarAutoHide = true;

		set(0, 0, BAR_WIDTH, 0);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore, IInputClickedFocusManager pTrackedControlManager) {
		mInputHandledInCoreFrame = true;

		final var lMouseInScrollbarRegion = intersectsAA(pCore.HUD().getMouseCameraSpace());
		final var lMouseInContentRegion = mScrollBarArea.contentDisplayArea().intersectsAA(pCore.HUD().getMouseCameraSpace());
		final var lLeftMouseButtonDown = pCore.input().mouse().isMouseLeftButtonDown();
		final var lDoWeAlreadyHaveTheMouse = pCore.input().mouse().isMouseLeftClickOwnerAssigned(hashCode()) && pCore.input().mouse().isMouseLeftButtonDown();
		final var lCanAcquireMouse = lDoWeAlreadyHaveTheMouse || lMouseInScrollbarRegion && lLeftMouseButtonDown && pCore.input().mouse().tryAcquireMouseLeftClick(hashCode());

		if (lMouseInContentRegion && pCore.input().mouse().tryAcquireMouseMiddle(hashCode())) {
			mScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;
		}

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
			if (pTrackedControlManager != null) {
				pTrackedControlManager.setTrackedClickedFocusControl(this);
			}
			mLastMouseXPos = pCore.HUD().getMouseWorldSpaceX();
		}

		if (mClickActive)
			constrainScrollBarPosition(pCore.HUD().getMouseWorldSpaceX());

		return true;
	}

	private void constrainScrollBarPosition(float pNewPositionSS) {
		final float lMouseScreenSpaceX = pNewPositionSS;
		final float lMaxDiff = mScrollBarArea.fullContentArea().width() - mScrollBarArea.contentDisplayArea().width();

		if (lMaxDiff > 0) {
			float lDiffY = lMouseScreenSpaceX - mLastMouseXPos;
			RelCurrentXPos(-lDiffY * mMarkerMoveMod);

			if (mScrollPosition < -lMaxDiff)
				AbsCurrentXPos(-lMaxDiff);
			if (mScrollPosition > 0)
				AbsCurrentXPos(0);

			mLastMouseXPos = lMouseScreenSpaceX;
		}
	}

	public void update(LintfordCore pCore) {
		if (mIsActive == false)
			return;

		if (mScrollbarAutoHide) {
			mScrollbarEnabled = mScrollBarArea.fullContentArea().width() - mScrollBarArea.contentDisplayArea().width() > 0;
		} else {
			mScrollbarEnabled = true;
		}

		if (mScrollbarEnabled) {
			updateMovement(pCore);
			updateBar(pCore);
		}
	}

	private void updateMovement(LintfordCore pCore) {
		final var lContent = mScrollBarArea.fullContentArea();
		mScrollbarEnabled = true;
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
			if (mScrollPosition < -(lContent.width() - this.mW + mHeaderOffset + mFooterOffset)) {
				mScrollPosition = -(lContent.width() - this.mW + mHeaderOffset + mFooterOffset);
			}
		}
	}

	private void updateBar(LintfordCore pCore) {
		mMouseTimer -= pCore.appTime().elapsedTimeMilli();

		float lViewportHeight = mScrollBarArea.contentDisplayArea().width();
		float lContentHeight = mScrollBarArea.fullContentArea().width();

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = ((lViewportHeight - ARROW_SIZE * 2) * (lViewableRatio));

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight - ARROW_SIZE * 2;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		final float lX = mScrollBarArea.contentDisplayArea().x();
		final float lY = mScrollBarArea.contentDisplayArea().y() + mScrollBarArea.contentDisplayArea().height() - BAR_WIDTH;
		final float lW = mScrollBarArea.contentDisplayArea().width();
		final float lH = BAR_WIDTH;
		set(lX, lY, lW, lH);
	}

	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, float pZDepth) {
		if (mIsActive == false)
			return;

		if (mScrollbarEnabled == false)
			return;

		// Scroll bar background
		mScrollBarAlpha = 1.0f;
		if (mMarkerMoveMod == 0.f) {
			return;
		}

		// Render the actual scroll bar
		final float bx = ARROW_SIZE + mScrollBarArea.contentDisplayArea().x() - (mScrollPosition / mMarkerMoveMod);

		final var lBackgroundColor = ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor.r * .8f, ColorConstants.TertiaryColor.g * .8f, ColorConstants.TertiaryColor.b * .8f, .6f, .6f);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX + ARROW_SIZE, mY, mW - ARROW_SIZE * 2.f, ARROW_SIZE, pZDepth, lBackgroundColor);

		var lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX + ARROW_SIZE, mY + ARROW_SIZE * .5f - 1.f, mW - ARROW_SIZE * 2.f, 2.f, pZDepth, lWhiteColorWithAlpha);

		// Draw the moving bar
		final float lColorMod = mClickActive ? 0.35f : 0.55f;
		final var lBarColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor.r * .8f, ColorConstants.PrimaryColor.g * .8f, ColorConstants.PrimaryColor.g * .8f, mScrollBarAlpha, lColorMod);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, bx, mY, mMarkerBarHeight, 16, pZDepth, lBarColor);

		lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_LEFT, mX, mY, ARROW_SIZE, ARROW_SIZE, pZDepth, lWhiteColorWithAlpha);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_RIGHT, mX + mW - ARROW_SIZE, mY, ARROW_SIZE, ARROW_SIZE, pZDepth - 0.01f, lWhiteColorWithAlpha);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			pSpriteBatch.begin(pCore.HUD());
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, ZLayers.LAYER_DEBUG, ColorConstants.Debug_Transparent_Magenta);
			pSpriteBatch.end();
		}
	}

	public void resetBarTop() {
		AbsCurrentXPos(0);
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
		return -mScrollBarArea.fullContentArea().height() + mH;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;

	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowKeyboardInput() {
		return false;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}

	public float currentXPos() {
		return mScrollPosition;
	}

	public void RelCurrentXPos(float pAmt) {
		mScrollPosition += pAmt;
	}

	public void AbsCurrentXPos(float pValue) {
		mScrollPosition = pValue;
	}

}
