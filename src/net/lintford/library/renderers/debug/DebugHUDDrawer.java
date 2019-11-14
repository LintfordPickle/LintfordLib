package net.lintford.library.renderers.debug;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.hud.UIHUDStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugHUDDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG HUD Outlines";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UIHUDStructureController mUIHUDController;
	private int mDebugDraw;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return true;

	}
	
	public int debugDrawEnable() {
		return mDebugDraw;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugHUDDrawer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mIsActive = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mUIHUDController = (UIHUDStructureController) pCore.controllerManager().getControllerByNameRequired(UIHUDStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!isActive())
			return false;

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F2)) {
			mDebugDraw++;
			if (mDebugDraw > 2)
				mDebugDraw = 0;
		}

		return super.handleInput(pCore);
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isActive() || mDebugDraw == 0)
			return;

		// menu rects
		// Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.menuMainRectangle());
		// Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.menuTitleRectangle());
		// Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.menuFooterRectangle());

		// Game HUD
		Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.gameHUDRectangle(), 1f, 0f, 1f);
		Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.gameHeaderRectangle(), 0f, 1f, 0f);
		Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.gameFooterRectangle(), 0f, 1f, 0f);

	}

}
