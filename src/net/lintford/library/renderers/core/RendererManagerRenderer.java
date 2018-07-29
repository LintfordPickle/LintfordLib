package net.lintford.library.renderers.core;

import net.lintford.library.controllers.core.ControllerManagerController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.windows.UIWindow;

public class RendererManagerRenderer extends UIWindow {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "RendererManagerRenderer";

	// --------------------------------------
	// variables
	// --------------------------------------

	private ControllerManagerController mControllerManagerController;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RendererManagerRenderer(final RendererManager pRendererManager, final int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		super.initialise(pCore);

		mControllerManagerController = (ControllerManagerController) pCore.controllerManager().getControllerByNameRequired(ControllerManagerController.CONTROLLER_NAME, LintfordCore.CORE_ID);

	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// --------------------------------------
	// UIWindow Methods
	// --------------------------------------

}
