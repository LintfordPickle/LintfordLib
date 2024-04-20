package net.lintfordlib.controllers.editor;

import java.io.File;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;

public class EditorWorkspaceController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Editor Workspace Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private File mWorkspaceFile;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public File workspaceFile() {
		return mWorkspaceFile;
	}

	public void workspaceFile(File workspaceFile) {
		mWorkspaceFile = workspaceFile;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorWorkspaceController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		var defaultWorkspaceLocation = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
		if (defaultWorkspaceLocation == null) {
			defaultWorkspaceLocation = System.getProperty("user.dir");
			System.setProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME, defaultWorkspaceLocation);
		}
		mWorkspaceFile = new File(defaultWorkspaceLocation);
	}

}
