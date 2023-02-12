package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class DualMenuScreen extends MenuScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<BaseLayout> mRightLayouts;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DualMenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager, pMenuTitle);

		mRightLayouts = new ArrayList<>();

		mPaddingLeftNormalized = 0.f;
		mPaddingRightNormalized = 0.f;
		mPaddingTopNormalized = 0.f;
		mPaddingBottomNormalized = 0.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final int lCount = mRightLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mRightLayouts.get(i).initialize();
		}

	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		final int lLayoutCount = mRightLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mRightLayouts.get(i).loadResources(pResourceManager);
		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		final int lCount = mRightLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mRightLayouts.get(i).unloadResources();
		}
	}

	@Override
	public void handleInput(LintfordCore core) {
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		if (mESCBackEnabled) {
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_B)) {
				if (mScreenState == ScreenState.Active) {
					exitScreen();
					return;
				}
			}
		}

		if (mLayouts.size() == 0 && mRightLayouts.size() == 0)
			return; // nothing to do

		// Respond to UP key
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP)) {
			core.input().mouse().isMouseMenuSelectionEnabled(false);
			mSelectedEntryIndex = getPreviousEnabledEntry(mSelectedEntryIndex);

			updateAllEntriesToMatchSelected();
			
			mScreenManager.toolTip().toolTipProvider(null);

			// TODO: play sound for menu entry changed

		}

		// Respond to DOWN key
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN)) {
			core.input().mouse().isMouseMenuSelectionEnabled(false);
			mSelectedEntryIndex = getNextEnabledEntry(mSelectedEntryIndex);

			updateAllEntriesToMatchSelected();
			
			mScreenManager.toolTip().toolTipProvider(null);

			// TODO: play sound for menu entry changed

		}

		// Process ENTER key press
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_A)) {
			final var lEntry = getSelectedEntry();
			lEntry.onClick(core.input());
		}

		final var lLeftLayoutCount = mLayouts.size();
		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lLayout = mLayouts.get(i);
			lLayout.handleInput(core);
		}

		final var lRightLayoutCount = mRightLayouts.size();
		for (int i = 0; i < lRightLayoutCount; i++) {
			final var lLayout = mRightLayouts.get(i);
			lLayout.handleInput(core);
		}

		return;
	}

	@Override
	public void updateLayoutSize(LintfordCore core) {
		final int lLeftCount = mLayouts.size();
		for (int i = 0; i < lLeftCount; i++) {
			mLayouts.get(i).layoutWidth(LAYOUT_WIDTH.HALF);
		}

		updateLayout(core, mLayouts, LAYOUT_ALIGNMENT.LEFT);

		final int lRightCount = mRightLayouts.size();
		for (int i = 0; i < lRightCount; i++) {
			mRightLayouts.get(i).layoutWidth(LAYOUT_WIDTH.HALF);
		}

		updateLayout(core, mRightLayouts, LAYOUT_ALIGNMENT.RIGHT);
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		// TODO: If the right column mode is currently activate, we need to disable the left column follow-the-entry functionality (in MenuScreen)

		final var lNumLayouts = mRightLayouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lLayout = mRightLayouts.get(i);
			if (mSelectedLayoutIndex == i) {
				lLayout.focusedEntryIndex(mSelectedEntryIndex);
			} else {
				lLayout.focusedEntryIndex(-1);
			}

			lLayout.update(core);
		}
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		mRendererManager.draw(pCore);

		final float lMenuScreenZDepth = ZLayers.LAYER_SCREENMANAGER;

		drawMenuTitle(pCore);

		// Draw each layout in turn.
		final var lLeftLayoutCount = mLayouts.size();
		for (int i = 0; i < lLeftLayoutCount; i++) {
			mLayouts.get(i).draw(pCore, lMenuScreenZDepth + (i * 0.001f));
		}

		final var lRightLayoutCount = mRightLayouts.size();
		for (int i = 0; i < lRightLayoutCount; i++) {
			mRightLayouts.get(i).draw(pCore, lMenuScreenZDepth + (i * 0.001f));
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		// TODO Auto-generated method stub

	}

}
