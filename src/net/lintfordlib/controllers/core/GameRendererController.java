package net.lintfordlib.controllers.core;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.renderers.SimpleRendererManager;

/** A {@link BaseController} instance which stores a handle to a {@link SimpleRendererManager} instantiated by a game screen. */
public class GameRendererController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Game Renderer Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RendererManagerBase mRendererManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the {@link RendererManagerBase} associated with this controller. */
	public RendererManagerBase rendererManager() {
		return mRendererManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameRendererController(ControllerManager controllerManager, RendererManagerBase rendererManager, final int entityGroupUid) {
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