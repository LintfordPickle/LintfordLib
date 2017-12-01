package net.lintford.library.screenmanager.screens;

import net.lintford.library.controllers.BaseControllerGroups;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.renderers.BaseRendererGroups;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class BaseGameScreen extends Screen {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseGameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mScreenManager.core().setNewGameCamera();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {

	}

	@Override
	public void unloadGLContent() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void exitScreen() {

		mScreenManager.core().removeGameCamera();

		// Remove all controllers and renderers related to the game
		mScreenManager.core().controllerManager().removeControllerGroup(BaseControllerGroups.CONTROLLER_GAME_GROUP_ID);
		mScreenManager.core().rendererManager().removeRendererGroup(BaseRendererGroups.RENDERER_GAME_GROUP_ID);

		super.exitScreen();
	}

}
