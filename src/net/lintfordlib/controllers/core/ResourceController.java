package net.lintfordlib.controllers.core;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.ResourceManager;

public class ResourceController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Resource Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ResourceManager mResourceManager;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ResourceManager resourceManager() {
		return mResourceManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ResourceController(final ControllerManager controllerManager, ResourceManager resourceManager, int controllerGroupID) {
		super(controllerManager, CONTROLLER_NAME, controllerGroupID);

		mResourceManager = resourceManager;
	}
}