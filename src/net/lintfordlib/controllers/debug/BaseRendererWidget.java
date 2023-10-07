package net.lintfordlib.controllers.debug;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.renderers.BaseRenderer;

public class BaseRendererWidget extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2484883804535495015L;

	// --------------------------------------
	// Variables
	// --------------------------------------

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

	public void handleInput(LintfordCore core) {

	}

	public void update(LintfordCore core) {
		if (baseRenderer != null)
			isRendererActive = baseRenderer.isActive();
	}
}