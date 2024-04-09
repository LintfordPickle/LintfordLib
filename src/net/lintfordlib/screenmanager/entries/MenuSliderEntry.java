package net.lintfordlib.screenmanager.entries;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.screenmanager.ConstantsScreenManagerAudio;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuSliderEntry extends MenuEntry {

	private static final long serialVersionUID = -8125859270010821953L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Rectangle mDownButton;
	private Rectangle mUpButton;
	private String mLabel;
	private final String mSeparator = ":";
	private String mUnit = "%";
	private int mValue;
	private int mLowerBound;
	private int mUpperBound;
	private boolean mButtonsEnabled;
	private boolean mShowValueEnabled;
	private boolean mShowGuideValuesEnabled;
	private boolean mShowUnit;
	private float mBarPosX;
	private float mBarWidth;
	private int mStep;
	private boolean mTrackingClick;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int step() {
		return mStep;
	}

	public void step(int step) {
		mStep = step;
	}

	public void showValueGuides(boolean newValue) {
		mShowGuideValuesEnabled = newValue;
	}

	public boolean showValueGuides() {
		return mShowGuideValuesEnabled;
	}

	public void showValueUnit(boolean newValue) {
		mShowUnit = newValue;
	}

	public boolean showValueUnit() {
		return mShowUnit;
	}

	public void setValueUnit(String valueUnit) {
		mUnit = valueUnit;
	}

	public void showValue(boolean newValue) {
		mShowValueEnabled = newValue;
	}

	public boolean showValue() {
		return mShowValueEnabled;
	}

	public void buttonsEnabled(boolean newValue) {
		mButtonsEnabled = newValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	public int getCurrentValue() {
		return mValue;
	}

	public void setBounds(int lowBound, int highBound, int stepSize) {
		mLowerBound = lowBound;
		mUpperBound = highBound;
		setValue(highBound - lowBound / 2);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuSliderEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mLabel = "Label:";

		mDownButton = new Rectangle(0, 0, 32, 32);
		mUpButton = new Rectangle(0, 0, 32, 32);
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode()))
			return false;

		if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
			if (mEnabled) {
				mStep = 1;
				if (mDownButton.intersectsAA(core.HUD().getMouseCameraSpace())) {
					setValue(mValue - mStep);
				} else if (mUpButton.intersectsAA(core.HUD().getMouseCameraSpace())) {
					setValue(mValue + mStep);
				} else {
					mTrackingClick = true;
				}

				final var lScreenOffset = mParentScreen.screenPositionOffset();

				if (mTrackingClick && core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
					mValue = (int) MathHelper.scaleToRange(core.HUD().getMouseCameraSpace().x - (lScreenOffset.x + mBarPosX + 8), 0, mBarWidth - 32 - 16, mLowerBound, mUpperBound);
					mValue = MathHelper.clampi(mValue, mLowerBound, mUpperBound);

					onClick(core.input());

				} else {
					mTrackingClick = false;
				}

				return mTrackingClick;

			}

		} else {
			if (mHasFocus == false)
				mParentScreen.setFocusOnEntry(this);

			mTrackingClick = false;
		}

		if (mToolTipEnabled)
			mToolTipTimer += core.appTime().elapsedTimeMilli();

		return false;
	}

	@Override
	public boolean onHandleKeyboardInput(LintfordCore core) {
		mStep = 1;
		if (mIsActive) {
			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT)) {
				mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_TICK);
				setValue(mValue - mStep);
				return true;
			}

			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
				mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_TICK);
				setValue(mValue + mStep);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {
		mStep = 1;
		if (mIsActive) {
			if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, this)) {
				setValue(mValue - mStep);
				return true;
			}

			if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, this)) {
				setValue(mValue + mStep);
				return true;
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		mDownButton.setPosition(mX + mW / 2 + 16, mY);
		mUpButton.setPosition(mX + mW - 32, mY);

		mBarPosX = mX + mW / 2 + mDownButton.width() + 16;
		mBarWidth = mW / 2 - 48;
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var lTextBoldFont = mParentScreen.fontBold();
		final var lSpriteBatch = mParentScreen.spriteBatch();
		final var lUiTextScale = mParentScreen.uiTextScale();

		final var lLabelWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);
		final var lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final var lLabelHeight = lTextBoldFont.getStringHeight(mLabel, lUiTextScale);// ;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		if (mIsActive) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuPanelPrimaryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuPanelPrimaryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuPanelPrimaryColor);
			lSpriteBatch.end();
		} else if (mHasFocus & mEnabled) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}
		mButtonsEnabled = true;
		if (mButtonsEnabled) {
			lSpriteBatch.begin(core.HUD());
			final float lArrowButtonSize = 32;
			final float lArrowButtonPaddingX = mDownButton.width() - lArrowButtonSize;

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, lScreenOffset.x + mDownButton.x() + lArrowButtonPaddingX, lScreenOffset.y + mY, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, lScreenOffset.x + mUpButton.x() + lArrowButtonPaddingX, lScreenOffset.y + mY, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);

			lSpriteBatch.end();
		}

		// Draw the slider bar and caret
		lSpriteBatch.begin(core.HUD());

		final var lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, 0, mBarWidth - 32 - 32);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_LEFT, lScreenOffset.x + mBarPosX, lScreenOffset.y + mY, 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_MID, lScreenOffset.x + mBarPosX + 32, lScreenOffset.y + mY, mBarWidth - 64 - 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_RIGHT, lScreenOffset.x + mBarPosX + mBarWidth - 64, lScreenOffset.y + mY, 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_NUBBLE, lScreenOffset.x + mBarPosX + lCaretPos, lScreenOffset.y + mY, 32, 32, mZ, entryColor);
		lSpriteBatch.end();

		// draw the label to the left and the value //
		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mLabel, lScreenOffset.x + mX + mW / 2 - lLabelWidth - 10 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2f - lLabelHeight / 2f, mZ, ColorConstants.getWhiteWithAlpha(lParentScreenAlpha), lUiTextScale, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2f - lLabelHeight / 2f, mZ, ColorConstants.getWhiteWithAlpha(lParentScreenAlpha), lUiTextScale, -1);

		if (mShowValueEnabled) {
			var lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}
			final float lValueStringWidth = lTextBoldFont.getStringWidth(lValueString, lUiTextScale);

			mShowGuideValuesEnabled = true;
			if (mShowGuideValuesEnabled) {
				lTextBoldFont.drawText(Integer.toString(mLowerBound), lScreenOffset.x + mBarPosX + 24, lScreenOffset.y + mY + mH * .5f - lTextBoldFont.fontHeight() * .5f, mZ, ColorConstants.getWhiteWithAlpha(lParentScreenAlpha * .5f), 1f);
			}

			lTextBoldFont.drawText(lValueString, lScreenOffset.x + mBarPosX + mBarWidth * .5f - lValueStringWidth * .5f, lScreenOffset.y + mY + mH * .5f - lTextBoldFont.fontHeight() * .5f, mZ, ColorConstants.getWhiteWithAlpha(1.f), lUiTextScale);

			if (mShowGuideValuesEnabled) {
				final float lUpperBoundStringWidth = lTextBoldFont.getStringWidth(Integer.toString(mUpperBound));
				lTextBoldFont.drawText(Integer.toString(mUpperBound), lScreenOffset.x + mBarPosX + mBarWidth - lUpperBoundStringWidth - 48, lScreenOffset.y + mY + mH * .5f - lTextBoldFont.fontHeight() * .5f, mZ, ColorConstants.getWhiteWithAlpha(lParentScreenAlpha * .5f), 1f);
			}
		}

		lTextBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, entryColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, entryColor.a);

		if (!mEnabled)
			drawdisabledBlackOverbar(core, lSpriteBatch, entryColor.a);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
//		if (mClickListener == null || mMenuEntryID == -1)
//			return;
//
////		if (mClickListener.isActionConsumed())
////			return;
////
////		mAnimationTimer = MenuScreen.ANIMATION_TIMER_LENGTH;
//		// mScreenManager.uiSounds().play("SOUND_MENU_CLICK");
		mClickListener.menuEntryOnClick(inputManager, mMenuEntryID);
	}

	public void setValue(int newValue) {
		if (newValue < mLowerBound)
			newValue = mLowerBound;

		if (newValue > mUpperBound)
			newValue = mUpperBound;

		mValue = newValue;
	}

	@Override
	public void resetCoolDownTimer() {
		mInputTimer = 50;
	}
}
