package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.renderers.windows.UIRectangle;
import net.lintford.library.renderers.windows.UIWindow;

public abstract class UIWidget extends UIRectangle {

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

	public UIWidget(final UIWindow pParentWindow, final UIRectangle pBounds) {
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

	public boolean handleInput(final InputState pInputState, ICamera pHUDCamera) {
		return false;
	}

	public void update(final GameTime pGameTime) {

	}

	public abstract void draw(final RenderState pRenderState);

}
