package net.lintford.library.screenmanager.screens;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.GameRendererController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
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

	public BaseGameScreen(ScreenManager screenManager) {
		super(screenManager);
	}

	public BaseGameScreen(ScreenManager screenManager, RendererManager rendererManager) {
		super(screenManager, rendererManager);

		mSingletonScreen = true;
		final var lCore = screenManager().core();
		final var lControllerManager = lCore.controllerManager();
		createControllers(lControllerManager);
		createRenderers(lCore);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		new GameRendererController(mScreenManager.core().controllerManager(), mRendererManager, entityGroupUid());
		mGameCamera = mScreenManager.core().setNewGameCamera(mGameCamera);

		final var lCore = screenManager().core();
		final var lControllerManager = lCore.controllerManager();

		createControllers(lControllerManager);
		createRenderers(lCore);

		initializeControllers(lCore);

		initializeRenderers(lCore);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		loadRendererResources(resourceManager);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected abstract void createControllers(ControllerManager controllerManager);

	protected abstract void initializeControllers(LintfordCore core);

	protected abstract void createRenderers(LintfordCore core);

	protected abstract void initializeRenderers(LintfordCore core);

	protected abstract void loadRendererResources(ResourceManager resourceManager);

	@Override
	public void exitScreen() {
		super.exitScreen();

		mScreenManager.core().removeGameCamera();
	}
}