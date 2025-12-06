package net.lintfordlib.data.scene;

import java.io.File;
import java.nio.file.Paths;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public class SceneHeaderIoService {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private SceneHeaderIoService() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static SceneHeader loadSceneHeaderFileFromFilepath(String sceneName, String sceneDirectory) {
		final var sceneFilePath = Paths.get(sceneDirectory, sceneName, SceneHeader.HEADER_FILENAME).toString();

		if (sceneFilePath == null || sceneFilePath.length() == 0) {
			Debug.debugManager().logger().e(SceneHeader.class.getSimpleName(), "Filepath for SceneHeader file cannot be null or empty!");
			return null;
		}

		return loadSceneHeaderFileFromFilepath(sceneFilePath);
	}

	public static SceneHeader loadSceneHeaderFileFromFilepath(String sceneHeaderFilePath) {
		if (sceneHeaderFilePath == null || sceneHeaderFilePath.length() == 0)
			return null; // TODO: log it

		final var sceneHeaderFile = new File(sceneHeaderFilePath);
		if (!sceneHeaderFile.exists())
			return null; // TODO: log it

		final var sceneDirectory = sceneHeaderFile.getParent();

		final var gson = new GsonBuilder().create();

		final var fileContents = FileUtils.loadString(sceneHeaderFilePath);
		final var sceneHeader = gson.fromJson(fileContents, SceneHeader.class);

		if (sceneHeader == null) {
			Debug.debugManager().logger().e(SceneHeader.class.getSimpleName(), "Couldn't deserialize SceneHeader file!");
			return null;
		}

		// need to set the header filePath so it can be saved later.
		sceneHeader.sceneHeaderFilePath(sceneHeaderFilePath);

		if (!sceneHeader.dataExistsOnDisk()) {
			final var sceneDataPath = Paths.get(sceneDirectory, SceneHeader.DATA_FILENAME).toString();
			final var sceneDataFile = new File(sceneDataPath);
			if (sceneDataFile.exists()) {
				sceneHeader.sceneDataFilePath(sceneDataPath);
			} else {
				Debug.debugManager().logger().w(SceneHeader.class.getSimpleName(), "Could not find or resolve the scene data file. Looking in " + sceneDataPath);
			}
		}

		return sceneHeader;

	}
}
