package net.lintfordlib.renderers.windows.components;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IBufferedTextInputCallback;
import net.lintfordlib.core.input.keyboard.IUiInputKeyPressCallback;
import net.lintfordlib.renderers.windows.ConstantsUi;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiInputFloat extends UIWidget implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// The UiInputInteger primarily only allows numerical characters as input. The characters in this list are whitelisted and will be added.
	public static final List<Character> CHAR_WHITELIST = Arrays.asList('-', '.');

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mMaxInputCharacters;
	private transient boolean mHasFocus;
	private transient float mCaretFlashTimer;
	private transient boolean mShowCaret;
	private transient String mTempString;
	private transient String mEmptyString;

	private transient Rectangle mDecrementRectangle;
	private transient Rectangle mIncrementRectangle;

	private IUiInputKeyPressCallback mIUiInputKeyPressCallback;
	private int mKeyListenerUid;
	private transient int mStringLength;

	private transient boolean mResetOnDefaultClick;
	private boolean mMouseClickBreaksInputTextFocus;
	private boolean mIsReadonly;
	private float mTextScale;

	private transient StringBuilder mInputField;
	private String mLabelText;

	private float mValue;
	private boolean mIsValueBounded;
	private float mMinValue;
	private float mMaxValue;
	private float mStepSize;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void stepSize(float newStepSize) {
		if (newStepSize < 0.f)
			newStepSize = 0.1f;

		mStepSize = newStepSize;
	}

	public float stepSize() {
		return mStepSize;
	}

	public float currentValue() {
		return mValue;
	}

	public String label() {
		return mLabelText;
	}

	public void label(String newLabel) {
		mLabelText = newLabel;
	}

	public void textScale(float newTextScale) {
		mTextScale = newTextScale;
	}

	public float textScale() {
		return mTextScale;
	}

	public boolean isReadonly() {
		return mIsReadonly;
	}

	public void isReadonly(boolean newValue) {
		mIsReadonly = newValue;
	}

	public int maxnumInputCharacters() {
		return mMaxInputCharacters;
	}

	public void maxnumInputCharacters(int maxNumInputCharacters) {
		mMaxInputCharacters = maxNumInputCharacters;
	}

	public boolean mouseClickBreaksInputTextFocus() {
		return mMouseClickBreaksInputTextFocus;
	}

	public void mouseClickBreaksInputTextFocus(boolean newValue) {
		mMouseClickBreaksInputTextFocus = newValue;
	}

	public String emptyString() {
		return mEmptyString;
	}

	public void emptyString(String newValue) {
		if (newValue == null)
			mEmptyString = "";
		else
			mEmptyString = newValue;
	}

	public int stringLength() {
		return mStringLength;
	}

	public boolean isEmpty() {
		return isEmptyString();
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean v) {
		mHasFocus = v;
	}

	public void inputString(String newValue) {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}

		mInputField.append(newValue);
	}

	public StringBuilder inputString() {
		return mInputField;
	}

	public boolean isEmptyString() {
		return mInputField.toString().equals(mEmptyString);
	}

	public void setMinMax(float minValue, float maxValue) {
		if (minValue > maxValue) {
			var t = minValue;
			minValue = maxValue;
			maxValue = t;

			mMinValue = minValue;
			mMaxValue = maxValue;
			mIsValueBounded = true;

		} else if (minValue == 0 && maxValue == 0) {
			mMinValue = 0;
			mMaxValue = 0;
			mIsValueBounded = false;
		}

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiInputFloat(UiWindow parentWindow) {
		super(parentWindow);

		mResetOnDefaultClick = true;
		mMouseClickBreaksInputTextFocus = true;
		mInputField = new StringBuilder();

		mDecrementRectangle = new Rectangle();
		mIncrementRectangle = new Rectangle();

		mEmptyString = "";
		mMaxInputCharacters = 60;

		mTextScale = 1.f;
		mW = 100;
		mH = 25.f;

		mMinValue = -10.f;
		mMaxValue = 10.f;

		mValue = 0.f;
		mInputField.append(mValue);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		if (mIsReadonly)
			return false;

		if (mDecrementRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				var t = mValue;
				mValue--;
				if (mIsValueBounded && mValue < mMinValue)
					mValue = mMinValue;

				updateFloatValue();

				if (t != mValue) {
					updateFloatValue();

					if (mUiWidgetListenerCallback != null)
						mUiWidgetListenerCallback.widgetOnDataChanged(null, mUiWidgetListenerUid);

				}
			}
		}

		else if (mIncrementRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				var t = mValue;
				mValue++;
				if (mIsValueBounded && mValue > mMaxValue)
					mValue = mMaxValue;

				if (t != mValue) {
					updateFloatValue();

					if (mUiWidgetListenerCallback != null)
						mUiWidgetListenerCallback.widgetOnDataChanged(null, mUiWidgetListenerUid);
				}

			}
		}

		else if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				onClick(core.input());
				mHasFocus = true;

				return true;
			}
		}

		if (mHasFocus) {
			if (mMouseClickBreaksInputTextFocus && (core.input().mouse().isMouseLeftButtonDownTimed(this) || core.input().mouse().isMouseRightButtonDownTimed(this))) {
				core.input().keyboard().stopBufferedTextCapture();

				mHasFocus = false;
				mShowCaret = false;
			}
		}

		return false;
	}

	public void update(LintfordCore core) {
		super.update(core);

		if (mIsReadonly)
			return;

		mCaretFlashTimer += core.appTime().elapsedTimeMilli();

		var xx = mX;
		var ww = mW;

		if (mLabelText != null) {
			xx = mX + mW * .5f;
			ww = mW * .5f;
		}

		final int lRectSize = 16;
		mDecrementRectangle.set(xx + 4, mY + mH / 2 - lRectSize / 2, lRectSize, lRectSize);
		mIncrementRectangle.set(xx + ww - lRectSize - 4, mY + mH / 2 - lRectSize / 2, lRectSize, lRectSize);

		if (mHasFocus) {
			if (mCaretFlashTimer > ConstantsUi.CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}

			if (mInputField.length() > mMaxInputCharacters)
				mInputField.delete(mInputField.length() - 2, mInputField.length() - 1);
		}
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {

		var lTextColor = ColorConstants.TextEntryColor;
		final float lTextHeight = textFont.fontHeight();

		var xx = mX;
		var ww = mW;

		if (mLabelText != null) {
			textFont.begin(core.HUD());
			textFont.drawText(mLabelText, mX, mY + mH * .5f - lTextHeight * .5f * mTextScale, componentZDepth, lTextColor, mTextScale);
			textFont.end();

			xx = mX + mW * .5f;
			ww = mW * .5f;
		}

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, (int) xx, mY, 32, mH, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
		if (mW > 32) {
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, (int) xx + 32, mY, ww - 64, mH, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, (int) xx + ww - 32, mY, 32, mH, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
		}

		final var lIconColor = ColorConstants.WHITE; // TODO: hoevered / activated
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WIDGET_LEFT_ARROW, mDecrementRectangle, componentZDepth, lIconColor);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WIDGET_RIGHT_ARROW, mIncrementRectangle, componentZDepth, lIconColor);

		var lText = mInputField.toString();
		if (lText.length() == 0 && !mHasFocus) {
			if (mEmptyString.isEmpty()) {
				lText = "";
			} else {
				lText = mEmptyString;
			}
		}

		if (isReadonly())
			lTextColor = ColorConstants.GREY_DARK;

		final var lTextWidth = textFont.getStringWidth(mInputField.toString(), mTextScale);
		final var lTextPosX = xx + ww / 2.f - lTextWidth * .5f;

		if (mShowCaret && mHasFocus) {
			final var lCarotPositionX = lTextPosX + lTextWidth;
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, lCarotPositionX, mY + mH * .5f - lTextHeight * .5f * mTextScale, 1.f, textFont.fontHeight() * mTextScale, componentZDepth, ColorConstants.WHITE);
		}
		spriteBatch.end();
		final int lCancelRectSize = 16;
		ContentRectangle.preDraw(core, spriteBatch, mX + 8, mY, mW - lCancelRectSize, mH, -0, 1);

		textFont.begin(core.HUD());
		textFont.drawText(lText, lTextPosX, mY + mH * .5f - lTextHeight * .5f * mTextScale, componentZDepth, lTextColor, mTextScale);
		textFont.end();

		ContentRectangle.postDraw(core);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateFloatValue() {
		mInputField.delete(0, mInputField.length());
		mInputField.append(mValue);
	}

	public void setKeyUpdateListener(IUiInputKeyPressCallback keyUpdateListener, int keyListenerUid) {
		mIUiInputKeyPressCallback = keyUpdateListener;
		mKeyListenerUid = keyListenerUid;
	}

	public void onClick(InputManager inputState) {
		if (mIsReadonly)
			return;

		mHasFocus = !mHasFocus;

		if (mHasFocus) {
			inputState.keyboard().startBufferedTextCapture(this);

			if (mInputField.length() > 0)
				mTempString = mInputField.toString();

			if (mResetOnDefaultClick && mInputField.toString().equals(mEmptyString)) {
				if (mInputField.length() > 0) {
					mInputField.delete(0, mInputField.length());
				}
			}
		}
	}

	@Override
	public boolean onEnterPressed() {
		mHasFocus = false;
		mShowCaret = false;

		// Try and parse the input text to an int
		try {
			mValue = Float.parseFloat(mInputField.toString());

		} catch (NumberFormatException e) {
			mValue = mMinValue;
			mInputField.delete(0, mInputField.length());
			mInputField.append(mValue);
		}

		return getEnterFinishesInput();
	}

	@Override
	public void onKeyPressed(int codePoint) {
		if (codePoint == GLFW.GLFW_KEY_BACKSPACE) {
			if (mInputField.length() > 0) {
				mInputField.delete(mInputField.length() - 1, mInputField.length());
			}
			mStringLength = mInputField.length();
			return;
		} else if (CHAR_WHITELIST.contains((char) codePoint)) {
			// skip
		} else if (Character.isDigit((char) codePoint) == false)
			return;

		mInputField.append((char) codePoint);
		mStringLength = mInputField.length();

		if (mIUiInputKeyPressCallback != null) {
			mIUiInputKeyPressCallback.keyPressUpdate(codePoint);
		}
	}

	@Override
	public boolean getEnterFinishesInput() {
		return true;
	}

	@Override
	public StringBuilder getStringBuilder() {
		return mInputField;
	}

	@Override
	public boolean onEscapePressed() {
		if (mInputField.length() > 0)
			mInputField.delete(0, mInputField.length());

		if (mTempString != null && mTempString.length() == 0)
			mInputField.append(mTempString);

		mStringLength = 0;

		mHasFocus = false;
		mShowCaret = false;

		return getEscapeFinishesInput();
	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}

	@Override
	public void onCaptureStarted() {
		updateFloatValue();
	}

	@Override
	public void onCaptureStopped() {
		mHasFocus = false;
		mShowCaret = false;

		if (mUiWidgetListenerCallback != null) {
			mUiWidgetListenerCallback.widgetOnDataChanged(null, mUiWidgetListenerUid);
		}

		if (mIUiInputKeyPressCallback != null) {
			mIUiInputKeyPressCallback.UiInputEnded(mKeyListenerUid);
		}
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 30;
	}

}
