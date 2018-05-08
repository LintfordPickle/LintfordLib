package net.lintford.library.screenmanager.screens;

import net.lintford.library.controllers.core.GameRendererController;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class BaseGameScreen extends Screen {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseGameScreen(ScreenManager pScreenManager, Camera pCamera) {
		super(pScreenManager);

		new GameRendererController(pScreenManager.core().controllerManager(), mRendererManager, entityGroupID);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		mScreenManager.core().setNewGameCamera(null);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void exitScreen() {
		super.exitScreen();

		mScreenManager.core().removeGameCamera();

	}

}
