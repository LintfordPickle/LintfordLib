package net.lintfordlib.data.scene;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.options.ResourcePathsConfig;

public abstract class BaseSceneSettings {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCENENAME_REGEX = "[^a-zA-Z0-9]";

	public static final String SCENES_DIR_KEY_NAME = "ScenesDirKeyName";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourcePathsConfig mResourcePathsConfig;

	private String mSceneHeaderFileExtension = ".hdr";
	private String mSceneDataFileExtension = ".data";

	private String mScenesBaseDirectory;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String sceneFileExtension() {
		return mSceneHeaderFileExtension;
	}

	public void sceneFileExtension(String newSceneHeaderFileExtension) {
		if (newSceneHeaderFileExtension == null || newSceneHeaderFileExtension.length() <= 1) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set scene header file extension to null or '.'.");
			return;
		}

		mSceneHeaderFileExtension = newSceneHeaderFileExtension;
	}

	public String sceneDataExtension() {
		return mSceneDataFileExtension;
	}

	public void sceneDataExtension(String newSceneDataFileExtension) {
		if (newSceneDataFileExtension == null || newSceneDataFileExtension.length() <= 1) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set scene data file extension to null or '.'.");
			return;
		}

		mSceneDataFileExtension = newSceneDataFileExtension;
	}

	public String scenesDirectory() {
		return mScenesBaseDirectory;
	}

	public void scenesDirectory(String newScenesDirectory) {
		if (newScenesDirectory == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to set new scenesDirectory to <null>");
			return;
		}

		mScenesBaseDirectory = mResourcePathsConfig.insertOrUpdateValue(SCENES_DIR_KEY_NAME, newScenesDirectory);
	}

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	protected BaseSceneSettings(ResourcePathsConfig paths) {
		mResourcePathsConfig = paths;

		// get (or set) the paths directory.
		final var lPath = new File("res/def/scenes/");
		mScenesBaseDirectory = paths.getKeyValue(SCENES_DIR_KEY_NAME, lPath.getAbsolutePath());
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public List<File> getListOfHeaderFilesInScenesDirectory(String subDirectory) {
		final var path = Paths.get(scenesDirectory(), subDirectory);

		final var lScenesDirectory = path.toFile();
		final var lSubDirectoryList = lScenesDirectory.listFiles((dir, name) -> new File(dir, name).isDirectory());

		final List<File> lAllHeaderFiles = new ArrayList<>();

		if (lSubDirectoryList == null)
			return lAllHeaderFiles;

		for (var subDir : lSubDirectoryList) {
			final var lFilesInSubDir = FileUtils.getListOfFilesInDirectory(subDir.getPath(), mSceneHeaderFileExtension);
			lAllHeaderFiles.addAll(lFilesInSubDir);
		}

		return lAllHeaderFiles;
	}

}
