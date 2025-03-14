package net.lintfordlib.renderers.debug;

import net.lintfordlib.controllers.camera.CameraBoundsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.SimpleRendererManager;

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
		return mCameraBoundsController != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugCameraBoundsDrawer(SimpleRendererManager rendererManager, int entityGroupUid) {
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
	public void draw(LintfordCore core, RenderPass renderPass) {
		if (!isActive() || !isInitialized())
			return;

		if (mCameraBoundsController.drawBounds()) {
			final var x = -mCameraBoundsController.widthBoundInPx() * .5f;
			final var y = -mCameraBoundsController.heightBoundInPx() * .5f;
			final var w = mCameraBoundsController.widthBoundInPx();
			final var h = mCameraBoundsController.heightBoundInPx();

			Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), x, y, w, h, 0f, 1f, 0f);
		}
	}
}