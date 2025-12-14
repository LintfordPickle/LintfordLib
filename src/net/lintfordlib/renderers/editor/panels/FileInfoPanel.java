package net.lintfordlib.renderers.editor.panels;

import net.lintfordlib.controllers.editor.EditorFileController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiHorizontalEntryGroup;
import net.lintfordlib.renderers.windows.components.UiInputText;
import net.lintfordlib.renderers.windows.components.UiLabel;

public class FileInfoPanel extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int BUTTON_NEW = 10;
	private static final int BUTTON_SAVE = 11;
	private static final int BUTTON_LOAD = 12;

	private static final int BUTTON_VALIDATE_PATH = 15;
	private static final int BUTTON_CREATE_DIRS = 16;

	private static final int ENTRY_SCENE_NAME = 50;
	private static final int ENTRY_FILENAME = 51;
	private static final int ENTRY_DIRECTORY = 52;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorFileController mEditorFileController;

	private UiInputText mSceneName;
	private UiInputText mLevelFileName;
	private UiInputText mLevelDirectoryName;

	private UiHorizontalEntryGroup mHorizontalGroup;

	private UiLabel mSceneNameLabel;
	private UiLabel mFileInfoLabel;
	private UiButton mNewSceneButton;
	private UiButton mSaveSceneButton;
	private UiButton mLoadSceneButton;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FileInfoPanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "File Info", entityGroupUid);

		mShowActiveLayerButton = false;
		mShowShowLayerButton = false;

		mRenderPanelTitle = true;

		mSceneNameLabel = new UiLabel("Scene Name");
		mFileInfoLabel = new UiLabel("File:");

		mSceneName = new UiInputText();
		mSceneName.maxnumInputCharacters(20);
		mSceneName.setUiWidgetListener(this, ENTRY_SCENE_NAME);
		mSceneName.emptyString("<scene name>");

		mLevelFileName = new UiInputText();
		mLevelFileName.maxnumInputCharacters(200);
		mLevelFileName.emptyString("<filename>");
		mLevelFileName.setUiWidgetListener(this, ENTRY_FILENAME);

		mLevelDirectoryName = new UiInputText();
		mLevelDirectoryName.maxnumInputCharacters(200);
		mLevelDirectoryName.emptyString("<directory name>");
		mLevelDirectoryName.setUiWidgetListener(this, ENTRY_DIRECTORY);

		mNewSceneButton = new UiButton();
		mNewSceneButton.buttonLabel("New");
		mNewSceneButton.setUiWidgetListener(this, BUTTON_NEW);

		mSaveSceneButton = new UiButton();
		mSaveSceneButton.buttonLabel("Save");
		mSaveSceneButton.setUiWidgetListener(this, BUTTON_SAVE);

		mLoadSceneButton = new UiButton();
		mLoadSceneButton.buttonLabel("Load");
		mLoadSceneButton.setUiWidgetListener(this, BUTTON_LOAD);

		mHorizontalGroup = new UiHorizontalEntryGroup();
		mHorizontalGroup.widgets().add(mNewSceneButton);
		mHorizontalGroup.widgets().add(mSaveSceneButton);

		addWidget(mSceneNameLabel);
		addWidget(mSceneName);
		addWidget(mFileInfoLabel);
		addWidget(mLevelFileName);
		addWidget(mLevelDirectoryName);
		addWidget(mHorizontalGroup);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();

		mEditorFileController = (EditorFileController) lControllerManager.getControllerByNameRequired(EditorFileController.CONTROLLER_NAME, mEntityGroupUid);

		updateUiElements();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateUiElements() {
		mSceneName.inputString(mEditorFileController.sceneName());
		mLevelFileName.inputString(mEditorFileController.sceneFileName());
		mLevelDirectoryName.inputString(mEditorFileController.sceneDirectory());
	}

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_NEW:
			Debug.debugManager().logger().i(getClass().getSimpleName(), "New Track Clicked");
			break;

		case BUTTON_SAVE:
			mEditorFileController.saveScene();
			break;

		case BUTTON_LOAD:
			break;

		case BUTTON_CREATE_DIRS:
			break;

		case BUTTON_VALIDATE_PATH:
			break;

		default:
			// ignore
			break;
		}
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case ENTRY_SCENE_NAME:
			mEditorFileController.sceneName(mSceneName.inputString().toString());
			break;

		case ENTRY_FILENAME:
			mEditorFileController.changeSceneFileName(mLevelFileName.inputString().toString());

			updateUiElements();

			break;

		case ENTRY_DIRECTORY:
			mEditorFileController.changeSceneDirectory(mLevelDirectoryName.inputString().toString());

			updateUiElements();

			break;

		default:
			// ignore
			break;
		}
	}
}