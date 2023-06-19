package net.lintford.library.screenmanager.screens;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.player.PlayerSessionController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.splitscreen.IPlayerSession;
import net.lintford.library.core.splitscreen.PlayerSessionsManager;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class BaseGameSplitScreen<T extends IPlayerSession> extends BaseGameScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected PlayerSessionsManager<T> mPlayerSessions;

	private PlayerSessionController mPlayerSessionController;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public PlayerSessionsManager<T> playerSessions() {
		return mPlayerSessions;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseGameSplitScreen(ScreenManager screenManager, PlayerSessionsManager<T> playerManager) {
		this(screenManager, playerManager, null);
	}

	public BaseGameSplitScreen(ScreenManager screenManager, PlayerSessionsManager<T> playerManager, RendererManager rendererManager) {
		super(screenManager, rendererManager);

		mPlayerSessions = playerManager;
		mPlayerSessions.initialize(screenManager.core());
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

	}

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);

	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void createControllers(ControllerManager controllerManager) {
		mPlayerSessionController = new PlayerSessionController(controllerManager, mPlayerSessions, entityGroupUid());
	}

	protected void initializeControllers(LintfordCore core) {
		mPlayerSessionController.initialize(core);
	}

	protected abstract void createRenderers(LintfordCore core);

	protected abstract void initializeRenderers(LintfordCore core);

	protected abstract void loadRendererResources(ResourceManager resourceManager);

}