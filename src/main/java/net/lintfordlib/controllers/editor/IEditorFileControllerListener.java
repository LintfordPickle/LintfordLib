package net.lintfordlib.controllers.editor;

public interface IEditorFileControllerListener {

	void onSave();

	void onLoad();

	void onSceneNameChanged(String newScenename);

	void onFilepathChanged(String newFilename);

}
