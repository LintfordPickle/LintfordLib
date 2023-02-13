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

	private boolean mLeftColumnSelected;

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

		mLeftColumnSelected = true;
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

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP)) {
			onNavigationUp(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN)) {
			onNavigationDown(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_LEFT) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT)) {
			onNavigationLeft(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_RIGHT) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)) {
			onNavigationRight(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_A)) {

			final var lSelectedLayouts = mLeftColumnSelected ? mLayouts : mRightLayouts;
			final var lEntry = getSelectedEntry(lSelectedLayouts);
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

	// --------------------------------------
	// Overriden-Methods
	// --------------------------------------

	protected void onNavigationUp(LintfordCore core) {
		if (mActiveEntry != null)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		final var lSelectedLayout = mLeftColumnSelected ? mLayouts : mRightLayouts;
		mSelectedEntryIndex = getPreviousEnabledEntry(lSelectedLayout, mSelectedEntryIndex);

		updateAllEntriesToMatchSelected(mLayouts, mLeftColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, !mLeftColumnSelected);

		mScreenManager.toolTip().toolTipProvider(null);

		// TODO: play sound for menu entry changed
	}

	protected void onNavigationDown(LintfordCore core) {
		if (mActiveEntry != null)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		final var lSelectedLayout = mLeftColumnSelected ? mLayouts : mRightLayouts;
		mSelectedEntryIndex = getNextEnabledEntry(lSelectedLayout, mSelectedEntryIndex);

		updateAllEntriesToMatchSelected(mLayouts, mLeftColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, !mLeftColumnSelected);

		mScreenManager.toolTip().toolTipProvider(null);

		// TODO: play sound for menu entry changed
	}

	@Override
	protected void onNavigationLeft(LintfordCore core) {
		if (mActiveEntry != null)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		mLeftColumnSelected = !mLeftColumnSelected;
		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;

		updateAllEntriesToMatchSelected(mLayouts, mLeftColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, !mLeftColumnSelected);
	}

	@Override
	protected void onNavigationRight(LintfordCore core) {
		if (mActiveEntry != null)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		mLeftColumnSelected = !mLeftColumnSelected;
		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;

		updateAllEntriesToMatchSelected(mLayouts, mLeftColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, !mLeftColumnSelected);
	}

}
