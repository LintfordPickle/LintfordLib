package net.lintfordlib.screenmanager.entries.input;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.MenuActions;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.ActionManager;
import net.lintfordlib.core.input.IGamepadInputMappingCallback;
import net.lintfordlib.core.input.gamepad.Gamepad;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;
import net.lintfordlib.core.input.gamepad.LintfordGamepadState;
import net.lintfordlib.screenmanager.ConstantsScreenManagerAudio;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuGamepadInputMapEntry extends MenuEntry implements IGamepadInputMappingCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 4404902856375062146L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/**
	 * This is the target LintfordInputCode we are trying to map to with a physical button or axis
	 */
	public final int inputCodeUid;

	private IBindingCallback mBindingCallback;

	public void setBindingCallback(IBindingCallback listener) {
		mBindingCallback = listener;
	}

	private boolean mIsBindingInput;

	private boolean mIsInputOn;

	public boolean isInputOn() {
		return mIsInputOn;
	}

	public void isInputOn(boolean isInputOn) {
		mIsInputOn = isInputOn;
	}

	public boolean spriteEnabled;
	public int spriteFrameUidOn;
	public int spriteFrameUidOff;
	public float spritePositionX;
	public float spritePositionY;
	public float spritePositionW;
	public float spritePositionH;
	public float mFlashTimer;

	private Gamepad mActiveGamepad;

	public void setSpriteEnabled(boolean enabled) {
		spriteEnabled = enabled;
	}

	public void setSpriteFrameIds(int onFrameUid, int offFrameUid) {
		spriteFrameUidOn = onFrameUid;
		spriteFrameUidOff = offFrameUid;
	}

	public void setSpritePosition(float xPos, float yPos, float w, float h) {
		spritePositionX = xPos;
		spritePositionY = yPos;
		spritePositionW = w;
		spritePositionH = h;
	}

	public void activeGamepad(Gamepad activeGamepad) {
		mActiveGamepad = activeGamepad;
	}

	public Gamepad activeGamepad() {
		return mActiveGamepad;
	}

	public LintfordGamepadState activeControllerMap() {
		if (mActiveGamepad == null)
			return null;

		return mActiveGamepad.state;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuGamepadInputMapEntry(ScreenManager screenManager, MenuScreen parentScreen, int targetLintfordInputCode) {
		super(screenManager, parentScreen);

		assert (targetLintfordInputCode >= 0) : "The targetLintfordInputCode is invalid";

		spriteEnabled = false;
		inputCodeUid = targetLintfordInputCode;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean onHandleKeyboardInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {
			final var isKeyDownTimed = core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this);

			if (isKeyDownTimed && handleCaptureNewMapping(core))
				return true;

		}

		return super.onHandleKeyboardInput(core);
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {
			final var eventManager = core.input().actionManager();

			final var confirmAction = eventManager.getActionState(MenuActions.NAV_CONFIRM, ActionManager.PLAYER_INDEX_ALL);
			if (confirmAction.isDownTimed(this) && handleCaptureNewMapping(core)) {
				return true;
			}

		}

		return super.onHandleGamepadInput(core);
	}

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {

		if (!mIsActive)
			return false;

		if (mParentScreen == null || !mEnabled || mReadOnly)
			return false;

		if (!core.input().mouse().isMouseMenuSelectionEnabled()) {
			mIsMouseOver = false;
			return false;
		}

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = false;
			mAnimation.stop();
			return false;
		}

		if (!mIsMouseOver) {
			mAnimation.start();
			mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_OVER);
		}

		mIsMouseOver = true;

		if (!mHasFocus && mCanHaveFocus)
			mParentScreen.setFocusOnEntry(this);

		if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
			if (handleCaptureNewMapping(core)) {
				return true;
			}
		}

		return false;
		// return super.onHandleMouseInput(core);
	}

	private boolean handleCaptureNewMapping(LintfordCore core) {
		final var capturingGamepad = core.input().gamepads().isSomeComponentCapturingInput();

		if (capturingGamepad)
			return false;

		final var bindingToInputName = String.format("'%s' (%d)", GamepadInputCodes.getLintfordCodeName(inputCodeUid), inputCodeUid);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "changing gamepad input map for " + bindingToInputName + " ... ");
		core.input().gamepads().startGamepadMappingCapture(this, mActiveGamepad.index());

		mIsBindingInput = true;
		hasFocus(true);

		mBindingCallback.setIsBinding(this);

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		return true;
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var textureBatch = core.sharedResources().uiSpriteBatch();
		final var fontUnit = core.sharedResources().uiTextFont();

		final var parentScreenOffset = mParentScreen.screenPositionOffset();
		final var parentScreenAlpha = mParentScreen.screenColor.a;
		final var alpha = enabled() ? 1.f : 0.15f;

		final var buttonWidth = spritePositionW;
		final var buttonHeight = spritePositionH;

		if (mDrawBackground) {
			textureBatch.begin(core.HUD());
			textureBatch.setColorWhite();
			textureBatch.setColorA(0.5f * parentScreenAlpha);
			textureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, parentScreenOffset.x + mX, parentScreenOffset.y + mY, mW, mH, mZ);
			textureBatch.end();
		}

		textureBatch.begin(core.HUD());

		// 2 cases - off sprite not present vs. present
		if (spriteFrameUidOff == -1) {

			final var buttonSpriteFrame = mCoreSpritesheet.getSpriteFrame(spriteFrameUidOn);
			textureBatch.setColorBlack();
			textureBatch.setColorA(0.75f * alpha * parentScreenAlpha);
			textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 3, buttonWidth, buttonHeight, 1f);

			if (mIsInputOn) {
				// pressed
				textureBatch.setColorWhite();
				textureBatch.setColorA(alpha * parentScreenAlpha);
				textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);

			} else {
				if (mIsMouseOver) {
					textureBatch.setColorWhite();
					textureBatch.setColorA(alpha * parentScreenAlpha);
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY, buttonWidth, buttonHeight, 1f);

				} else {
					final var colorOffset = 0.7f;
					textureBatch.setColorRGBA(colorOffset, colorOffset, colorOffset, alpha * parentScreenAlpha);
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY, buttonWidth, buttonHeight, 1f);
				}
			}

		} else {
			// dedicated off-sprite
			final var buttonSpriteFrame = mCoreSpritesheet.getSpriteFrame(mIsInputOn ? spriteFrameUidOn : spriteFrameUidOff);
			textureBatch.setColorBlack();
			textureBatch.setColorA(0.75f * alpha * parentScreenAlpha);
			textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 3, buttonWidth, buttonHeight, 1f);

			textureBatch.setColorWhite();
			if (mIsMouseOver) {
				textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);
			} else {

				if (mIsInputOn) {
					textureBatch.setColorWhite();
					textureBatch.setColorA(alpha * parentScreenAlpha);
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);
				} else {
					final var colorOffset = 0.7f;
					textureBatch.setColorRGBA(colorOffset, colorOffset, colorOffset, alpha * parentScreenAlpha);
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);
				}
			}
		}

		textureBatch.end();

		textureBatch.begin(core.HUD());
		fontUnit.setTextColorWhite();
		fontUnit.setTextColorA(alpha * parentScreenAlpha);
		fontUnit.begin(core.HUD());

		final var targetInputCodetext = GamepadInputCodes.getLintfordCodeName(inputCodeUid);
		fontUnit.drawText(targetInputCodetext, parentScreenOffset.x + left() + 5.f, parentScreenOffset.y + top(), .9f, .9f * mScale);

		if (mActiveGamepad != null) {
			final var state = mActiveGamepad.state;

			final var gamepadInputMap = state.getInputMapping(inputCodeUid);
			final var mappedToType = gamepadInputMap.mappedToType();
			final var mappedToSignum = gamepadInputMap.mappedToSignum();
			final var mappedToIndex = gamepadInputMap.mappedTo();

			String mappedToName = "unmapped";
			switch (mappedToType) {
			default:
				break;
			case button:
				mappedToName = "button " + mappedToIndex;
				break;
			case axis:
				mappedToName = "axis " + mappedToIndex + " " + (mappedToSignum > 0 ? "+" : "-");
				break;
			}

			fontUnit.drawText(mappedToName, parentScreenOffset.x + left() + 10.f, parentScreenOffset.y + top() + 20, 1f, .8f * mScale);
		}

		textureBatch.end();
		fontUnit.end();

		if (mHasFocus && mEnabled)
			renderHighlight(core, screen, true, textureBatch);

		if (!mIsBindingInput)
			drawGamepadIcon(core, textureBatch, parentScreenOffset.x + mX + mW - 16, parentScreenOffset.y + mY + mH - 16, parentScreenAlpha);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			textureBatch.begin(core.HUD());
			textureBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			textureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, parentScreenOffset.x + mX, parentScreenOffset.y + mY, mW, mH, mZ);
			textureBatch.end();
		}

	}

	// --------------------------------------
	// Method
	// --------------------------------------

	public void endBinding() {
		if (!mIsBindingInput)
			return;

		mIsBindingInput = false;
		mBindingCallback.finishedBinding();

		mScreenManager.core().input().gamepads().stopGamepadCapture();
	}

	// called from parent screen
	public void cancelBinding() {
		if (mIsBindingInput) {
			endBinding();
		}
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public boolean gamepadButtonInput(int rawButtonUid) {
		if (mIsBindingInput && isCoolDownElapsed()) {
			final var inputName = String.format("'%s' (%d)", GamepadInputCodes.getLintfordCodeName(inputCodeUid), inputCodeUid);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "gamepad bind " + inputName + " mapped to " + rawButtonUid);

			mActiveGamepad.state.setButtonMapping(rawButtonUid, inputCodeUid);
			endBinding();

			return true;
		}

		return false;
	}

	@Override
	public boolean gamepadAxisInput(int rawAxisUid, float signum) {

		if (mIsBindingInput && isCoolDownElapsed()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "gamepad bind axis <...> mapped to " + rawAxisUid);

			// I want to set <inputCodeUid> to use <rawAxisUid, signum>

			mActiveGamepad.state.setAxisMapping(rawAxisUid, signum, inputCodeUid);
			endBinding();

			return true;
		}

		return false;
	}

	@Override
	public void mappingCancelled() {
		endBinding();
	}
}
