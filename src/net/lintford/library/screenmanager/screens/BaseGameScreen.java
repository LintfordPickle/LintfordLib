package net.lintford.library.screenmanager.screens;

import net.lintford.library.controllers.core.GameRendererController;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class BaseGameScreen extends Screen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ICamera mGameCamera;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseGameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);
	}

	public BaseGameScreen(ScreenManager pScreenManager, RendererManager pRendererManager) {
		super(pScreenManager, pRendererManager);

		mSingletonScreen = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		new GameRendererController(screenManager.core().controllerManager(), rendererManager, entityGroupID());

		mGameCamera = screenManager.core().setNewGameCamera(mGameCamera);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void exitScreen() {
		super.exitScreen();

		screenManager.core().removeGameCamera();
	}
}