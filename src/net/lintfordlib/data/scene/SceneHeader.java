package net.lintfordlib.data.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.options.ResourcePathsConfig;

public class SceneHeader implements Serializable {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 1301516618574644330L;

	// these must be next to each other in the scene path
	public static final String DATA_FILENAME = "scene.data";
	public static final String HEADER_FILENAME = "scene.hdr";

	public static final String SCENE_NAME = "SCENE_NAME";
	public static final String SCENE_DESCRIPTION = "SCENE_DESCRIPTION";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	@SerializedName(value = "SceneName")
	protected String mSceneName;

	@SerializedName(value = "Values")
	protected Map<String, String> mKeyValues = new HashMap<>();

	protected transient String _cmpName;

	private transient String mSceneFolderName; // diddy_ko
	private transient String mSceneParentDirectory; // res/scenes/custom/

	private transient String mSceneHeaderFilePath;
	private transient String mSceneDataFilePath;

	private transient boolean mDirtyChanges;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public String sceneName() {
		if (mSceneName != null && !mSceneName.isEmpty())
			return mSceneName;
		if (mKeyValues != null && mKeyValues.containsKey(SCENE_NAME))
			return mKeyValues.get(SCENE_NAME);
		return mSceneFolderName;
	}

	public void sceneName(String sceneName) {
		if (sceneName == null || sceneName.length() == 0)
			return;

		mSceneName = sceneName;
		if (mKeyValues == null)
			mKeyValues = new HashMap<>();
		mKeyValues.put(SCENE_NAME, sceneName);
		_cmpName = sceneName.toUpperCase();

		mDirtyChanges = true;
	}

	/** Returns the name of this scene in upper case, so it can be easily compared to other entities. */
	public String cmpName() {
		if (_cmpName == null && sceneName() != null) {
			_cmpName = sceneName().toUpperCase();
		}
		return _cmpName != null ? _cmpName : "";
	}

	public String sceneFolderName() {
		return mSceneFolderName;
	}

	public void sceneFolderName(String folderName) {
		mSceneFolderName = folderName;
	}

	public String sceneParentDirectory() {
		return mSceneParentDirectory;
	}

	public void sceneParentDirectory(String parentDirectoryName) {
		mSceneParentDirectory = parentDirectoryName;
	}

	public String sceneHeaderFilePath() {
		return mSceneHeaderFilePath;
	}

	public void sceneHeaderFilePath(String headerFilePath) {
		mSceneHeaderFilePath = headerFilePath;
	}

	public String sceneDataFilePath() {
		return mSceneDataFilePath;
	}

	public void sceneDataFilePath(String dataFilePath) {
		mSceneDataFilePath = dataFilePath;
	}

	public String sceneDirectory() {
		if (mSceneHeaderFilePath != null && !mSceneHeaderFilePath.isEmpty()) {
			final var parent = new File(mSceneHeaderFilePath).getParent();
			if (parent != null) {
				return parent.endsWith(File.separator) ? parent : parent + File.separator;
			}
		}
		if (mSceneParentDirectory != null && mSceneFolderName != null) {
			final var dir = Paths.get(mSceneParentDirectory, mSceneFolderName).toString();
			return dir.endsWith(File.separator) ? dir : dir + File.separator;
		}
		if (mSceneParentDirectory != null) {
			return mSceneParentDirectory.endsWith(File.separator) ? mSceneParentDirectory : mSceneParentDirectory + File.separator;
		}
		return "";
	}

	public String sceneDataFilepath() {
		return sceneDataFilePath();
	}

	public String sceneHeaderFilepath() {
		return sceneHeaderFilePath();
	}

	public boolean isHeaderValid() {
		return isSceneValid();
	}

	public void initialize(String sceneDirectory, ResourcePathsConfig settings) {
		final var file = new File(sceneDirectory);
		final var folderName = file.getName();
		final var parentDir = file.getParent() != null ? file.getParent() : "";

		sceneFolderName(folderName);
		sceneParentDirectory(parentDir);

		sceneHeaderFilePath(Paths.get(sceneDirectory, HEADER_FILENAME).toString());
		sceneDataFilePath(Paths.get(sceneDirectory, DATA_FILENAME).toString());

		if (sceneName() == null || sceneName().isEmpty()) {
			sceneName(folderName);
		}
		_cmpName = sceneName() != null ? sceneName().toUpperCase() : "";
	}

	public boolean isSceneValid() {
		final var lHeaderExists = headerExistsOnDisk();
		if (!lHeaderExists)
			return false;

		return dataExistsOnDisk();
	}

	public boolean headerExistsOnDisk() {
		if (mSceneHeaderFilePath == null || mSceneHeaderFilePath.isEmpty())
			return false;

		final var headerFile = new File(mSceneHeaderFilePath);
		return headerFile.exists();
	}

	public boolean dataExistsOnDisk() {
		if (mSceneDataFilePath == null || mSceneDataFilePath.isEmpty())
			return false;

		final var dataFile = new File(mSceneDataFilePath);
		return dataFile.exists();
	}

	public boolean hasDirtyChanges() {
		return mDirtyChanges;
	}

	public void resetDirty() {
		mDirtyChanges = false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SceneHeader() {

	}

	public SceneHeader(String sceneName) {
		this();

		sceneName(sceneName);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static boolean sceneExists(String sceneName, String scenesDirectoryFilePath) {
		final var headerFilePath = Paths.get(scenesDirectoryFilePath, sceneName, HEADER_FILENAME).toString();
		if (headerFilePath == null || headerFilePath.length() == 0)
			return false;

		final var headerFile = new File(headerFilePath);
		return headerFile.exists();
	}

	public static SceneHeader createNewSceneHeader(String sceneFolderName, String sceneParentDirectory) {
		final var newScene = new SceneHeader(sceneFolderName);

		// set these so we can easily save the scene files later
		newScene.sceneFolderName(sceneFolderName);
		newScene.sceneParentDirectory(sceneParentDirectory);

		newScene.sceneHeaderFilePath(Paths.get(sceneParentDirectory, sceneFolderName, HEADER_FILENAME).toString());
		newScene.sceneDataFilePath(Paths.get(sceneParentDirectory, sceneFolderName, DATA_FILENAME).toString());

		newScene.saveSceneHeaderFile();
		return newScene;
	}

	// Load

	public static SceneHeader loadSceneHeaderFileFromFilepath(String sceneFolderName, String sceneParentDirectory) {
		final var sceneHeaderFilePath = Paths.get(sceneParentDirectory, sceneFolderName, HEADER_FILENAME).toString();

		final var sceneHeaderFile = new File(sceneHeaderFilePath);
		if (!sceneHeaderFile.exists()) {
			Debug.debugManager().logger().w(SceneHeader.class.getSimpleName(), "Failed to load scene header from path: " + sceneHeaderFilePath);
			return null;
		}

		final var gson = new GsonBuilder().create();
		String fileContents = null;

		try {
			fileContents = FileUtils.loadString(sceneHeaderFilePath);
		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(SceneHeader.class.getSimpleName(), "Couldn't find header file at: " + sceneHeaderFilePath);
			return null;
		}

		final var loadedHeader = gson.fromJson(fileContents, SceneHeader.class);

		if (loadedHeader == null) {
			Debug.debugManager().logger().e(SceneHeader.class.getSimpleName(), "Couldn't deserialize SceneHeader file: " + sceneHeaderFilePath);
			return null;
		}

		// set these so we can easily save the scene files later
		loadedHeader.sceneFolderName(sceneFolderName);
		loadedHeader.sceneParentDirectory(sceneParentDirectory);

		loadedHeader.sceneHeaderFilePath(Paths.get(sceneParentDirectory, sceneFolderName, HEADER_FILENAME).toString());
		loadedHeader.sceneDataFilePath(Paths.get(sceneParentDirectory, sceneFolderName, DATA_FILENAME).toString());

		if (!loadedHeader.dataExistsOnDisk()) {
			final var sceneDataPath = Paths.get(sceneParentDirectory, SceneHeader.DATA_FILENAME).toString();
			final var sceneDataFile = new File(sceneDataPath);
			if (sceneDataFile.exists()) {
				loadedHeader.sceneDataFilePath(sceneDataPath);
			} else {
				Debug.debugManager().logger().w(SceneHeader.class.getSimpleName(), "Could not find or resolve the scene data file. Looking in " + sceneDataPath);
			}
		}

		return loadedHeader;
	}

	// Save

	public void saveSceneHeaderFile() {
		final var sceneHeaderFile = sceneHeaderFilePath();

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

	public void moveSceneHeader() {

	}
}
