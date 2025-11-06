package net.lintfordlib.renderers.debug;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputHelper;
import net.lintfordlib.core.input.gamepad.Gamepad;
import net.lintfordlib.core.input.gamepad.IGamepadListener;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class DebugControllerRenderer extends BaseRenderer implements IGamepadListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "Debug Controller Renderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mActiveGamepadIndex;
	private Gamepad mActiveGamepad;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return true;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugControllerRenderer(RendererManagerBase rendererManager, int entityGroupUid) {
		super(rendererManager, RENDERER_NAME, entityGroupUid);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		core.input().gamepads().addGamepadListener(this);
	}

	@Override
	public boolean handleInput(LintfordCore core) {

		// Allow toggling between active controllers.
		final var gamepadManager = core.input().gamepads();
		final var gamepads = gamepadManager.getActiveGamepads();
		final var numGamepads = gamepads.size();

		if (numGamepads == 0) {
			mActiveGamepadIndex = -1;
			mActiveGamepad = null;
		} else if (numGamepads == 0) {
			mActiveGamepadIndex = 0;
			mActiveGamepad = gamepads.get(mActiveGamepadIndex);
		} else {
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_COMMA, this)) {
				mActiveGamepadIndex--;
			}

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_PERIOD, this)) {
				mActiveGamepadIndex++;

			}

			if (mActiveGamepadIndex < 0)
				mActiveGamepadIndex = numGamepads - 1;
			if (mActiveGamepadIndex >= numGamepads)
				mActiveGamepadIndex = 0;

			mActiveGamepad = gamepads.get(mActiveGamepadIndex);
		}

		return super.handleInput(core);
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var hudBounds = core.HUD().boundingRectangle();
		final var fontUnit = Debug.debugManager().drawers().textRenderer();
		fontUnit.setTextColorWhite();

		if (mActiveGamepad == null) {
			final var text = "No controller connected";
			final var textWidth = fontUnit.getStringWidth(text);

			fontUnit.begin(core.HUD());
			fontUnit.drawText(text, hudBounds.right() - 10 - textWidth, hudBounds.top() + 10, 1f, 1f);
			fontUnit.end();

			return;
		}

		float yPos = 10;
		final float lineHeight = 20;

		final var text = mActiveGamepad.name();
		final var textWidth = fontUnit.getStringWidth(text);

		fontUnit.begin(core.HUD());

		fontUnit.drawText(text, hudBounds.right() - 10 - textWidth, hudBounds.top() + 10 + yPos, 1f, 1f);
		yPos += lineHeight;
		fontUnit.drawText("Controller ID: " + mActiveGamepadIndex, hudBounds.right() - 10 - textWidth, hudBounds.top() + 10 + yPos, 1f, 1f);
		yPos += lineHeight * 2;

		final var mappedValuesAvailable = mActiveGamepad.isGamepadMappingAvailable();
		final var showMappedValues = mappedValuesAvailable && true;

		if (showMappedValues) {
			final var typeText = "Mapped Values";
			final var typeTextWidth = fontUnit.getStringWidth(typeText);

			fontUnit.drawText(typeText, hudBounds.right() - 10 - typeTextWidth, hudBounds.top() + 10 + yPos, 1f, 1f);
			final var numButtons = mActiveGamepad.numButtons();
			for (int i = 0; i < numButtons; i++) {
				final var buttonState = mActiveGamepad.debug_getSdlButtonState(i, GLFW.GLFW_PRESS);

				final var buttonName = InputHelper.getGlfwPrintableKeyForGamepadButtons(i);
				final var buttonText = buttonName + " : " + buttonState;
				final var buttonNameWidth = fontUnit.getStringWidth(buttonText);

				fontUnit.drawText(buttonText, hudBounds.right() - 10 - buttonNameWidth, hudBounds.top() + 10 + (yPos += lineHeight), 1f, 1f);
			}

			final var numAxis = mActiveGamepad.numAxis();
			for (int i = 0; i < numAxis; i++) {
				final var axisState = mActiveGamepad.debug_getSdlAxisValue(i);

				final var buttonName = InputHelper.getGlfwPrintableKeyForGamepadAxis(i);
				final var buttonText = String.format("%s : %.2f", buttonName, axisState);
				final var buttonNameWidth = fontUnit.getStringWidth(buttonText);

				fontUnit.drawText(buttonText, hudBounds.right() - 10 - buttonNameWidth, hudBounds.top() + 10 + (yPos += lineHeight), 1f, 1f);
			}

		} else {
			final var typeText = "Raw Values";
			final var typeTextWidth = fontUnit.getStringWidth(typeText);

			fontUnit.drawText(typeText, hudBounds.right() - 10 - typeTextWidth, hudBounds.top() + 10 + yPos, 1f, 1f);

			final var numButtons = mActiveGamepad.numButtons();
			for (int i = 0; i < numButtons; i++) {
				final var buttonState = mActiveGamepad.debug_getPhysicalButtonState(i, GLFW.GLFW_PRESS);

				final var buttonName = "button: " + i;
				final var buttonText = buttonName + " : " + buttonState;
				final var buttonNameWidth = fontUnit.getStringWidth(buttonText);

				fontUnit.drawText(buttonText, hudBounds.right() - 10 - buttonNameWidth, hudBounds.top() + 10 + (yPos += lineHeight), 1f, 1f);
			}

			final var numAxis = mActiveGamepad.numAxis();
			for (int i = 0; i < numAxis; i++) {
				final var axisState = mActiveGamepad.debug_getPhysicalAxisValue(i);

				final var buttonName = "axis: " + i;
				final var buttonText = String.format("%s : %.2f ", buttonName, axisState);
				final var buttonNameWidth = fontUnit.getStringWidth(buttonText);

				fontUnit.drawText(buttonText, hudBounds.right() - 10 - buttonNameWidth, hudBounds.top() + 10 + (yPos += lineHeight), 1f, 1f);
			}
		}

		fontUnit.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onGamepadConnected(Gamepad gamepad) {
		if (mActiveGamepad == null) {
			mActiveGamepad = gamepad;
		}

	}

	@Override
	public void onGamepadDisconnected(Gamepad gamepad) {
		if (mActiveGamepad == gamepad) {
			mActiveGamepad = null;
		}

	}
}