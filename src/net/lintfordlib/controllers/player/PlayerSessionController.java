package net.lintfordlib.controllers.player;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.splitscreen.PlayerSessionsManager;

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
