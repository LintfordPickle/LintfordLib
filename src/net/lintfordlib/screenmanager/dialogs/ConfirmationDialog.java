package net.lintfordlib.screenmanager.dialogs;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.layouts.FloatingLayout;

public class ConfirmationDialog extends BaseDialog {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_CONFIRM_YES = 100;
	public static final int BUTTON_CONFIRM_NO = 101;

	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 25;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected FloatingLayout mFloatingLayout;

	private MenuEntry mConfirmEntry;
	private MenuEntry mCancelEntry;

	private boolean mHasCancelButton;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public MenuEntry confirmEntry() {
		return mConfirmEntry;
	}

	public MenuEntry cancelEntry() {
		return mCancelEntry;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public ConfirmationDialog(ScreenManager screenManager, Screen parentScreen, String dialogMessage) {
		this(screenManager, parentScreen, "", dialogMessage, true);
	}

	public ConfirmationDialog(ScreenManager screenManager, Screen parentScreen, String title, String dialogMessage) {
		this(screenManager, parentScreen, title, dialogMessage, true);
	}

	public ConfirmationDialog(ScreenManager screenManager, Screen parentScreen, String title, String dialogMessage, boolean withCancelButton) {
		super(screenManager, parentScreen, dialogMessage);

		mFloatingLayout = new FloatingLayout(this);
		mHasCancelButton = withCancelButton;

		if (mHasCancelButton) {
			mCancelEntry = new MenuEntry(screenManager, this, "Cancel");
			mCancelEntry.registerClickListener(this, BUTTON_CONFIRM_NO);

			mFloatingLayout.addMenuEntry(mCancelEntry);
		}

		mConfirmEntry = new MenuEntry(screenManager, this, "Okay");
		mConfirmEntry.registerClickListener(this, BUTTON_CONFIRM_YES);

		mMenuTitle = title;

		mFloatingLayout.addMenuEntry(mConfirmEntry);

		addLayout(mFloatingLayout);

		mIsPopup = true;
		mShowBackgroundScreens = true;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		// Because we are using a floating layout for the dialog, we need to manually place the buttons.

		final var dialogArea = mDialogArea;

		if (mHasCancelButton) {
			mCancelEntry.width(150);
			mCancelEntry.setPosition(dialogArea.centerX() - 150 - 5, dialogArea.centerY() + 10);

			mConfirmEntry.width(150);
			mConfirmEntry.setPosition(dialogArea.centerX() + 5, dialogArea.centerY() + 10);
		} else {
			mConfirmEntry.width(150);
			mConfirmEntry.setPosition(dialogArea.centerX() - 150 / 2, dialogArea.centerY() + 10);
		}

	}

	@Override
	protected void handleOnClick() {
		// Handle onClick in registered MenuEntry's handlers

		exitScreen();
	}
}
