package net.lintfordlib.screenmanager.entries;

import java.util.Objects;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IBufferedTextInputCallback;
import net.lintfordlib.renderers.windows.components.StencilHelper;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuInputEntry extends MenuEntry implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3017844090126571950L;

	private static final float CARET_FLASH_TIME = 250; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private String mDefaultText;
	private final Rectangle mInputAreaRectangle = new Rectangle();
	private float mCaretFlashTimer;
	private boolean mShowCaret;
	protected boolean mIsInputActive;
	private String mTempString;
	private boolean mEnableScaleTextToWidth;
	private StringBuilder mInputField;
	private boolean mResetOnDefaultClick;
	private boolean mSingleLine;
	private int mCursorPos;
	private boolean mNumericInputOnly;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean singleLine() {
		return mSingleLine;
	}

	public void singleLine(boolean newValue) {
		mSingleLine = newValue;
	}

	@Override
	public float desiredHeight() {
		if (singleLine())
			return super.desiredHeight();

		return ENTRY_DEFAULT_HEIGHT * 2;
	}

	public boolean scaleTextToWidth() {
		return mEnableScaleTextToWidth;
	}

	public void scaleTextToWidth(boolean newValue) {
		mEnableScaleTextToWidth = newValue;
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	@Override
	public String entryText() {
		return inputString();
	}

	public void inputString(String newValue) {
		if (Objects.equals(mInputField.toString(), newValue))
			return;

		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}

		if (newValue != null)
			mInputField.append(newValue);

		// Set caret position to last char
		mCursorPos = mInputField.length();

		if (mClickListener != null) {
			mClickListener.onMenuEntryChanged(this);
		}

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
		this(screenManager, parentScreen, "label");
	}

	public MenuInputEntry(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, "");

		mLabel = label;
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

		if (!mIsActive)
			return;

		if (!mEnableUpdateDraw)
			return;

		if (!mEnabled || mReadOnly) {
			hasFocus(false);
			mIsInputActive = false;
			return;
		}

		if (mSingleLine) {
			final var separatorPadding = 16.f;
			mInputAreaRectangle.set(mX + mW / 2.f + mSeparatorOffsetX + separatorPadding, mY, mW / 2.f + -(mSeparatorOffsetX - separatorPadding), mH);
		} else {
			mInputAreaRectangle.set(mX, mY + mH / 2.f, mW, mH / 2.f);
		}

		if (mIsInputActive) {
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
		if (!mEnableUpdateDraw || !mIsActive)
			return;

		final var font = mParentScreen.font();
		final var uiTextScale = mParentScreen.uiTextScale();
		final var spriteBatch = mParentScreen.spriteBatch();

		final var screenOffset = screen.screenPositionOffset();

		mZ = parentZDepth;

		if (font == null)
			return;

		entryColor.setRGB(1.f, 1.f, 1.f);
		if (mIsInputActive) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.MenuEntrySelectedColor);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + centerX() - mW / 2, screenOffset.y + centerY() - mH / 2, 32, mH, mZ);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + centerX() - mW / 2 + 32, screenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + centerX() + mW / 2 - 32, screenOffset.y + centerY() - mH / 2, 32, mH, mZ);
			spriteBatch.end();

		}

		if (mHasFocus)
			renderHighlight(core, screen, true, spriteBatch);

		entryColor.r = mEnabled ? 1f : 0.6f;
		entryColor.g = mEnabled ? 1f : 0.6f;
		entryColor.b = mEnabled ? 1f : 0.6f;
		textColor.a = mParentScreen.screenColor.a;

		if (mCursorPos >= mInputField.length())
			mCursorPos = mInputField.length();

		final int lCancelRectSize = 16;

		final var firstPartOfString = mCursorPos > 0 ? mInputField.subSequence(0, mCursorPos) : "";
		final var caretPositionX = font.getStringWidth(firstPartOfString.toString()) + font.getStringWidth(" ");

		float cutoffWidth;
		if (mSingleLine) {
			cutoffWidth = mW / 2 - 8 - 32;
		} else {
			cutoffWidth = mLabel == null ? mW - 32 : mInputAreaRectangle.width() - lCancelRectSize - 10;
		}

		final var lIsTextTooLong = caretPositionX > cutoffWidth;
		final var lInputTextWidth = font.getStringWidth(mInputField.toString(), uiTextScale);
		final var lTextOverlapWithBox = lInputTextWidth - cutoffWidth;

		float lTextPosX;
		if (mSingleLine) {
			final var lCenterX = mX + mW / 2.f;
			lTextPosX = lIsTextTooLong ? lCenterX + 8.f - lTextOverlapWithBox : lCenterX + 8.f;
		} else {
			lTextPosX = lIsTextTooLong ? mInputAreaRectangle.x() - lTextOverlapWithBox : mInputAreaRectangle.x();
		}

		font.begin(core.HUD());
		font.setTextColor(textColor);
		final float lTextHeight = font.fontHeight();

		final var lSingleLineTextOffsetX = (mSingleLine ? mSeparatorOffsetX : 0.f);

		if (mSingleLine) {
			// Draw the center separator char
			final var mSeparator = " : ";
			final var lCenterX = mX + mW / 2.f + mSeparatorOffsetX;
			final var lLabelTextPositionY = mY + mInputAreaRectangle.height() / 2.f - lTextHeight * .5f;
			final var lSeparatorHalfWidth = font.getStringWidth(mSeparator, uiTextScale) * 0.5f;
			font.drawText(mSeparator, screenOffset.x + lCenterX - lSeparatorHalfWidth, screenOffset.y + lLabelTextPositionY, mZ, 1.f);
		}

		if (mLabel != null) {
			if (mSingleLine) {
				final var lLabelTextPositionY = mY + mInputAreaRectangle.height() / 2.f - lTextHeight * .5f;

				final var lCenterX = mX + mW / 2.f + mSeparatorOffsetX;
				final var lLabelWidth = font.getStringWidth(mLabel, uiTextScale);
				font.drawText(mLabel, screenOffset.x + lCenterX - lLabelWidth - 32, screenOffset.y + lLabelTextPositionY, mZ, 1.f);
			} else {
				final var lLabelTextPositionY = mY + mInputAreaRectangle.height() / 2.f - lTextHeight * .5f;
				font.drawText(mLabel, screenOffset.x + mInputAreaRectangle.x() + 8, screenOffset.y + lLabelTextPositionY, mZ, 1.f);
			}
		}

		font.end();

		StencilHelper.preDraw(core, spriteBatch, screenOffset.x + mInputAreaRectangle.x(), mY, screenOffset.y + mInputAreaRectangle.width() - lCancelRectSize - 5.f, mH, -0, 2);

		font.begin(core.HUD());
		final var inputText = mInputField.toString();
		if (mDefaultText != null && mDefaultText.equals(inputText)) {
			font.setTextColorRGBA(.59f, .61f, .6f, 1.f);
		} else {
			font.setTextColor(textColor);
		}

		font.drawText(inputText, screenOffset.x + lTextPosX + 8 + lSingleLineTextOffsetX, screenOffset.y + mInputAreaRectangle.y() + mInputAreaRectangle.height() * .5f - lTextHeight * .5f, mZ, 1.f);
		font.end();

		StencilHelper.postDraw(core);

		if (mShowCaret && mHasFocus) {
			final var lCaretWidth = 0.f;
			final var lCaretPositionX = screenOffset.x + lTextPosX + caretPositionX + lCaretWidth + lSingleLineTextOffsetX;
			final var lCaretPositionY = screenOffset.y + mInputAreaRectangle.y() + mInputAreaRectangle.height() * .5f - lTextHeight * .5f;

			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(entryColor);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lCaretPositionX + 4, lCaretPositionY, lTextHeight / 2.f, lTextHeight, mZ);
			spriteBatch.end();
		}

		font.end();

		if (!mEnabled)
			drawdisabledBlackOverbar(core, spriteBatch, entryColor.a);

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, 1.f);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, 1.f);

		drawDebugCollidableBounds(core, spriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputState) {
		super.onClick(inputState);

		if (mReadOnly || !mIsActive)
			return;

		mIsInputActive = !mIsInputActive;

		if (mIsInputActive) {
			inputState.keyboard().startBufferedTextCapture(this);
			mParentScreen.onMenuEntryActivated(this);

		} else {
			inputState.keyboard().stopBufferedTextCapture();
			mParentScreen.onMenuEntryDeactivated(this);

		}

		if (mInputField.length() > 0)
			mTempString = mInputField.toString();

		if (mResetOnDefaultClick && mInputField.toString().equals(mDefaultText) && mInputField.length() > 0)
			mInputField.delete(0, mInputField.length());

	}

	@Override
	public void onGainFocus() {
		// TODO Auto-generated method stub
		super.onGainFocus();
	}

	@Override
	public void onDeactivation(InputManager inputManager) {
		super.onDeactivation(inputManager);

		if (mIsInputActive) {
			inputManager.keyboard().stopBufferedTextCapture();
			mIsInputActive = false;
		}

	}

	private void checkResetText() {
		if (mResetOnDefaultClick && mInputField.length() == 0)
			setDefaultText(mDefaultText, true);
	}

	@Override
	public StringBuilder getStringBuilder() {
		return mInputField;
	}

	@Override
	public boolean onEnterPressed() {
		mIsInputActive = false;
		mParentScreen.onMenuEntryDeactivated(this);
		mShowCaret = false;

		checkResetText();

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

		if (mTempString != null && mTempString.length() > 0)
			mInputField.append(mTempString);

		checkResetText();

		mIsInputActive = false;
		mShowCaret = false;

		return getEscapeFinishesInput();
	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}

	@Override
	public void onKeyPressed(int codePoint) {
		if (codePoint == GLFW.GLFW_KEY_BACKSPACE) {
			if (mInputField.length() > 0 && mCursorPos > 0) {
				mInputField.delete(mCursorPos - 1, mCursorPos);
				mCursorPos--;
			}
		}

		else if (codePoint == GLFW.GLFW_KEY_HOME) {
			mCursorPos = 0;
		}

		else if (codePoint == GLFW.GLFW_KEY_END) {
			mCursorPos = mInputField.length();
		}

		else if (codePoint == GLFW.GLFW_KEY_LEFT) {
			if (mCursorPos > 0)
				mCursorPos--;

			mShowCaret = true;
			mCaretFlashTimer = 0;
		}

		else if (codePoint == GLFW.GLFW_KEY_RIGHT) {
			if (mCursorPos < mInputField.length())
				mCursorPos++;

			mShowCaret = true;
			mCaretFlashTimer = 0;
		}

		else {
			if (mNumericInputOnly && !Character.isDigit((char) codePoint))
				return;

			mInputField.insert(mCursorPos, (char) codePoint);
			mCursorPos++;
		}
	}

	@Override
	public void onCaptureStarted() {
		// ignore
	}

	@Override
	public void onCaptureStopped() {
		hasFocus(false);
		mShowCaret = false;

		checkResetText();

		if (mClickListener != null) {
			mClickListener.onMenuEntryChanged(this);
		}
	}

	@Override
	public void onLostFocus() {
		super.onLostFocus();

		checkResetText();
	}
}
