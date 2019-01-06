package net.lintford.library.controllers.core;

import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.textures.TextureManager.TextureGroup;

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
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F5)) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Texture Manager"));
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  Entity Group Count: &d", mResourceManager.textureManager().textureGroupCount()));

			Iterator<Entry<Integer, TextureGroup>> it = mResourceManager.textureManager().textureGroups().entrySet().iterator();

			while (it.hasNext()) {
				Entry<Integer, TextureGroup> lEntryPair = it.next();
				Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  EntityGroupID (%d) has %d textures loaded", lEntryPair.getKey(), lEntryPair.getValue().textureMap().size()));

			}

		}

		return super.handleInput(pCore);

	}

	@Override
	public void unload() {

	}

}
