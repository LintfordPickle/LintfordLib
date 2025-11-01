package net.lintfordlib.screenmanager.entries.input;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.IGamepadInputMappingCallback;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;
import net.lintfordlib.core.input.gamepad.LintfordGamepadState;
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

	// TODO: This is the target LintfordInputCode we are trying to map to with a physical button or axis
	private final int inputCodeUid;
	private LintfordGamepadState mInputGamepadCustomMap;
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

	public void activeControllerMap(LintfordGamepadState inputGamepadCustomMap) {
		mInputGamepadCustomMap = inputGamepadCustomMap;
	}

	public LintfordGamepadState activeControllerMap() {
		return mInputGamepadCustomMap;
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
			final var gamepadManager = core.input().gamepads();

			// TOOD: Do t
			if (gamepadManager.isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_A, this) && handleCaptureNewMapping(core)) {
				return true;
			}

		}

		return super.onHandleGamepadInput(core);
	}

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {
		return super.onHandleMouseInput(core);
	}

	private boolean handleCaptureNewMapping(LintfordCore core) {
		final var capturingGamepad = core.input().gamepads().isSomeComponentCapturingInput();

		if (capturingGamepad)
			return false;

		final var bindingToInputName = String.format("'%s' (%d)", GamepadInputCodes.getLintfordCodeName(inputCodeUid), inputCodeUid);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "changing gamepad input map for " + bindingToInputName + " ... ");
		core.input().gamepads().startGamepadMappingCapture(this);

		mIsBindingInput = true;
		mHasFocus = true;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		return true;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		mIsInputOn = false;
		if (mInputGamepadCustomMap != null) {
			final var gamepadInputMap = mInputGamepadCustomMap.getInputMapping(inputCodeUid);
			final var mappedToType = gamepadInputMap.mappedToType();
			final var mappedToSignum = gamepadInputMap.mappedToSignum();
			final var mappedToIndex = gamepadInputMap.mappedTo();

			if (gamepadInputMap != null) {
				mIsInputOn = gamepadInputMap.isDown();
			}

		}

	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var textureBatch = core.sharedResources().uiSpriteBatch();
		final var fontUnit = core.sharedResources().uiTextFont();

		final var parentScreenOffset = mParentScreen.screenPositionOffset();
		final var parentScreenAlpha = mParentScreen.screenColor.a;

		final var buttonWidth = spritePositionW;
		final var buttonHeight = spritePositionH;

		textureBatch.begin(core.HUD());
		// 2 cases - off sprite not present vs. present
		if (spriteFrameUidOff == -1) {

			final var buttonSpriteFrame = mCoreSpritesheet.getSpriteFrame(spriteFrameUidOn);
			textureBatch.setColorBlack();
			textureBatch.setColorA(0.75f);
			textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 3, buttonWidth, buttonHeight, 1f);

			if (mIsInputOn) {
				// pressed
				textureBatch.setColorWhite();
				textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);

			} else {
				if (mIsMouseOver) {
					textureBatch.setColorWhite();
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY, buttonWidth, buttonHeight, 1f);

				} else {
					final var colorOffset = 0.7f;
					textureBatch.setColorRGBA(colorOffset, colorOffset, colorOffset, 1.f);
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY, buttonWidth, buttonHeight, 1f);
				}
			}

		} else {
			// dedicated off-sprite
			final var buttonSpriteFrame = mCoreSpritesheet.getSpriteFrame(mIsInputOn ? spriteFrameUidOn : spriteFrameUidOff);
			textureBatch.setColorBlack();
			textureBatch.setColorA(0.75f);
			textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 3, buttonWidth, buttonHeight, 1f);

			textureBatch.setColorWhite();
			if (mIsMouseOver) {
				textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);
			} else {

				if (mIsInputOn) {
					textureBatch.setColorWhite();
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);
				} else {
					final var colorOffset = 0.7f;
					textureBatch.setColorRGBA(colorOffset, colorOffset, colorOffset, 1.f);
					textureBatch.draw(mCoreSpritesheet, buttonSpriteFrame, parentScreenOffset.x + spritePositionX, parentScreenOffset.y + spritePositionY + 2, buttonWidth, buttonHeight, 1f);
				}

			}

		}

		textureBatch.end();

		textureBatch.begin(core.HUD());
		fontUnit.setTextColorWhite();
		fontUnit.begin(core.HUD());

		final var targetInputCodetext = GamepadInputCodes.getLintfordCodeName(inputCodeUid);
		fontUnit.drawText(targetInputCodetext, parentScreenOffset.x + left(), parentScreenOffset.y + top(), .9f, .9f);

		// TODO: Need to get whatever is currently mapped on the active gamepad to our inputCodeUid
		if (mInputGamepadCustomMap != null) {
			final var gamepadInputMap = mInputGamepadCustomMap.getInputMapping(inputCodeUid);
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

			fontUnit.drawText(mappedToName, parentScreenOffset.x + left() + 5.f, parentScreenOffset.y + top() + 20, 1f, .8f);
		}

		textureBatch.end();
		fontUnit.end();

		if (mHasFocus) {
			// Set which hint icon to render
			mGamepadMenuIcon.lintfordInputCode(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH);
			drawGamepadIcon(core, textureBatch, mGamepadMenuIcon.bounds, parentScreenAlpha);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			textureBatch.begin(core.HUD());
			textureBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			textureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, parentScreenOffset.x + mX, parentScreenOffset.y + mY, mW, mH, mZ);
			textureBatch.end();
		}

	}

	// --------------------------------------
	// Inherited-Method
	// --------------------------------------

	@Override
	public boolean gamepadButtonInput(int rawButtonUid) {
		if (mIsBindingInput && isCoolDownElapsed()) {
			final var inputName = String.format("'%s' (%d)", GamepadInputCodes.getLintfordCodeName(inputCodeUid), inputCodeUid);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "gamepad bind " + inputName + " mapped to " + rawButtonUid);

			mInputGamepadCustomMap.setButtonMapping(rawButtonUid, inputCodeUid);
			mIsBindingInput = false;

			return true;
		}

		return false;
	}

	@Override
	public boolean gamepadAxisInput(int rawAxisUid, float signum) {

		// TODO: Need to use the value to figure out which direction the axis was trigger in.
		System.out.println("Axis input : " + rawAxisUid + " (" + (signum > 0.f ? "+" : "-") + ")");

		if (mIsBindingInput && isCoolDownElapsed()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "gamepad bind axis <...> mapped to " + rawAxisUid);

			// I want to set <inputCodeUid> to use <rawAxisUid, signum>

			mInputGamepadCustomMap.setAxisMapping(rawAxisUid, signum, inputCodeUid);
			mIsBindingInput = false;

			return true;
		}

		return false;
	}

}
