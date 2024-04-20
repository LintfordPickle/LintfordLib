package net.lintfordlib.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.layouts.BaseLayout;

public abstract class DualMenuScreen extends MenuScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<BaseLayout> mRightLayouts;

	private boolean mRightColumnSelected;
	private int mRightColumnSelectedLayoutIndex;
	private int mRightColumnSelectedEntryIndex;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DualMenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager, pMenuTitle);

		mRightLayouts = new ArrayList<>();

		mScreenPaddingLeft = 0.f;
		mScreenPaddingRight = 0.f;
		mScreenPaddingTop = 0.f;
		mScreenPaddingBottom = 0.f;

		mRightColumnSelected = false;
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
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_B, this)) {
				if (mScreenState == ScreenState.Active) {
					exitScreen();
					return;
				}
			}
		}

		if (mLayouts.size() == 0 && mRightLayouts.size() == 0)
			return; // nothing to do

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, this)) {
			onNavigationUp(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, this)) {
			onNavigationDown(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_LEFT, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, this)) {
			onNavigationLeft(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_RIGHT, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, this)) {
			onNavigationRight(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_A, this)) {

			final var lSelectedLayouts = mRightColumnSelected ? mRightLayouts : mLayouts;
			final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
			final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;
			final var lEntry = getSelectedEntry(lSelectedLayouts, lSelectedLayoutIndex, lSelectedEntryIndex);

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
	// Overriden-Methods
	// --------------------------------------

	protected void onNavigationUp(LintfordCore core) {
		if (isEntryActive())
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		getPreviousEnabledEntry();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, mRightColumnSelectedLayoutIndex, mRightColumnSelectedEntryIndex, mRightColumnSelected);

		mScreenManager.toolTip().toolTipProvider(null);

		mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_NAVIGATION_UP);
	}

	protected void onNavigationDown(LintfordCore core) {
		if (isEntryActive())
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		getNextEnabledEntry();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, mRightColumnSelectedLayoutIndex, mRightColumnSelectedEntryIndex, mRightColumnSelected);

		mScreenManager.toolTip().toolTipProvider(null);

		mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_NAVIGATION_DOWN);
	}

	@Override
	protected void onNavigationLeft(LintfordCore core) {
		if (isEntryActive())
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		mRightColumnSelected = !mRightColumnSelected;
		final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
		final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

		updateAllEntriesToMatchSelected(mLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, mRightColumnSelected);
	}

	@Override
	protected void onNavigationRight(LintfordCore core) {
		if (isEntryActive())
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		mRightColumnSelected = !mRightColumnSelected;
		final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
		final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

		updateAllEntriesToMatchSelected(mLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, mRightColumnSelected);
	}

	// --------------------------------------

	@Override
	protected void getNextEnabledEntry() {
		if (isEntryActive())
			return;

		var selectedLayouts = mRightColumnSelected ? mRightLayouts : mLayouts;
		int checkEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

		final var maxTries = 5;
		var currentTry = 0;

		while (currentTry < maxTries) {
			checkEntryIndex++;
			final var lLayout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);

			if (checkEntryIndex >= lLayout.entries().size()) {
				getNextEnabledLayout();
				checkEntryIndex = 0;

				final var lNextLayout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);
				if (lNextLayout.entries().get(checkEntryIndex).enabled() == false)
					continue;

				if (mRightColumnSelected) {
					mRightColumnSelectedEntryIndex = 0;
				} else {
					mSelectedEntryIndex = 0;
				}

				return;

			} else {
				if (checkEntryIndex == (mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex)) {
					return;
				}

				final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
				if (lFoundEntry.enabled() == false) {
					currentTry++;
					continue;
				}

				if (checkEntryIndex == (mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex))
					return;

				if (mRightColumnSelected) {
					mRightColumnSelectedEntryIndex = checkEntryIndex;
				} else {
					mSelectedEntryIndex = checkEntryIndex;
				}

				return;
			}
		}

		return;
	}

	@Override
	protected void getPreviousEnabledEntry() {
		if (isEntryActive())
			return;

		final var maxTries = 5;
		var currentTry = 0;

		var selectedLayouts = mRightColumnSelected ? mRightLayouts : mLayouts;
		var checkEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

		if (mRightColumnSelected) {
			if (mRightColumnSelectedLayoutIndex >= mRightLayouts.size()) {
				mRightColumnSelectedLayoutIndex = 0;
			}
		} else {
			if (mSelectedLayoutIndex >= mLayouts.size()) {
				mSelectedLayoutIndex = 0;
			}
		}

		while (currentTry < maxTries) {
			checkEntryIndex--;

			if (checkEntryIndex < 0) {
				if (selectedLayouts.size() > 1) {
					getPreviousEnabledLayout();

					// whatever layout is now active, go with it
					final var lLayout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);

					if (lLayout.entries().size() == 0) {
						if (mRightColumnSelected) {
							mRightColumnSelectedEntryIndex = 0;
						} else {
							mSelectedEntryIndex = 0;
						}
						return;
					}

					checkEntryIndex = lLayout.entries().size() - 1;

					if (checkEntryIndex == (mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex))
						return;

					if (!lLayout.hasEntry(checkEntryIndex)) {

						if (mRightColumnSelected) {
							mRightColumnSelectedEntryIndex = 0;
						} else {
							mSelectedEntryIndex = 0;
						}
						return;
					}

					if (mRightColumnSelected) {
						mRightColumnSelectedEntryIndex = checkEntryIndex;
					} else {
						mSelectedEntryIndex = checkEntryIndex;
					}
					return;

				} else {
					final var lLayout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);
					checkEntryIndex = lLayout.entries().size() - 1;

					if (lLayout.entries().size() == 0) {
						if (mRightColumnSelected) {
							mRightColumnSelectedEntryIndex = 0;
						} else {
							mSelectedEntryIndex = 0;
						}
						return;
					}

					if (checkEntryIndex == (mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex))
						return;

					final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
					if (lFoundEntry.enabled() == false)
						continue;

					if (mRightColumnSelected) {
						mRightColumnSelectedEntryIndex = checkEntryIndex;
					} else {
						mSelectedEntryIndex = checkEntryIndex;
					}

					return;
				}
			} else {
				final var lLayout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);

				if (checkEntryIndex == (mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex)) {
					if (mRightColumnSelected) {
						mRightColumnSelectedEntryIndex = checkEntryIndex;
					} else {
						mSelectedEntryIndex = checkEntryIndex;
					}
					return;
				}

				final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
				if (lFoundEntry.enabled() == false) {
					currentTry++;
					continue;
				}

				if (mRightColumnSelected) {
					mRightColumnSelectedEntryIndex = checkEntryIndex;
				} else {
					mSelectedEntryIndex = checkEntryIndex;
				}
				return;
			}
		}

		return;
	}

	@Override
	protected void getNextEnabledLayout() {
		if (isEntryActive())
			return;

		var selectedLayout = mRightColumnSelected ? mRightLayouts : mLayouts;
		var selectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;

		if (selectedLayout.size() == 1) {
			if (mRightColumnSelected) {
				mRightColumnSelectedEntryIndex = 0;
			} else {
				mSelectedLayoutIndex = 0;
			}
			return;
		}

		boolean found = false;
		while (found == false) {
			selectedLayoutIndex++;

			if (selectedLayoutIndex == (mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex)) {
				return;
			}

			if (selectedLayoutIndex >= selectedLayout.size()) {
				selectedLayoutIndex = 0;

				final var lLayout = selectedLayout.get(selectedLayoutIndex);
				if (lLayout.entries().size() == 0) {
					selectedLayoutIndex++;
					continue;
				}

				if (mRightColumnSelected) {
					mRightColumnSelectedLayoutIndex = selectedLayoutIndex;
				} else {
					mSelectedLayoutIndex = selectedLayoutIndex;
				}
				return;
			}

			final var lLayout = selectedLayout.get(selectedLayoutIndex);
			if (lLayout.entries().size() == 0) {
				selectedLayoutIndex++;
				continue;
			}

			if (mRightColumnSelected) {
				mRightColumnSelectedLayoutIndex = selectedLayoutIndex;
			} else {
				mSelectedLayoutIndex = selectedLayoutIndex;
			}
			return;
		}

		return;
	}

	@Override
	protected void getPreviousEnabledLayout() {
		if (isEntryActive())
			return;

		var selectedLayout = mRightColumnSelected ? mRightLayouts : mLayouts;
		int selectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;

		if (selectedLayout.size() == 1) {
			if (mRightColumnSelected) {
				mRightColumnSelectedEntryIndex = 0;
			} else {
				mSelectedLayoutIndex = 0;
			}
			return;
		}

		boolean found = false;
		while (found == false) {
			selectedLayoutIndex--;

			if (selectedLayoutIndex == (mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex))
				return;

			if (selectedLayoutIndex < 0) {
				selectedLayoutIndex = selectedLayout.size() - 1;

				final var lLayout = selectedLayout.get(selectedLayoutIndex);
				if (lLayout.entries().size() == 0) {
					selectedLayoutIndex--;
					continue;
				}

				if (mRightColumnSelected) {
					mRightColumnSelectedLayoutIndex = selectedLayoutIndex;
				} else {
					mSelectedLayoutIndex = selectedLayoutIndex;
				}
				return;
			}

			final var lLayout = selectedLayout.get(selectedLayoutIndex);
			if (lLayout.entries().size() == 0) {
				selectedLayoutIndex--;
				continue;
			}

			if (mRightColumnSelected) {
				mRightColumnSelectedLayoutIndex = selectedLayoutIndex;
			} else {
				mSelectedLayoutIndex = selectedLayoutIndex;
			}
			return;

		}

		return;
	}

	// --------------------------------------

	public void setFocusOnEntry(MenuEntry entry) {
		if (mActiveEntry != null)
			mActiveEntry.onDeselection(mScreenManager.core().input());

		mScreenManager.contextHintManager().contextHintProvider(null);
		mActiveEntry = null;

		// left

		final int lNumLayouts = mLayouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lLayout = mLayouts.get(i);
			final int lNumEntries = lLayout.entries().size();
			for (int j = 0; j < lNumEntries; j++) {
				final var lEntry = lLayout.entries().get(j);
				final var IsDesiredEntry = lEntry == entry;

				if (IsDesiredEntry) {
					lEntry.mHasFocus = true;

					mScreenManager.contextHintManager().contextHintProvider(lEntry);

					mRightColumnSelected = false;
					mSelectedLayoutIndex = i;
					mSelectedEntryIndex = j;

				} else {
					lEntry.mHasFocus = false;
					lEntry.mIsActive = false;
				}
			}
		}

		// right
		final int lNumRightLayouts = mRightLayouts.size();
		for (int i = 0; i < lNumRightLayouts; i++) {
			final var lLayout = mRightLayouts.get(i);
			final int lNumEntries = lLayout.entries().size();
			for (int j = 0; j < lNumEntries; j++) {
				final var lEntry = lLayout.entries().get(j);
				final var IsDesiredEntry = lEntry == entry;

				if (IsDesiredEntry) {
					lEntry.mHasFocus = true;

					mScreenManager.contextHintManager().contextHintProvider(lEntry);

					mRightColumnSelected = true;
					mRightColumnSelectedEntryIndex = i;
					mRightColumnSelectedEntryIndex = j;

				} else {
					lEntry.mHasFocus = false;
					lEntry.mIsActive = false;
				}
			}
		}
	}

}
