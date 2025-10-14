package net.lintfordlib.screenmanager.entries;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.GameInputAction;
import net.lintfordlib.core.input.IGamepadInputCallback;
import net.lintfordlib.core.input.IKeyInputCallback;
import net.lintfordlib.core.input.InputHelper;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;

//       | LABEL      | Keyboard         | Gamepad    |

public class MenuBindingKeyEntry extends MenuEntry implements IKeyInputCallback, IGamepadInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -6246272207476797676L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsDirty;

	private String mBoundKeyText;
	private String mBoundGamePadText;

	private float mPadding = 15f;
	private final GameInputAction mInputAction;
	private boolean mShow;

	private boolean mIsBindingInput;

	private float mCaretFlashTimer;

	private final Rectangle mKeyArea = new Rectangle();
	private final Rectangle mGamepadArea = new Rectangle();

	private boolean mIsKeyAreaSelected;

	private boolean mIsStateValid;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean keyAreaSelected() {
		return mIsKeyAreaSelected;
	}

	public void keyAreaSelected(boolean keyAreaSelected) {
		mIsKeyAreaSelected = keyAreaSelected;
	}

	public boolean isStateValid() {
		return mIsStateValid;
	}

	public void isStateValid(boolean newValue) {
		mIsStateValid = newValue;
	}

	public GameInputAction eventAction() {
		return mInputAction;
	}

	/** Padding is applied when the label is either aligned left or right (not when centered). */
	public float padding() {
		return mPadding;
	}

	/** Padding is applied when the label is either aligned left or right (not when centered). */
	public void padding(float newValue) {
		mPadding = newValue;
	}

	public boolean show() {
		return mShow;
	}

	public void show(boolean newValue) {
		mShow = newValue;
	}

	public void label(String newLabel) {
		mText = newLabel;
	}

	public String label() {
		return mText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuBindingKeyEntry(ScreenManager screenManager, MenuScreen parentScreen, GameInputAction eventAction) {
		super(screenManager, parentScreen, "");

		mInputAction = eventAction;
		mDrawBackground = false;
		mText = "Add your message";
		mShow = true;

		mCanHaveFocus = true;
		mIsStateValid = true;

		mIsDirty = true;
		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean onHandleKeyboardInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {

			if (!mIsBindingInput && core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_LEFT, this)) {
				mIsKeyAreaSelected = !mIsKeyAreaSelected;
			}

			if (!mIsBindingInput && core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_RIGHT, this)) {
				mIsKeyAreaSelected = !mIsKeyAreaSelected;
			}

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this) && handleCaptureNewBinding(core)) {
				return true;
			}

		}

		return super.onHandleKeyboardInput(core);
	}

	private boolean handleCaptureNewBinding(LintfordCore core) {
		if (mInputAction == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Error calibrating EventAction. The EventAction has not been correctly registered. Check the stack trace below:");
			Debug.debugManager().logger().printStacktrace(getClass().getSimpleName());
			return false; // TODO: handle this case
		}

		final var capturingKeyboard = core.input().keyboard().isSomeComponentCapturingInputKeys();
		final var capturingGamepad = core.input().gamepads().isSomeComponentCapturingInput();

		if (capturingKeyboard || capturingGamepad)
			return false;

		if (mIsKeyAreaSelected) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "changing key bind for " + mInputAction.eventActionUid());
			core.input().keyboard().StartKeyInputCapture(this);

			mIsBindingInput = true;
			mHasFocus = true;

			core.input().mouse().isMouseMenuSelectionEnabled(false);

		} else {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "changing gamepad bind for " + mInputAction.eventActionUid());
			core.input().gamepads().StartGamepadInputCapture(this);

			mIsBindingInput = true;
			mHasFocus = true;

			core.input().mouse().isMouseMenuSelectionEnabled(false);

		}

		return true;
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {
			final var gamepadManager = core.input().gamepads();

			final var dpadButtonLeftPressed = gamepadManager.isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, this);
			final var dpadButtonRightPressed = gamepadManager.isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, this);
			final var leftAxisX = gamepadManager.getGamepadAxisValueTimed(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, this);
			if (!mIsBindingInput && (dpadButtonLeftPressed || leftAxisX < 0)) {
				mIsKeyAreaSelected = !mIsKeyAreaSelected;
			}

			if (!mIsBindingInput && (dpadButtonRightPressed || leftAxisX > 0)) {
				mIsKeyAreaSelected = !mIsKeyAreaSelected;
			}

			if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_A, this) && handleCaptureNewBinding(core)) {
				return true;
			}

		}

		return super.onHandleGamepadInput(core);
	}

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = false;
			return false;
		}

		if (mHasFocus) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				if (mKeyArea.intersectsAA(core.HUD().getMouseCameraSpace()))
					mIsKeyAreaSelected = true;

				if (mGamepadArea.intersectsAA(core.HUD().getMouseCameraSpace()))
					mIsKeyAreaSelected = false;

				if (handleCaptureNewBinding(core)) {
					return true;
				}
			}
		}

		return super.onHandleMouseInput(core);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mCoreSpritesheet = null;

	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		if (mIsDirty) {
			if (mInputAction != null) {

				mBoundKeyText = InputHelper.getGlfwPrintableKeyFromKeyCode(mInputAction.getBoundKeyCode()).toUpperCase();
				mBoundGamePadText = InputHelper.getLintfordPrintableKeyForGamepadInputIndex(mInputAction.getBoundGamepadCode()).toUpperCase();

			}

			mIsDirty = false;
		}

		final var partWidth = width() / 3;
		mKeyArea.set(x() + partWidth, y(), partWidth, height());
		mGamepadArea.set(x() + partWidth * 2, y(), partWidth, height());
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!enabled())
			return;

		final var xoffset = screen.screenPositionOffset().x;

		final var numBindColumns = 3;
		final var columnWidth = width() / numBindColumns;

		final var column0X = xoffset + left() + paddingLeft() + 10;
		final var column1X = xoffset + x() + columnWidth * 1;
		final var column2X = xoffset + x() + columnWidth * 2;

		// ---

		final var lTextBoldFont = mParentScreen.fontBold();

		entryColor.setRGB(1.f, 1.f, 1.f);
		textColor.a = mParentScreen.screenColor.a;

		final var lUiTextScale = mParentScreen.uiTextScale();
		final var lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(entryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, parentZDepth + .15f);
			lSpriteBatch.end();

		} else if (mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(ColorConstants.MenuEntrySelectedColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, centerX() - mW / 2, centerY() - mH / 2, 32, mH, parentZDepth + .15f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, centerX() - mW / 2 + 32, centerY() - mH / 2, mW - 64, mH, parentZDepth + .15f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, centerX() + mW / 2 - 32, centerY() - mH / 2, 32, mH, parentZDepth + .15f);
			lSpriteBatch.end();
		}

		// half way
		float lX = mX + mW / 2;

		mCaretFlashTimer += core.appTime().elapsedTimeMilli() * 0.001f;

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.setTextColor(textColor);
		// lTextBoldFont.drawText(mText, lX - lLabelWidth - 20.f, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
		lTextBoldFont.drawText(mText, column0X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);

		// Keybinding
		if (mIsBindingInput && mIsKeyAreaSelected) {
			// Rebinding the key input

			final String lBoundKeyText = "<ENTER KEY>";
			final float lColorMod = .5f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, parentZDepth + .15f);
			lSpriteBatch.end();

			if (mCaretFlashTimer % 1.f > .5f) {
				lTextBoldFont.drawText(lBoundKeyText, column1X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
			}

		} else if (mBoundKeyText != null && mBoundKeyText.length() > 0) {
			lTextBoldFont.drawText(mBoundKeyText, column1X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
		}

		if (mIsBindingInput && !mIsKeyAreaSelected) {
			// Rebinding a gamepad input

			final String lBoundKeyText = "<ENTER GAMEPAD>";
			final float lColorMod = .5f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, parentZDepth + .15f);
			lSpriteBatch.end();

			if (mCaretFlashTimer % 1.f > .5f) {
				lTextBoldFont.drawText(lBoundKeyText, column2X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
			}

		} else if (mBoundGamePadText != null && mBoundGamePadText.length() > 0) {
			lTextBoldFont.drawText(mBoundGamePadText, column2X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
		}

		// Gamepad Binding

		if (mIsBindingInput) {

		}

		lTextBoldFont.end();

		// -- END

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, mParentScreen.screenColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, mParentScreen.screenColor.a);

		// TODO: This would be useful for all MenuInput types -
		if (!mIsStateValid) {
			final var lLineBatch = mParentScreen.lineBatch();
			lLineBatch.begin(core.HUD());
			lLineBatch.changeColorNormalized(.7f, .04f, .02f, 1.f);
			lLineBatch.lineType(GL11.GL_LINES);
			lLineBatch.drawRect(this, .1f);
			lLineBatch.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", true)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
//			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, mZ);

			if (mHasFocus) {
				if (mIsKeyAreaSelected) {
					lSpriteBatch.setColor(ColorConstants.RED());
					lSpriteBatch.setColorA(.25f);
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mKeyArea, mZ);
				} else {
					lSpriteBatch.setColor(ColorConstants.GREEN());
					lSpriteBatch.setColorA(.25f);
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mGamepadArea, mZ);
				}
			}

			lSpriteBatch.end();
		}
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public boolean keyInput(int key, int scanCode, int action, int mods) {
		if (mIsBindingInput && isCoolDownElapsed()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "key bind invoke " + mInputAction.eventActionUid() + " called to " + GLFW.glfwGetKeyName(GLFW.glfwGetKeyScancode(key), scanCode));
			mInputAction.boundKeyCode(key);
			mIsBindingInput = false;
			mIsDirty = true;

			return true;
		}

		return false;
	}

	@Override
	public boolean gamepadInput(int lintfordGamepadButtonId) {
		if (mIsBindingInput && isCoolDownElapsed()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "gamepad bind invoke " + mInputAction.eventActionUid() + " called to " + lintfordGamepadButtonId);

			mInputAction.boundGamepadCode(lintfordGamepadButtonId);
			mIsBindingInput = false;
			mIsDirty = true;

			return true;
		}

		return false;
	}
}
