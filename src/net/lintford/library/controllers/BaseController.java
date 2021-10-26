package net.lintford.library.controllers;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.debug.BaseControllerWidget;
import net.lintford.library.core.LintfordCore;

public abstract class BaseController {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected BaseControllerWidget mBaseControllerWidget;
	protected final int mControllerId;
	protected ControllerManager mControllerManager;
	protected String mControllerName;
	protected boolean mIsActive;
	protected boolean mIsInitialized;
	protected boolean mUniqueController;

	/**
	 * A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e.
	 * unload the game controllers when returning to the main menu)
	 */
	protected int mEntityGroupID;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e.
	 * unload the game controllers when returning to the main menu)
	 */
	public int entityGroupID() {
		return mEntityGroupID;
	}

	/** If true, only one controller of this type can exist at a time. */
	public boolean isUniqueController() {
		return mUniqueController;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public void isActive(boolean pNewValue) {
		mIsActive = pNewValue;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	public String controllerName() {

		return mControllerName;
	}

	public ControllerManager controllerManager() {
		return mControllerManager;
	}

	/** Returns the unique Id assigned to this BaseController instance. */
	public int controllerId() {
		return mControllerId;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseController(final ControllerManager pControllerManager, final String pControllerName, final int pEntityGroupID) {
		if (pControllerManager == null) {
			throw new RuntimeException("ControllerManager cannot be null!");

		}

		mControllerManager = pControllerManager;
		mControllerId = mControllerManager.getNewControllerId();
		mControllerName = pControllerName;

		if (pControllerName.length() == 0) {
			throw new RuntimeException("Controller names cannot be null or empty when registering with a ControllerManager");

		}

		if (mUniqueController && mControllerManager.controllerExists(pControllerName, pEntityGroupID)) {
			throw new RuntimeException("Cannot register two controllers with the same name if they are specified to be unique (" + pControllerName + ")");

		}

		mControllerManager.addController(this, pEntityGroupID);

		mEntityGroupID = pEntityGroupID;

		mIsActive = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(LintfordCore pCore) {
		mIsInitialized = true;
	}

	public abstract void unload();

	public boolean handleInput(LintfordCore pCore) {
		return false;

	}

	public void update(LintfordCore pCore) {

	}

}
