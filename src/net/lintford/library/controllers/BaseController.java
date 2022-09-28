package net.lintford.library.controllers;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.debug.BaseControllerWidget;
import net.lintford.library.core.LintfordCore;

public abstract class BaseController {

	// --------------------------------------
	// Variables
	// --------------------------------------

	/**
	 * A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e.
	 * unload the game controllers when returning to the main menu)
	 */
	protected int mEntityGroupUid;
	protected BaseControllerWidget mBaseControllerWidget;
	protected final int mControllerId;
	protected ControllerManager mControllerManager;
	protected String mControllerName;
	protected boolean mIsActive;
	protected boolean mIsInitialized;
	protected boolean mUniqueController;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e.
	 * unload the game controllers when returning to the main menu)
	 */
	public int entityGroupID() {
		return mEntityGroupUid;
	}

	/** If true, only one controller of this type can exist at a time. */
	public boolean isUniqueController() {
		return mUniqueController;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public void isActive(boolean isActive) {
		mIsActive = isActive;
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

	public BaseController(final ControllerManager controllerManager, final String controllerName, final int entityGroupUid) {
		if (controllerManager == null)
			throw new RuntimeException("ControllerManager cannot be null!");

		if (controllerName.length() == 0)
			throw new RuntimeException("Controller names cannot be null or empty when registering with a ControllerManager");

		if (mUniqueController && mControllerManager.controllerExists(controllerName, entityGroupUid))
			throw new RuntimeException("Cannot register two controllers with the same name if they are specified to be unique (" + controllerName + ")");

		mControllerManager = controllerManager;
		mControllerId = mControllerManager.getNewControllerId();
		mControllerName = controllerName;

		mControllerManager.addController(this, entityGroupUid);

		mEntityGroupUid = entityGroupUid;

		mIsActive = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(LintfordCore core) {
		mIsInitialized = true;
	}

	public abstract void unload();

	public boolean handleInput(LintfordCore core) {
		return false;
	}

	public void update(LintfordCore core) {

	}
}
