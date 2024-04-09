package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.audio.AudioManager;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.dialogs.ConfirmationDialog;
import net.lintfordlib.screenmanager.entries.MenuSliderEntry;
import net.lintfordlib.screenmanager.entries.MenuToggleEntry;
import net.lintfordlib.screenmanager.layouts.BaseLayout;
import net.lintfordlib.screenmanager.layouts.HorizontalLayout;
import net.lintfordlib.screenmanager.layouts.ListLayout;

public class AudioOptionsScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

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

	public AudioOptionsScreen(ScreenManager screenManager) {
		super(screenManager, null);

		final var lAudioListLayout = new ListLayout(this);
		lAudioListLayout.paddingTop(10.f);
		lAudioListLayout.paddingBottom(10.f);
		lAudioListLayout.cropPaddingTop(0.f);
		lAudioListLayout.cropPaddingBottom(0.f);
		lAudioListLayout.setDrawBackground(true, ColorConstants.MenuPanelSecondaryColor);
		lAudioListLayout.layoutFillType(FILLTYPE.FILL_CONTAINER);
		lAudioListLayout.showTitle(true);
		lAudioListLayout.title("Audio Options");

		createAudioSection(lAudioListLayout);

		/* Screen control buttons */
		final var lHorizontalButtonLayout = new HorizontalLayout(this);
		lHorizontalButtonLayout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		final var lBackButton = new MenuEntry(screenManager, this, "Back");
		lBackButton.registerClickListener(this, BUTTON_CANCEL_CHANGES);
		final var lApplyButton = new MenuEntry(screenManager, this, "Apply");
		lApplyButton.registerClickListener(this, BUTTON_APPLY_CHANGES);

		lHorizontalButtonLayout.addMenuEntry(lBackButton);
		lHorizontalButtonLayout.addMenuEntry(lApplyButton);

		addLayout(lAudioListLayout);
		addLayout(lHorizontalButtonLayout);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mAudioManager = resourceManager.audioManager();
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	private void createAudioSection(BaseLayout layout) {
		final var lAudioConfig = mScreenManager.core().config().audio();

		mMasterEnabledEntry = new MenuToggleEntry(mScreenManager, this);
		mMasterEnabledEntry.registerClickListener(this, BUTTON_ENABLED_MASTER);
		mMasterEnabledEntry.label("Audio Enabled");
		mMasterEnabledEntry.isChecked(lAudioConfig.musicEnabled());
		mMasterEnabledEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mMasterVolumeEntry = new MenuSliderEntry(mScreenManager, this);
		mMasterVolumeEntry.label("Master Volume");
		mMasterVolumeEntry.registerClickListener(this, BUTTON_VOLUME_MASTER);
		mMasterVolumeEntry.setBounds(0, 100, 5);
		final var lDisplayMasterValue = MathHelper.scaleToRange(lAudioConfig.masterVolume(), 0f, 1f, 0f, 100f);
		mMasterVolumeEntry.setValue((int) lDisplayMasterValue);
		mMasterVolumeEntry.buttonsEnabled(true);
		mMasterVolumeEntry.showValue(true);
		mMasterVolumeEntry.showValueUnit(true);
		mMasterVolumeEntry.showValueGuides(false);
		mMasterVolumeEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mMusicEnabledEntry = new MenuToggleEntry(mScreenManager, this);
		mMusicEnabledEntry.registerClickListener(this, BUTTON_ENABLED_MUSIC);
		mMusicEnabledEntry.label("Music Enabled");
		mMusicEnabledEntry.isChecked(lAudioConfig.musicEnabled());
		mMusicEnabledEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mMusicVolumeEntry = new MenuSliderEntry(mScreenManager, this);
		mMusicVolumeEntry.label("Music Volume");
		mMusicVolumeEntry.registerClickListener(this, BUTTON_VOLUME_MUSIC);
		mMusicVolumeEntry.setBounds(0, 100, 5);
		final var lDisplayMusicValue = MathHelper.scaleToRange(lAudioConfig.musicVolume(), 0f, 1f, 0f, 100f);
		mMusicVolumeEntry.setValue((int) lDisplayMusicValue);
		mMusicVolumeEntry.buttonsEnabled(true);
		mMusicVolumeEntry.showValue(true);
		mMusicVolumeEntry.showValueUnit(true);
		mMusicVolumeEntry.showValueGuides(false);
		mMusicVolumeEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mSoundEnabledEntry = new MenuToggleEntry(mScreenManager, this);
		mSoundEnabledEntry.label("SoundFX Enabled");
		mSoundEnabledEntry.registerClickListener(this, BUTTON_ENABLED_SOUNDFX);
		mSoundEnabledEntry.isChecked(lAudioConfig.soundFxEnabled());
		mSoundEnabledEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mSoundVolumnEntry = new MenuSliderEntry(mScreenManager, this);
		mSoundVolumnEntry.label("SoundFX Volume");
		mSoundVolumnEntry.registerClickListener(this, BUTTON_VOLUME_SOUNDFX);
		mSoundVolumnEntry.setBounds(0, 100, 5);
		final var lDisplaySoundFxValue = MathHelper.scaleToRange(lAudioConfig.soundFxVolume(), 0f, 1f, 0f, 100f);
		mSoundVolumnEntry.setValue((int) lDisplaySoundFxValue);
		mSoundVolumnEntry.buttonsEnabled(true);
		mSoundVolumnEntry.showValue(true);
		mSoundVolumnEntry.showValueUnit(true);
		mSoundVolumnEntry.showValueGuides(false);
		mSoundVolumnEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		layout.addMenuEntry(mMasterEnabledEntry);
		layout.addMenuEntry(mMasterVolumeEntry);
		layout.addMenuEntry(MenuEntry.menuSeparator());

		layout.addMenuEntry(mMusicEnabledEntry);
		layout.addMenuEntry(mMusicVolumeEntry);
		layout.addMenuEntry(MenuEntry.menuSeparator());

		layout.addMenuEntry(mSoundEnabledEntry);
		layout.addMenuEntry(mSoundVolumnEntry);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void updateLayoutSize(LintfordCore core) {
		final int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mLayouts.get(i).layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
			mLayouts.get(i).marginLeft(50);
			mLayouts.get(i).marginRight(50);
		}

		super.updateLayoutSize(core);
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
	public void menuEntryOnClick(InputManager inputState, int entryUid) {
		super.menuEntryOnClick(inputState, entryUid);

		switch (entryUid) {
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