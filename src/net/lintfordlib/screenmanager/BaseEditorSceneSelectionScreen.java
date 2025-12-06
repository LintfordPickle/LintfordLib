package net.lintfordlib.screenmanager;

import net.lintfordlib.ConstantsEditor;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.options.ResourcePathsConfig;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.entries.MenuInputEntry;
import net.lintfordlib.screenmanager.entries.MenuListBox;
import net.lintfordlib.screenmanager.entries.MenuListBoxItem;
import net.lintfordlib.screenmanager.layouts.BaseLayout;
import net.lintfordlib.screenmanager.layouts.ListLayout;

public abstract class BaseEditorSceneSelectionScreen<T extends SceneHeader> extends MenuScreen implements IListBoxItemSelected, IListBoxItemDoubleClick {

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

	protected ResourcePathsConfig mResourcePathsConfig;

	protected MenuListBox mSceneFilenameEntries;
	protected MenuInputEntry mSceneNameInput;
	protected MenuEntry mCreateNewTrack;
	protected MenuEntry mLoadTrack;
	protected MenuEntry mBackButton;

	protected String mTextureHudLocation = "res/textures/textureHud.png";
	protected String mSpritesheetHudLocation = "res/spritesheets/spritesheetHud.json";

	// ---------------------------------------------
	// Properities
	// ---------------------------------------------

	public String sceneNameInput() {
		return mSceneNameInput.inputString();
	}

	public ResourcePathsConfig gameResourcePaths() {
		return mResourcePathsConfig;
	}

	public void setTextureHudFilepath(String newFilepath) {
		mTextureHudLocation = newFilepath;
	}

	public void setSpritesheetHudFilepath(String newFilepath) {
		mSpritesheetHudLocation = newFilepath;
	}

	protected abstract String getInitialScenesDirectory();

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	protected BaseEditorSceneSelectionScreen(ScreenManager screenManager, ResourcePathsConfig pathsConfig, boolean enableBackButton) {
		super(screenManager, TITLE);

		mResourcePathsConfig = pathsConfig;
		final var listLayout1 = new ListLayout(this);
		listLayout1.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		final var listLayout2 = new ListLayout(this);

		mSceneFilenameEntries = new MenuListBox(screenManager, this);
		mSceneFilenameEntries.setItemSelectedListener(this);
		mSceneFilenameEntries.setItemDoubleClickListener(this);

		mCreateNewTrack = new MenuEntry(screenManager, this, "Create New");
		mCreateNewTrack.registerClickListener(this, BUTTON_CREATE_NEW_ID);

		mSceneNameInput = new MenuInputEntry(screenManager, this, "Name");
		mSceneNameInput.singleLine(true);

		mLoadTrack = new MenuEntry(screenManager, this, "Load");
		mLoadTrack.registerClickListener(this, BUTTON_LOAD_ID);

		listLayout1.addMenuEntry(mSceneNameInput);
		listLayout1.addMenuEntry(mCreateNewTrack);

		listLayout2.addMenuEntry(mSceneFilenameEntries);
		listLayout2.addMenuEntry(mLoadTrack);

		listLayout2.addMenuEntry(MenuEntry.menuSeparator());

		if (enableBackButton) {
			mBackButton = new MenuEntry(screenManager, this, "Back");
			mBackButton.registerClickListener(this, BUTTON_BACK_ID);
			listLayout2.addMenuEntry(mBackButton);
		} else {
			mESCBackEnabled = false;
		}

		addLayout(listLayout1);
		addLayout(listLayout2);

		finalizeScreenLayout(listLayout1);

	}

	protected void finalizeScreenLayout(BaseLayout layout) {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		populateDropDownListWithSceneFilenames(mSceneFilenameEntries, getInitialScenesDirectory());
	}

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
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_CREATE_NEW_ID:
			onCreateNewScene();
			break;

		case BUTTON_LOAD_ID:
			final var sceneItem = mSceneFilenameEntries.getSelectedItem();
			if (sceneItem != null) {
				onLoadScene(sceneItem);
			}

			break;

		case BUTTON_BACK_ID:
			exitScreen();
			break;

		default:
			// ignore
			break;
		}
	}

	protected abstract void onCreateNewScene();

	protected abstract void onLoadScene(MenuListBoxItem selectedItem);

	protected abstract void populateDropDownListWithSceneFilenames(MenuListBox sceneList, String scenesDirectory);
}
