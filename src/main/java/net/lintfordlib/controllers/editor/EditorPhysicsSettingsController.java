package net.lintfordlib.controllers.editor;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.physics.PhysicsSettings;

public class EditorPhysicsSettingsController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Physics Settings Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PhysicsSettings mPhysicsSettings;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public PhysicsSettings physicsSettings() {
		return mPhysicsSettings;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public EditorPhysicsSettingsController(ControllerManager controllerManager, PhysicsSettings physicsSettings, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mPhysicsSettings = physicsSettings;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void resizeGrid(float width, float height, int tilesWide, int tilesHigh) {
		mPhysicsSettings.hashGridWidthInUnits = width;
		mPhysicsSettings.hashGridHeightInUnits = height;

		mPhysicsSettings.hashGridCellsWide = tilesWide;
		mPhysicsSettings.hashGridCellsHigh = tilesHigh;
	}
}
