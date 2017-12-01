package net.lintford.library.core.graphics.geometry;

import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.options.IResizeListener;

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

		mDisplayConfig = pResourceManager.masterConfig().display();
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
