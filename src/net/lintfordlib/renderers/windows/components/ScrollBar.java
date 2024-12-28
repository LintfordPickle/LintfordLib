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
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

public class ScrollBar extends Rectangle implements IInputProcessor, IInputClickedFocusTracker {

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
	private transient float mLastMouseYPos;
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

	public Vector2f positionOffset = new Vector2f();

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

	public void isActive(boolean isActive) {
		mIsActive = isActive;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public float scrollAcceleration() {
		return mScrollAcceleration;
	}

	public void scrollAbsAcceleration(float newAbsAcceleration) {
		mScrollAcceleration = newAbsAcceleration;
	}

	public void scrollRelAcceleration(float newRelAcceleration) {
		mScrollAcceleration += newRelAcceleration;
	}

	public boolean scrollBarEnabled() {
		return mIsActive && mScrollbarEnabled;
	}

	public void scrollBarEnabled(boolean newValue) {
		mScrollbarEnabled = newValue;
	}

	public void scrollBarAlpha(float scrollbarAlpha) {
		mScrollBarAlpha = (float) MathHelper.clamp(scrollbarAlpha, 0.f, 1.f);
	}

	public float scrollBarAlpha() {
		return mScrollBarAlpha;
	}

	public float windowRightOffset() {
		return mWindowRightOffset;
	}

	public void windowRightOffset(float newValue) {
		mWindowRightOffset = newValue;
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

	public ScrollBar(IScrollBarArea windowBounds, Rectangle contentBounds) {
		super(contentBounds);

		mScrollBarArea = windowBounds;
		mWindowRightOffset = -25;
		mIsActive = true;
		mScrollbarAutoHide = true;

		set(0, 0, BAR_WIDTH, 0);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core, IInputClickedFocusManager trackedControlManager) {
		mInputHandledInCoreFrame = true;

		var lMouseHud = core.HUD().getMouseCameraSpace();

		final var lMouseInScrollbarRegion = intersectsAA(lMouseHud);
		final var lMouseInContentRegion = mScrollBarArea.contentDisplayArea().intersectsAA(lMouseHud);

		final var lLeftMouseButtonDown = core.input().mouse().isMouseLeftButtonDown();
		final var lDoWeAlreadyHaveTheMouse = core.input().mouse().isMouseLeftClickOwnerAssigned(hashCode()) && core.input().mouse().isMouseLeftButtonDown();
		var ttt = lMouseInScrollbarRegion && lMouseInContentRegion && core.input().mouse().tryAcquireMouseLeftClick(hashCode());
		final var lCanAcquireMouse = lDoWeAlreadyHaveTheMouse || (lMouseInScrollbarRegion && lLeftMouseButtonDown && ttt);

		if (lMouseInContentRegion && core.input().mouse().tryAcquireMouseMiddle(hashCode())) {
			mScrollAcceleration += core.input().mouse().mouseWheelYOffset() * 250.0f;
		}

		if (!mClickActive && !lCanAcquireMouse) {
			return false;
		}

		if (mClickActive && !lLeftMouseButtonDown) {
			mClickActive = false;

			return false;
		}

		if (!core.input().mouse().isMouseOverThisComponent(hashCode())) {
			return false;
		}

		// TODO: Add clickable down/up arrows

		if (!mClickActive && lCanAcquireMouse) {
			mClickActive = true;
			if (trackedControlManager != null) {
				trackedControlManager.setTrackedClickedFocusControl(this);
			}
			mLastMouseYPos = core.HUD().getMouseWorldSpaceY();
		}

		if (mClickActive)
			constrainScrollBarPosition(core.HUD().getMouseWorldSpaceY());

		return true;
	}

	private void constrainScrollBarPosition(float newPosition) {
		final float lMouseScreenSpaceY = newPosition;
		final float lMaxDiff = mScrollBarArea.fullContentArea().height() - mScrollBarArea.contentDisplayArea().height();

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

	public void update(LintfordCore core) {
		if (!mIsActive)
			return;

		if (mScrollbarAutoHide) {
			mScrollbarEnabled = mScrollBarArea.fullContentArea().height() - mScrollBarArea.contentDisplayArea().height() > 0;
		} else {
			mScrollbarEnabled = true;
		}

		updateMovement(core);
		updateBar(core);
	}

	private void updateMovement(LintfordCore core) {
		final var lContent = mScrollBarArea.fullContentArea();
		if (mScrollbarEnabled) {
			final float lDeltaTime = (float) core.appTime().elapsedTimeMilli() / 1000f;
			float lScrollSpeedFactor = mScrollPosition;

			mScrollVelocity += mScrollAcceleration;
			lScrollSpeedFactor += mScrollVelocity * lDeltaTime;
			mScrollVelocity *= 0.85f;
			mScrollAcceleration = 0.0f;
			mScrollPosition = lScrollSpeedFactor;

			// Constrain
			if (mScrollPosition > 0)
				mScrollPosition = 0;
			if (mScrollPosition < -(lContent.height() - this.mH + mHeaderOffset + mFooterOffset)) {
				mScrollPosition = -(lContent.height() - this.mH + mHeaderOffset + mFooterOffset);
			}
		}
	}

	private void updateBar(LintfordCore core) {
		mMouseTimer -= core.appTime().elapsedTimeMilli();

		float lViewportHeight = mScrollBarArea.contentDisplayArea().height();
		float lContentHeight = mScrollBarArea.fullContentArea().height();

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = ((lViewportHeight - ARROW_SIZE * 2) * (lViewableRatio));

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight - ARROW_SIZE * 2;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		final float lX = mScrollBarArea.contentDisplayArea().x() + mScrollBarArea.contentDisplayArea().width() - BAR_WIDTH;
		final float lY = mScrollBarArea.contentDisplayArea().y();
		final float lW = BAR_WIDTH;
		final float lH = mScrollBarArea.contentDisplayArea().height();
		set(lX, lY, lW, lH);
	}

	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, float zDepth) {
		if (!mIsActive)
			return;

		if (mMarkerMoveMod == 0.f)
			return;

		// Scroll bar background

		// Render the actual scroll bar
		final float by = ARROW_SIZE + mScrollBarArea.contentDisplayArea().y() - (mScrollPosition / mMarkerMoveMod);

		final var lBackgroundColor = ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor.r * .8f, ColorConstants.TertiaryColor.g * .8f, ColorConstants.TertiaryColor.b * .8f, .6f, .6f);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + mX, positionOffset.y + mY + ARROW_SIZE, 16, mH - ARROW_SIZE * 2, zDepth, lBackgroundColor);

		// Draw the background bar
		var lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + mX + 7, positionOffset.y + mY + 16, 2, mH - 32, zDepth, lWhiteColorWithAlpha);

		// Draw the moving bar
		final float lColorMod = mClickActive ? 0.35f : 0.55f;
		final var lBarColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor.r * .8f, ColorConstants.PrimaryColor.g * .8f, ColorConstants.PrimaryColor.g * .8f, mScrollBarAlpha, lColorMod);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + mX, by, 16, mMarkerBarHeight, zDepth, lBarColor);

		lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(mScrollBarAlpha);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_UP, positionOffset.x + mX, positionOffset.y + mY + 3, ARROW_SIZE, ARROW_SIZE, zDepth, lWhiteColorWithAlpha);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_DOWN, positionOffset.x + mX, positionOffset.y + mY + mH - ARROW_SIZE - 3, ARROW_SIZE, ARROW_SIZE, zDepth - 0.01f, lWhiteColorWithAlpha);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + mX, positionOffset.y + mY, mW, mH, ZLayers.LAYER_DEBUG, ColorConstants.Debug_Transparent_Magenta);
		}

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
		return -mScrollBarArea.fullContentArea().height() + mH;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mMouseTimer = cooldownInMs;
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

	public float currentYPos() {
		return mScrollPosition;
	}

	public void RelCurrentYPos(float amt) {
		mScrollPosition += amt;
	}

	public void AbsCurrentYPos(float value) {
		mScrollPosition = value;
	}

}
