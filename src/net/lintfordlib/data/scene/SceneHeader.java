package net.lintfordlib.data.scene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.debug.Debug;

public class SceneHeader implements Serializable {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 1301516618574644330L;

	public static final String DATA_FILENAME = "scene.data";
	public static final String HEADER_FILENAME = "scene.hdr";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	@SerializedName(value = "SceneName")
	private String mSceneName;

	@SerializedName(value = "SceneDataFilePath")
	private String mSceneDataFilePath;

	private transient boolean mDirtyChanges;
	private transient String mSceneHeaderFilePath;

	@SerializedName(value = "Values")
	private Map<String, String> mKeyValues = new HashMap<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean hasDirtyChanges() {
		return mDirtyChanges;
	}

	public void resetDirty() {
		mDirtyChanges = false;
	}

	public String sceneName() {
		return mSceneName;
	}

	public void sceneName(String sceneName) {
		if (sceneName == null || sceneName.length() == 0)
			return;

		if (mSceneName.equals(sceneName)) {
			return;
		}

		mSceneName = sceneName;
		mDirtyChanges = true;
	}

	public String sceneHeaderFilePath() {
		return mSceneHeaderFilePath;
	}

	public void sceneHeaderFilePath(String sceneHeaderFilePath) {
		mSceneHeaderFilePath = sceneHeaderFilePath;
	}

	public String sceneDataFilePath() {
		return mSceneDataFilePath;
	}

	public void sceneDataFilePath(String sceneDataFilePath) {
		mSceneDataFilePath = sceneDataFilePath;
	}

	public boolean isSceneValid() {
		final var lHeaderExists = headerExistsOnDisk();
		if (!lHeaderExists)
			return false;

		return dataExistsOnDisk();
	}

	public boolean headerExistsOnDisk() {
		final var headerFilePath = sceneHeaderFilePath();
		if (headerFilePath == null || headerFilePath.length() == 0)
			return false;

		final var headerFile = new File(headerFilePath);
		return headerFile.exists();
	}

	public boolean dataExistsOnDisk() {
		final var dataFilePath = sceneDataFilePath();
		if (dataFilePath == null || dataFilePath.length() == 0)
			return false;

		final var dataFile = new File(dataFilePath);
		return dataFile.exists();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SceneHeader() {

	}

	public SceneHeader(String sceneName) {
		this();

		mSceneName = sceneName;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static SceneHeader createNewSceneHeader(String sceneName, String scenesDirectoryFilePath) {
		final var newScene = new SceneHeader(sceneName);

		final var sceneHeaderFilePath = Paths.get(scenesDirectoryFilePath, sceneName, HEADER_FILENAME).toString();
		newScene.sceneHeaderFilePath(sceneHeaderFilePath);

		final var sceneDataFilePath = Paths.get(scenesDirectoryFilePath, sceneName, DATA_FILENAME).toString();
		newScene.sceneDataFilePath(sceneDataFilePath);

		return newScene;

	}

	public void saveSceneHeaderFile() {
		final var sceneHeaderFile = sceneHeaderFilePath().toString();

		if (sceneHeaderFile == null || sceneHeaderFile.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to save the scene header into " + sceneHeaderFile);
			return;
		}

		final var saveDirectory = new File(sceneHeaderFile);
		final var parentDirectory = saveDirectory.getParentFile();

		if (!parentDirectory.exists()) {
			if (!parentDirectory.mkdirs()) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to save the scene data into " + sceneHeaderFile);
				return;
			}
		}

		try (final var writer = new FileWriter(sceneHeaderFile)) {

			final var gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(this, writer);

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Error serializing SceneHeader file.");
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}
	}
}
