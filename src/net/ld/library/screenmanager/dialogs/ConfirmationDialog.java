package net.ld.library.screenmanager.dialogs;

import net.ld.library.core.input.InputState;
import net.ld.library.screenmanager.MenuScreen;
import net.ld.library.screenmanager.ScreenManager;
import net.ld.library.screenmanager.entries.MenuEntry;
import net.ld.library.screenmanager.entries.MenuEntry.BUTTON_SIZE;

public class ConfirmationDialog extends BaseDialog {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int BUTTON_CONFIRM_YES = 100;
	public static final int BUTTON_CONFIRM_NO = 101;

	// ===========================================================
	// Variables
	// ===========================================================

	private MenuEntry mConfirmEntry;
	private MenuEntry mCancelEntry;

	// ===========================================================
	// Properties
	// ===========================================================

	public MenuEntry confirmEntry() {
		return mConfirmEntry;
	}

	public MenuEntry cancelEntry() {
		return mCancelEntry;
	}

	// ===========================================================
	// Constructors
	// ===========================================================

	public ConfirmationDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogTitle, String pDialogMessage) {
		super(pScreenManager, pParentScreen, pDialogTitle, pDialogMessage);

		mConfirmEntry = new MenuEntry(pScreenManager, this, "Okay");
		mConfirmEntry.registerClickListener(pParentScreen, BUTTON_CONFIRM_YES);
		mConfirmEntry.buttonSize(BUTTON_SIZE.narrow);
		mCancelEntry = new MenuEntry(pScreenManager, this, "Cancel");
		mCancelEntry.registerClickListener(pParentScreen, BUTTON_CONFIRM_NO);
		mCancelEntry.buttonSize(BUTTON_SIZE.narrow);

		menuEntries().add(mCancelEntry);
		menuEntries().add(mConfirmEntry);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public void onClick(InputState pInputState, int pEntryID) {

		switch (pEntryID) {
		case BUTTON_CONFIRM_YES:

			break;

		case BUTTON_CONFIRM_NO:

			break;

		default:
			break;
		}

	}

}
