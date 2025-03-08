package net.lintfordlib.renderers.windows.components;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IBufferedTextInputCallback;
import net.lintfordlib.core.input.keyboard.IUiInputKeyPressCallback;
import net.lintfordlib.core.rendering.SharedResources;
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
	private boolean mShowControlArrows;

	private transient StringBuilder mInputField;
	private String mLabelText;

	private float mValue;
	private int mNumDecimalPlaces;
	private boolean mIsValueBounded;
	private float mMinValue;
	private float mMaxValue;
	private float mStepSize;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean showControlArrows() {
		return mShowControlArrows;
	}

	public void showControlArrows(boolean newValue) {
		mShowControlArrows = newValue;
	}

	public int numDecimalPlaces() {
		return mNumDecimalPlaces;
	}

	public void numDecimalPlaces(int numDecimalPlaces) {
		if (numDecimalPlaces < 1)
			numDecimalPlaces = 1;

		if (numDecimalPlaces > 5)
			numDecimalPlaces = 5;

		if (mNumDecimalPlaces == numDecimalPlaces)
			return;

		final var decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');

		mNumDecimalPlaces = numDecimalPlaces;
	}

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

	public void currentValue(float newValue) {
		float v = newValue;

		if (mIsValueBounded) {
			if (v < mMinValue)
				v = mMinValue;

			if (v > mMaxValue)
				v = mMaxValue;

		}

		if (mValue == v)
			return;

		mValue = v;

		updateFloatValue();
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

	public void inputString(float newValue) {
		if (mValue == newValue)
			return;

		mValue = newValue;
		updateFloatValue();
	}

	public StringBuilder inputString() {
		return mInputField;
	}

	public boolean isEmptyString() {
		return mInputField.toString().equals(mEmptyString);
	}

	public void setMinMax(float minValue, float maxValue) {
		if (minValue == 0 && maxValue == 0) {
			mMinValue = 0;
			mMaxValue = 0;
			mIsValueBounded = false;

			return;
		}

		if (minValue > maxValue) {
			final var t = minValue;
			minValue = maxValue;
			maxValue = t;
		}

		mMinValue = minValue;
		mMaxValue = maxValue;
		mIsValueBounded = true;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiInputFloat(UiWindow parentWindow) {
		this(parentWindow, null);
	}

	public UiInputFloat(UiWindow parentWindow, String label) {
		super(parentWindow);

		mResetOnDefaultClick = true;
		mMouseClickBreaksInputTextFocus = true;
		mShowControlArrows = true;
		mInputField = new StringBuilder();

		mDecrementRectangle = new Rectangle();
		mIncrementRectangle = new Rectangle();

		mEmptyString = "";
		mMaxInputCharacters = 60;

		mLabelText = label;

		mTextScale = 1.f;
		mW = 100;
		mH = 25.f;

		mIsValueBounded = false;
		mMinValue = 0.f;
		mMaxValue = 0.f;

		mValue = 0.f;
		mInputField.append(mValue);

		numDecimalPlaces(3);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mIsReadonly || !mIsEnabled)
			return false;

		if (mShowControlArrows && mDecrementRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this, 30)) {
				var t = mValue;
				mValue -= mStepSize;
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

		else if (mShowControlArrows && mIncrementRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this, 30)) {
				var t = mValue;
				mValue += mStepSize;
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

				onClick(core.input(), true);

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

	@Override
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

		if (mShowControlArrows) {
			final int lRectSize = 16;
			mDecrementRectangle.set(xx + 4, mY + mH / 2 - lRectSize / 2.f, lRectSize, lRectSize);
			mIncrementRectangle.set(xx + ww - lRectSize - 4, mY + mH / 2 - lRectSize / 2.f, lRectSize, lRectSize);
		}

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
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {

		var lTextColor = ColorConstants.TextEntryColor;
		final float lTextHeight = textFont.fontHeight();

		var xx = mX;
		var ww = mW;

		if (mLabelText != null) {
			textFont.begin(core.HUD());
			textFont.setTextColor(lTextColor);
			textFont.drawText(mLabelText, mX, mY + mH * .5f - lTextHeight * .5f * mTextScale, componentZDepth, mTextScale);
			textFont.end();

			xx = mX + mW * .5f;
			ww = mW * .5f;
		}

		final var lColorMod = !mIsEnabled ? .4f : mIsHoveredOver ? .9f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.MenuPanelPrimaryColor, lColorMod);

		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.setColor(lColor);
		lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, (int) xx, mY, 32, mH, componentZDepth);
		if (mW > 32) {
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, (int) xx + 32.f, mY, ww - 64, mH, componentZDepth);
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, (int) xx + ww - 32.f, mY, 32, mH, componentZDepth);
		}

		if (mShowControlArrows) {
			lSpriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WIDGET_LEFT_ARROW, mDecrementRectangle, componentZDepth);
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WIDGET_RIGHT_ARROW, mIncrementRectangle, componentZDepth);
		}

		var lText = mInputField.toString();
		if (lText.length() == 0 && !mHasFocus) {
			if (mEmptyString.isEmpty()) {
				lText = "";
			} else {
				lText = mEmptyString;
			}
		}

		if (isReadonly())
			lTextColor = ColorConstants.GREY_DARK();

		final var lTextWidth = textFont.getStringWidth(mInputField.toString(), mTextScale);
		final var lTextPosX = xx + ww / 2.f - lTextWidth * .5f;

		if (mShowCaret && mHasFocus) {
			final var lCarotPositionX = lTextPosX + lTextWidth;
			lSpriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, (int) lCarotPositionX, mY + mH * .5f - lTextHeight * .5f * mTextScale, 1.f, textFont.fontHeight() * mTextScale, componentZDepth);
		}

		lSpriteBatch.end();

		final int lCancelRectSize = 16;
		ContentRectangle.preDraw(core, lSpriteBatch, mX + 8, mY, mW - lCancelRectSize, mH, -0, 1);

		textFont.begin(core.HUD());
		textFont.setTextColor(lTextColor);
		textFont.drawText(lText, lTextPosX, mY + mH * .5f - lTextHeight * .5f * mTextScale, componentZDepth, mTextScale);
		textFont.end();

		ContentRectangle.postDraw(core);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void limitValueToDecimalPlaces() {
		final var decimalPlaces = 3;

		final var bd = BigDecimal.valueOf(mValue).setScale(decimalPlaces, RoundingMode.HALF_UP);
		mValue = bd.floatValue();
	}

	private boolean applyInputFieldAsValue() {
		float tempValue = mMinValue;
		try {
			tempValue = Float.parseFloat(mInputField.toString());

		} catch (NumberFormatException e) {
			tempValue = mMinValue;
			mInputField.delete(0, mInputField.length());
			mInputField.append(mValue);
		}

		if (mValue == tempValue)
			return false;

		mValue = tempValue;

		limitValueToDecimalPlaces();

		if (mIsValueBounded) {
			if (mValue < mMinValue)
				mValue = mMinValue;

			if (mValue > mMaxValue)
				mValue = mMaxValue;
		}

		updateFloatValue();

		return true;
	}

	private void updateFloatValue() {
		limitValueToDecimalPlaces();

		mInputField.delete(0, mInputField.length());
		mInputField.append(mValue);
	}

	public void setKeyUpdateListener(IUiInputKeyPressCallback keyUpdateListener, int keyListenerUid) {
		mIUiInputKeyPressCallback = keyUpdateListener;
		mKeyListenerUid = keyListenerUid;
	}

	public void onClick(InputManager inputState, boolean newFocus) {
		if (mIsReadonly)
			return;

		mHasFocus = newFocus;

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

		if (applyInputFieldAsValue()) {
			if (mUiWidgetListenerCallback != null) {
				mUiWidgetListenerCallback.widgetOnDataChanged(null, mUiWidgetListenerUid);
			}

			if (mIUiInputKeyPressCallback != null) {
				mIUiInputKeyPressCallback.UiInputEnded(mKeyListenerUid);
			}
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
		} else if (!Character.isDigit((char) codePoint))
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

		if (applyInputFieldAsValue()) {
			if (mUiWidgetListenerCallback != null) {
				mUiWidgetListenerCallback.widgetOnDataChanged(null, mUiWidgetListenerUid);
			}

			if (mIUiInputKeyPressCallback != null) {
				mIUiInputKeyPressCallback.UiInputEnded(mKeyListenerUid);
			}
		}
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = cooldownInMs;
	}

}
