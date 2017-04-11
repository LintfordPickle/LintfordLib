package net.ld.library.renderers.windows.components;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.renderers.windows.UIWindow;

public abstract class UIWidget extends Rectangle {

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

	public UIWidget(final UIWindow pParentWindow, final Rectangle pBounds) {
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

	public boolean handleInput(final InputState pInputState) {
		return false;
	}

	public void update(final GameTime pGameTime) {

	}

	public abstract void draw(final RenderState pRenderState);

}
