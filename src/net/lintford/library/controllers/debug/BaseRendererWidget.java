package net.lintford.library.controllers.debug;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.renderers.BaseRenderer;

public class BaseRendererWidget extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2484883804535495015L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	// Id is used when building the BaseRendererWidget tree (to check we have captured all renderers).
	public int rendererId;
	public int rendererLevel;
	public BaseRenderer baseRenderer;
	public String displayName;
	public boolean isRendererActive;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseRendererWidget() {

	}

	public void handleInput(LintfordCore pCore) {

	}

	public void update(LintfordCore pCore) {
		if (baseRenderer != null) {
			isRendererActive = baseRenderer.isActive();

		}

	}

}
