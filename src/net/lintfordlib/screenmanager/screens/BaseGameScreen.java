package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.core.GameRendererController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.time.LogicialCounter;
import net.lintfordlib.data.DataManager;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

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

	protected BaseGameScreen(ScreenManager screenManager) {
		this(screenManager, null);
	}

	protected BaseGameScreen(ScreenManager screenManager, RendererManagerBase rendererManager) {
		super(screenManager, rendererManager);

		mSingletonScreen = true;

		screenManager.core().input().eventActionManager().setInputProcessor(this);
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

		final var lCore = screenManager.core();
		final var lDataManager = lCore.dataManager();
		final var lControllerManager = lCore.controllerManager();

		lDataManager.removeDataManagerGroup(entityGroupUid());

		new GameRendererController(lControllerManager, mRendererManager, entityGroupUid());
		mGameCamera = screenManager.core().setNewGameCamera(mGameCamera);

		createData(lDataManager);

		createControllers(lControllerManager);
		createRenderers(lCore);

		initializeControllers(lCore);

		mRendererManager.initializeRenderers();

		createRendererStructure(lCore);
		ensureDefaultRenderStructure(lCore);
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

	protected abstract void createData(DataManager dataManager);

	protected abstract void createControllers(ControllerManager controllerManager);

	protected abstract void initializeControllers(LintfordCore core);

	protected abstract void createRenderers(LintfordCore core);

	protected abstract void createRendererStructure(LintfordCore core);

	private void ensureDefaultRenderStructure(LintfordCore core) {
		// TODO: If no renderer structure was defined / created, then setup a default color stage
	}

	@Override
	public void exitScreen() {
		super.exitScreen();

		screenManager.core().input().eventActionManager().setInputProcessor(null);
		screenManager.core().removeGameCamera();
	}
}