package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.graphics.ResourceManager;

public class ResourceController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "ResourceController";

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

	@Override
	public boolean isInitialised() {
		return false;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ResourceController(final ControllerManager pControllerManager, ResourceManager pResourceManager, int pControllerGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroupID);

		mResourceManager = pResourceManager;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialise() {

	}

}
