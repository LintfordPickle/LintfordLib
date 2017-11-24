package net.ld.library.core.graphics.helpers;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.config.IResizeListener;
import net.ld.library.core.graphics.ResourceManager;

public class FullScreenTexturedQuad extends TexturedQuad implements IResizeListener {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected DisplayConfig mDisplayConfig;

	protected int mWindowWidth;
	protected int mWindowHeight;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FullScreenTexturedQuad() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mDisplayConfig = pResourceManager.displayConfig();
		mDisplayConfig.addResizeListener(this);

		mWindowWidth = mDisplayConfig.windowSize().x;
		mWindowHeight = mDisplayConfig.windowSize().y;

		createModelMatrix();

	}

	public void unloadGLContent() {
		super.unloadGLContent();

		mDisplayConfig.removeResizeListener(this);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void createModelMatrix() {
		mModelMatrix.setIdentity();
		mModelMatrix.scale(mWindowWidth, mWindowHeight, 1);
		mModelMatrix.translate(0, 0, mZDepth);

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onResize(int pWidth, int pHeight) {
		mWindowWidth = pWidth;
		mWindowHeight = pHeight;

	}
}
