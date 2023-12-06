package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IBufferedTextInputCallback;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuInputEntry extends MenuEntry implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3017844090126571950L;

	private static final float SPACE_BETWEEN_TEXT = 15;
	private static final float CARET_FLASH_TIME = 250; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private String mDefaultText;
	private final String mSeparator = ":";
	private float mCaretFlashTimer;
	private boolean mShowCaret;
	private String mTempString;
	private boolean mEnableScaleTextToWidth;
	private StringBuilder mInputField;
	private boolean mResetOnDefaultClick;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean scaleTextToWidth() {
		return mEnableScaleTextToWidth;
	}

	public void scaleTextToWidth(boolean newValue) {
		mEnableScaleTextToWidth = newValue;
	}

	@Override
	public boolean hasFocus() {
		return super.hasFocus();
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	public String entryText() {
		return inputString();
	}

	public void inputString(String newValue) {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}
		if (newValue != null)
			mInputField.append(newValue);

	}

	public String inputString() {
		return mInputField.toString();
	}

	public void setDefaultText(String text, boolean resetOnClick) {
		mDefaultText = text;
		mResetOnDefaultClick = resetOnClick;
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}

		mInputField.append(mDefaultText);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuInputEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mLabel = "Label:";
		mResetOnDefaultClick = true;

		mDrawBackground = false;
		mHighlightOnHover = false;
		mEnableScaleTextToWidth = true;

		mInputField = new StringBuilder();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		if (!mEnableUpdateDraw)
			return;

		if (!mEnabled || mReadOnly) {
			mHasFocus = false;
			mIsActive = false;
			return;
		}

		if (mIsActive) {
			final double lDeltaTime = core.appTime().elapsedTimeMilli();
			mCaretFlashTimer += lDeltaTime;

			if (mCaretFlashTimer > CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}
		} else {
			mShowCaret = false;
		}
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!mEnableUpdateDraw)
			return;

		final var lTextBoldFont = mParentScreen.fontBold();
		final var lScreenOffset = screen.screenPositionOffset();
		final var lSpriteBatch = mParentScreen.spriteBatch();

		mZ = parentZDepth;

		if (lTextBoldFont == null)
			return;

		final float lUiTextScale = mParentScreen.uiTextScale();
		final float lLabelTextWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);

		float lAdjustedLabelScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && mW * 0.4f < lLabelTextWidth && lLabelTextWidth > 0)
			lAdjustedLabelScaleW = (mW * 0.4f) / lLabelTextWidth;

		entryColor.setRGB(1.f, 1.f, 1.f);

		if (mHasFocus || mIsActive) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}

		final float lLabelTextHeight = lTextBoldFont.fontHeight() * lAdjustedLabelScaleW;
		final float lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final float lInputTextWidth = lTextBoldFont.getStringWidth(mInputField.toString(), lUiTextScale);

		float lAdjustedLInputScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && mW * 0.4f < lInputTextWidth && lInputTextWidth > 0)
			lAdjustedLInputScaleW = (mW * 0.4f) / lInputTextWidth;

		final float lInputTextHeight = lTextBoldFont.fontHeight() * lAdjustedLInputScaleW;

		entryColor.r = mEnabled ? 1f : 0.6f;
		entryColor.g = mEnabled ? 1f : 0.6f;
		entryColor.b = mEnabled ? 1f : 0.6f;
		textColor.a = mParentScreen.screenColor.a;

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mLabel, lScreenOffset.x + mX + mW / 2 - 10 - (lLabelTextWidth * lAdjustedLabelScaleW) - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lLabelTextHeight * 0.5f, parentZDepth + .1f, textColor, lAdjustedLabelScaleW, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lLabelTextHeight * 0.5f, parentZDepth + .1f, textColor, 1.f, -1);
		lTextBoldFont.drawText(mInputField.toString(), lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth * lAdjustedLInputScaleW + SPACE_BETWEEN_TEXT, lScreenOffset.y + mY + mH / 2 - lInputTextHeight * 0.5f, parentZDepth + .1f, textColor, lAdjustedLInputScaleW, -1);

		final float lTextHeight = lTextBoldFont.fontHeight();

		if (mShowCaret && mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			final float lCaretPositionX = lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT + lInputTextWidth * lAdjustedLInputScaleW;
			final float lCaretPositionY = lScreenOffset.y + mY + mH / 2 - lTextHeight / 2.f;
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lCaretPositionX, lCaretPositionY, lTextHeight / 2.f, lTextHeight, mZ, ColorConstants.WHITE);
			lSpriteBatch.end();
		}

		lTextBoldFont.end();

		if (!mEnabled)
			drawdisabledBlackOverbar(core, lSpriteBatch, entryColor.a);

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, 1.f);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, 1.f);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputState) {
		super.onClick(inputState);

		if (mReadOnly)
			return;

		mIsActive = !mIsActive;

		if (mIsActive) {
			mParentScreen.onMenuEntryActivated(this);

			inputState.keyboard().startBufferedTextCapture(this);
		} else {
			inputState.keyboard().stopBufferedTextCapture();
			mParentScreen.onMenuEntryDeactivated(this);
		}

		if (mInputField.length() > 0)
			mTempString = mInputField.toString();

		if (mResetOnDefaultClick && mInputField.toString().equals(mDefaultText)) {
			if (mInputField.length() > 0) {
				mInputField.delete(0, mInputField.length());
			}
		}

	}

	@Override
	public void onDeselection(InputManager inputManager) {
		if (mIsActive)
			inputManager.keyboard().stopBufferedTextCapture();

	}

	@Override
	public StringBuilder getStringBuilder() {
		return mInputField;
	}

	@Override
	public boolean onEnterPressed() {
		mIsActive = false;
		mParentScreen.onMenuEntryDeactivated(this);
		mShowCaret = false;

		if (mResetOnDefaultClick && mInputField.length() == 0)
			setDefaultText(mDefaultText, true);

		return getEnterFinishesInput();
	}

	@Override
	public boolean getEnterFinishesInput() {
		return true;
	}

	@Override
	public boolean onEscapePressed() {
		if (mInputField.length() > 0)
			mInputField.delete(0, mInputField.length());

		if (mTempString != null && mTempString.length() == 0)
			mInputField.append(mTempString);

		mIsActive = false;
		mShowCaret = false;

		return getEscapeFinishesInput();
	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}

	@Override
	public void onKeyPressed(int codePoint) {

	}

	@Override
	public void onCaptureStarted() {

	}
	
	@Override
	public void onCaptureStopped() {
		mHasFocus = false;
		mShowCaret = false;
	}
}
