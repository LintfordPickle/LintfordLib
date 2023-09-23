package net.lintford.library.screenmanager.screens;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.GameRendererController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.time.LogicialCounter;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class BaseGameScreen extends Screen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ICamera mGameCamera;
	protected LogicialCounter mGameInputLogicalCounter;
	protected LogicialCounter mGameDrawLogicalCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public LogicialCounter inputCounter() {
		return mGameInputLogicalCounter;
	}

	public LogicialCounter drawCounter() {
		return mGameDrawLogicalCounter;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseGameScreen(ScreenManager screenManager) {
		this(screenManager, null);
	}

	public BaseGameScreen(ScreenManager screenManager, RendererManager rendererManager) {
		super(screenManager, rendererManager);

		mSingletonScreen = true;

		mScreenManager.core().input().eventActionManager().setInputProcessor(this);
		mShowContextualKeyHints = false;

		mGameInputLogicalCounter = new LogicialCounter();
		mGameDrawLogicalCounter = new LogicialCounter();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final var lCore = screenManager().core();
		final var lControllerManager = lCore.controllerManager();

		new GameRendererController(lControllerManager, mRendererManager, entityGroupUid());
		mGameCamera = mScreenManager.core().setNewGameCamera(mGameCamera);

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

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);

		mGameInputLogicalCounter.incrementCounter();
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

		mGameDrawLogicalCounter.incrementCounter();
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

		mScreenManager.core().input().eventActionManager().setInputProcessor(null);
		mScreenManager.core().removeGameCamera();
	}
}