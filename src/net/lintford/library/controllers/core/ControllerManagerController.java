package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;

public class ControllerManagerController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "ControllerManagerController";

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isinitialized() {
		return false;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ControllerManagerController(ControllerManager pControllerManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

}
