package net.lintfordlib.data.scene;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.options.ResourcePathsConfig;

public abstract class BaseSceneSettings {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String scenenameRegex = "[^a-zA-Z0-9]";

	private String SCENES_DIR_KEY_NAME = "ScenesDirectoryPath";

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
		// TODO: allow for modifying scenes directory
	}

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	protected BaseSceneSettings(ResourcePathsConfig paths) {
		mResourcePathsConfig = paths;

		// get (or set) the paths directory.
		mScenesBaseDirectory = paths.getKeyValue(SCENES_DIR_KEY_NAME, "res/def/scenes/");
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public List<File> getListOfHeaderFilesInScenesDirectory() {
		final var lScenesDirectory = new File(scenesDirectory());
		final var lDirectoryFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		};

		final var lSubDirectoryList = lScenesDirectory.listFiles(lDirectoryFilter);
		final List<File> lAllHeaderFiles = new ArrayList<>();

		if (lSubDirectoryList == null)
			return lAllHeaderFiles;

		for (var subDir : lSubDirectoryList) {
			final var lFilesInSubDir = FileUtils.getListOfFileInDirectory(subDir.getAbsolutePath(), mSceneHeaderFileExtension);
			lAllHeaderFiles.addAll(lFilesInSubDir);
		}

		return lAllHeaderFiles;
	}

}
