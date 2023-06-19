package net.lintford.library.controllers.player;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.splitscreen.PlayerSessionsManager;

public class PlayerSessionController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Player Session Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PlayerSessionsManager<?> mPlayerSession;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public PlayerSessionsManager<?> playerSessionManager() {
		return mPlayerSession;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlayerSessionController(ControllerManager controllerManager, PlayerSessionsManager<?> playerSession, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mPlayerSession = playerSession;

	}

}
