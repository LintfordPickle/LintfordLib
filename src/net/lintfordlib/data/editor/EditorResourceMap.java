package net.lintfordlib.data.editor;

import java.io.File;

import net.lintfordlib.assets.ResourceMap;
import net.lintfordlib.assets.ResourceMapIo;

public class EditorResourceMap {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourceMap mResourceMap;
	private File mFile;
	private File mBaseDirectory;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isResourceMapSet() {
		return mResourceMap != null;
	}

	public ResourceMap resourceMap() {
		return mResourceMap;
	}

	public File file() {
		return mFile;
	}

	public File baseDirectory() {
		return mBaseDirectory;
	}

	public void baseDirectory(File baseDirectory) {
		mBaseDirectory = baseDirectory;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorResourceMap() {
	}

	public void tryCreateNewResourceMap(File resMapFile) {
		final var lResMap = ResourceMapIo.tryCreateNewResourceMap(resMapFile);
		if (lResMap != null) {
			mResourceMap = lResMap;
			mFile = resMapFile;
		}
	}

	public void setResourceMap(ResourceMap resMap, File resMapFile) {
		if (resMap == null)
			return;

		mResourceMap = resMap;
		mFile = resMapFile;

	}

	public void reset() {
		mResourceMap = null;
		mFile = null;
	}

	public void saveResourceMap() {
		if (mResourceMap == null || mFile == null)
			return;

		ResourceMapIo.trySaveResourceMap(mResourceMap, mFile);
	}
}
