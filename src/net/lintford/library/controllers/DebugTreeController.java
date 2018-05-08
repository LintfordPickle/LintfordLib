package net.lintford.library.controllers;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.core.ControllerManager;

public class DebugTreeController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "ControllerTreeController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public List<DebugTreeComponent> mDebugTreeComponents;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialised() {
		return false;
	}

	public List<DebugTreeComponent> treeComponents() {
		return mDebugTreeComponents;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugTreeController(ControllerManager pControllerManager, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		mDebugTreeComponents = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {

	}

	@Override
	public void unload() {
		if (mDebugTreeComponents != null) {
			mDebugTreeComponents.clear();
			mDebugTreeComponents = null;

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addDebugComponent() {

	}

}
