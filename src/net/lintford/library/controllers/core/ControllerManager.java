package net.lintford.library.controllers.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;

public class ControllerManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LintfordCore mCore;
	private int mControllerIdCounter;
	private Map<Integer, List<BaseController>> mControllers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Map<Integer, List<BaseController>> allControllers() {
		return mControllers;
	}

	/** Returns a list of controllers currently registered with the {@link ControllerManager}. */
	public List<BaseController> controllers(int controllerGroupId) {
		return mControllers.get(controllerGroupId);
	}

	public LintfordCore core() {
		return mCore;
	}

	public int getNewControllerId() {
		return mControllerIdCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ControllerManager(LintfordCore core) {
		mCore = core;
		mControllerIdCounter = 0;
		mControllers = new HashMap<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core, int controllerGroup) {
		final var lControllerList = controllers(controllerGroup);
		if (lControllerList == null)
			return false;

		final var lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (!lControllerList.get(i).isActive())
				continue;

			if (lControllerList.get(i).handleInput(core))
				return true;
		}

		return false;
	}

	public void update(LintfordCore core, int controllerGroup) {
		final var lControllerList = controllers(controllerGroup);
		if (lControllerList == null)
			return;

		final var lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (!lControllerList.get(i).isActive())
				continue;

			lControllerList.get(i).update(core);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initializeControllers(LintfordCore core) {
		for (final var lEntry : mControllers.entrySet()) {
			final var lControllerList = lEntry.getValue();
			final var lCount = lControllerList.size();
			for (int i = 0; i < lCount; i++) {
				if (!lControllerList.get(i).isActive())
					continue;

				if (lControllerList.get(i).isInitialized())
					continue;

				lControllerList.get(i).initialize(core);
			}
		}
	}

	public BaseController getControllerByType(Class<?> controllerClass, int entityGroupUid) {
		final var lControllerList = controllers(entityGroupUid);
		if (lControllerList == null)
			return null;

		final var lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (lControllerList.get(i).getClass().equals(controllerClass)) {
				return lControllerList.get(i);
			}
		}

		return null;
	}

	public List<BaseController> getControllerGroupByUid(int entityGroupUid) {
		return controllers(entityGroupUid);
	}

	/**
	 * Returns the controller with the given name. In case no {@link BaseController} instance with the given name is found (i.e. has not been registered) or has not been initialized, an exception will be thrown.
	 */
	public BaseController getControllerByNameRequired(String controllerName, int entityGroupUid) {
		final var lController = getControllerByName(controllerName, entityGroupUid);

		if (lController == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Required controller not found: " + controllerName);
			throw new RuntimeException(String.format("Required controller not found: %s. Check you are using the correct pGroupEntityID", controllerName));
		}

		return lController;
	}

	/** Returns the controller with the given name. If no controller is found, null is returned. */
	public BaseController getControllerByName(String controllerName, int entityGroupUid) {
		if (controllerName == null || controllerName.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Controller requested but no identifier given");
			return null;
		}

		final var lControllerList = controllers(entityGroupUid);
		if (lControllerList == null)
			return null;

		final var lCount = lControllerList.size();
		for (int i = 0; i < lCount; i++) {
			if (lControllerList.get(i).controllerName().equals(controllerName)) {
				return lControllerList.get(i);
			}
		}

		return null;
	}

	/** Returns true if a {@link BaseController} has been registered with the given name. */
	public boolean controllerExists(final String controllerName, int entityGroupUid) {
		return getControllerByName(controllerName, entityGroupUid) != null;
	}

	public void addController(BaseController controller, int entityGroupUid) {
		if (!controller.isUniqueController()) {
			var lControllerList = controllers(entityGroupUid);
			if (lControllerList == null) {
				lControllerList = new ArrayList<>();
				mControllers.put(entityGroupUid, lControllerList);
			}

			lControllerList.add(controller);
			return;
		}

		if (getControllerByName(controller.controllerName(), entityGroupUid) == null) {
			List<BaseController> lControllerList = controllers(entityGroupUid);
			if (lControllerList == null) {
				lControllerList = new ArrayList<>();
				mControllers.put(entityGroupUid, lControllerList);
			}

			lControllerList.add(controller);
		}
	}

	public void removeController(BaseController controller, int entityGroupUid) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Removing Controller " + controller.controllerName() + " from id:" + entityGroupUid);

		final var lControllerList = controllers(entityGroupUid);
		if (lControllerList == null)
			return;

		if (lControllerList.contains(controller))
			lControllerList.remove(controller);

		controller.unloadController();
	}

	/** Unloads all {@link BaseController} instances registered to this {@link ControllerManager} which have the given group ID assigned to them. */
	public void removeControllerGroup(final int entityGroupUid) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Removing ControllerGroup id:" + entityGroupUid);

		final var lControllerList = mControllers.get(entityGroupUid);
		if (lControllerList == null)
			return;

		final var lControllerCount = lControllerList.size();
		for (int i = 0; i < lControllerCount; i++) {
			lControllerList.get(i).unloadController();
		}

		lControllerList.clear();
		mControllers.remove(entityGroupUid);
	}

	public void removeAllControllers() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "ControllerManager: Removing all controllers");

		for (var lControllerEntry : mControllers.entrySet()) {
			final var lControllerGroupKey = lControllerEntry.getKey();
			removeControllerGroup(lControllerGroupKey);
		}

		mControllers.clear();
	}
}