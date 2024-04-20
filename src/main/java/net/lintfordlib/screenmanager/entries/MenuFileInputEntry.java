package net.lintfordlib.screenmanager.entries;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IBufferedTextInputCallback;
import net.lintfordlib.renderers.windows.components.ContentRectangle;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuFileInputEntry extends MenuEntry implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3017844090126571950L;

	private static final float CARET_FLASH_TIME = 250; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private final Rectangle mInputAreaRectangle = new Rectangle();
	private final Rectangle mFileFolderRectangle = new Rectangle();
	private boolean mCancelRectHovered;
	private File mFile;
	private boolean mDirectorySelection;
	private String mBaseDirectory;
	private float mCaretFlashTimer;
	private boolean mShowCaret;
	private String mTempString;
	private boolean mEnableScaleTextToWidth;
	private StringBuilder mInputField;
	private int mCursorPos;
	private boolean mNumericInputOnly;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean directorySelection() {
		return mDirectorySelection;
	}

	public void directorySelection(boolean directorySelection) {
		mDirectorySelection = directorySelection;
	}

	@Override
	public float desiredHeight() {
		return ENTRY_DEFAULT_HEIGHT * 2;
	}

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

		if (newValue == null) {
			mFile = null;
			return;
		}

		if (newValue != null)
			mInputField.append(newValue);

		mCursorPos = mInputField.length();

		mFile = new File(newValue);

	}

	public String inputString() {
		return mInputField.toString();
	}

	public String baseDirectory() {
		return mBaseDirectory;
	}

	public void baseDirectory(String newBaseDirectory) {
		mBaseDirectory = newBaseDirectory;
	}

	public File selectedFile() {
		return mFile;
	}

	// filepath can come from either
	public String getCurrentFilepath() {
		if (mFile != null)
			return mFile.getAbsolutePath().toString();

		return mInputField.toString();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuFileInputEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mLabel = "Label:";

		mDrawBackground = false;
		mHighlightOnHover = false;
		mEnableScaleTextToWidth = true;

		mInputField = new StringBuilder();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {
		if (mFileFolderRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mCancelRectHovered = true;
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							var chooser = new JFileChooser();
							chooser.setMultiSelectionEnabled(false);

							if (mDirectorySelection) {
								chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							} else {
								var filter = new FileNameExtensionFilter("Definition Meta File", "json");
								chooser.setFileFilter(filter);
								chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							}

							if (mBaseDirectory != null) {
								final var lBaseDirectoryFile = new File(mBaseDirectory);
								if (lBaseDirectoryFile.exists() && lBaseDirectoryFile.isDirectory())
									chooser.setCurrentDirectory(lBaseDirectoryFile);
							}

							int returnVal = chooser.showOpenDialog(new JFrame());
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								mFile = chooser.getSelectedFile();
								inputString(mFile.getAbsolutePath());
							}
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (mClickListener != null)
					mClickListener.menuEntryOnClick(core.input(), mMenuEntryID);

				core.input().keyboard().stopBufferedTextCapture();

				mHasFocus = false;
				mShowCaret = false;

				return true;
			}
		} else {
			mCancelRectHovered = false;
		}

		return super.onHandleMouseInput(core);

	}

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

		mInputAreaRectangle.set(mX, mY + mH / 2.f, mW, mH / 2.f);
		final int lCancelRectSize = 32;
		mFileFolderRectangle.set(mInputAreaRectangle.x() + mInputAreaRectangle.width() - lCancelRectSize - 4, mInputAreaRectangle.y() + mInputAreaRectangle.height() / 2 - lCancelRectSize / 2, lCancelRectSize, lCancelRectSize);

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

		final float lUiTextScale = 1.f; // mParentScreen.uiTextScale();

		entryColor.setRGB(1.f, 1.f, 1.f);
		mDrawBackground = true;
		if (mDrawBackground) {
			boolean use5Steps = mW > 32 * 8;

			final float lTileSize = 32;
			final float lHalfWidth = (int) (mW * .5f);
			int lLeft = (int) (lScreenOffset.x + centerX() - lHalfWidth);
			final float lInnerWidth = mW - 32 * (use5Steps ? 4 : 2);
			entryColor.a = 1.f;

			if (isInClickedState()) {
				entryColor.r = 1.f;
				entryColor.g = 1.f;
				entryColor.b = 1.f;
			} else if (mHasFocus) {
				entryColor.r = .8f;
				entryColor.g = .8f;
				entryColor.b = .8f;
			} else {
				entryColor.r = .6f;
				entryColor.g = .6f;
				entryColor.b = .6f;
			}

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_TITLE_HORIZONTAL_LEFT, lLeft, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
			if (use5Steps)
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_TITLE_HORIZONTAL_MID_LEFT, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_TITLE_HORIZONTAL_MID, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lInnerWidth, mH, mZ, entryColor);
			if (use5Steps)
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_TITLE_HORIZONTAL_MID_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 96, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_TITLE_HORIZONTAL_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
			lSpriteBatch.end();
		}

		if (mHasFocus || mIsActive) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}

		final var lInputTextWidth = lTextBoldFont.getStringWidth(mInputField.toString(), lUiTextScale);

		entryColor.r = mEnabled ? 1f : 0.6f;
		entryColor.g = mEnabled ? 1f : 0.6f;
		entryColor.b = mEnabled ? 1f : 0.6f;
		textColor.a = mParentScreen.screenColor.a;

		if (mCursorPos >= mInputField.length())
			mCursorPos = mInputField.length();

		final var first_part_of_string = mCursorPos > 0 ? mInputField.subSequence(0, mCursorPos) : "";
		final var carot_position_x = lTextBoldFont.getStringWidth(first_part_of_string.toString(), 1.f);
		final int lCancelRectSize = 32;
		final var mw = mLabel == null ? mW - 32 : mInputAreaRectangle.width() - lCancelRectSize - 16.f;

		final var lIsTextTooLong = carot_position_x > mw;
		final var lTextOverlapWithBox = lInputTextWidth - mw;
		final var lTextPosX = lIsTextTooLong ? mInputAreaRectangle.x() - lTextOverlapWithBox : mInputAreaRectangle.x();

		lTextBoldFont.begin(core.HUD());
		final float lTextHeight = lTextBoldFont.fontHeight();

		lTextBoldFont.begin(core.HUD());
		if (mLabel != null) {
			final var lY = mY + ENTRY_DEFAULT_HEIGHT / 2.f - lTextHeight * .5f;
			lTextBoldFont.drawText(mLabel, mX, lY, mZ, textColor, 1.f);
		}

		lTextBoldFont.end();

		ContentRectangle.preDraw(core, lSpriteBatch, mInputAreaRectangle.x() + 2.f, mY, mInputAreaRectangle.width() - lCancelRectSize - 5.f, mH, -0, 1);

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mInputField.toString(), lTextPosX + 8, mInputAreaRectangle.y() + mInputAreaRectangle.height() * .5f - lTextHeight * .5f, mZ, textColor, 1.f);
		lTextBoldFont.end();

		ContentRectangle.postDraw(core);

		if (mShowCaret && mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			final var lCaretPositionX = lScreenOffset.x + lTextPosX + carot_position_x + 7;
			final var lCaretPositionY = lScreenOffset.y + mInputAreaRectangle.y() + mInputAreaRectangle.height() * .5f - lTextHeight * .5f;

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lCaretPositionX, lCaretPositionY, lTextHeight / 2.f, lTextHeight, mZ, ColorConstants.WHITE);
			lSpriteBatch.end();
		}

		lTextBoldFont.end();

		final var lDirectoryIconColor = mCancelRectHovered ? ColorConstants.WHITE : ColorConstants.getWhiteWithAlpha(.5f);
		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTUREDIRECTORY, mFileFolderRectangle, mZ, lDirectoryIconColor);
		lSpriteBatch.end();

		if (!mEnabled)
			drawdisabledBlackOverbar(core, lSpriteBatch, entryColor.a);

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, 1.f);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, 1.f);

		drawDebugCollidableBounds(core, lSpriteBatch);

		Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mInputAreaRectangle);
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
