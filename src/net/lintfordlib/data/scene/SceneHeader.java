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

public abstract class SceneHeader implements Serializable {

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

	private transient boolean mIsValid;

	private transient String mSceneDirectory;

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

	public String sceneDirectory() {
		return mSceneDirectory;
	}

	public void setSceneDirectory(String newBaseSceneDirectory) {
		if (newBaseSceneDirectory == null || newBaseSceneDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set scene directory name to null or empty.");
			return;
		}

		if (!newBaseSceneDirectory.endsWith(File.separator))
			newBaseSceneDirectory += File.separator;

		mSceneDirectory = newBaseSceneDirectory;
	}

	public String sceneHeaderFilepath() {
		return mSceneDirectory + File.separator + HEADER_FILENAME;
	}

	public String sceneDataFilepath() {
		return mSceneDirectory + File.separator + DATA_FILENAME;
	}

	public boolean isSceneValid() {
		validateHeader();

		return mIsValid;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	protected SceneHeader() {

	}

	protected SceneHeader(String sceneName) {
		mSceneName = sceneName;

		validateHeader();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void initialize(String sceneDirectory, BaseGameResourcePaths settings) {
		setSceneDirectory(sceneDirectory);
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

		final var lSaveDirectory = new File(lSceneHeaderFile);
		final var lParentDirectory = lSaveDirectory.getParentFile();

		if (!lParentDirectory.exists()) {
			if (!lParentDirectory.mkdirs()) {
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
