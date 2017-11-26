package net.lintford.library.controllers.debug;

import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.BaseControllerGroups;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.time.GameTime;

public class DebugControllerManager extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "DebugControllerManager";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mNumCoreControllers;
	private int mNumMenuControllers;
	private int mNumGameControllers;
	private int mNumMiscControllers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the number of {@link BaseController} in the {@link BaseControllerGroups.CORE} group currently registered with the {@link ControllerManager}. */
	public int numCoreControllers() {
		return mNumCoreControllers;
	}

	/** Returns the number of {@link BaseController} in the {@link BaseControllerGroups.MENU} group currently registered with the {@link ControllerManager}. */
	public int numMenuControllers() {
		return mNumMenuControllers;
	}

	/** Returns the number of {@link BaseController} in the {@link BaseControllerGroups.GAME} group currently registered with the {@link ControllerManager}. */
	public int numGameControllers() {
		return mNumGameControllers;
	}

	/** Returns the number of {@link BaseController} in the {@link BaseControllerGroups.MISC} group currently registered with the {@link ControllerManager}. */
	public int numMiscControllers() {
		return mNumMiscControllers;
	}

	@Override
	public boolean isInitialised() {
		return true;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugControllerManager(final ControllerManager pControllerManager, final int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {

	}

	public void update(GameTime pGameTime) {
		if (mControllerManager == null) {
			return;

		}

		mNumCoreControllers = 0;
		mNumMenuControllers = 0;
		mNumGameControllers = 0;
		mNumMiscControllers = 0;

		// Track the number of controller of each type.
		final List<BaseController> CONTROLLER_LIST = mControllerManager.controllers();
		final int CONTROLLER_COUNT = CONTROLLER_LIST.size();
		for (int i = 0; i < CONTROLLER_COUNT; i++) {
			BaseController ref = CONTROLLER_LIST.get(i);
			if (ref.groupID() == BaseControllerGroups.CONTROLLER_CORE_GROUP_ID) {
				mNumCoreControllers++;

			} else if (ref.groupID() == BaseControllerGroups.CONTROLLER_MENU_GROUP_ID) {
				mNumMenuControllers++;

			} else if (ref.groupID() == BaseControllerGroups.CONTROLLER_GAME_GROUP_ID) {
				mNumGameControllers++;

			} else {
				mNumMiscControllers++;

			}

		}

	}

}
