package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.MenuKeyBindEntry;
import net.lintfordlib.screenmanager.entries.MenuLabelEntry;
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public KeyBindOptionsScreen(ScreenManager screenManager) {
		super(screenManager, "KEYBOARD KEYBINDS");

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

		final var lFooterList = new HorizontalLayout(this);
		lFooterList.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		mBackButton = new MenuEntry(screenManager, this, "Go back");
		mBackButton.registerClickListener(this, BUTTON_CONFIRM);

		lFooterList.addMenuEntry(mBackButton);

		addLayout(mKeyBindListLayout);
		addLayout(lFooterList);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private void createKeyBindSection(BaseLayout layout) {
		final var lKeyBindOptionsTitle = new MenuLabelEntry(mScreenManager, this);

		lKeyBindOptionsTitle.label("KeyBinds");
		lKeyBindOptionsTitle.drawButtonBackground(true);
		lKeyBindOptionsTitle.horizontalAlignment(ALIGNMENT.LEFT);
		lKeyBindOptionsTitle.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		layout.addMenuEntry(lKeyBindOptionsTitle);

		final var inputManager = screenManager().core().input();
		final var actionEventManager = inputManager.eventActionManager();

		final var lGameKeyBinds = actionEventManager.gameKeyActions();
		if (lGameKeyBinds != null) {
			final var lGameKeyMap = lGameKeyBinds.gameKeyMap();
			final var lNumKeysMappedInGame = lGameKeyMap.size();
			for (int i = 0; i < lNumKeysMappedInGame; i++) {
				final var lKeyMap = lGameKeyMap.get(i);

				final var lRegisteredEventAction = actionEventManager.getEventActionByUid(lKeyMap.eventActionUid);
				if (lRegisteredEventAction == null)
					continue;

				final var lNewActionEntry = new MenuKeyBindEntry(mScreenManager, this, lRegisteredEventAction);
				lNewActionEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
				lNewActionEntry.label(lKeyMap.eventActionName);
				lNewActionEntry.registerClickListener(this, lKeyMap.eventActionUid);

				layout.addMenuEntry(lNewActionEntry);
			}
		}
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void exitScreen() {
		mScreenManager.core().input().eventActionManager().saveConfig();

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
}