package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.renderers.RendererManager;

/** A {@link BaseController} instance which stores a handle to a {@link RendererManager} instantiated by a game screen. */
public class GameRendererController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Game Renderer Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RendererManager mRendererManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the {@link RendererManager} associated with this controller. */
	public RendererManager rendererManager() {
		return mRendererManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameRendererController(ControllerManager controllerManager, RendererManager rendererManager, final int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mRendererManager = rendererManager;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unloadController() {
		mRendererManager = null;
	}

	@Override
	public void update(LintfordCore core) {
		if (!isInitialized())
			return;

		super.update(core);

		mRendererManager.update(core);
	}
}