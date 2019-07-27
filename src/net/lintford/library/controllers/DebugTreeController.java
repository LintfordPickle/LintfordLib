package net.lintford.library.controllers;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

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
	public boolean isinitialized() {
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
	public void initialize(LintfordCore pCore) {

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
