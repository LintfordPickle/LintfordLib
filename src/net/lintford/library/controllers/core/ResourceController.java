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
	public boolean isinitialized() {
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
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F6)) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Texture Manager"));
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  Entity Group Count: %d", mResourceManager.textureManager().textureGroupCount()));

			Iterator<Entry<Integer, TextureGroup>> it = mResourceManager.textureManager().textureGroups().entrySet().iterator();

			while (it.hasNext()) {
				Entry<Integer, TextureGroup> lEntryPair = it.next();
				Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  EntityGroupID (%d) has %d textures loaded", lEntryPair.getKey(), lEntryPair.getValue().textureMap().size()));

			}

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Font Manager"));

			final var lNumberFontsLoaded = mResourceManager.fontManager().fontCount();

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  Application has %d fonts loaded", lNumberFontsLoaded));

		}

		return super.handleInput(pCore);

	}

	@Override
	public void unload() {

	}

}
