package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
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

	public ConfirmationDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogMessage) {
		this(pScreenManager, pParentScreen, "", pDialogMessage, true);

	}

	public ConfirmationDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pTitle, String pDialogMessage) {
		this(pScreenManager, pParentScreen, pTitle, pDialogMessage, true);

	}

	public ConfirmationDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pTitle, String pDialogMessage, boolean pWithCancel) {
		super(pScreenManager, pParentScreen, pDialogMessage);

		ListLayout lListLayout = new ListLayout(this);

		mConfirmEntry = new MenuEntry(pScreenManager, lListLayout, "Okay");
		mConfirmEntry.registerClickListener(pParentScreen, BUTTON_CONFIRM_YES);

		if (pWithCancel) {
			mCancelEntry = new MenuEntry(pScreenManager, lListLayout, "Cancel");
			mCancelEntry.registerClickListener(pParentScreen, BUTTON_CONFIRM_NO);

			lListLayout.menuEntries().add(mCancelEntry);

		}

		mMenuTitle = pTitle;

		lListLayout.menuEntries().add(mConfirmEntry);

		layouts().add(lListLayout);

		// Offset of the buttons (from 0 - center screen)
		mPaddingTop = 10;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_CONFIRM_YES:
			System.out.println("I'm called");
			break;

		case BUTTON_CONFIRM_NO:
			exitScreen();
			System.out.println("I'm called");
			break;

		default:
			break;
		}
	}

}
