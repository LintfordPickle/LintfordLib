package net.lintford.library.controllers;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public abstract class BaseController {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ControllerManager mControllerManager;
	protected String mControllerName;
	protected boolean mIsActive;
	protected boolean mUniqueController;

	/** A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu) */
	protected int mGroupID;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu) */
	public int groupID() {
		return mGroupID;
	}

	/** If true, only one controller of this type can exist at a time. */
	public boolean isUniqueController() {
		return mUniqueController;
	}

	public abstract boolean isInitialised();

	public boolean isActive() {
		return mIsActive;
	}

	public void isActive(boolean pNewValue) {
		mIsActive = pNewValue;
	}

	public String controllerName() {

		return mControllerName;
	}

	public ControllerManager controllerManager() {
		return mControllerManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseController(final ControllerManager pControllerManager, final String pControllerName, final int pEntityGroupID) {
		if (pControllerManager != null) {
			if (pControllerName.length() == 0)
				throw new RuntimeException("Controller names cannot be null or empty when registering with a ControllerManager");

			mControllerManager = pControllerManager;
			mControllerName = pControllerName;

			if (mUniqueController && mControllerManager.controllerExists(pControllerName, pEntityGroupID)) {
				throw new RuntimeException("Cannot register two controllers with the same name if they are specified to be unique (" + pControllerName + ")");
			}

			mControllerManager.addController(this, pEntityGroupID);

			mGroupID = pEntityGroupID;

			mIsActive = true;

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public abstract void initialise(LintfordCore pCore);

	public abstract void unload();

	public boolean handleInput(LintfordCore pCore) {
		return false;

	}

	public void update(LintfordCore pCore) {

	}

}
