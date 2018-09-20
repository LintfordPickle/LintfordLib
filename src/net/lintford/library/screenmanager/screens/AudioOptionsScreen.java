package net.lintford.library.screenmanager.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuEntry.BUTTON_SIZE;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.dialogs.ConfirmationDialog;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.entries.HorizontalEntryGroup;
import net.lintford.library.screenmanager.entries.MenuLabelEntry;
import net.lintford.library.screenmanager.entries.MenuSliderEntry;
import net.lintford.library.screenmanager.entries.MenuToggleEntry;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.layouts.BaseLayout.FILL_TYPE;

public class AudioOptionsScreen extends MenuScreen implements EntryInteractions {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_CANCEL_CHANGES = 0;
	public static final int BUTTON_APPLY_CHANGES = 1;

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
		lAudioList.fillType(FILL_TYPE.DYNAMIC);
		lAudioList.setDrawBackground(true, 0f, 0f, 0f, 0.75f);

		ListLayout lNavList = new ListLayout(this);
		lNavList.fillType(FILL_TYPE.STATIC);

		createAudioSection(lAudioList);

		/* Screen control buttons */
		HorizontalEntryGroup lGroup = new HorizontalEntryGroup(pScreenManager, lNavList);

		MenuEntry lButton1 = new MenuEntry(pScreenManager, lNavList, "Back");
		lButton1.buttonSize(BUTTON_SIZE.narrow);
		lButton1.registerClickListener(this, BUTTON_CANCEL_CHANGES);
		MenuEntry lButton2 = new MenuEntry(pScreenManager, lNavList, "Apply");
		lButton2.buttonSize(BUTTON_SIZE.narrow);
		lButton2.registerClickListener(this, BUTTON_APPLY_CHANGES);

		lGroup.addEntry(lButton1);
		lGroup.addEntry(lButton2);

		lNavList.menuEntries().add(lGroup);

		// Add the layouts to the screen
		layouts().add(lAudioList);
		layouts().add(lNavList);

	}

	private void createAudioSection(BaseLayout lLayout) {
		MenuEntry lSeparator = new MenuEntry(mScreenManager, lLayout, "");
		lSeparator.enabled(false);
		lSeparator.drawButtonBackground(false);

		MenuLabelEntry lMusicOptionsTitle = new MenuLabelEntry(mScreenManager, lLayout);
		lMusicOptionsTitle.label("Music Options");
		lMusicOptionsTitle.enableBackground(true);
		lMusicOptionsTitle.alignment(ALIGNMENT.left);

		MenuToggleEntry mMusicEnabledEntry = new MenuToggleEntry(mScreenManager, lLayout);
		mMusicEnabledEntry.label("Music Enabled");
		MenuSliderEntry mMusicVolumnEntry = new MenuSliderEntry(mScreenManager, lLayout);
		mMusicVolumnEntry.label("Music Volume");
		mMusicVolumnEntry.setBounds(0, 100, 5);
		mMusicVolumnEntry.setValue(75); // TODO: Music volume should be loaded from the config onLoad.
		mMusicVolumnEntry.buttonsEnabled(true);
		mMusicVolumnEntry.showValue(true);
		mMusicVolumnEntry.showValueUnit(true);
		mMusicVolumnEntry.showValueGuides(false);

		MenuLabelEntry lSoundOptionsTitle = new MenuLabelEntry(mScreenManager, lLayout);
		lSoundOptionsTitle.label("Sound Options");
		lSoundOptionsTitle.enableBackground(true);
		lSoundOptionsTitle.alignment(ALIGNMENT.left);

		MenuToggleEntry mSoundEnabledEntry = new MenuToggleEntry(mScreenManager, lLayout);
		mSoundEnabledEntry.label("SoundFX Enabled");

		MenuSliderEntry mSoundVolumnEntry = new MenuSliderEntry(mScreenManager, lLayout);
		mSoundVolumnEntry.label("SoundFX Volume");
		mSoundVolumnEntry.setBounds(0, 100, 5);
		mSoundVolumnEntry.setValue(75); // TODO: Sound volume should be loaded from the config onLoad.
		mSoundVolumnEntry.buttonsEnabled(true);
		mSoundVolumnEntry.showValue(true);
		mSoundVolumnEntry.showValueUnit(true);
		mSoundVolumnEntry.showValueGuides(false);

		lLayout.menuEntries().add(lMusicOptionsTitle);
		lLayout.menuEntries().add(mMusicEnabledEntry);
		lLayout.menuEntries().add(mMusicVolumnEntry);
		lLayout.menuEntries().add(lSeparator);

		lLayout.menuEntries().add(lSoundOptionsTitle);
		lLayout.menuEntries().add(mSoundEnabledEntry);
		lLayout.menuEntries().add(mSoundVolumnEntry);
		lLayout.menuEntries().add(lSeparator);

	}

	// --------------------------------------==============
	// Methods
	// --------------------------------------==============

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_CANCEL_CHANGES:
			exitScreen();
			break;

		case BUTTON_APPLY_CHANGES:
			// Temp
			exitScreen();
			break;

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