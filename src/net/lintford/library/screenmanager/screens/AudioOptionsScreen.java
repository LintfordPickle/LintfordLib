package net.lintford.library.screenmanager.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuEntry.BUTTON_SIZE;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.dialogs.ConfirmationDialog;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.entries.HorizontalEntryGroup;
import net.lintford.library.screenmanager.entries.MenuSliderEntry;
import net.lintford.library.screenmanager.entries.MenuToggleEntry;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class AudioOptionsScreen extends MenuScreen implements EntryInteractions {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_OKAY = 0;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ConfirmationDialog mConfirmationDialog;

	// --------------------------------------==============
	// Constructor
	// --------------------------------------==============

	public AudioOptionsScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "Audio Options");

		ListLayout lAudioList = new ListLayout(this);
		lAudioList.setDrawBackground(true, 0f, 0f, 0f, 0.75f);
		lAudioList.setPadding(lAudioList.paddingTop(), lAudioList.paddingLeft(), lAudioList.paddingRight(), 25f);
		lAudioList.forceHeight(400);

		ListLayout lNavList = new ListLayout(this);

		createAudioSection(lAudioList);

		/* Screen control buttons */
		HorizontalEntryGroup lGroup = new HorizontalEntryGroup(pScreenManager, this);

		MenuEntry lOkayButton = new MenuEntry(pScreenManager, this, "Okay");
		lOkayButton.buttonSize(BUTTON_SIZE.narrow);
		lOkayButton.registerClickListener(this, BUTTON_OKAY);

		lGroup.addEntry(lOkayButton);

		lNavList.menuEntries().add(lGroup);

		// Add the layouts to the screen
		layouts().add(lAudioList);
		layouts().add(lNavList);

	}

	private void createAudioSection(BaseLayout lLayout) {
		MenuToggleEntry mMusicEnabledEntry = new MenuToggleEntry(mScreenManager, this);
		mMusicEnabledEntry.label("Music Enabled");
		MenuSliderEntry mMusicVolumnEntry = new MenuSliderEntry(mScreenManager, this);
		mMusicVolumnEntry.label("Music Volume");

		MenuToggleEntry mSoundEnabledEntry = new MenuToggleEntry(mScreenManager, this);
		mSoundEnabledEntry.label("SoundFX Enabled");

		MenuSliderEntry mSoundVolumnEntry = new MenuSliderEntry(mScreenManager, this);
		mSoundVolumnEntry.label("SoundFX Volume");

		lLayout.menuEntries().add(mMusicEnabledEntry);
		lLayout.menuEntries().add(mMusicVolumnEntry);
		lLayout.menuEntries().add(mSoundEnabledEntry);
		lLayout.menuEntries().add(mSoundVolumnEntry);

	}

	// --------------------------------------==============
	// Methods
	// --------------------------------------==============

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case ConfirmationDialog.BUTTON_CONFIRM_YES: // exit without saving
			if (mConfirmationDialog != null)
				mScreenManager.removeScreen(mConfirmationDialog);

			exitScreen();

			break;

		case ConfirmationDialog.BUTTON_CONFIRM_NO: // go back and dont exit yet
			if (mConfirmationDialog != null)
				mScreenManager.removeScreen(mConfirmationDialog);

			break;
		}
	}

	// --------------------------------------
	// Listeners
	// --------------------------------------

	@Override
	public void menuEntryChanged(MenuEntry e) {

	}
}
