package net.lintfordlib.data.scene;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.AppResources;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public abstract class BaseSceneSettings {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String scenenameRegex = "[^a-zA-Z0-9]";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AppResources mAppResources;

	private String mSceneHeaderFileExtension = ".hdr";
	private String mSceneDataFileExtension = ".data";

	private String mScenesBaseDirectory = "scenes" + FileUtils.FILE_SEPERATOR;

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
		return mAppResources.defsDirectory() + mScenesBaseDirectory;
	}

	public void scenesDirectory(String newScenesDirectory) {
		if (newScenesDirectory == null || newScenesDirectory.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot set scenes resources directory to null or empty.");
			return;
		}

		if (newScenesDirectory.startsWith(mAppResources.defsDirectory())) {
			newScenesDirectory = newScenesDirectory.substring(mAppResources.defsDirectory().length());
		}

		if (newScenesDirectory.startsWith(mAppResources.root())) {
			newScenesDirectory = newScenesDirectory.substring(mAppResources.root().length());
		}

		if (newScenesDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false) {
			newScenesDirectory = newScenesDirectory + FileUtils.FILE_SEPERATOR;
		}

		mScenesBaseDirectory = newScenesDirectory;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Scenes resources directory set to : " + mScenesBaseDirectory);
	}

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	public BaseSceneSettings(AppResources appResources) {
		mAppResources = appResources;
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
		
		if(lSubDirectoryList == null)
			return lAllHeaderFiles;

		for (var subDir : lSubDirectoryList) {
			final var lFilesInSubDir = FileUtils.getListOfFileInDirectory(subDir.getAbsolutePath(), mSceneHeaderFileExtension);
			lAllHeaderFiles.addAll(lFilesInSubDir);
		}

		return lAllHeaderFiles;
	}

}
