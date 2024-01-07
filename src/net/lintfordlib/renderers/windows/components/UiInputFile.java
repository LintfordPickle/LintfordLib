package net.lintfordlib.renderers.windows.components;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

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

public class UiInputFile extends UIWidget implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mMaxInputCharacters;
	private transient boolean mHasFocus;
	private transient float mCaretFlashTimer;
	private transient boolean mShowCaret;
	private transient String mTempString;
	private transient String mEmptyString;
	private transient Rectangle mFileFolderRectangle;
	private IUiInputKeyPressCallback mIUiInputKeyPressCallback;
	private int mKeyListenerUid;
	private transient int mStringLength;
	private transient StringBuilder mInputField;
	private transient boolean mResetOnDefaultClick;
	private boolean mMouseClickBreaksInputTextFocus;
	private boolean mCancelRectHovered;
	private boolean mIsReadonly;
	private float mTextScale;
	private String mLabelText;
	private File mFile;
	private String mFileExtension;
	private int mCursorPos;
	private boolean mDirectorySelection;
	private String mBaseDirectory;
	private boolean mNumericInputOnly;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String baseDirectory() {
		return mBaseDirectory;
	}

	public void baseDirectory(String newBaseDirectory) {
		if (newBaseDirectory == null || newBaseDirectory.length() == 0) {
			mBaseDirectory = null;
			return;
		}

		mBaseDirectory = newBaseDirectory;
	}

	public boolean directorySelection() {
		return mDirectorySelection;
	}

	public void directorySelection(boolean newValue) {
		mDirectorySelection = newValue;
	}

	public File file() {
		return mFile;
	}

	public void file(File file) {
		mFile = file;
		inputString();
		if (mFile != null) {
			inputString(mFile.getAbsolutePath());
		} else {
			inputString(null);
		}
	}

	public String fileExtension() {
		return mFileExtension;
	}

	public void fileExtension(String fileExtension) {
		mFileExtension = fileExtension;
	}

	public String label() {
		return mLabelText;
	}

	public void label(String newLabel) {
		mLabelText = newLabel;
	}

	public void numericInputOnly(boolean allowOnlyNumbers) {
		mNumericInputOnly = allowOnlyNumbers;
	}

	public boolean numericInputOnly() {
		return mNumericInputOnly;
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

		if (newValue != null) {
			mInputField.append(newValue);
			mCursorPos = mInputField.length();
		}
	}

	public StringBuilder inputString() {
		return mInputField;
	}

	public boolean isEmptyString() {
		return mInputField.toString().equals(mEmptyString);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiInputFile(UiWindow parentWindow) {
		this(parentWindow, null);
	}

	public UiInputFile(UiWindow parentWindow, String labelText) {
		super(parentWindow);

		mResetOnDefaultClick = true;
		mMouseClickBreaksInputTextFocus = true;
		mInputField = new StringBuilder();
		mLabelText = labelText;

		mFileFolderRectangle = new Rectangle();
		mEmptyString = "";
		mMaxInputCharacters = 256;

		mTextScale = 1.f;
		mW = 100;
		mH = 25.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		if (mIsReadonly)
			return false;

		if (mFileFolderRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mCancelRectHovered = true;
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				var chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);

				if (mDirectorySelection) {
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				} else {
					var filter = new FileNameExtensionFilter("Definition Meta File", "json");
					chooser.setFileFilter(filter);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				}

				if (mBaseDirectory != null)
					chooser.setCurrentDirectory(new File(mBaseDirectory));

				int returnVal = chooser.showOpenDialog(new JFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mFile = chooser.getSelectedFile();

					inputString(mFile.getAbsolutePath());

					if (mUiWidgetListenerCallback != null)
						mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);

				}
				core.input().keyboard().stopBufferedTextCapture();

				mHasFocus = false;
				mShowCaret = false;
			}
		} else {
			mCancelRectHovered = false;
		}

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				onClick(core.input());
				mHasFocus = true;
				mCursorPos = mInputField.length();

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

		if (mCursorPos > mInputField.length())
			mCursorPos = mInputField.length();

		if (mCursorPos > mMaxInputCharacters)
			mCursorPos = mMaxInputCharacters;

		mCaretFlashTimer += core.appTime().elapsedTimeMilli();

		final int lCancelRectSize = 16;
		mFileFolderRectangle.set(mX + mW - lCancelRectSize - 4, mY + mH / 2 - lCancelRectSize / 2, lCancelRectSize, lCancelRectSize);

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

		final var lEraserColor = mCancelRectHovered ? ColorConstants.WHITE : ColorConstants.getWhiteWithAlpha(.5f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTUREDIRECTORY, mFileFolderRectangle, componentZDepth, lEraserColor);

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

		final var lInputTextWidth = textFont.getStringWidth(mInputField.toString());

		if (mCursorPos >= mInputField.length())
			mCursorPos = mInputField.length();

		final var first_part_of_string = mCursorPos > 0 ? mInputField.subSequence(0, mCursorPos) : "";
		final var carot_position_x = textFont.getStringWidth(first_part_of_string.toString(), mTextScale);
		final int lCancelRectSize = 16;
		final var mw = mLabelText == null ? mW - 32 : ww - lCancelRectSize - 16.f;

		final var lIsTextTooLong = carot_position_x > mw;
		final var lTextOverlapWithBox = lInputTextWidth - mw;
		final var lTextPosX = lIsTextTooLong ? xx - lTextOverlapWithBox : xx;

		if (mShowCaret && mHasFocus) {
			final var lCarotPositionX = lTextPosX + carot_position_x;
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, lCarotPositionX + 8, mY + mH * .5f - lTextHeight * .5f * mTextScale, 1.f, textFont.fontHeight() * mTextScale, componentZDepth, ColorConstants.WHITE);
		}
		spriteBatch.end();

		ContentRectangle.preDraw(core, spriteBatch, xx + 2.f, mY, ww - lCancelRectSize - 5.f, mH, -0, 1);

		textFont.begin(core.HUD());
		textFont.drawText(lText, lTextPosX + 8, mY + mH * .5f - lTextHeight * .5f * mTextScale, componentZDepth, lTextColor, mTextScale);
		textFont.end();

		ContentRectangle.postDraw(core);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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

		return getEnterFinishesInput();
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
			if (mNumericInputOnly && Character.isDigit((char) codePoint) == false)
				return;

			mInputField.insert(mCursorPos, (char) codePoint);
			mCursorPos++;
		}

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
}
