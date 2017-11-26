package net.lintford.library.renderers;

import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;

public abstract class BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String TAG = "BaseRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected RendererManager mRendererManager;
	protected final String mRendererName;
	protected boolean mIsActive;
	protected boolean mIsLoaded;

	/** A group ID is assigned to all {@link BaseRenderer} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu) */
	protected int mGroupID;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** A group ID is assigned to all {@link BaseRenderer} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu) */
	public int groupID() {
		return mGroupID;
	}

	/** Returns the comparative Z depth for this renderer. See RendererZTable for a list of relative values. */
	public int ZDepth() {
		return 0;
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

	/** Returns the {@link RendererManager} that this BaseRenderer is attached to. */
	public RendererManager rendererManager() {
		return mRendererManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseRenderer(final RendererManager pRendererManager, final String pRendererName, final int pGroupID) {
		if (pRendererName == null || pRendererName.length() == 0)
			throw new RuntimeException("Controller names cannot be null or empty!");

		mRendererManager = pRendererManager;
		mRendererName = pRendererName;

		pRendererManager.addRenderer(this);

		mGroupID = pGroupID;

		isActive(true);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "Loading GL Content (" + getClass().getSimpleName() + ")");
		mIsLoaded = true;

	}

	public void unloadGLContent() {
		DebugManager.DEBUG_MANAGER.logger().i(TAG, "Unloading GL Content (" + getClass().getSimpleName() + ")");
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
