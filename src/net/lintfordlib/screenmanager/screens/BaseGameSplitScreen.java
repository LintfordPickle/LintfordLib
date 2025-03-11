package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.player.PlayerSessionController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.splitscreen.IPlayerSession;
import net.lintfordlib.core.splitscreen.PlayerSessionsManager;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.screenmanager.ScreenManager;

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

	public BaseGameSplitScreen(ScreenManager screenManager, PlayerSessionsManager<T> playerManager, RendererManagerBase rendererManager) {
		super(screenManager, rendererManager);

		mPlayerSessions = playerManager;
		mPlayerSessions.initialize(screenManager.core());
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
}