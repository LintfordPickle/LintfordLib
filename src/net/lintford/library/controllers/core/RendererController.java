package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.renderers.RendererManager;

public class RendererController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "RendererController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RendererManager mRendererManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if this Controller has been properly initialised. Otherwise returns false. */
	@Override
	public boolean isInitialised() {
		return mRendererManager != null;

	}

	/** Returns the {@link RendererManager} associated with this controller. */
	public RendererManager rendererManager() {
		return mRendererManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RendererController(ControllerManager pControllerManager, RendererManager pRendererManager, final int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mRendererManager = pRendererManager;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GameTime pGameTime) {
		if (!isInitialised())
			return;

		super.update(pGameTime);

		mRendererManager.update(pGameTime);

	}

}