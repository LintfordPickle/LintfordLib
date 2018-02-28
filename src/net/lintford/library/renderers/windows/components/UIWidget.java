package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.renderers.windows.UIWindow;

public abstract class UIWidget extends AARectangle {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected UIWindow mParentWindow;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIWidget(final UIWindow pParentWindow) {
		super();

		mParentWindow = pParentWindow;

	}

	public UIWidget(final UIWindow pParentWindow, final AARectangle pBounds) {
		super(pBounds);

		mParentWindow = pParentWindow;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void loadGLContent(final ResourceManager pResourceManager) {

	}

	public void unloadGLContent() {

	}

	public boolean handleInput(LintfordCore pCore) {
		return false;
	}

	public void update(LintfordCore pCore) {

	}

	public abstract void draw(LintfordCore pCore);

}
