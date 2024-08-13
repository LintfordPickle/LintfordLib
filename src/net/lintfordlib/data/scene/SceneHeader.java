package net.lintfordlib.data.scene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public class SceneHeader implements Serializable {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 1301516618574644330L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	@SerializedName(value = "SceneName")
	private String mSceneName;

	private transient boolean mIsValid;
	private transient BaseSceneSettings mSceneSettings;
	private transient String mSceneDirectoryName;

	@SerializedName(value = "Values")
	private Map<String, String> mKeyValues = new HashMap<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public String sceneName() {
		return mSceneName;
	}

	public void sceneName(String sceneName) {
		mSceneName = sceneName;

		final var lPointOccursAt = mSceneName.lastIndexOf('.');
		if (lPointOccursAt > -1)
			mSceneName = mSceneName.substring(0, lPointOccursAt);

		validateHeader();
	}

	public String baseScenesDirectory() {
		return mSceneDirectoryName;
	}

	public void baseSceneDirectory(String newBaseSceneDirectory) {
		if (newBaseSceneDirectory == null || newBaseSceneDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set scene directory name to null or empty.");
			return;
		}

		if (newBaseSceneDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newBaseSceneDirectory = newBaseSceneDirectory + FileUtils.FILE_SEPERATOR;
		}

		mSceneDirectoryName = newBaseSceneDirectory;
	}

	public String sceneDataDirectory() {
		return mSceneSettings.scenesDirectory() + baseScenesDirectory();
	}

	public String sceneHeaderFilepath() {
		return mSceneSettings.scenesDirectory() + baseScenesDirectory() + "scene" + mSceneSettings.sceneFileExtension();
	}

	public String sceneDataFilepath() {
		return mSceneSettings.scenesDirectory() + baseScenesDirectory() + "scene" + mSceneSettings.sceneDataExtension();
	}

	public boolean isSceneValid() {
		validateHeader();

		return mIsValid;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SceneHeader(BaseSceneSettings settings) {
		mSceneSettings = settings;
	}

	public SceneHeader(String sceneName, BaseSceneSettings settings) {
		this(settings);

		mSceneName = sceneName;

		validateHeader();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void initialize(String baseSceneDirectory, BaseSceneSettings settings) {
		final var lSceneDirectory = new File(baseSceneDirectory);
		baseSceneDirectory(lSceneDirectory.getName());

		mSceneSettings = settings;
	}

	public void validateHeader() {
		// TODO: Validate the directory + filename
		mIsValid = mSceneName != null && mSceneName != null;
	}

	public static SceneHeader loadSceneHeaderFileFromFilepath(String filepath) {
		if (filepath == null || filepath.length() == 0) {
			Debug.debugManager().logger().e(SceneHeader.class.getSimpleName(), "Filepath for SceneHeader file cannot be null or empty!");
			return null;
		}

		try {
			final var lGson = new GsonBuilder().create();
			final var lFileContents = new String(Files.readAllBytes(Paths.get(filepath)));
			final var lSceneHeader = lGson.fromJson(lFileContents, SceneHeader.class);

			if (lSceneHeader == null) {
				Debug.debugManager().logger().e(SceneHeader.class.getSimpleName(), "Couldn't deserialize SceneHeader file!");
				return null;
			}

			return lSceneHeader;
		} catch (IOException e) {
			Debug.debugManager().logger().e(SceneHeader.class.getSimpleName(), "Error deserializing SceneHeader file.");
			Debug.debugManager().logger().printException(SceneHeader.class.getSimpleName(), e);
		}

		return null;
	}

	public void saveSceneHeaderFile() {
		final var lSceneHeaderFile = sceneHeaderFilepath();

		if (lSceneHeaderFile == null || lSceneHeaderFile.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to save the scene header into " + lSceneHeaderFile);
			return;
		}

		// TODO: This isn't the correct place to be doing logical on the file system.

		final var lSaveDirectory = new File(lSceneHeaderFile);
		final var lParentDirectory = lSaveDirectory.getParentFile();

		if (lParentDirectory.exists() == false) {
			if (lParentDirectory.mkdirs() == false) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to save the scene data into " + lSceneHeaderFile);
				return;
			}
		}

		try (Writer writer = new FileWriter(lSceneHeaderFile)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(this, writer);
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Error serializing SceneHeader file.");
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}
	}
}
