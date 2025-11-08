package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.input.InputType;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.MenuBindingKeyEntry;
import net.lintfordlib.screenmanager.entries.MenuLabelHeadersEntry;
import net.lintfordlib.screenmanager.layouts.BaseLayout;
import net.lintfordlib.screenmanager.layouts.HorizontalLayout;
import net.lintfordlib.screenmanager.layouts.ListLayout;

public class KeyBindOptionsScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_CONFIRM = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MenuEntry mBackButton;
	private ListLayout mKeyBindListLayout;

	private boolean mKeyAreaSelected;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public KeyBindOptionsScreen(ScreenManager screenManager) {
		super(screenManager, "KEY BINDINGS");

		mScreenPaddingTop = 0;

		mKeyBindListLayout = new ListLayout(this);
		mKeyBindListLayout.cropPaddingTop(9.f);
		mKeyBindListLayout.cropPaddingBottom(13.f);
		mKeyBindListLayout.setDrawBackground(true, ColorConstants.MenuPanelSecondaryColor);
		mKeyBindListLayout.layoutFillType(FILLTYPE.FILL_CONTAINER);
		mKeyBindListLayout.layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
		mKeyBindListLayout.marginLeft(100);
		mKeyBindListLayout.marginRight(100);

		createKeyBindSection(mKeyBindListLayout);

		final var footerList = new HorizontalLayout(this);
		footerList.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		mBackButton = new MenuEntry(screenManager, this, "Go back");
		mBackButton.registerClickListener(this, BUTTON_CONFIRM);
		mBackButton.setGamepadIcon(ALIGNMENT.LEFT, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);

		footerList.addMenuEntry(mBackButton);

		addLayout(mKeyBindListLayout);
		addLayout(footerList);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private void createKeyBindSection(BaseLayout layout) {
		final var keyBindOptionsTitle = new MenuLabelHeadersEntry(screenManager, this);

		keyBindOptionsTitle.addHeader("Game Actions");
		keyBindOptionsTitle.addHeader("Keyboard");
		keyBindOptionsTitle.addHeader("Gamepad");
		keyBindOptionsTitle.drawButtonBackground(true);
		keyBindOptionsTitle.horizontalAlignment(ALIGNMENT.LEFT);
		keyBindOptionsTitle.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		layout.addMenuEntry(keyBindOptionsTitle);

		final var inputManager = screenManager.core().input();
		final var keyEventActionManager = inputManager.eventActionManager();

		final var bindableGameInputActions = keyEventActionManager.bindableGameInputActions();
		if (bindableGameInputActions != null) {
			final var binableInputActionList = bindableGameInputActions.bindableEventActions();
			final var numBindableInputActions = binableInputActionList.size();
			for (int i = 0; i < numBindableInputActions; i++) {
				final var bindableInput = binableInputActionList.get(i);

				final var registeredEventAction = keyEventActionManager.getGameInputActionByUid(bindableInput.eventActionUid);
				if (registeredEventAction == null)
					continue;

				final var newActionEntry = new MenuBindingKeyEntry(screenManager, this, registeredEventAction);
				newActionEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
				newActionEntry.label(bindableInput.eventActionName);
				newActionEntry.registerClickListener(this, bindableInput.eventActionUid);

				layout.addMenuEntry(newActionEntry);
			}
		}

		// Menu Binds
		final var menuKeyBindOptionsTitle = new MenuLabelHeadersEntry(screenManager, this);

		menuKeyBindOptionsTitle.addHeader("Menu Actions");
		menuKeyBindOptionsTitle.addHeader("Keyboard");
		menuKeyBindOptionsTitle.addHeader("Gamepad");
		menuKeyBindOptionsTitle.drawButtonBackground(true);
		menuKeyBindOptionsTitle.horizontalAlignment(ALIGNMENT.LEFT);
		menuKeyBindOptionsTitle.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		final var newMenuSeparator = MenuEntry.newMenuSeparator();
		newMenuSeparator.desiredHeight(20);
		layout.addMenuEntry(newMenuSeparator);
		layout.addMenuEntry(menuKeyBindOptionsTitle);

		final var bindableMenuInputActions = keyEventActionManager.bindableMenuInputActions();
		if (bindableMenuInputActions != null) {
			final var binableInputActionList = bindableMenuInputActions.bindableEventActions();
			final var numBindableInputActions = binableInputActionList.size();
			for (int i = 0; i < numBindableInputActions; i++) {
				final var bindableInput = binableInputActionList.get(i);

				final var registeredEventAction = keyEventActionManager.getGameInputActionByUid(bindableInput.eventActionUid);
				if (registeredEventAction == null)
					continue;

				final var newActionEntry = new MenuBindingKeyEntry(screenManager, this, registeredEventAction);
				newActionEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
				newActionEntry.label(bindableInput.eventActionName);
				newActionEntry.registerClickListener(this, bindableInput.eventActionUid);

				layout.addMenuEntry(newActionEntry);
			}
		}

		layout.addMenuEntry(newMenuSeparator);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void exitScreen() {
		// TODO: Maybe cancel and waiting keybaord/gamepad input events?

		screenManager.core().input().eventActionManager().saveConfig();

		super.exitScreen();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {

		case BUTTON_CONFIRM:
			exitScreen();
			break;
		}
	}

	@Override
	protected void onNavigationUp(LintfordCore core, InputType inputType) {

		final var selectedEntry = getSelectedEntry(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex);
		if (selectedEntry instanceof MenuBindingKeyEntry entry) {
			mKeyAreaSelected = entry.keyAreaSelected();
		}

		super.onNavigationUp(core, inputType);

		final var newSelectedEntry = getSelectedEntry(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex);
		if (newSelectedEntry instanceof MenuBindingKeyEntry entry) {
			entry.keyAreaSelected(mKeyAreaSelected);
		}
	}

	@Override
	protected void onNavigationDown(LintfordCore core, InputType inputType) {
		final var selectedEntry = getSelectedEntry(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex);
		if (selectedEntry instanceof MenuBindingKeyEntry entry) {
			mKeyAreaSelected = entry.keyAreaSelected();
		}

		super.onNavigationDown(core, inputType);

		final var newSelectedEntry = getSelectedEntry(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex);
		if (newSelectedEntry instanceof MenuBindingKeyEntry entry) {
			entry.keyAreaSelected(mKeyAreaSelected);
		}
	}

}