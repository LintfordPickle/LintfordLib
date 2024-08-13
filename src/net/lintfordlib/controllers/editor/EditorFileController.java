package net.lintfordlib.controllers.editor;

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

	public String sceneName() {
		return mSceneHeader.sceneName();
	}

	public void sceneName(String newFilename) {
		mSceneHeader.sceneName(newFilename);

		if (mCallbackListener != null) {
			mCallbackListener.onSceneNameChanged(newFilename);
		}
	}

	public String worldDirectory() {
		return mSceneHeader.baseScenesDirectory();
	}

	public void worldDirectory(String newWorldDirectory) {
		mSceneHeader.baseSceneDirectory(newWorldDirectory);

		if (mCallbackListener != null) {
			mCallbackListener.onFilepathChanged(newWorldDirectory);
		}
	}

	public String sceneFullFilepath() {
		return worldDirectory() + sceneName();
	}

	public void setCallbackListener(IEditorFileControllerListener listener) {
		mCallbackListener = listener;
	}

	public void removeCallbackListener() {
		mCallbackListener = null;
	}

	public SceneHeader sceneHeader() {
		return mSceneHeader;
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