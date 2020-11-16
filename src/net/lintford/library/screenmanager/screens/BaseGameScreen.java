package net.lintford.library.screenmanager.screens;

import net.lintford.library.controllers.core.GameRendererController;
import net.lintford.library.core.camera.ICamera;
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

		new GameRendererController(pScreenManager.core().controllerManager(), rendererManager, entityGroupID());

		mSingletonScreen = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

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
