package net.lintfordlib.controllers;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public abstract class BaseController implements IInputProcessor {

	// --------------------------------------
	// Variables
	// --------------------------------------

	/**
	 * A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu)
	 */
	protected int mEntityGroupUid;
	protected final int mControllerId;
	protected ControllerManager mControllerManager;
	protected String mControllerName;
	protected boolean mIsActive;
	protected boolean mIsInitialized;
	protected boolean mUniqueController;

	private float mMouseInputTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * A group ID is assigned to all {@link BaseController} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu)
	 */
	public int entityGroupUid() {
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

	protected BaseController(ControllerManager controllerManager, String controllerName, int entityGroupUid) {
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

	public void unloadController() {
		mIsInitialized = false;
	}

	public boolean handleInput(LintfordCore core) {
		return false;
	}

	public void update(LintfordCore core) {
		if (mMouseInputTimer > 0.f)
			mMouseInputTimer -= core.gameTime().elapsedTimeMilli();
	}

	// --------------------------------------
	// IInputProcessor-Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mMouseInputTimer = cooldownInMs;

	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowMouseInput() {
		return false;
	}
}
