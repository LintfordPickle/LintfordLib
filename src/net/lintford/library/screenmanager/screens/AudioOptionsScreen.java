package net.lintford.library.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.audio.AudioManager;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintford.library.screenmanager.dialogs.ConfirmationDialog;
import net.lintford.library.screenmanager.entries.HorizontalEntryGroup;
import net.lintford.library.screenmanager.entries.MenuSliderEntry;
import net.lintford.library.screenmanager.entries.MenuToggleEntry;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class AudioOptionsScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String SCREEN_TITLE = "AUDIO OPTIONS";

	public static final int BUTTON_CANCEL_CHANGES = 0;
	public static final int BUTTON_APPLY_CHANGES = 1;

	public static final int BUTTON_ENABLED_MASTER = 15;
	public static final int BUTTON_ENABLED_MUSIC = 10;
	public static final int BUTTON_ENABLED_SOUNDFX = 11;
	public static final int BUTTON_VOLUME_MASTER = 12;
	public static final int BUTTON_VOLUME_MUSIC = 13;
	public static final int BUTTON_VOLUME_SOUNDFX = 14;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AudioManager mAudioManager;
	private ConfirmationDialog mConfirmationDialog;

	private MenuToggleEntry mMasterEnabledEntry;
	private MenuSliderEntry mMasterVolumeEntry;
	private MenuToggleEntry mMusicEnabledEntry;
	private MenuSliderEntry mMusicVolumeEntry;
	private MenuToggleEntry mSoundEnabledEntry;
	private MenuSliderEntry mSoundVolumnEntry;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioOptionsScreen(ScreenManager pScreenManager) {
		super(pScreenManager, SCREEN_TITLE);

		final var lAudioList = new ListLayout(this);
		lAudioList.setDrawBackground(true, 1f, 1f, 1f, 0.85f);
		lAudioList.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		createAudioSection(lAudioList);

		/* Screen control buttons */
		final var lGroup = new HorizontalEntryGroup(pScreenManager, footerLayout());

		final var lBackButton = new MenuEntry(pScreenManager, footerLayout(), "Back");
		lBackButton.registerClickListener(this, BUTTON_CANCEL_CHANGES);
		final var lApplyButton = new MenuEntry(pScreenManager, footerLayout(), "Apply");
		lApplyButton.registerClickListener(this, BUTTON_APPLY_CHANGES);

		lGroup.addEntry(lBackButton);
		lGroup.addEntry(lApplyButton);

		footerLayout().menuEntries().add(lGroup);

		// Add the layouts to the screen
		layouts().add(lAudioList);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mAudioManager = pResourceManager.audioManager();

	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private void createAudioSection(BaseLayout lLayout) {
		final var lAudioConfig = mScreenManager.core().config().audio();

		lLayout.setDrawBackground(true, 0.1f, 0.1f, 0.1f, 0.5f);

		MenuEntry lSeparator = new MenuEntry(mScreenManager, lLayout, "");
		lSeparator.enabled(false);
		lSeparator.drawButtonBackground(false);

		mMasterEnabledEntry = new MenuToggleEntry(mScreenManager, lLayout);
		mMasterEnabledEntry.registerClickListener(this, BUTTON_ENABLED_MASTER);
		mMasterEnabledEntry.label("Audio Enabled");
		mMasterEnabledEntry.isChecked(lAudioConfig.musicEnabled());
		mMasterEnabledEntry.horizontalFillType(FILLTYPE.FILL_PARENT);

		mMasterVolumeEntry = new MenuSliderEntry(mScreenManager, lLayout);
		mMasterVolumeEntry.label("Master Volume");
		mMasterVolumeEntry.registerClickListener(this, BUTTON_VOLUME_MASTER);
		mMasterVolumeEntry.setBounds(0, 100, 5);
		final var lDisplayMasterValue = MathHelper.scaleToRange(lAudioConfig.masterVolume(), 0f, 1f, 0f, 100f);
		mMasterVolumeEntry.setValue((int) lDisplayMasterValue);
		mMasterVolumeEntry.buttonsEnabled(true);
		mMasterVolumeEntry.showValue(true);
		mMasterVolumeEntry.showValueUnit(true);
		mMasterVolumeEntry.showValueGuides(false);
		mMasterVolumeEntry.horizontalFillType(FILLTYPE.FILL_PARENT);

		mMusicEnabledEntry = new MenuToggleEntry(mScreenManager, lLayout);
		mMusicEnabledEntry.registerClickListener(this, BUTTON_ENABLED_MUSIC);
		mMusicEnabledEntry.label("Music Enabled");
		mMusicEnabledEntry.isChecked(lAudioConfig.musicEnabled());
		mMusicEnabledEntry.horizontalFillType(FILLTYPE.FILL_PARENT);

		mMusicVolumeEntry = new MenuSliderEntry(mScreenManager, lLayout);
		mMusicVolumeEntry.label("Music Volume");
		mMusicVolumeEntry.registerClickListener(this, BUTTON_VOLUME_MUSIC);
		mMusicVolumeEntry.setBounds(0, 100, 5);
		final var lDisplayMusicValue = MathHelper.scaleToRange(lAudioConfig.musicVolume(), 0f, 1f, 0f, 100f);
		mMusicVolumeEntry.setValue((int) lDisplayMusicValue);
		mMusicVolumeEntry.buttonsEnabled(true);
		mMusicVolumeEntry.showValue(true);
		mMusicVolumeEntry.showValueUnit(true);
		mMusicVolumeEntry.showValueGuides(false);
		mMusicVolumeEntry.horizontalFillType(FILLTYPE.FILL_PARENT);

		mSoundEnabledEntry = new MenuToggleEntry(mScreenManager, lLayout);
		mSoundEnabledEntry.label("SoundFX Enabled");
		mSoundEnabledEntry.registerClickListener(this, BUTTON_ENABLED_SOUNDFX);
		mSoundEnabledEntry.isChecked(lAudioConfig.soundFxEnabled());
		mSoundEnabledEntry.horizontalFillType(FILLTYPE.FILL_PARENT);

		mSoundVolumnEntry = new MenuSliderEntry(mScreenManager, lLayout);
		mSoundVolumnEntry.label("SoundFX Volume");
		mSoundVolumnEntry.registerClickListener(this, BUTTON_VOLUME_SOUNDFX);
		mSoundVolumnEntry.setBounds(0, 100, 5);
		final var lDisplaySoundFxValue = MathHelper.scaleToRange(lAudioConfig.soundFxVolume(), 0f, 1f, 0f, 100f);
		mSoundVolumnEntry.setValue((int) lDisplaySoundFxValue);
		mSoundVolumnEntry.buttonsEnabled(true);
		mSoundVolumnEntry.showValue(true);
		mSoundVolumnEntry.showValueUnit(true);
		mSoundVolumnEntry.showValueGuides(false);
		mSoundVolumnEntry.horizontalFillType(FILLTYPE.FILL_PARENT);

		// lLayout.menuEntries().add(mMasterEnabledEntry);
		lLayout.menuEntries().add(mMasterVolumeEntry);
		lLayout.menuEntries().add(lSeparator);

		lLayout.menuEntries().add(mMusicEnabledEntry);
		lLayout.menuEntries().add(mMusicVolumeEntry);
		lLayout.menuEntries().add(lSeparator);

		lLayout.menuEntries().add(mSoundEnabledEntry);
		lLayout.menuEntries().add(mSoundVolumnEntry);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateLayoutSize(LintfordCore pCore) {
		final int lLayoutCount = layouts().size();
		for (int i = 0; i < lLayoutCount; i++) {
			layouts().get(i).layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
			layouts().get(i).marginLeft(50);
			layouts().get(i).marginRight(50);

		}

		super.updateLayoutSize(pCore);

	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_CANCEL_CHANGES:
			exitScreen();
			break;

		case BUTTON_APPLY_CHANGES:
			mAudioManager.audioConfig().saveConfig();
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
		switch (e.entryID()) {
		case BUTTON_ENABLED_MASTER:
			mAudioManager.audioConfig().masterEnabled(mMasterEnabledEntry.isChecked());
			mAudioManager.updateSettings();
			break;

		case BUTTON_ENABLED_MUSIC:
			mAudioManager.audioConfig().musicEnabled(mMusicEnabledEntry.isChecked());
			mAudioManager.updateSettings();
			break;

		case BUTTON_ENABLED_SOUNDFX:
			mAudioManager.audioConfig().soundFxEnabled(mSoundEnabledEntry.isChecked());
			mAudioManager.updateSettings();
			break;

		case BUTTON_VOLUME_MASTER:
			final var lScaledMasterVolumeRange = MathHelper.scaleToRange(mMasterVolumeEntry.getCurrentValue(), 0f, 100f, 0f, 1f);
			mAudioManager.audioConfig().masterVolume(lScaledMasterVolumeRange);
			mAudioManager.updateSettings();
			break;

		case BUTTON_VOLUME_MUSIC:
			final var lScaledMusicVolumeRange = MathHelper.scaleToRange(mMusicVolumeEntry.getCurrentValue(), 0f, 100f, 0f, 1f);
			mAudioManager.audioConfig().musicVolume(lScaledMusicVolumeRange);
			mAudioManager.updateSettings();
			break;

		case BUTTON_VOLUME_SOUNDFX:
			final var lScaledSoundFxVolumeRange = MathHelper.scaleToRange(mSoundVolumnEntry.getCurrentValue(), 0f, 100f, 0f, 1f);
			mAudioManager.audioConfig().soundFxVolume(lScaledSoundFxVolumeRange);
			mAudioManager.updateSettings();
			break;

		}

	}

}