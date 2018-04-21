package net.lintford.library.controllers.core;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class ControllerManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LintfordCore mCore;
	private List<BaseController> mControllers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns a list of controllers currently registered with the {@link ControllerManager}. */
	public List<BaseController> controllers() {
		return mControllers;

	}

	public LintfordCore core() {
		return mCore;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ControllerManager(LintfordCore pCore) {
		mCore = pCore;
		mControllers = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (!mControllers.get(i).isActive())
				continue;

			if (mControllers.get(i).handleInput(pCore)) {
				return true;

			}

		}

		return false;

	}

	public void update(LintfordCore pCore) {
		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (!mControllers.get(i).isActive())
				continue;

			mControllers.get(i).update(pCore);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialiseControllers() {
		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (!mControllers.get(i).isActive())
				continue;
			
			if (mControllers.get(i).isInitialised())
				continue;

			mControllers.get(i).initialise();
		}
	}
	
	public BaseController getControllerByType(Class<?> pClass) {
		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (mControllers.get(i).getClass().equals(pClass)) {
				return mControllers.get(i);
			}
		}

		return null;
	}

	/** Returns the controller with the given name. In case no {@link BaseController} instance with the given name is found (i.e. has not been registered) or has not been initialised, an exception will be thrown. */
	public BaseController getControllerByNameRequired(String pControllerName) {
		final BaseController RESULT = getControllerByName(pControllerName);

		// In case this required controller is missing, then throw an exception.
		// TODO: Don't throw an exception in the future, rather gracefully quit and inform the player.
		if (RESULT == null) {
			throw new RuntimeException("Required controller not found: " + pControllerName);
		}

		return RESULT;
	}

	/** Returns the controller with the given name. If no controller is found, null is returned. */
	public BaseController getControllerByName(String pControllerName) {
		if (pControllerName == null || pControllerName.length() == 0) {
			DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Controller requested but no identifier given");

			return null;
		}

		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (mControllers.get(i).controllerName().equals(pControllerName)) {
				return mControllers.get(i);
			}
		}

		return null;

	}

	/** Returns true if a {@link BaseController} has been registered with the given name. */
	public boolean controllerExists(final String pControllerName) {
		return getControllerByName(pControllerName) != null;
	}

	public void addController(BaseController pController) {
		// Only add one controller of each time (and with unique names).
		if (getControllerByName(pController.controllerName()) == null) {
			mControllers.add(pController);

		}

	}

	public void removeController(BaseController pController) {
		if (mControllers.contains(pController)) {
			mControllers.remove(pController);
		}

	}

	public void removeAllControllers() {
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "ControllerManager: Removing all controllers");

		mControllers.clear();

	}

	/** Unloads all {@link BaseRenderer} instances registered to this {@link RendererManager} which have the given group ID assigned to them. */
	public void removeControllerGroup(final int pGroupID) {
		// Heap assignment
		final List<BaseController> CONTROLLER_UPDATE_LIST = new ArrayList<>();
		final int CONTROLLER_COUNT = mControllers.size();
		for (int i = 0; i < CONTROLLER_COUNT; i++) {
			CONTROLLER_UPDATE_LIST.add(mControllers.get(i));

		}

		for (int i = 0; i < CONTROLLER_COUNT; i++) {
			if (CONTROLLER_UPDATE_LIST.get(i).groupID() == pGroupID) {
				// Unload this BaseRenderer instance
				final BaseController CONTROLLER = CONTROLLER_UPDATE_LIST.get(i);

				// TODO: Need to explicitly unload controllers?

				// Remove the BaseRenderer instance from the mWindowRenderers list
				mControllers.remove(CONTROLLER);

			}

		}

	}

}
