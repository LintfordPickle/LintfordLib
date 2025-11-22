package net.lintfordlib.screenmanager.entries;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.MenuActions;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.IGamepadInputBindingCallback;
import net.lintfordlib.core.input.IKeyInputCallback;
import net.lintfordlib.core.input.InputAction;
import net.lintfordlib.core.input.InputHelper;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;

//       | LABEL      | Keyboard         | Gamepad    |

public class MenuBindingKeyEntry extends MenuEntry implements IKeyInputCallback, IGamepadInputBindingCallback {

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
	private final InputAction mInputAction; // TODO: Rename to EventAction

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

	public InputAction eventAction() {
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

	public void label(String newLabel) {
		mText = newLabel;
	}

	public String label() {
		return mText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuBindingKeyEntry(ScreenManager screenManager, MenuScreen parentScreen, InputAction eventAction) {
		super(screenManager, parentScreen, "");

		mInputAction = eventAction;
		mDrawBackground = false;
		mText = "Missing Label";

		mCanHaveFocus = true;
		mIsStateValid = true;

		mIsDirty = true;
		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean onHandleInputActions(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {

			final var actionManager = core.input().actionManager();

			final var navLeft = actionManager.getActionState(MenuActions.NAV_LEFT);
			final var navRight = actionManager.getActionState(MenuActions.NAV_RIGHT);
			final var navConfirm = actionManager.getActionState(MenuActions.NAV_CONFIRM);

			if (!mIsBindingInput && navLeft.isDownTimed(this)) {
				mIsKeyAreaSelected = !mIsKeyAreaSelected;
				return true;
			}

			if (!mIsBindingInput && navRight.isDownTimed(this)) {
				mIsKeyAreaSelected = !mIsKeyAreaSelected;
				return true;
			}

			if (!mIsBindingInput && navConfirm.isDownTimed(this) && handleCaptureNewBinding(core)) {
				return true;
			}
		}

		return super.onHandleInputActions(core);
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

			if (mKeyArea.intersectsAA(core.HUD().getMouseCameraSpace()))
				mIsKeyAreaSelected = true;

			if (mGamepadArea.intersectsAA(core.HUD().getMouseCameraSpace()))
				mIsKeyAreaSelected = false;

			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

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
				mBoundGamePadText = InputHelper.getFriendlyKeyNameForGamepadInputIndex(mInputAction.getBoundGamepadCode()).toUpperCase();

			}

			mIsDirty = false;
		}

	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!enabled())
			return;

		final var xoffset = screen.screenPositionOffset().x;
		final var yoffset = screen.screenPositionOffset().y;

		final var partWidth = width() / 3;
		mKeyArea.set(xoffset + x() + partWidth, yoffset + y(), partWidth, height());
		mGamepadArea.set(xoffset + x() + partWidth * 2, yoffset + y(), partWidth, height());

		final var numBindColumns = 3;
		final var columnWidth = width() / numBindColumns;

		final var column0X = xoffset + x() + padding();
		final var column1X = xoffset + x() + padding() + columnWidth * 1;
		final var column2X = xoffset + x() + padding() + columnWidth * 2;

		final var textBoldFont = mParentScreen.fontBold();

		entryColor.setRGB(1.f, 1.f, 1.f);
		textColor.a = mParentScreen.screenColor.a;

		final var lUiTextScale = mParentScreen.uiTextScale();
		final var lFontHeight = textBoldFont.fontHeight() * lUiTextScale;
		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(entryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, xoffset + mX, yoffset + mY, mW, mH, parentZDepth + .15f);
			lSpriteBatch.end();

		} else if (mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(ColorConstants.MenuEntrySelectedColor);
			lSpriteBatch.setColorA(.15f);

			final var xx = xoffset + centerX() - mW / 2;
			final var yy = yoffset + centerY() - mH / 2;

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, xx, yy, 32, mH, parentZDepth + .15f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, xx + 32, yy, mW - 64, mH, parentZDepth + .15f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, xoffset + centerX() + mW / 2 - 32, yy, 32, mH, parentZDepth + .15f);
			lSpriteBatch.end();
		}

		mCaretFlashTimer += core.appTime().elapsedTimeMilli() * 0.001f;

		textBoldFont.begin(core.HUD());
		textBoldFont.setTextColor(textColor);
		textBoldFont.drawText(mText, column0X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);

		// Keybinding
		if (mIsBindingInput && mIsKeyAreaSelected) {
			// Rebinding the key input

			final float lColorMod = .5f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, parentZDepth + .15f);
			lSpriteBatch.end();

			if (mCaretFlashTimer % 1.f > .5f) {
				final var boundKeyText = "<ENTER KEY>";
				textBoldFont.drawText(boundKeyText, column1X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
			}

		} else if (mBoundKeyText != null && mBoundKeyText.length() > 0) {
			textBoldFont.drawText(mBoundKeyText, column1X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
		}

		if (mIsBindingInput && !mIsKeyAreaSelected) {
			// Rebinding a gamepad input

			final String lBoundKeyText = "<PRESS BUTTON>";
			final float lColorMod = .5f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, xoffset + mX, yoffset + mY, mW, mH, parentZDepth + .15f);
			lSpriteBatch.end();

			if (mCaretFlashTimer % 1.f > .5f) {
				textBoldFont.drawText(lBoundKeyText, column2X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
			}

		} else if (mBoundGamePadText != null && mBoundGamePadText.length() > 0) {
			textBoldFont.drawText(mBoundGamePadText, column2X, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
		}

		textBoldFont.end();

		// Gamepad hints
		if (mHasFocus) {
			if (mIsKeyAreaSelected) {
				drawGamepadIcon(core, lSpriteBatch, xoffset + column1X + columnWidth - 32, yoffset + mY + mH - 16, 1.f);
			} else {
				drawGamepadIcon(core, lSpriteBatch, xoffset + column2X + columnWidth - 32, yoffset + mY + mH - 16, 1.f);
			}
		}

		//
		if (mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			if (mIsKeyAreaSelected) {
				lSpriteBatch.setColor(ColorConstants.MenuEntrySelectedColor);
				lSpriteBatch.setColorA(.15f);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mKeyArea, mZ);
			} else {
				lSpriteBatch.setColor(ColorConstants.MenuEntrySelectedColor);
				lSpriteBatch.setColorA(.15f);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mGamepadArea, mZ);
			}
			lSpriteBatch.end();

			renderHighlight(core, screen, false, lSpriteBatch);
		}

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, mParentScreen.screenColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, mParentScreen.screenColor.a);

		// TODO: Move this into the base MenuEntry type (so all MenuEntries can be valid/invalid and highlighted).
		if (!mIsStateValid) {
			final var lLineBatch = mParentScreen.lineBatch();
			lLineBatch.begin(core.HUD());
			lLineBatch.changeColorNormalized(.7f, .04f, .02f, 1.f);
			lLineBatch.lineType(GL11.GL_LINES);
			lLineBatch.drawRect(this, .1f);
			lLineBatch.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, mZ);

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
	// Methods
	// --------------------------------------

	private boolean handleCaptureNewBinding(LintfordCore core) {
		if (mInputAction == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Error calibrating action. The action has not been correctly registered. Check the stack trace below:");
			Debug.debugManager().logger().printStacktrace(getClass().getSimpleName());
			return false;
		}

		final var capturingKeyboard = core.input().keyboard().isSomeComponentCapturingInputKeys();
		final var capturingGamepad = core.input().gamepads().isSomeComponentCapturingInput();

		if (capturingKeyboard || capturingGamepad)
			return false;

		if (mIsKeyAreaSelected) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "changing key binding for " + mInputAction.actionUid);
			core.input().keyboard().StartKeyInputCapture(this);

			resetCoolDownTimer();

			mIsBindingInput = true;
			hasFocus(true);

			core.input().mouse().isMouseMenuSelectionEnabled(false);

		} else {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "changing gamepad binding for " + mInputAction.actionUid);
			core.input().gamepads().startGamepadBindingCapture(this);

			resetCoolDownTimer();

			mIsBindingInput = true;
			hasFocus(true);

			core.input().mouse().isMouseMenuSelectionEnabled(false);

		}

		return true;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public boolean onNavigationGainFocus(LintfordCore core) {
		return super.onNavigationGainFocus(core);
	}

	@Override
	public boolean keyInput(int key, int scanCode, int action, int mods) {
		if (mIsBindingInput && isCoolDownElapsed()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "key bind invoke " + mInputAction.actionUid + " called to " + GLFW.glfwGetKeyName(GLFW.glfwGetKeyScancode(key), scanCode));
			mInputAction.boundKeyCode(key);
			mIsBindingInput = false;
			mIsDirty = true;

			return true;
		}

		return false;
	}

	@Override
	public boolean gamepadButtonBindingInput(int lintfordGamepadButtonId) {
		if (mIsBindingInput && isCoolDownElapsed()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "gamepad bind button " + mInputAction.actionUid + " called to " + lintfordGamepadButtonId);

			mInputAction.boundGamepadCode(lintfordGamepadButtonId);
			mIsBindingInput = false;
			mIsDirty = true;

			return true;
		}

		return false;
	}

	@Override
	public boolean gamepadAxisBindingInput(int lintfordGamepadButtonId) {
		if (mIsBindingInput && isCoolDownElapsed()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "gamepad bind axis " + mInputAction.actionUid + " called to " + lintfordGamepadButtonId);

			mInputAction.boundGamepadCode(lintfordGamepadButtonId);
			mIsBindingInput = false;
			mIsDirty = true;

			return true;
		}

		return false;
	}
}
