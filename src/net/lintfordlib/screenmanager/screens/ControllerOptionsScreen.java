package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.input.gamepad.GamepadManager;
import net.lintfordlib.core.input.gamepad.IGamepadListener;
import net.lintfordlib.core.input.gamepad.InputGamepad;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.MenuEnumEntry;
import net.lintfordlib.screenmanager.entries.MenuLabelEntry;
import net.lintfordlib.screenmanager.entries.MenuToggleEntry;
import net.lintfordlib.screenmanager.layouts.BaseLayout;
import net.lintfordlib.screenmanager.layouts.HorizontalLayout;
import net.lintfordlib.screenmanager.layouts.ListLayout;

// TODO: This whole screen needs a rework - it is NOT about the key binds to a controller for ingame actions - rather the gamepad mapping of physical buttons/axis to the Lintford Codes.
// TODO: Needs to listen to controller connections/disconnections


public class ControllerOptionsScreen extends MenuScreen implements IGamepadListener {

	public class ControllerContainer {
		public String mControllerName;
		public int mControllerUid;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_CONFIRM = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MenuEntry mBackButton;
	private ListLayout mKeyBindListLayout;

	private GamepadManager mGamepadManager;
	private MenuEnumEntry mAvailableControllers;
	private MenuToggleEntry mCustomBindings;

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
		// controllerSelectionLayout.cropPaddingBottom(13.f);
		controllerSelectionLayout.setDrawBackground(true, ColorConstants.MenuPanelSecondaryColor);
		controllerSelectionLayout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		controllerSelectionLayout.layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
		controllerSelectionLayout.marginBottom(10);

		mAvailableControllers = new MenuEnumEntry(screenManager, this, "Controller");
		mAvailableControllers.setButtonsEnabled(true);

		mCustomBindings = new MenuToggleEntry(screenManager, this, "Custom Bindings");

		controllerSelectionLayout.addMenuEntry(mAvailableControllers);
		controllerSelectionLayout.addMenuEntry(mCustomBindings);

		// Binding Section

		mKeyBindListLayout = new ListLayout(this);
		mKeyBindListLayout.cropPaddingTop(9.f);
		mKeyBindListLayout.cropPaddingBottom(13.f);
		mKeyBindListLayout.setDrawBackground(true, ColorConstants.MenuPanelSecondaryColor);
		mKeyBindListLayout.layoutFillType(FILLTYPE.FILL_CONTAINER);
		mKeyBindListLayout.layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
		mKeyBindListLayout.marginLeft(100);
		mKeyBindListLayout.marginRight(100);

		createKeyBindSection(mKeyBindListLayout);

		final var lFooterList = new HorizontalLayout(this);
		lFooterList.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		mBackButton = new MenuEntry(screenManager, this, "Go back");
		mBackButton.registerClickListener(this, BUTTON_CONFIRM);

		lFooterList.addMenuEntry(mBackButton);

		addLayout(controllerSelectionLayout);
		addLayout(mKeyBindListLayout);
		addLayout(lFooterList);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private void createKeyBindSection(BaseLayout layout) {
		final var keyBindOptionsTitle = new MenuLabelEntry(screenManager, this);

		keyBindOptionsTitle.label("KeyBinds");
		keyBindOptionsTitle.drawButtonBackground(true);
		keyBindOptionsTitle.horizontalAlignment(ALIGNMENT.LEFT);
		keyBindOptionsTitle.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		layout.addMenuEntry(keyBindOptionsTitle);

		final var inputManager = screenManager.core().input();
		final var keyEventActionManager = inputManager.eventActionManager();

		// TODO: Eventually this needs to read the current mapping from a file - so that it can be on a per-controller basis!

//		final var lGameKeyBinds = keyEventActionManager.gameKeyActions();
//		if (lGameKeyBinds != null) {
//			final var gameControllermap = lGameKeyBinds.gameControllerMap();
//			final var numKeysMappedInGame = gameControllermap.size();
//			for (int i = 0; i < numKeysMappedInGame; i++) {
//				final var keyMap = gameControllermap.get(i);
//
//				final var registeredEventAction = keyEventActionManager.getGamepadEventActionByUid(keyMap.eventActionUid);
//				if (registeredEventAction == null)
//					continue;
//
//				final var newActionEntry = new MenuBindingGamepadEntry(screenManager, this, registeredEventAction);
//				newActionEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
//				newActionEntry.label(keyMap.eventActionName);
//				newActionEntry.registerClickListener(this, keyMap.eventActionUid);
//
//				layout.addMenuEntry(newActionEntry);
//			}
//		}
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void exitScreen() {

		if (mGamepadManager != null)
			mGamepadManager.removeGamepadListener(this);

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

	private void populateActiveGamepads() {
		mAvailableControllers.clearItems();

		final var connectedGamepads = mGamepadManager.getActiveGamepads();
		final var numConnectedGamepads = connectedGamepads.size();

		if (numConnectedGamepads == 0) {

		} else {
			for (int i = 0; i < numConnectedGamepads; i++) {
				final var gamepad = connectedGamepads.get(i);

				final var gamepadName = gamepad.name();
				mAvailableControllers.addItem(gamepadName);

			}
		}

		mAvailableControllers.setButtonsEnabled(numConnectedGamepads > 1);

	}

	// --------------------------------------
	// Interface-Methods
	// --------------------------------------

	@Override
	public void onGamepadConnected(InputGamepad gamepad) {
		populateActiveGamepads();

	}

	@Override
	public void onGamepadDisconnected(InputGamepad gamepad) {
		populateActiveGamepads();

	}

}