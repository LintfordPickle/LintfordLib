package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class ConfirmationDialog extends BaseDialog {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_CONFIRM_YES = 100;
	public static final int BUTTON_CONFIRM_NO = 101;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ListLayout mListLayout;
	private MenuEntry mConfirmEntry;
	private MenuEntry mCancelEntry;

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

		mListLayout = new ListLayout(this);

		createMenuEntries(mListLayout);

		mConfirmEntry = new MenuEntry(screenManager, this, "Okay");
		mConfirmEntry.registerClickListener(this, BUTTON_CONFIRM_YES);

		if (withCancelButton) {
			mCancelEntry = new MenuEntry(screenManager, this, "Cancel");
			mCancelEntry.registerClickListener(this, BUTTON_CONFIRM_NO);

			mListLayout.addMenuEntry(mCancelEntry);
		}

		mMenuTitle = title;

		mListLayout.addMenuEntry(mConfirmEntry);

		addLayout(mListLayout);
		mPaddingTopNormalized = DIALOG_HEIGHT / 2.f - (mListLayout.getMenuEntryCount() > 1 ? 96.f : 64.f);

		mIsPopup = true;
		mShowBackgroundScreens = true;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void createMenuEntries(BaseLayout layout) {

	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_CONFIRM_YES:
			break;

		case BUTTON_CONFIRM_NO:
			exitScreen();
			break;

		default:
			break;
		}
	}
}
