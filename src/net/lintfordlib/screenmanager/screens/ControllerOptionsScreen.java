package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.gamepad.Gamepad;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;
import net.lintfordlib.core.input.gamepad.GamepadManager;
import net.lintfordlib.core.input.gamepad.IGamepadListener;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.HorizontalEntryGroup;
import net.lintfordlib.screenmanager.entries.MenuEnumEntryIndexed;
import net.lintfordlib.screenmanager.entries.MenuToggleEntry;
import net.lintfordlib.screenmanager.entries.input.IBindingCallback;
import net.lintfordlib.screenmanager.entries.input.MenuControllerImageEntry;
import net.lintfordlib.screenmanager.entries.input.MenuGamepadInputMapEntry;
import net.lintfordlib.screenmanager.layouts.BaseLayout;
import net.lintfordlib.screenmanager.layouts.FloatingLayout;
import net.lintfordlib.screenmanager.layouts.HorizontalLayout;
import net.lintfordlib.screenmanager.layouts.ListLayout;

// Handles mapping of physical gamepad input to the Lintford gamepad Codes.
public class ControllerOptionsScreen extends MenuScreen implements IGamepadListener, IBindingCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_BACK = 10;
	public static final int BUTTON_SAVE = 11;

	public static final int BUTTON_AVAILABLE_CONTROLLERS = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MenuEntry mBackButton;
	private MenuEntry mSaveButton;
	private FloatingLayout mmInputMapFloatingLayout;

	private GamepadManager mGamepadManager;
	private MenuEnumEntryIndexed<Gamepad> mAvailableControllers;
	private MenuToggleEntry mSdlMappingAvailable;
	private MenuToggleEntry mUseCustomBindings;

	private Gamepad mActiveGamepad; // Tracks the gamepad we are currently mapping (using the enum entry).
	private MenuEntry mBindingEntry; // Used for tracking the binding process

	private MenuGamepadInputMapEntry buttonWestEntry;
	private MenuGamepadInputMapEntry buttonNorthEntry;
	private MenuGamepadInputMapEntry buttonSouthEntry;
	private MenuGamepadInputMapEntry buttonEastEntry;

	private MenuGamepadInputMapEntry duButtonEntry;
	private MenuGamepadInputMapEntry ddButtonEntry;
	private MenuGamepadInputMapEntry dlButtonEntry;
	private MenuGamepadInputMapEntry drButtonEntry;

	private MenuGamepadInputMapEntry selectButtonEntry;
	private MenuGamepadInputMapEntry startButtonEntry;

	private MenuGamepadInputMapEntry ltriggerButtonEntry;
	private MenuGamepadInputMapEntry rtriggerButtonEntry;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ControllerOptionsScreen(ScreenManager screenManager) {
		super(screenManager, "CONTROLLER SETUP");

		mScreenPaddingTop = 0;

		// Controller Selection
		mGamepadManager = screenManager.core().input().gamepads();
		mGamepadManager.addGamepadListener(this);

		final var controllerSelectionLayout = new ListLayout(this);
		controllerSelectionLayout.cropPaddingTop(9.f);
		controllerSelectionLayout.setDrawBackground(true, ColorConstants.MenuPanelSecondaryColor);
		controllerSelectionLayout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		controllerSelectionLayout.layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
		controllerSelectionLayout.marginBottom(10);

		mAvailableControllers = new MenuEnumEntryIndexed<Gamepad>(screenManager, this, "Controller");
		mAvailableControllers.setButtonsEnabled(true);
		mAvailableControllers.registerClickListener(this, BUTTON_AVAILABLE_CONTROLLERS);
		mAvailableControllers.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mSdlMappingAvailable = new MenuToggleEntry(screenManager, this, "SDL Mapping Available");
		mSdlMappingAvailable.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		mSdlMappingAvailable.readOnly(true);

		mUseCustomBindings = new MenuToggleEntry(screenManager, this, "Custom Bindings");
		mUseCustomBindings.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		controllerSelectionLayout.addMenuEntry(mAvailableControllers);
		controllerSelectionLayout.addMenuEntry(mSdlMappingAvailable);
		controllerSelectionLayout.addMenuEntry(mUseCustomBindings);

		// Mapping Section

		mmInputMapFloatingLayout = new FloatingLayout(this);
		mmInputMapFloatingLayout.cropPaddingTop(9.f);
		mmInputMapFloatingLayout.cropPaddingBottom(13.f);
		mmInputMapFloatingLayout.setDrawBackground(true, ColorConstants.RED());
		mmInputMapFloatingLayout.layoutFillType(FILLTYPE.FILL_CONTAINER);
		mmInputMapFloatingLayout.layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
		mmInputMapFloatingLayout.marginLeft(100);
		mmInputMapFloatingLayout.marginRight(100);

		createControllerSection(mmInputMapFloatingLayout);

		final var lFooterList = new HorizontalLayout(this);
		lFooterList.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		mBackButton = new MenuEntry(screenManager, this, "Back");
		mBackButton.registerClickListener(this, BUTTON_BACK);
		mBackButton.setGamepadIcon(ALIGNMENT.LEFT, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);

		mSaveButton = new MenuEntry(screenManager, this, "Save & Exit");
		mSaveButton.registerClickListener(this, BUTTON_SAVE);

		final var footerBar = new HorizontalEntryGroup(screenManager, this);
		footerBar.addEntry(mBackButton);
		footerBar.addEntry(mSaveButton);

		lFooterList.addMenuEntry(footerBar);

		addLayout(controllerSelectionLayout);
		addLayout(mmInputMapFloatingLayout);
		addLayout(lFooterList);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;
	}

	private void createControllerSection(BaseLayout layout) {

		final var desiredButtonWidth = 120;

		final var buttonOffsetX = 7;
		final var buttonOffsetY = 31;

		final var entryOffsetX = 0;
		final var entryOffsetY = 45;

		// Select / Start

		selectButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_BACK);
		selectButtonEntry.desiredWidth(desiredButtonWidth);
		selectButtonEntry.setSpriteEnabled(true);
		selectButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_SELECT, -1);
		selectButtonEntry.setPosition(entryOffsetX + -100, entryOffsetY + 100);
		selectButtonEntry.setSpritePosition(-20, 19, 16, 16);
		selectButtonEntry.contextHintState.buttonAHint = "bind";
		selectButtonEntry.setBindingCallback(this);

		startButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_GUIDE);
		startButtonEntry.desiredWidth(desiredButtonWidth);
		startButtonEntry.setSpriteEnabled(true);
		startButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_START, -1);
		startButtonEntry.setPosition(entryOffsetX + 20, entryOffsetY + 100);
		startButtonEntry.setSpritePosition(16, 19, 16, 16);
		startButtonEntry.contextHintState.buttonAHint = "bind";
		startButtonEntry.setBindingCallback(this);

		// Buttons

		buttonNorthEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH);
		buttonNorthEntry.desiredWidth(desiredButtonWidth);
		buttonNorthEntry.setSpriteEnabled(true);
		buttonNorthEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_YELLOW, -1);
		buttonNorthEntry.setPosition(entryOffsetX + 210, entryOffsetY + -40);
		buttonNorthEntry.setSpritePosition(buttonOffsetX + 67, buttonOffsetY + -26, 16, 16);
		buttonNorthEntry.contextHintState.buttonAHint = "bind";
		buttonNorthEntry.setBindingCallback(this);

		buttonEastEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);
		buttonEastEntry.desiredWidth(desiredButtonWidth);
		buttonEastEntry.setSpriteEnabled(true);
		buttonEastEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_RED, -1);
		buttonEastEntry.setPosition(entryOffsetX + 210, entryOffsetY + 0);
		buttonEastEntry.setSpritePosition(buttonOffsetX + 82, buttonOffsetY + -10, 16, 16);
		buttonEastEntry.contextHintState.buttonAHint = "bind";
		buttonEastEntry.setBindingCallback(this);

		buttonSouthEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH);
		buttonSouthEntry.desiredWidth(desiredButtonWidth);
		buttonSouthEntry.setSpriteEnabled(true);
		buttonSouthEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_GREEN, -1);
		buttonSouthEntry.setPosition(entryOffsetX + 210, entryOffsetY + 40);
		buttonSouthEntry.setSpritePosition(buttonOffsetX + 67, buttonOffsetY + 8, 16, 16);
		buttonSouthEntry.contextHintState.buttonAHint = "bind";
		buttonSouthEntry.setBindingCallback(this);

		buttonWestEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST);
		buttonWestEntry.desiredWidth(desiredButtonWidth);
		buttonWestEntry.setSpriteEnabled(true);
		buttonWestEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_BLUE, -1);
		buttonWestEntry.setPosition(entryOffsetX + 210, entryOffsetY + 80);
		buttonWestEntry.setSpritePosition(buttonOffsetX + 50, buttonOffsetY + -10, 16, 16);
		buttonWestEntry.contextHintState.buttonAHint = "bind";
		buttonWestEntry.setBindingCallback(this);

		// Direction Buttons (DPAD)

		ddButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN);
		ddButtonEntry.desiredWidth(desiredButtonWidth);
		ddButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_DPAD_DOWN, -1);
		ddButtonEntry.setSpriteEnabled(true);
		ddButtonEntry.setPosition(entryOffsetX + -310, entryOffsetY + 40);
		ddButtonEntry.setSpritePosition(buttonOffsetX + -83, buttonOffsetY + 8, 16, 16);
		ddButtonEntry.contextHintState.buttonAHint = "bind";
		ddButtonEntry.setBindingCallback(this);

		dlButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT);
		dlButtonEntry.desiredWidth(desiredButtonWidth);
		dlButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_DPAD_LEFT, -1);
		dlButtonEntry.setSpriteEnabled(true);
		dlButtonEntry.setPosition(entryOffsetX + -310, entryOffsetY + 0);
		dlButtonEntry.setSpritePosition(buttonOffsetX + -98, buttonOffsetY + -8, 16, 16);
		dlButtonEntry.contextHintState.buttonAHint = "bind";
		dlButtonEntry.setBindingCallback(this);

		duButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP);
		duButtonEntry.desiredWidth(desiredButtonWidth);
		duButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_DPAD_UP, -1);
		duButtonEntry.setSpriteEnabled(true);
		duButtonEntry.setPosition(entryOffsetX + -310, entryOffsetY + -40);
		duButtonEntry.setSpritePosition(buttonOffsetX + -83, buttonOffsetY + -25, 16, 16);
		duButtonEntry.contextHintState.buttonAHint = "bind";
		duButtonEntry.setBindingCallback(this);

		drButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT);
		drButtonEntry.desiredWidth(desiredButtonWidth);
		drButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_DPAD_RIGHT, -1);
		drButtonEntry.setSpriteEnabled(true);
		drButtonEntry.setPosition(entryOffsetX + -310, entryOffsetY + 80);
		drButtonEntry.setSpritePosition(buttonOffsetX + -70, buttonOffsetY + -8, 16, 16);
		drButtonEntry.contextHintState.buttonAHint = "bind";
		drButtonEntry.setBindingCallback(this);

		// Shoulder buttons

		final var triggerOffsetX = 74;
		final var triggerOffsetY = 60;

		ltriggerButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER);
		ltriggerButtonEntry.desiredWidth(desiredButtonWidth);
		ltriggerButtonEntry.setPosition(entryOffsetX + -290, entryOffsetY + -85);
		ltriggerButtonEntry.setSpriteEnabled(true);
		ltriggerButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_LEFT_TRIGGER_ON, CoreTextureNames.TEXTURE_GAMEPAD_LEFT_TRIGGER_OFF);
		ltriggerButtonEntry.setSpritePosition(-triggerOffsetX - 24, -triggerOffsetY + 16, 64, 32);
		ltriggerButtonEntry.contextHintState.buttonAHint = "bind";
		ltriggerButtonEntry.setBindingCallback(this);

		rtriggerButtonEntry = new MenuGamepadInputMapEntry(screenManager, this, GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER);
		rtriggerButtonEntry.desiredWidth(desiredButtonWidth);
		rtriggerButtonEntry.setPosition(entryOffsetX + 170, entryOffsetY + -85);
		rtriggerButtonEntry.setSpriteEnabled(true);
		rtriggerButtonEntry.setSpriteFrameIds(CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_TRIGGER_ON, CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_TRIGGER_OFF);
		rtriggerButtonEntry.setSpritePosition(triggerOffsetX - 24, -triggerOffsetY + 16, 64, 32);
		rtriggerButtonEntry.contextHintState.buttonAHint = "bind";
		rtriggerButtonEntry.setBindingCallback(this);

		// The order the buttons are added, if the tab-order for the layout

		layout.addMenuEntry(ltriggerButtonEntry);
		layout.addMenuEntry(rtriggerButtonEntry);

		layout.addMenuEntry(duButtonEntry);
		layout.addMenuEntry(dlButtonEntry);
		layout.addMenuEntry(ddButtonEntry);
		layout.addMenuEntry(drButtonEntry);

		layout.addMenuEntry(buttonNorthEntry);
		layout.addMenuEntry(buttonEastEntry);
		layout.addMenuEntry(buttonSouthEntry);
		layout.addMenuEntry(buttonWestEntry);

		layout.addMenuEntry(selectButtonEntry);
		layout.addMenuEntry(startButtonEntry);

		// buttons are rendered from last to first, so add the base last.
		layout.addMenuEntry(new MenuControllerImageEntry(screenManager, this));

		populateActiveGamepads();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {

		case BUTTON_BACK:
			exitScreen();
			break;

		case BUTTON_SAVE:
			if (mActiveGamepad != null) {
				mActiveGamepad.state.saveConfig();
			}
			break;
		}
	}

	@Override
	public void onMenuEntryChanged(MenuEntry menuEntry) {
		super.onMenuEntryChanged(menuEntry);

		switch (menuEntry.entryID()) {
		case BUTTON_AVAILABLE_CONTROLLERS:

			// TODO: Check for changes in the current active gamepad mapping and ask to save before moving on ...

			final var selectedIndex = mAvailableControllers.selectedEntry();

			if (mAvailableControllers.items().size() == 0) {
				setActiveGamepad(null);

				return;
			}

			if (selectedIndex == -1) {
				setActiveGamepad(null);
				return;
			}

			final var selectedItem = mAvailableControllers.selectedItem();
			setActiveGamepad(selectedItem.value);

			break;
		}
	}

	private void populateActiveGamepads() {
		mAvailableControllers.clearItems();

		final var connectedGamepads = mGamepadManager.getActiveGamepads();
		final var numConnectedGamepads = connectedGamepads.size();

		if (numConnectedGamepads == 0) {
			mAvailableControllers.addItem(mAvailableControllers.new MenuEnumEntryItem("No Controllers Detected", null));
			setActiveGamepad(null);

		} else {
			for (int i = 0; i < numConnectedGamepads; i++) {
				final var gamepad = connectedGamepads.get(i);
				final var gamepadName = gamepad.name();

				mAvailableControllers.addItem(mAvailableControllers.new MenuEnumEntryItem(gamepadName, gamepad));

				if (mActiveGamepad == null) // set active if not already
					setActiveGamepad(gamepad);

			}
		}

		// TODO: Iterate through the list and make sure the currently selected mActiveGamepad is still available.

		mAvailableControllers.setButtonsEnabled(numConnectedGamepads > 1);

	}

	@Override
	public void exitScreen() {

		// TODO: Need to detect changes in any of the gamepad mappings and offer to save.

		if (mBindingEntry != null && mBindingEntry instanceof MenuGamepadInputMapEntry) {
			final var entry = (MenuGamepadInputMapEntry) mBindingEntry;
			entry.cancelBinding();

			return; // don't exit yet
		}

		if (mGamepadManager != null) {
			mGamepadManager.stopGamepadCapture();
			mGamepadManager.removeGamepadListener(this);
		}

		if (mActiveGamepad != null) {
			mActiveGamepad.state.saveConfig();
		}

		super.exitScreen();
	}

	private void setActiveGamepad(Gamepad activeGamepad) {
		mActiveGamepad = activeGamepad;

		if (mActiveGamepad == null) {
			buttonWestEntry.activeControllerMap(null);
			buttonNorthEntry.activeControllerMap(null);
			buttonSouthEntry.activeControllerMap(null);
			buttonEastEntry.activeControllerMap(null);

			duButtonEntry.activeControllerMap(null);
			ddButtonEntry.activeControllerMap(null);
			dlButtonEntry.activeControllerMap(null);
			drButtonEntry.activeControllerMap(null);

			ltriggerButtonEntry.activeControllerMap(null);
			rtriggerButtonEntry.activeControllerMap(null);

			selectButtonEntry.activeControllerMap(null);
			startButtonEntry.activeControllerMap(null);

			mSdlMappingAvailable.isChecked(false);

			return;
		}

		final var isSdlMappingAvailable = activeGamepad.isGamepadMappingAvailable();
		if (isSdlMappingAvailable) {
			mSdlMappingAvailable.isChecked(isSdlMappingAvailable);
			mUseCustomBindings.readOnly(false);
		} else {
			mUseCustomBindings.isChecked(true);
			mUseCustomBindings.readOnly(true);
		}

		buttonWestEntry.activeControllerMap(mActiveGamepad.state);
		buttonNorthEntry.activeControllerMap(mActiveGamepad.state);
		buttonSouthEntry.activeControllerMap(mActiveGamepad.state);
		buttonEastEntry.activeControllerMap(mActiveGamepad.state);

		duButtonEntry.activeControllerMap(mActiveGamepad.state);
		ddButtonEntry.activeControllerMap(mActiveGamepad.state);
		dlButtonEntry.activeControllerMap(mActiveGamepad.state);
		drButtonEntry.activeControllerMap(mActiveGamepad.state);

		selectButtonEntry.activeControllerMap(mActiveGamepad.state);
		startButtonEntry.activeControllerMap(mActiveGamepad.state);

		ltriggerButtonEntry.activeControllerMap(mActiveGamepad.state);
		rtriggerButtonEntry.activeControllerMap(mActiveGamepad.state);
	}

	@Override
	public boolean allowGamepadInput() {
		return true; // disable menu navigation with the gamepad in this screen
	}

	// --------------------------------------
	// Interface-Methods
	// --------------------------------------

	@Override
	public void onGamepadConnected(Gamepad gamepad) {
		populateActiveGamepads();

	}

	@Override
	public void onGamepadDisconnected(Gamepad gamepad) {
		populateActiveGamepads();

	}

	@Override
	public void finishedBinding() {
		mBindingEntry = null;

	}

	@Override
	public void setIsBinding(MenuEntry entry) {
		if (mBindingEntry != null) {
			return;
		}

		mBindingEntry = entry;

	}

}