package net.lintfordlib.controllers.editor;

import java.nio.file.Paths;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.data.scene.SceneHeader;

public class EditorFileController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Editor File Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneHeader mSceneHeader;

	private IEditorFileControllerListener mCallbackListener;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public SceneHeader sceneHeader() {
		return mSceneHeader;
	}

	public String sceneName() {
		return mSceneHeader.sceneName();
	}

	public String sceneFileName() {
		return mSceneHeader.sceneFolderName();
	}

	public String sceneDirectory() {
		return mSceneHeader.sceneParentDirectory();
	}

	public String sceneFullFilepath() {
		return Paths.get(sceneDirectory(), sceneName()).toString();
	}

	public void setCallbackListener(IEditorFileControllerListener listener) {
		mCallbackListener = listener;
	}

	public void removeCallbackListener() {
		mCallbackListener = null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorFileController(ControllerManager controllerManager, SceneHeader sceneHeader, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mSceneHeader = sceneHeader;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void sceneName(String newFilename) {
		// TODO: implement the scene name change in the EditorFileController (SceneManager can do the rename)
		// mSceneHeader.sceneName(newFilename);

		if (mCallbackListener != null) {
			mCallbackListener.onSceneNameChanged(newFilename);
		}
	}

	public void changeSceneDirectory(String newDirectory) {

		// TODO:

		if (mCallbackListener != null) {
			mCallbackListener.onSceneDirectoryChanged(newDirectory);
		}

	}

	public void changeSceneFileName(String newFileName) {

		// TODO:

		if (mCallbackListener != null) {
			mCallbackListener.onSceneFileNameChanged(newFileName);
		}

	}

	public boolean saveScene() {
		if (mCallbackListener != null) {
			mCallbackListener.onSave();
		}

		return true;
	}

	public void loadScene() {
		if (mCallbackListener != null) {
			mCallbackListener.onLoad();
		}

	}

}