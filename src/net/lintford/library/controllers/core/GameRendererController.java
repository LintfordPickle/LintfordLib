package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.renderers.RendererManager;

/** A {@link BaseController} instance which stores a handle to a {@link RendererManager} instantiated by a game screen. */
public class GameRendererController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "GameRendererController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RendererManager mRendererManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if this Controller has been properly initialized. Otherwise returns false. */
	@Override
	public boolean isInitialized() {
		return mRendererManager != null;

	}

	/** Returns the {@link RendererManager} associated with this controller. */
	public RendererManager rendererManager() {
		return mRendererManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameRendererController(ControllerManager pControllerManager, RendererManager pRendererManager, final int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mRendererManager = pRendererManager;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		mRendererManager = null;

	}

	@Override
	public void update(LintfordCore pCore) {
		if (!isInitialized())
			return;

		super.update(pCore);

		mRendererManager.update(pCore);

	}

}