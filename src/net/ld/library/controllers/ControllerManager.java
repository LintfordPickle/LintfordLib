package net.ld.library.controllers;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.input.InputState;
import net.ld.library.core.time.GameTime;
import net.ld.library.renderers.RendererManager;

public class ControllerManager {

	// --------------------------------------
	// variables
	// --------------------------------------

	private RendererManager mRenderManager;
	private List<BaseController> mControllers;
	private boolean mIsInitialised;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public RendererManager renderManager() {
		return mRenderManager;
	}

	public boolean isInitialised() {
		return mIsInitialised;

	}

	// --------------------------------------
	// constructor
	// --------------------------------------

	public ControllerManager() {
		mControllers = new ArrayList<>();
		mIsInitialised = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise(RendererManager pRenderManager) {
		mRenderManager = pRenderManager;

		mIsInitialised = true;

	}

	public boolean handleInput(InputState pInputState) {
		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (!mControllers.get(i).isActive())
				continue;

			if (mControllers.get(i).handleInput(pInputState)) {
				return true;

			}

		}

		return false;

	}

	public void update(GameTime pGameTime) {
		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (!mControllers.get(i).isActive())
				continue;

			mControllers.get(i).update(pGameTime);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public BaseController getControllerByType(Class<?> pClass) {
		int lCount = mControllers.size();
		for (int i = 0; i < lCount; i++) {
			if (mControllers.get(i).getClass().equals(pClass)) {
				return mControllers.get(i);
			}
		}

		return null;
	}

	/**
	 * Returns the controller with the given name. In case no
	 * {@link BaseController} instance with the given name is found (i.e. has
	 * not been registered) or has not been initialised, an exception will be
	 * thrown.
	 */
	public BaseController getControllerByNameRequired(String pControllerName) {
		final BaseController RESULT = getControllerByName(pControllerName);

		// In case this required controller is missing, then throw an exception.
		// TODO: Don't throw an exception in the future, rather gracefully quit
		// and inform the player.
		if (RESULT == null) {
			throw new RuntimeException("Required controller not found: " + pControllerName);
		}

		// if (!RESULT.isInitialised()) {
		// throw new RuntimeException("Required controller not properly
		// initialised: " + pControllerName);
		// }

		return RESULT;
	}

	/**
	 * Returns the controller with the given name. If no controller is found,
	 * null is returned.
	 */
	public BaseController getControllerByName(String pControllerName) {
		if (pControllerName == null || pControllerName.length() == 0) {
			System.out.println("Controller requested but no identifier given");

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

	/**
	 * Returns true if a {@link BaseController} has been registered with the
	 * given name.
	 */
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
		System.out.println("ControllerManager: Removing all controllers");

		mControllers.clear();

	}

}
