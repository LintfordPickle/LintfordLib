package net.lintfordlib.controllers.debug;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;

public class BaseControllerWidget extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2484883804535495015L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	// Id is used when building the BaseControllerDebugArea tree (to check we have captured all controllers).
	public int controllerId;
	public int controllerLevel;
	public BaseController baseController;
	public String displayName;
	public boolean isExpanded;
	public boolean isControllerActive;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseControllerWidget() {

	}

	public void handleInput(LintfordCore core) {

	}

	public void update(LintfordCore core) {
		if (baseController != null)
			isControllerActive = baseController.isActive();
	}
}