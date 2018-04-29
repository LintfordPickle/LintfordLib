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
	private boolean mIsDirty;

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

		MenuEntry lButton1 = new MenuEntry(pScreenManager, this, "Back");
		lButton1.buttonSize(BUTTON_SIZE.narrow);
		lButton1.registerClickListener(this, BUTTON_CANCEL_CHANGES);
		MenuEntry lButton2 = new MenuEntry(pScreenManager, this, "Apply");
		lButton2.buttonSize(BUTTON_SIZE.narrow);
		lButton2.registerClickListener(this, BUTTON_APPLY_CHANGES);

		lGroup.addEntry(lButton1);
		lGroup.addEntry(lButton2);

		lNavList.menuEntries().add(lGroup);

		// Add the layouts to the screen
		layouts().add(lAudioList);
		layouts().add(lNavList);

		mIsDirty = true;

	}

	private void createAudioSection(BaseLayout lLayout) {
		MenuLabelEntry lMusicOptionsTitle = new MenuLabelEntry(mScreenManager, this);
		lMusicOptionsTitle.label("Music Options");
		lMusicOptionsTitle.alignment(ALIGNMENT.left);

		MenuToggleEntry mMusicEnabledEntry = new MenuToggleEntry(mScreenManager, this);
		mMusicEnabledEntry.label("Music Enabled");
		MenuSliderEntry mMusicVolumnEntry = new MenuSliderEntry(mScreenManager, this);
		mMusicVolumnEntry.label("Music Volume");
		mMusicVolumnEntry.setBounds(0, 100, 5);
		mMusicVolumnEntry.setValue(75); // TODO: Music volume should be loaded from the config onLoad.
		mMusicVolumnEntry.buttonsEnabled(true);
		mMusicVolumnEntry.showValue(true);
		mMusicVolumnEntry.showValueUnit(true);
		mMusicVolumnEntry.showValueGuides(false);

		MenuLabelEntry lSoundOptionsTitle = new MenuLabelEntry(mScreenManager, this);
		lSoundOptionsTitle.label("Sound Options");
		lSoundOptionsTitle.alignment(ALIGNMENT.left);

		MenuToggleEntry mSoundEnabledEntry = new MenuToggleEntry(mScreenManager, this);
		mSoundEnabledEntry.label("SoundFX Enabled");

		MenuSliderEntry mSoundVolumnEntry = new MenuSliderEntry(mScreenManager, this);
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

		lLayout.menuEntries().add(lSoundOptionsTitle);
		lLayout.menuEntries().add(mSoundEnabledEntry);
		lLayout.menuEntries().add(mSoundVolumnEntry);

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
			mIsDirty = false;
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