package net.lintfordlib.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputType;
import net.lintfordlib.renderers.SimpleRendererManager;
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

	protected DualMenuScreen(ScreenManager screenManager, String menuTitle) {
		this(screenManager, menuTitle, null);
	}

	protected DualMenuScreen(ScreenManager screenManager, String menuTitle, SimpleRendererManager rendererManager) {
		super(screenManager, menuTitle, rendererManager);

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
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		final int lLayoutCount = mRightLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mRightLayouts.get(i).loadResources(resourceManager);
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
				if (mScreenState == ScreenState.ACTIVE) {
					onEscPressed();
					return;
				}
			}
		}

		if (mLayouts.isEmpty() && mRightLayouts.isEmpty())
			return; // nothing to do

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this)) {
			onNavigationUp(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, this)) {
			onNavigationUp(core, InputType.Gamepad);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this)) {
			onNavigationDown(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, this)) {
			onNavigationDown(core, InputType.Gamepad);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_LEFT, this)) {
			onNavigationLeft(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, this)) {
			onNavigationLeft(core, InputType.Gamepad);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_RIGHT, this)) {
			onNavigationRight(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, this)) {
			onNavigationRight(core, InputType.Gamepad);
		}

		// This might become a problem if we have entries which capture the input in a dual screen. Should call out to onNavigationBack/Confirm

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_A, this)) {

			final var lSelectedLayouts = mRightColumnSelected ? mRightLayouts : mLayouts;
			final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
			final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;
			final var lEntry = getSelectedEntry(lSelectedLayouts, lSelectedLayoutIndex, lSelectedEntryIndex);

			if (lEntry != null && !lEntry.readOnly() && lEntry.enabled())
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
	public void draw(LintfordCore core) {
		if (mScreenState != ScreenState.ACTIVE && mScreenState != ScreenState.TRANSITION_STARTING && mScreenState != ScreenState.TRANSITION_SLEEPING)
			return;

		mRendererManager.draw(core);

		final float lMenuScreenZDepth = ZLayers.LAYER_SCREENMANAGER;

		drawMenuTitle(core);

		// Draw each layout in turn.
		final var lLeftLayoutCount = mLayouts.size();
		for (int i = 0; i < lLeftLayoutCount; i++) {
			mLayouts.get(i).draw(core, lMenuScreenZDepth + (i * 0.001f));
		}

		final var lRightLayoutCount = mRightLayouts.size();
		for (int i = 0; i < lRightLayoutCount; i++) {
			mRightLayouts.get(i).draw(core, lMenuScreenZDepth + (i * 0.001f));
		}
	}

	// --------------------------------------
	// Overriden-Methods
	// --------------------------------------

	@Override
	protected void scrollItemIntoLayoutView() {

		if (!mRightColumnSelected) {

			// left column scroll

			final var selectedLayout = mLayouts.get(mSelectedLayoutIndex);
			selectedLayout.scrollContentItemIntoView(mSelectedEntryIndex);

		} else {

			// right column scroll

			final var selectedLayout = mRightLayouts.get(mRightColumnSelectedLayoutIndex);
			selectedLayout.scrollContentItemIntoView(mRightColumnSelectedEntryIndex);
		}

	}

	@Override
	protected void onNavigationUp(LintfordCore core, InputType inputType) {
		if (isEntryActive())
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		// in the dual menu screen, up/down should only toggle layers I guess?
		getPreviousEnabledEntry();

		scrollItemIntoLayoutView();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, mRightColumnSelectedLayoutIndex, mRightColumnSelectedEntryIndex, mRightColumnSelected);

		screenManager.toolTip().toolTipProvider(null);

		screenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_NAVIGATION_UP);
	}

	@Override
	protected void onNavigationDown(LintfordCore core, InputType inputType) {
		if (isEntryActive())
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		// in the dual menu screen, up/down should only toggle layers I guess?
		getNextEnabledEntry();

		scrollItemIntoLayoutView();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, mRightColumnSelectedLayoutIndex, mRightColumnSelectedEntryIndex, mRightColumnSelected);

		screenManager.toolTip().toolTipProvider(null);

		screenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_NAVIGATION_DOWN);
	}

	@Override
	protected void onNavigationLeft(LintfordCore core, InputType inputType) {
		if (isEntryActive())
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		// first give the entries a chance to react to the nav right
		if (!mRightColumnSelected) {
			final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
			final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;
			final var selectedEntry = mLayouts.get(lSelectedLayoutIndex).entries().get(lSelectedEntryIndex);

			// if the current menu entry utilizes left/right navigation (e.g. the slider), then repress the navLeft/navRight feature.
			if (selectedEntry.onNavigationLeft(core))
				return;

		} else {
			final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
			final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;
			final var selectedEntry = mRightLayouts.get(lSelectedLayoutIndex).entries().get(lSelectedEntryIndex);

			// if the current menu entry utilizes left/right navigation (e.g. the slider), then repress the navLeft/navRight feature.
			if (selectedEntry.onNavigationLeft(core))
				return;

			final var setFirstEntryFocus = false;
			toggleColumns(false, setFirstEntryFocus);

		}

		scrollItemIntoLayoutView();

		final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
		final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

		updateAllEntriesToMatchSelected(mLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, mRightColumnSelected);
	}

	@Override
	protected void onNavigationRight(LintfordCore core, InputType inputType) {
		if (isEntryActive())
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);

		// first give the entries a chance to react to the nav right
		if (!mRightColumnSelected) {
			final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
			final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;
			final var selectedEntry = mLayouts.get(lSelectedLayoutIndex).entries().get(lSelectedEntryIndex);

			// if the current menu entry utilizes left/right navigation (e.g. the slider), then repress the navLeft/navRight feature.
			if (selectedEntry.onNavigationRight(core))
				return;

			final var setFirstEntryFocus = false;
			toggleColumns(true, setFirstEntryFocus);

		} else {
			final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
			final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

			var selectedLayout = mRightLayouts.get(lSelectedLayoutIndex);
			var selectedLayoutEntries = selectedLayout.entries();

			final var selectedEntry = selectedLayoutEntries.get(lSelectedEntryIndex);

			// if the current menu entry utilizes left/right navigation (e.g. the slider), then repress the navLeft/navRight feature.
			if (selectedEntry.onNavigationRight(core))
				return;

			// Cannot go right from the right hand column

		}

		scrollItemIntoLayoutView();

		final var lSelectedLayoutIndex = mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex;
		final var lSelectedEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

		updateAllEntriesToMatchSelected(mLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, !mRightColumnSelected);
		updateAllEntriesToMatchSelected(mRightLayouts, lSelectedLayoutIndex, lSelectedEntryIndex, mRightColumnSelected);
	}

	private boolean toggleColumns(boolean rightColumn, boolean setFirstEntityFocus) {
		final var columnLayout = rightColumn ? mRightLayouts : mLayouts;
		if (columnLayout == null || columnLayout.size() == 0)
			return false;

		final var numLayouts = columnLayout.size();
		for (int i = 0; i < numLayouts; i++) {
			final var layout = columnLayout.get(i);

			if (!layout.canHaveFocus())
				continue;

			final var numEntries = layout.entries().size();
			for (int j = 0; j < numEntries; j++) {
				final var entry = layout.entries().get(j);

				if (entry == null || !entry.enabled() || entry == MenuEntry.menuSeparator())
					continue;

				if (rightColumn) {
					mRightColumnSelectedLayoutIndex = i;

					// switching between columns sometimes causes the selected entry index to be out of bounds (if the layout index was changed)
					if (mRightColumnSelectedEntryIndex > layout.entries().size() - 1)
						mRightColumnSelectedEntryIndex = layout.entries().size() - 1;

					if (setFirstEntityFocus)
						mRightColumnSelectedEntryIndex = j;

					mRightColumnSelected = true;

				} else {
					mSelectedLayoutIndex = i;

					// switching between columns sometimes causes the selected entry index to be out of bounds (if the layout index was changed)
					if (mSelectedEntryIndex > layout.entries().size() - 1)
						mSelectedEntryIndex = layout.entries().size() - 1;

					if (setFirstEntityFocus)
						mSelectedEntryIndex = j;

					mRightColumnSelected = false;
				}

				return true;
			}
		}

		return false;
	}

	// --------------------------------------

	@Override
	protected void getNextEnabledEntry() {
		if (isEntryActive())
			return;

		var selectedLayouts = mRightColumnSelected ? mRightLayouts : mLayouts;
		var checkEntryIndex = mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex;

		final var maxTries = 6;
		var currentTry = 0;

		while (currentTry < maxTries) {
			checkEntryIndex++;
			final var lLayout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);

			if (checkEntryIndex >= lLayout.entries().size()) {

				// we have extended passed number of entries in this layout

				getNextEnabledLayout();
				checkEntryIndex = 0;

				final var nextLayout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);
				final var fFoundEntry = nextLayout.entries().get(checkEntryIndex);
				if (!fFoundEntry.enabled() || !fFoundEntry.canHaveFocus())
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
				if (!lFoundEntry.enabled() || !lFoundEntry.canHaveFocus()) {
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

		final var maxTries = 6;
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
					final var layout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);

					if (layout.entries().size() == 0) {
						if (mRightColumnSelected) {
							mRightColumnSelectedEntryIndex = 0;
						} else {
							mSelectedEntryIndex = 0;
						}
						return;
					}

					checkEntryIndex = layout.entries().size() - 1;

					if (checkEntryIndex == (mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex))
						return;

					if (!layout.hasEntry(checkEntryIndex)) {

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
					final var layout = selectedLayouts.get(mRightColumnSelected ? mRightColumnSelectedLayoutIndex : mSelectedLayoutIndex);
					checkEntryIndex = layout.entries().size() - 1;

					if (layout.entries().size() == 0) {
						if (mRightColumnSelected) {
							mRightColumnSelectedEntryIndex = 0;
						} else {
							mSelectedEntryIndex = 0;
						}
						return;
					}

					if (checkEntryIndex == (mRightColumnSelected ? mRightColumnSelectedEntryIndex : mSelectedEntryIndex))
						return;

					final var foundEntry = layout.entries().get(checkEntryIndex);
					if (!foundEntry.enabled() || !foundEntry.canHaveFocus())
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

				final var foundEntry = lLayout.entries().get(checkEntryIndex);
				if (!foundEntry.enabled() || !foundEntry.canHaveFocus()) {
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
			mActiveEntry.onDeactivation(screenManager.core().input());

		screenManager.contextHintManager().contextHintProvider(null);
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

					screenManager.contextHintManager().contextHintProvider(lEntry);

					mRightColumnSelected = false;
					mSelectedLayoutIndex = i;
					mSelectedEntryIndex = j;

				} else {
					lEntry.mHasFocus = false;
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

					screenManager.contextHintManager().contextHintProvider(lEntry);

					mRightColumnSelected = true;
					mRightColumnSelectedEntryIndex = i;
					mRightColumnSelectedEntryIndex = j;

				} else {
					lEntry.mHasFocus = false;
				}
			}
		}
	}
}
