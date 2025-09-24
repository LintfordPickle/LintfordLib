package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.audio.AudioManager;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.options.AudioConfig;
import net.lintfordlib.options.AudioSettings;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.dialogs.ConfirmationDialog;
import net.lintfordlib.screenmanager.entries.HorizontalEntryGroup;
import net.lintfordlib.screenmanager.entries.MenuLabelEntry;
import net.lintfordlib.screenmanager.entries.MenuSliderEntry;
import net.lintfordlib.screenmanager.entries.MenuToggleEntry;
import net.lintfordlib.screenmanager.layouts.BaseLayout;
import net.lintfordlib.screenmanager.layouts.ListLayout;

public class AudioOptionsScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_BACK_BUTTON = 0;
	public static final int BUTTON_APPLY_CHANGES = 1;

	public static final int BUTTON_ENABLED_MUSIC = 10;
	public static final int BUTTON_ENABLED_SOUNDFX = 11;
	public static final int BUTTON_VOLUME_MUSIC = 13;
	public static final int BUTTON_VOLUME_SOUNDFX = 14;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AudioManager mAudioManager;
	private AudioConfig mAudioConfig;

	private MenuToggleEntry mMusicEnabledEntry;
	private MenuSliderEntry mMusicVolumeEntry;
	private MenuToggleEntry mSoundEnabledEntry;
	private MenuSliderEntry mSoundVolumnEntry;

	private ListLayout mAudioList;
	private AudioSettings modifiedAudioConfig;
	private AudioSettings currentAudioConfig;
	private AudioSettings lastAudioConfig; // last known working config, in case we need to revert

	private ConfirmationDialog mConfirmationDialog;
	private MenuEntry mApplyButton;
	private ListLayout mConfirmChangesLayout;
	private MenuLabelEntry mChangesPendingWarning;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AudioOptionsScreen(ScreenManager screenManager) {
		super(screenManager, null);

		mAudioConfig = screenManager.core().config().audio();

		mAudioList = new ListLayout(this);
		mAudioList.paddingTop(10.f);
		mAudioList.paddingBottom(10.f);
		mAudioList.cropPaddingTop(0.f);
		mAudioList.cropPaddingBottom(0.f);
		mAudioList.setDrawBackground(true, ColorConstants.MenuPanelSecondaryColor);
		mAudioList.layoutFillType(FILLTYPE.FILL_CONTAINER);
		mAudioList.showTitle(true);
		mAudioList.title("Audio Options");

		createAudioSection(mAudioList);

		currentAudioConfig = mAudioConfig.settings();
		lastAudioConfig = new AudioSettings(currentAudioConfig);
		modifiedAudioConfig = new AudioSettings(lastAudioConfig);

		setUIFromAudioSettings(lastAudioConfig);

		mChangesPendingWarning = new MenuLabelEntry(screenManager, this);
		mChangesPendingWarning.label("Current changes have not yet been applied!");
		mChangesPendingWarning.textColor.setRGB(1.f, .12f, .17f);
		mChangesPendingWarning.enabled(true);
		mChangesPendingWarning.showWarnButton(true);
		mChangesPendingWarning.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mConfirmChangesLayout = new ListLayout(this);
		mConfirmChangesLayout.addMenuEntry(mChangesPendingWarning);
		mConfirmChangesLayout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		mConfirmChangesLayout.layoutWidth(LAYOUT_WIDTH.FULL);
		mConfirmChangesLayout.setDrawBackground(true, ColorConstants.MenuPanelSecondaryColor);

		/* Screen control buttons */
		final var lHorizontalButtonLayout = new HorizontalEntryGroup(screenManager, this);
		lHorizontalButtonLayout.horizontalFillType(FILLTYPE.THREEQUARTER_PARENT);

		final var lBackButton = new MenuEntry(screenManager, this, "Back");
		lBackButton.registerClickListener(this, BUTTON_BACK_BUTTON);
		mApplyButton = new MenuEntry(screenManager, this, "Apply");
		mApplyButton.registerClickListener(this, BUTTON_APPLY_CHANGES);
		mApplyButton.enabled(false);

		lHorizontalButtonLayout.addEntry(lBackButton);
		lHorizontalButtonLayout.addEntry(mApplyButton);

		final var lButtonLayout = new ListLayout(this);
		lButtonLayout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		lButtonLayout.addMenuEntry(lHorizontalButtonLayout);

		addLayout(mAudioList);
		addLayout(mConfirmChangesLayout);
		addLayout(lButtonLayout);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;

		mConfirmChangesLayout.visible(false);
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
		final var lAudioConfig = screenManager.core().config().audio();

		// As we know the game canvas size
		final float lDesiredEntryWidth = 56.f;
		final float lDesiredEntryHeight = 25.f;

		mMusicEnabledEntry = new MenuToggleEntry(screenManager, this);
		mMusicEnabledEntry.registerClickListener(this, BUTTON_ENABLED_MUSIC);
		mMusicEnabledEntry.label("Music Enabled");
		mMusicEnabledEntry.isChecked(lAudioConfig.settings().musicEnabled());
		mMusicEnabledEntry.desiredWidth(lDesiredEntryWidth);
		mMusicEnabledEntry.desiredHeight(lDesiredEntryHeight);
		mMusicEnabledEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mMusicVolumeEntry = new MenuSliderEntry(screenManager, this);
		mMusicVolumeEntry.label("Music Volume");
		mMusicVolumeEntry.registerClickListener(this, BUTTON_VOLUME_MUSIC);
		mMusicVolumeEntry.setBounds(0, 100, 5);

		mMusicVolumeEntry.buttonsEnabled(true);
		mMusicVolumeEntry.showValue(true);
		mMusicVolumeEntry.showValueUnit(true);
		mMusicVolumeEntry.showValueGuides(false);
		mMusicVolumeEntry.desiredWidth(lDesiredEntryWidth);
		mMusicVolumeEntry.desiredHeight(lDesiredEntryHeight);
		mMusicVolumeEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mSoundEnabledEntry = new MenuToggleEntry(screenManager, this);
		mSoundEnabledEntry.label("SoundFX Enabled");
		mSoundEnabledEntry.registerClickListener(this, BUTTON_ENABLED_SOUNDFX);

		mSoundEnabledEntry.desiredWidth(lDesiredEntryWidth);
		mSoundEnabledEntry.desiredHeight(lDesiredEntryHeight);
		mSoundEnabledEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mSoundVolumnEntry = new MenuSliderEntry(screenManager, this);
		mSoundVolumnEntry.label("SoundFX Volume");
		mSoundVolumnEntry.registerClickListener(this, BUTTON_VOLUME_SOUNDFX);
		mSoundVolumnEntry.setBounds(0, 100, 5);
		mSoundVolumnEntry.buttonsEnabled(true);
		mSoundVolumnEntry.showValue(true);
		mSoundVolumnEntry.showValueUnit(true);
		mSoundVolumnEntry.showValueGuides(false);
		mSoundVolumnEntry.desiredWidth(lDesiredEntryWidth);
		mSoundVolumnEntry.desiredHeight(lDesiredEntryHeight);
		mSoundVolumnEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		layout.addMenuEntry(MenuEntry.menuSeparator());

		layout.addMenuEntry(mMusicEnabledEntry);
		layout.addMenuEntry(mMusicVolumeEntry);
		layout.addMenuEntry(MenuEntry.menuSeparator());

		layout.addMenuEntry(mSoundEnabledEntry);
		layout.addMenuEntry(mSoundVolumnEntry);

		setUIFromAudioSettings(lAudioConfig.settings());
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void setUIFromAudioSettings(AudioSettings audioSettings) {
		mMusicEnabledEntry.isChecked(audioSettings.musicEnabled());
		mSoundEnabledEntry.isChecked(audioSettings.sfxEnabled());

		final var musicVolume = MathHelper.scaleToRange(audioSettings.musicVolume(), 0f, 1f, 0f, 100f);
		mMusicVolumeEntry.setValue((int) musicVolume);

		final var sfxVolume = MathHelper.scaleToRange(audioSettings.sfxVolume(), 0f, 1f, 0f, 100f);
		mSoundVolumnEntry.setValue((int) sfxVolume);
	}

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
		case BUTTON_BACK_BUTTON:
			if (modifiedAudioConfig.isDifferent(lastAudioConfig)) {
				mConfirmationDialog = new ConfirmationDialog(screenManager, this, "You have some changes which have not been saved, do you want to go back and save?");
				mConfirmationDialog.setDialogIcon(mCoreSpritesheet, CoreTextureNames.TEXTURE_ICON_WARNING);
				mConfirmationDialog.dialogTitle("Save Changes?");
				mConfirmationDialog.confirmEntry().entryText("Save");
				mConfirmationDialog.confirmEntry().registerClickListener(this, ConfirmationDialog.BUTTON_CONFIRM_YES);
				mConfirmationDialog.resetCoolDownTimer(300);

				mConfirmationDialog.cancelEntry().entryText("Discard");
				mConfirmationDialog.cancelEntry().registerClickListener(this, ConfirmationDialog.BUTTON_CONFIRM_NO);

				screenManager.addScreen(mConfirmationDialog);

			} else {
				exitScreen();

			}

			break;

		case BUTTON_APPLY_CHANGES:
			mAudioManager.audioConfig().saveConfig();

			exitScreen();
			break;

		case ConfirmationDialog.BUTTON_CONFIRM_YES: // exit without saving
			if (mConfirmationDialog != null)
				screenManager.removeScreen(mConfirmationDialog);

			exitScreen();
			break;

		case ConfirmationDialog.BUTTON_CONFIRM_NO: // go back and dont exit yet
			if (mConfirmationDialog != null)
				screenManager.removeScreen(mConfirmationDialog);

			break;
		}
	}

	// --------------------------------------
	// Listeners
	// --------------------------------------

	@Override
	public void menuEntryOnClick(InputManager inputState, int entryUid) {
		super.menuEntryOnClick(inputState, entryUid);

		// update the modified and the audio manager for instance effect.
		// (we only save or revert then we leave the screen).

		final var audioSettings = mAudioManager.audioConfig().settings();

		switch (entryUid) {

		case BUTTON_ENABLED_MUSIC:
			audioSettings.musicEnabled(mMusicEnabledEntry.isChecked());
			modifiedAudioConfig.musicEnabled(mMusicEnabledEntry.isChecked());
			mAudioManager.updateSettings();
			break;

		case BUTTON_ENABLED_SOUNDFX:
			audioSettings.sfxEnabled(mSoundEnabledEntry.isChecked());
			modifiedAudioConfig.sfxEnabled(mSoundEnabledEntry.isChecked());
			mAudioManager.updateSettings();
			break;

		case BUTTON_VOLUME_MUSIC:
			final var lScaledMusicVolumeRange = MathHelper.scaleToRange(mMusicVolumeEntry.getCurrentValue(), 0f, 100f, 0f, 1f);
			audioSettings.musicVolume(lScaledMusicVolumeRange);
			modifiedAudioConfig.musicVolume(lScaledMusicVolumeRange);
			mAudioManager.updateSettings();
			break;

		case BUTTON_VOLUME_SOUNDFX:
			final var lScaledSoundFxVolumeRange = MathHelper.scaleToRange(mSoundVolumnEntry.getCurrentValue(), 0f, 100f, 0f, 1f);
			audioSettings.sfxVolume(lScaledSoundFxVolumeRange);
			modifiedAudioConfig.sfxVolume(lScaledSoundFxVolumeRange);
			mAudioManager.updateSettings();
			break;

		case ConfirmationDialog.BUTTON_CONFIRM_NO:
			if (mConfirmationDialog != null)
				screenManager.removeScreen(mConfirmationDialog);

			audioSettings.copy(lastAudioConfig);
			mAudioManager.updateSettings();
			super.exitScreen();
			return;

		case ConfirmationDialog.BUTTON_CONFIRM_YES:
			if (mConfirmationDialog != null)
				screenManager.removeScreen(mConfirmationDialog);

			mAudioManager.audioConfig().saveConfig();
			mConfirmChangesLayout.visible(false);
			mApplyButton.enabled(false);
			super.exitScreen();
			return;
		}

		// To check if there are uncommited changes, compare current/modified to last
		mConfirmChangesLayout.visible(modifiedAudioConfig.isDifferent(lastAudioConfig));
		mApplyButton.enabled(modifiedAudioConfig.isDifferent(lastAudioConfig));

	}

}