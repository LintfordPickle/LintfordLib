package net.lintfordlib.screenmanager;

import net.lintfordlib.ConstantsEditor;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.data.scene.BaseSceneSettings;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.screenmanager.entries.MenuDropDownEntry;
import net.lintfordlib.screenmanager.layouts.ListLayout;

public abstract class BaseEditorSceneSelectionScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String TITLE = "Editor Scene Selection";

	private static final int BUTTON_LOAD_ID = 0;
	private static final int BUTTON_CREATE_NEW_ID = 1;
	private static final int BUTTON_BACK_ID = 2;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private BaseSceneSettings mSceneSettings;
	private MenuDropDownEntry<SceneHeader> mSceneFilenameEntries;

	protected String mTextureHudLocation = "res/textures/textureHud.png";
	protected String mSpritesheetHudLocation = "res/spritesheets/spritesheetHud.json";

	// ---------------------------------------------
	// Properities
	// ---------------------------------------------

	public BaseSceneSettings sceneSettings() {
		return mSceneSettings;
	}

	public void setTextureHudFilepath(String newFilepath) {
		mTextureHudLocation = newFilepath;
	}

	public void setSpritesheetHudFilepath(String newFilepath) {
		mSpritesheetHudLocation = newFilepath;
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public BaseEditorSceneSelectionScreen(ScreenManager screenManager, BaseSceneSettings sceneSettings, boolean enableBackButton) {
		super(screenManager, TITLE);

		mSceneSettings = sceneSettings;
		final var lListLayout = new ListLayout(this);

		mSceneFilenameEntries = new MenuDropDownEntry<SceneHeader>(screenManager, this);
		mSceneFilenameEntries.allowDuplicateNames(true);
		populateDropDownListWithTrackFilenames(mSceneFilenameEntries);

		final var lCreateNewTrack = new MenuEntry(screenManager, this, "Create New");
		lCreateNewTrack.registerClickListener(this, BUTTON_CREATE_NEW_ID);

		final var lLoadTrack = new MenuEntry(screenManager, this, "Load");
		lLoadTrack.registerClickListener(this, BUTTON_LOAD_ID);

		lListLayout.addMenuEntry(lCreateNewTrack);

		lListLayout.addMenuEntry(MenuEntry.menuSeparator());
		lListLayout.addMenuEntry(mSceneFilenameEntries);
		lListLayout.addMenuEntry(lLoadTrack);

		lListLayout.addMenuEntry(MenuEntry.menuSeparator());

		if (enableBackButton) {
			final var lBackButton = new MenuEntry(screenManager, this, "Back");
			lBackButton.registerClickListener(this, BUTTON_BACK_ID);
			lListLayout.addMenuEntry(lBackButton);
		}

		addLayout(lListLayout);

		if (enableBackButton == false)
			mESCBackEnabled = false; // cannot esc out

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		resourceManager.textureManager().loadTexture("TEXTURE_HUD", mTextureHudLocation, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		var t = resourceManager.textureManager().getTexture("TEXTURE_HUD", ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		if (t == null || t.getTextureID() == resourceManager.textureManager().textureNotFound().getTextureID())
			throw new RuntimeException("The LintfordLibEditor project requires you to include a texture named 'TEXTURE_HUD'. 'TEXTURE_HUD' not found under " + mTextureHudLocation);

		resourceManager.spriteSheetManager().loadSpriteSheet("SPRITESHEET_HUD", mSpritesheetHudLocation, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		var s = resourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_HUD", ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		if (s == null)
			throw new RuntimeException("The LintfordLibEditor project requires you to include a spritesheet defintion named 'SPRITESHEET_HUD'. 'SPRITESHEET_HUD' not found under " + mSpritesheetHudLocation);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();
	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_CREATE_NEW_ID:
			onCreateNewScene();
			break;

		case BUTTON_LOAD_ID:
			if (mSceneFilenameEntries.selectedItem() != null) {
				final var lGameFileHeader = mSceneFilenameEntries.selectedItem().value;
				OnLoadScene(lGameFileHeader);
			}

			break;

		case BUTTON_BACK_ID:
			exitScreen();
			break;
		}
	}

	protected abstract void onCreateNewScene();

	protected abstract void OnLoadScene(SceneHeader sceneHeader);

	private void populateDropDownListWithTrackFilenames(MenuDropDownEntry<SceneHeader> pEntry) {
		final var lListOfSceneHeaderFilesInDirectory = mSceneSettings.getListOfHeaderFilesInScenesDirectory();

		final int lSceneHeaderCount = lListOfSceneHeaderFilesInDirectory.size();
		for (int i = 0; i < lSceneHeaderCount; i++) {
			final var lWorldFile = lListOfSceneHeaderFilesInDirectory.get(i);

			final var lSceneHeader = SceneHeader.loadSceneHeaderFileFromFilepath(lWorldFile.getAbsoluteFile().getAbsolutePath());
			lSceneHeader.initialize(lWorldFile.getParentFile().getAbsolutePath(), mSceneSettings);

			final var lNewEntry = pEntry.new MenuEnumEntryItem(lSceneHeader.sceneName(), lSceneHeader);
			pEntry.addItem(lNewEntry);
		}
	}
}
