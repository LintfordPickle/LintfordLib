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
	private static final int ENTRY_DIRECTORY_NAME = 51;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorFileController mEditorFileController;

	private UiInputText mSceneName;
	private UiInputText mWorldDirectoryName;

	private UiHorizontalEntryGroup mHorizontalGroup;

	private UiLabel mSceneNameLabel;
	private UiLabel mDirectoryNameLabel;
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
		super(parentWindow, "File Info Panel", entityGroupUid);

		mShowActiveLayerButton = false;
		mShowShowLayerButton = false;

		mRenderPanelTitle = true;
		mPanelTitle = "File Info";

		mSceneNameLabel = new UiLabel("Scene Name");
		mDirectoryNameLabel = new UiLabel("Directory Name");

		mSceneName = new UiInputText();
		mSceneName.maxnumInputCharacters(20);
		mSceneName.setUiWidgetListener(this, ENTRY_SCENE_NAME);
		mSceneName.emptyString("<scene name>");

		mWorldDirectoryName = new UiInputText();
		mWorldDirectoryName.maxnumInputCharacters(200);
		mWorldDirectoryName.emptyString("<directory name>");
		mWorldDirectoryName.setUiWidgetListener(this, ENTRY_DIRECTORY_NAME);

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
		addWidget(mDirectoryNameLabel);
		addWidget(mWorldDirectoryName);
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

		mSceneName.inputString(mEditorFileController.sceneName());
		mWorldDirectoryName.inputString(mEditorFileController.sceneDirectory());
	}

	// --------------------------------------

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

		case ENTRY_DIRECTORY_NAME:
			mEditorFileController.worldDirectory(mWorldDirectoryName.inputString().toString());
			break;

		default:
			// ignore
			break;
		}
	}
}