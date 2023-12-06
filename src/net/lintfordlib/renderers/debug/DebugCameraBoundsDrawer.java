package net.lintfordlib.renderers.debug;

import net.lintfordlib.controllers.camera.CameraBoundsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class DebugCameraBoundsDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG Camera Bounds Drawer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private CameraBoundsController mCameraBoundsController;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return true;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugCameraBoundsDrawer(RendererManager rendererManager, int entityGroupUid) {
		super(rendererManager, RENDERER_NAME, entityGroupUid);

		mIsActive = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mCameraBoundsController = (CameraBoundsController) core.controllerManager().getControllerByNameRequired(CameraBoundsController.CONTROLLER_NAME, mEntityGroupUid);
	}

	@Override
	public void draw(LintfordCore core) {
		if (!isActive())
			return;

		if (mCameraBoundsController == null) {
			mCameraBoundsController = (CameraBoundsController) core.controllerManager().getControllerByNameRequired(CameraBoundsController.CONTROLLER_NAME, mEntityGroupUid);

		} else {
			final var x = -mCameraBoundsController.sceneWidthInPx * .5f;
			final var y = -mCameraBoundsController.sceneHeightInPx * .5f;
			final var w = mCameraBoundsController.sceneWidthInPx;
			final var h = mCameraBoundsController.sceneHeightInPx;

			Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), x, y, w, h, 0f, 1f, 0f);
		}
	}
}