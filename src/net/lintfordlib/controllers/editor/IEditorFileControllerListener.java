package net.lintfordlib.controllers.editor;

public interface IEditorFileControllerListener {

	void onSave();

	void onLoad();

	void onSceneNameChanged(String newScenename);

	
	void onSceneFileNameChanged(String newScenename);

	/** This is the directory, excluding the scene folder name (e.g. 'res/scenes/campaign/'. */
	void onSceneDirectoryChanged(String newDirectory);

}
