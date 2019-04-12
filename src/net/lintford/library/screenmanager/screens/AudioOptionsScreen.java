package net.lintford.library.screenmanager.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuEntry.ENTRY_ALIGNMENT;
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioOptionsScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "Audio Options");

		ListLayout lAudioList = new ListLayout(this);
		lAudioList.setDrawBackground(true, 0f, 0f, 0f, 0.75f);

		createAudioSection(lAudioList);

		/* Screen control buttons */
		HorizontalEntryGroup lGroup = new HorizontalEntryGroup(pScreenManager, footerLayout());

		MenuEntry lBackButton = new MenuEntry(pScreenManager, footerLayout(), "Back");
		lBackButton.registerClickListener(this, BUTTON_CANCEL_CHANGES);
		MenuEntry lApplyButton = new MenuEntry(pScreenManager, footerLayout(), "Apply");
		lApplyButton.registerClickListener(this, BUTTON_APPLY_CHANGES);

		lGroup.addEntry(lBackButton);
		lGroup.addEntry(lApplyButton);

		footerLayout().menuEntries().add(lGroup);

		// Add the layouts to the screen
		layouts().add(lAudioList);

	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private void createAudioSection(BaseLayout lLayout) {
		MenuEntry lSeparator = new MenuEntry(mScreenManager, lLayout, "");
		lSeparator.enabled(false);
		lSeparator.drawButtonBackground(false);

		MenuLabelEntry lMusicOptionsTitle = new MenuLabelEntry(mScreenManager, lLayout);
		lMusicOptionsTitle.label("Music Options");
		lMusicOptionsTitle.enableBackground(true);
		lMusicOptionsTitle.horizontalAlignment(ENTRY_ALIGNMENT.LEFT);

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
		lSoundOptionsTitle.horizontalAlignment(ENTRY_ALIGNMENT.LEFT);

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

	// --------------------------------------
	// Methods
	// --------------------------------------

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