package net.lintford.library.controllers.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;

public class ControllerManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LintfordCore mCore;
	private Map<Integer, List<BaseController>> mControllers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns a list of controllers currently registered with the {@link ControllerManager}. */
	public List<BaseController> controllers(int pControllerGroupID) {
		return mControllers.get(pControllerGroupID);

	}

	public LintfordCore core() {
		return mCore;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ControllerManager(LintfordCore pCore) {
		mCore = pCore;
		mControllers = new HashMap<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore, int pControllerGroup) {
		List<BaseController> lControllerList = controllers(pControllerGroup);
		if (lControllerList == null)
			return false;

		if (pCore.input().keyDown(GLFW.GLFW_KEY_F4)) {
			System.out.println("Controller groung count: " + mControllers.size());
			final int lCount = lControllerList.size();
			System.out.println("Controller count: " + lCount);
			for (int i = 0; i < lCount; i++) {
				System.out.printf("  %d: %s\n", i, lControllerList.get(i).getClass().getSimpleName());

			}

		}

		final int lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (!lControllerList.get(i).isActive())
				continue;

			if (lControllerList.get(i).handleInput(pCore)) {
				return true;

			}

		}

		return false;

	}

	public void update(LintfordCore pCore, int pControllerGroup) {
		List<BaseController> lControllerList = controllers(pControllerGroup);
		if (lControllerList == null)
			return;

		int lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (!lControllerList.get(i).isActive())
				continue;

			lControllerList.get(i).update(pCore);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialiseControllers(LintfordCore pCore) {

		for (Map.Entry<Integer, List<BaseController>> lEntry : mControllers.entrySet()) {
			List<BaseController> lControllerList = lEntry.getValue();

			int lCount = lControllerList.size();
			for (int i = 0; i < lCount; i++) {
				if (!lControllerList.get(i).isActive())
					continue;

				if (lControllerList.get(i).isInitialised())
					continue;

				lControllerList.get(i).initialise(pCore);
			}

		}

	}

	public BaseController getControllerByType(Class<?> pClass, int pEntityGroupID) {
		List<BaseController> lControllerList = controllers(pEntityGroupID);
		if (lControllerList == null)
			return null;

		int lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (lControllerList.get(i).getClass().equals(pClass)) {
				return lControllerList.get(i);

			}

		}

		return null;
	}

	/** Returns the controller with the given name. In case no {@link BaseController} instance with the given name is found (i.e. has not been registered) or has not been initialised, an exception will be thrown. */
	public BaseController getControllerByNameRequired(String pControllerName, int pEntityGroupID) {
		final BaseController RESULT = getControllerByName(pControllerName, pEntityGroupID);

		// In case this required controller is missing, then throw an exception.
		// TODO: Don't throw an exception in the future, rather gracefully quit and inform the player.
		if (RESULT == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Required controller not found: " + pControllerName);

			throw new RuntimeException(String.format("Required controller not found: %s. Check you are using the correct pGroupEntityID", pControllerName));

		}

		return RESULT;
	}

	/** Returns the controller with the given name. If no controller is found, null is returned. */
	public BaseController getControllerByName(String pControllerName, int pEntityGroupID) {
		if (pControllerName == null || pControllerName.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Controller requested but no identifier given");

			return null;
		}

		List<BaseController> lControllerList = controllers(pEntityGroupID);
		if (lControllerList == null)
			return null;

		int lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (lControllerList.get(i).controllerName().equals(pControllerName)) {
				return lControllerList.get(i);
			}
		}

		return null;

	}

	/** Returns true if a {@link BaseController} has been registered with the given name. */
	public boolean controllerExists(final String pControllerName, int pEntityGroupID) {
		return getControllerByName(pControllerName, pEntityGroupID) != null;
	}

	public void addController(BaseController pController, int pEntityGroupID) {
		// Only add one controller of each time (and with unique names).
		if (getControllerByName(pController.controllerName(), pEntityGroupID) == null) {
			List<BaseController> lControllerList = controllers(pEntityGroupID);
			if (lControllerList == null) {
				// In this case, the ControllerList itself doesn't exit, so we need to create one
				lControllerList = new ArrayList<>();
				mControllers.put(pEntityGroupID, lControllerList);

			}

			lControllerList.add(pController);

		} else {
			// Controller already exists

		}

	}

	public void removeController(BaseController pController, int pEntityGroupID) {
		List<BaseController> lControllerList = controllers(pEntityGroupID);
		if (lControllerList == null)
			return;

		if (lControllerList.contains(pController)) {
			lControllerList.remove(pController);

		}

	}

	public void removeAllControllers() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ControllerManager: Removing all controllers");

		mControllers.clear();

	}

	/** Unloads all {@link BaseController} instances registered to this {@link ControllerManager} which have the given group ID assigned to them. */
	public void removeControllerGroup(final int pEntityGroupID) {
		// Heap assignment
		final List<BaseController> lControllerList = mControllers.get(pEntityGroupID);
		if (lControllerList == null)
			return;

		final int CONTROLLER_COUNT = lControllerList.size();
		for (int i = 0; i < CONTROLLER_COUNT; i++) {
			lControllerList.get(i).unload();

		}

		lControllerList.clear();
		mControllers.remove(pEntityGroupID);

	}

}
