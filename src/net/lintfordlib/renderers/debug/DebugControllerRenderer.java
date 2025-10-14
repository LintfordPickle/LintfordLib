package net.lintfordlib.renderers.debug;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputHelper;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.gamepad.IGamepadListener;
import net.lintfordlib.core.input.gamepad.InputGamepad;
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

	private InputGamepad mActiveGamepad;

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

		mActiveGamepad = GetActiveController(core.input());

		core.input().gamepads().addGamepadListener(this);

	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {

		final var hudBounds = core.HUD().boundingRectangle();
		final var fontUnit = Debug.debugManager().drawers().textRenderer();

		if (mActiveGamepad == null) {
			final var text = "No controller connected";
			final var textWidth = fontUnit.getStringWidth(text);

			fontUnit.begin(core.HUD());
			fontUnit.drawText(text, hudBounds.right() - 10 - textWidth, hudBounds.top() + 10, 1f, 1f);
			fontUnit.end();

			return;
		}

		float yPos = 100;
		final float lineHeight = 20;

		final var text = mActiveGamepad.name();
		final var textWidth = fontUnit.getStringWidth(text);

		fontUnit.begin(core.HUD());

		fontUnit.drawText(text, hudBounds.right() - 10 - textWidth, hudBounds.top() + 10 + yPos, 1f, 1f);
		yPos += lineHeight*2;

		final var mappedValuesAvailable = mActiveGamepad.isGamepadMappingAvailable();
		final var showMappedValues = mappedValuesAvailable && true;

		if (showMappedValues) {
			final var typeText = "Mapped Values";
			final var typeTextWidth = fontUnit.getStringWidth(typeText);

			fontUnit.drawText(typeText, hudBounds.right() - 10 - typeTextWidth, hudBounds.top() + 10 + yPos, 1f, 1f);
			final var numButtons = mActiveGamepad.numMappedButtons();
			for (int i = 0; i < numButtons; i++) {

				final var buttonState = mActiveGamepad.getButtonStateMapped(i, GLFW.GLFW_PRESS);

				final var buttonName = InputHelper.getGlfwPrintableKeyForGamepadButtons(i);
				final var buttonText = buttonName + " : " + buttonState;
				final var buttonNameWidth = fontUnit.getStringWidth(buttonText);

				fontUnit.drawText(buttonText, hudBounds.right() - 10 - buttonNameWidth, hudBounds.top() + 10 + (yPos += lineHeight), 1f, 1f);
			}

			final var numAxis = mActiveGamepad.numAxisMapped();
			for (int i = 0; i < numAxis; i++) {

				final var axisState = mActiveGamepad.getAxisValueMapped(i);

				final var buttonName = InputHelper.getGlfwPrintableKeyForGamepadAxis(i);
				final var buttonText = String.format("%s : %.2f", buttonName, axisState);
				final var buttonNameWidth = fontUnit.getStringWidth(buttonText);

				fontUnit.drawText(buttonText, hudBounds.right() - 10 - buttonNameWidth, hudBounds.top() + 10 + (yPos += lineHeight), 1f, 1f);
			}
		} else {
			final var typeText = "Raw Values";
			final var typeTextWidth = fontUnit.getStringWidth(typeText);

			fontUnit.drawText(typeText, hudBounds.right() - 10 - typeTextWidth, hudBounds.top() + 10 + yPos, 1f, 1f);

			final var numButtons = mActiveGamepad.numButtonsRaw();
			for (int i = 0; i < numButtons; i++) {

				final var buttonState = mActiveGamepad.getButtonStateRaw(i, GLFW.GLFW_PRESS);

				final var buttonName = InputHelper.getGlfwPrintableKeyForGamepadButtons(i);
				final var buttonText = buttonName + " : " + buttonState;
				final var buttonNameWidth = fontUnit.getStringWidth(buttonText);

				fontUnit.drawText(buttonText, hudBounds.right() - 10 - buttonNameWidth, hudBounds.top() + 10 + (yPos += lineHeight), 1f, 1f);
			}

			final var numAxis = mActiveGamepad.numAxisRaw();
			for (int i = 0; i < numAxis; i++) {

				final var axisState = mActiveGamepad.getAxisValueRaw(i);

				final var buttonName = InputHelper.getGlfwPrintableKeyForGamepadAxis(i);
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

	private InputGamepad GetActiveController(InputManager inputManager) {
		final var gamepadManager = inputManager.gamepads();

		final var activeGamepads = gamepadManager.getActiveGamepads();
		if (activeGamepads != null && activeGamepads.size() > 0)
			return activeGamepads.get(0);

		return null;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onGamepadConnected(InputGamepad gamepad) {
		if (mActiveGamepad == null) {
			mActiveGamepad = gamepad;
		}

	}

	@Override
	public void onGamepadDisconnected(InputGamepad gamepad) {
		if (mActiveGamepad == gamepad) {
			mActiveGamepad = null;
		}

	}
}