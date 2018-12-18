package net.lintford.library.renderers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.ResourceManager;

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

	/**
	 * An entity group ID is assigned to all {@link BaseRenderer} instances. It allows you to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning
	 * to the main menu)
	 */
	protected int mEntityGroupID;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * A group ID is assigned to all {@link BaseRenderer} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the
	 * main menu)
	 */
	public int entityGroupID() {
		return mEntityGroupID;
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

	public BaseRenderer(final RendererManager pRendererManager, final String pRendererName, final int pEntityGroupID) {
		if (pRendererName == null || pRendererName.length() == 0)
			throw new RuntimeException("Controller names cannot be null or empty!");

		mRendererManager = pRendererManager;
		mRendererName = pRendererName;

		if (pRendererManager != null) {
			pRendererManager.addRenderer(this);

		}

		mEntityGroupID = pEntityGroupID;

		isActive(true);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public abstract void initialise(LintfordCore pCore);

	public void loadGLContent(ResourceManager pResourceManager) {
		Debug.debugManager().logger().i(TAG, "Loading GL Content (" + getClass().getSimpleName() + ")");
		mIsLoaded = true;

	}

	public void unloadGLContent() {
		Debug.debugManager().logger().i(TAG, "Unloading GL Content (" + getClass().getSimpleName() + ")");
		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		return false;
	}

	public void update(LintfordCore pCore) {

	}

	public abstract void draw(LintfordCore pCore);

	// --------------------------------------
	// Methods
	// --------------------------------------

}
