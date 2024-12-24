package net.lintfordlib.data.scene;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.options.ResourcePathsConfig;

public abstract class BaseGameResourcePaths {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ResourcePathsConfig mResourcePathsConfig;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ResourcePathsConfig paths() {
		return mResourcePathsConfig;
	}

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	protected BaseGameResourcePaths(ResourcePathsConfig paths) {
		mResourcePathsConfig = paths;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public List<File> getListOfFilesInResourceDirectory(String resourceDirectory, String subDirectory, String extType) {
		final var path = Paths.get(resourceDirectory, subDirectory);

		final var lScenesDirectory = path.toFile();
		final var lSubDirectoryList = lScenesDirectory.listFiles((dir, name) -> new File(dir, name).isDirectory());

		final List<File> lAllHeaderFiles = new ArrayList<>();

		if (lSubDirectoryList == null)
			return lAllHeaderFiles;

		for (var subDir : lSubDirectoryList) {
			final var lFilesInSubDir = FileUtils.getListOfFilesInDirectory(subDir.getPath(), extType);
			lAllHeaderFiles.addAll(lFilesInSubDir);
		}

		return lAllHeaderFiles;
	}

}
