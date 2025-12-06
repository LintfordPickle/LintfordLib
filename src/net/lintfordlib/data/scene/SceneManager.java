package net.lintfordlib.data.scene;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.data.BaseDataManager;
import net.lintfordlib.data.DataManager;
import net.lintfordlib.options.ResourcePathsConfig;

public class SceneManager extends BaseDataManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String DATA_MANAGER_NAME = "Scene Manager";

	public static final String SCENE_MAIN_DIRECTORY = "SCENE_DIRECTORY";
	public static final String SCENE_CUSTOM_DIRECTORY = "SCENE_CUSTOM_DIRECTORY";

	public static final String DEFAULT_BASE_SCENE_DIRECTORY = "res/scenes/";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient String mSceneDirectory;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns the scene directory set in this SceneManager.
	 */
	public String sceneDirectory() {
		return mSceneDirectory;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneManager(DataManager dataManager, ResourcePathsConfig pathsConfig, int entityGroupUid) {
		super(dataManager, DATA_MANAGER_NAME, entityGroupUid);

		final var baseScenesPath = pathsConfig.getKeyValue(SCENE_MAIN_DIRECTORY, DEFAULT_BASE_SCENE_DIRECTORY);
		setBaseScenesPath(baseScenesPath);
	}

	public SceneManager(DataManager dataManager, String baseSceneFilePath, int entityGroupUid) {
		super(dataManager, DATA_MANAGER_NAME, entityGroupUid);

		setBaseScenesPath(baseSceneFilePath);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void setBaseScenesPath(String newBaseSceneDirectory) {
		if (newBaseSceneDirectory == null || newBaseSceneDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set scene directory name to null or empty. Using " + DEFAULT_BASE_SCENE_DIRECTORY + " instead.");
			newBaseSceneDirectory = DEFAULT_BASE_SCENE_DIRECTORY;
		}

		mSceneDirectory = newBaseSceneDirectory;
	}

	public SceneHeader createSceneHeader(String sceneName) {
		return createSceneHeader(sceneName, mSceneDirectory);
	}

	public SceneHeader createSceneHeader(String sceneName, String sceneDirectory) {
		return SceneHeader.createNewSceneHeader(sceneName, sceneDirectory);
	}

	public SceneHeader loadSceneHeader(String sceneName) {
		return loadSceneHeader(sceneName, mSceneDirectory);
	}

	public SceneHeader loadSceneHeader(String sceneName, String sceneDirectory) {
		return SceneHeaderIoService.loadSceneHeaderFileFromFilepath("cscene1", "res/scenes/custom");
	}

	public boolean renameScene(String sceneName, String newSceneName) {
		// TODO: Implement the scene rename (within the file and the file/folder names).
		return false;
	}

}
