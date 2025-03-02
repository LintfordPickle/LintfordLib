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

public class ScrollBarHorizontal extends Rectangle implements IInputProcessor, IInputClickedFocusTracker {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 1303829783855348106L;

	public static final float BAR_WIDTH = 8;

	public static final float ARROW_SIZE = 8;

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

	public final Vector2f positionOffset = new Vector2f();
	private final Rectangle mLeftArrowRect = new Rectangle();
	private final Rectangle mRightArrowRect = new Rectangle();
	private boolean mLeftArrayHover;
	private boolean mRightArrayHover;

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
		mScrollBarAlpha = MathHelper.clamp(scrollbarAlpha, 0.f, 1.f);
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

	public ScrollBarHorizontal(IScrollBarArea windowBounds, Rectangle contentBounds) {
		super(contentBounds);

		mScrollBarArea = windowBounds;
		mWindowRightOffset = -25;
		mScrollBarAlpha = 1.f;
		mIsActive = true;
		mScrollbarAutoHide = true;

		set(0, 0, BAR_WIDTH, 0);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core, IInputClickedFocusManager trackedControlManager) {
		mInputHandledInCoreFrame = true;

		final var lMouseInScrollbarRegion = intersectsAA(core.HUD().getMouseCameraSpace());
		final var lMouseInContentRegion = mScrollBarArea.contentDisplayArea().intersectsAA(core.HUD().getMouseCameraSpace());
		final var lLeftMouseButtonDown = core.input().mouse().isMouseLeftButtonDown();
		final var lDoWeAlreadyHaveTheMouse = core.input().mouse().isMouseLeftClickOwnerAssigned(hashCode()) && core.input().mouse().isMouseLeftButtonDown();
		final var lCanAcquireMouse = lDoWeAlreadyHaveTheMouse || lMouseInScrollbarRegion && lLeftMouseButtonDown && core.input().mouse().tryAcquireMouseLeftClick(hashCode());

		if (lMouseInContentRegion && core.input().mouse().tryAcquireMouseMiddle(hashCode())) {
			mScrollAcceleration += core.input().mouse().mouseWheelYOffset() * 250.0f;
		}

		mLeftArrayHover = false;
		mRightArrayHover = false;
		if (mLeftArrowRect.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mLeftArrayHover = true;
		} else if (mRightArrowRect.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mRightArrayHover = true;
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

		if (!mClickActive && lCanAcquireMouse) {
			if (mLeftArrowRect.intersectsAA(core.HUD().getMouseCameraSpace())) {
				mScrollPosition += 5.f;
				mLastMouseXPos += 5.f;
				constrainScrollBarPosition(mLastMouseXPos);
			} else if (mRightArrowRect.intersectsAA(core.HUD().getMouseCameraSpace())) {
				mScrollPosition -= 5.f;
				mLastMouseXPos -= 5.f;
				constrainScrollBarPosition(mLastMouseXPos);
			} else {
				if (trackedControlManager != null) {
					trackedControlManager.setTrackedClickedFocusControl(this);
				}
				mLastMouseXPos = core.HUD().getMouseWorldSpaceX();
				mClickActive = true;
			}
		}

		if (mClickActive)
			constrainScrollBarPosition(core.HUD().getMouseWorldSpaceX());

		return true;
	}

	private void constrainScrollBarPosition(float newPositionSS) {
		final float lMouseScreenSpaceX = newPositionSS;
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

	public void update(LintfordCore core) {
		if (!mIsActive)
			return;

		if (mScrollbarAutoHide) {
			mScrollbarEnabled = mScrollBarArea.fullContentArea().width() - mScrollBarArea.contentDisplayArea().width() > 0;
		} else {
			mScrollbarEnabled = true;
		}

		if (mScrollbarEnabled) {
			updateMovement(core);
			updateBar(core);

			mLeftArrowRect.set(positionOffset.x + mX, positionOffset.y + mY, ARROW_SIZE, ARROW_SIZE);
			mRightArrowRect.set(positionOffset.x + mX + mW - ARROW_SIZE, positionOffset.y + mY, ARROW_SIZE, ARROW_SIZE);

		}
	}

	private void updateMovement(LintfordCore core) {
		final var lContent = mScrollBarArea.fullContentArea();
		mScrollbarEnabled = true;
		if (mScrollbarEnabled) {
			final float lDeltaTime = (float) core.appTime().elapsedTimeMilli() / 1000f;
			float lScrollSpeedFactor = mScrollPosition;

			mScrollVelocity += mScrollAcceleration;
			lScrollSpeedFactor += mScrollVelocity * lDeltaTime;
			mScrollVelocity *= 0.85f;
			mScrollAcceleration = 0.0f;
			mScrollPosition = lScrollSpeedFactor;

			// Constrain
			if (mScrollPosition > 5)
				mScrollPosition = 5;
			if (mScrollPosition < -(lContent.width() - this.mW + mHeaderOffset + mFooterOffset)) {
				mScrollPosition = -(lContent.width() - this.mW + mHeaderOffset + mFooterOffset);
			}
		}
	}

	private void updateBar(LintfordCore core) {
		mMouseTimer -= core.appTime().elapsedTimeMilli();

		float lViewportHeight = mScrollBarArea.contentDisplayArea().width();
		float lContentHeight = mScrollBarArea.fullContentArea().width();

		float lViewableRatio = lViewportHeight / lContentHeight;
		mMarkerBarHeight = ((lViewportHeight - ARROW_SIZE * 2) * (lViewableRatio));

		float lScrollTrackSpace = lContentHeight - lViewportHeight;
		float lScrollThumbSpace = lViewportHeight - mMarkerBarHeight - ARROW_SIZE * 2;
		mMarkerMoveMod = lScrollTrackSpace / lScrollThumbSpace;

		final float lX = mScrollBarArea.contentDisplayArea().x() + 5.f;
		final float lY = mScrollBarArea.contentDisplayArea().y() + mScrollBarArea.contentDisplayArea().height() - BAR_WIDTH - 5.f;
		final float lW = mScrollBarArea.contentDisplayArea().width() - 10.f;
		final float lH = BAR_WIDTH;
		set(lX, lY, lW, lH);
	}

	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, float zDepth) {
		if (!mIsActive)
			return;

		if (!mScrollbarEnabled)
			return;

		if (mMarkerMoveMod == 0.f) {
			return;
		}

		// Render the actual scroll bar
		final var bx = ARROW_SIZE + mScrollBarArea.contentDisplayArea().x() - (mScrollPosition / mMarkerMoveMod);
		final var lBackgroundColor = ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor.r * .8f, ColorConstants.TertiaryColor.g * .8f, ColorConstants.TertiaryColor.b * .8f, .6f, .6f);

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(lBackgroundColor);

		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + mX + ARROW_SIZE, positionOffset.y + mY, mW - ARROW_SIZE * 2.f, ARROW_SIZE, zDepth);

		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, mScrollBarAlpha);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + mX + ARROW_SIZE, positionOffset.y + mY + ARROW_SIZE * .5f - 1.f, mW - ARROW_SIZE * 2.f, 2.f, zDepth);

		// Draw the moving bar
		final var lColorMod = mClickActive ? 0.35f : 0.55f;
		final var lBarColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor.r * .8f, ColorConstants.PrimaryColor.g * .8f, ColorConstants.PrimaryColor.g * .8f, mScrollBarAlpha, lColorMod);
		spriteBatch.setColor(lBarColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + bx, positionOffset.y + mY, mMarkerBarHeight, BAR_WIDTH, zDepth);

		if (mLeftArrayHover)
			spriteBatch.setColor(ColorConstants.SecondaryColor);
		else
			spriteBatch.setColorWhite();

		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_LEFT, mLeftArrowRect, zDepth);

		if (mRightArrayHover)
			spriteBatch.setColor(ColorConstants.SecondaryColor);
		else
			spriteBatch.setColorWhite();

		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_RIGHT, mRightArrowRect, zDepth + 0.01f);
		spriteBatch.end();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, positionOffset.x + mX, positionOffset.y + mY, mW, mH, ZLayers.LAYER_DEBUG);
			spriteBatch.end();
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

	public float currentXPos() {
		return mScrollPosition;
	}

	public void RelCurrentXPos(float amt) {
		mScrollPosition += amt;
	}

	public void AbsCurrentXPos(float value) {
		mScrollPosition = value;
	}

}
