package net.lintfordlib.core.graphics.geometry;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.IResizeListener;

public class FullScreenTexturedQuad extends TexturedQuad implements IResizeListener {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected DisplayManager mDisplayConfig;

	protected int mWindowWidth;
	protected int mWindowHeight;

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mDisplayConfig = resourceManager.config().display();
		mDisplayConfig.addResizeListener(this);

		mWindowWidth = mDisplayConfig.windowWidth();
		mWindowHeight = mDisplayConfig.windowHeight();

		createModelMatrix();
	}

	@Override

	public void unloadResources() {
		super.unloadResources();

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
	public void onResize(int windowWidth, int windowHeight) {
		mWindowWidth = windowWidth;
		mWindowHeight = windowHeight;
	}
}