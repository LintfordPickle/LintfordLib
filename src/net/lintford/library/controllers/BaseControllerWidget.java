package net.lintford.library.controllers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;

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

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseControllerWidget() {

	}

	public void handleInput(LintfordCore pCore) {
		

	}

	public void update(LintfordCore pCore) {
		if (!isExpanded) {
			h = 25; // this is the height of the control in its collapsed state (only the display name is visible)

		} else {
			h = 100;
			
		}
	}

}
