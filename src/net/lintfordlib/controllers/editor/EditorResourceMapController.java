package net.lintfordlib.controllers.editor;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.data.editor.EditorResourceMap;

public class EditorResourceMapController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Editor Resource Map Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private EditorResourceMap mEditorResourceMap;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public EditorResourceMap editorResourceMap() {
		return mEditorResourceMap;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorResourceMapController(ControllerManager controllerManager, EditorResourceMap editorResourceMap, int controllerGroupID) {
		super(controllerManager, CONTROLLER_NAME, controllerGroupID);

		mEditorResourceMap = editorResourceMap;
	}
}