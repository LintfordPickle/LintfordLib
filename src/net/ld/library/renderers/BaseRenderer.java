package net.ld.library.renderers;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;

public abstract class BaseRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected RendererManager mRendererManager;
	protected final String mRendererName;
	protected boolean mIsActive;
	protected boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns the comparative Z depth for this renderer. See RendererZTable for
	 * a list of relative values.
	 */
	public float ZDepth() {
		return 0f;
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public void isActive(boolean pNewValue) {
		mIsActive = pNewValue;
	}

	public String rendererName() {
		return mRendererName;

	}

	/**
	 * Returns the {@link RendererManager} that this BaseRenderer is attached
	 * to.
	 */
	public RendererManager rendererManager() {
		return mRendererManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseRenderer(RendererManager pRendererManager, String pRendererName) {
		if (pRendererName == null || pRendererName.length() == 0)
			throw new RuntimeException("Controller names cannot be null or empty!");

		mRendererManager = pRendererManager;
		mRendererName = pRendererName;

		pRendererManager.addRenderer(this);

		isActive(true);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		System.out.println(getClass().getSimpleName() + " GL content loaded");

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		mIsLoaded = false;

	}

	public boolean handleInput(InputState pInputState) {
		return false;
	}

	public void update(GameTime pGameTime) {

	}

	public abstract void draw(RenderState pRenderState);

	// --------------------------------------
	// Methods
	// --------------------------------------

}
