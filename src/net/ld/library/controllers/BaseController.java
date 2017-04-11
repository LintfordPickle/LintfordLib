package net.ld.library.controllers;

import net.ld.library.core.input.InputState;
import net.ld.library.core.time.GameTime;

/**
 * All controllers in the game should inherit from this class. They can then be
 * maintained by the {@link ControllerManager}
 */
public abstract class BaseController {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ControllerManager mControllerManager;
	protected String mControllerName;
	protected boolean mIsActive;
	protected boolean mUniqueController;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public BaseController(final ControllerManager pControllerManager, final String pControllerName) {
		if (pControllerManager != null) {
			if (pControllerName.length() == 0)
				throw new RuntimeException(
						"Controller names cannot be null or empty when registering with a ControllerManager");

			mControllerManager = pControllerManager;
			mControllerName = pControllerName;

			if (mUniqueController && mControllerManager.controllerExists(pControllerName)) {
				throw new RuntimeException(
						"Cannot register two controllers with the same name if they are specified to be unique ("
								+ pControllerName + ")");
			}

			mControllerManager.addController(this);

			mIsActive = true;

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public abstract void initialise();

	public boolean handleInput(InputState pInputState) {

		return false;
	}

	public void update(GameTime pGameTime) {

	}

}
