package net.lintford.library.renderers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.input.mouse.IInputProcessor;

public abstract class BaseRenderer implements IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String TAG = "BaseRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final int mRendererId;
	protected RendererManager mRendererManager;
	protected final String mRendererName;
	protected boolean mIsActive;
	protected boolean mIsManagedDraw;
	protected boolean mResourcesLoaded;

	/**
	 * An entity group ID is assigned to all {@link BaseRenderer} instances. It allows you to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu)
	 */
	protected int mEntityGroupUid;

	protected float mInputTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void isManagedDraw(boolean newValue) {
		mIsManagedDraw = newValue;
	}

	public boolean isManagedDraw() {
		return mIsManagedDraw;
	}

	/** Returns the unique Id assigned to this BaseRenderer instance. */
	public int rendererId() {
		return mRendererId;
	}

	/**
	 * A group ID is assigned to all {@link BaseRenderer} instances. It allows the developer to programmatically unload batches of particular parts of the game when required (i.e. unload the game controllers when returning to the main menu)
	 */
	public int entityGroupID() {
		return mEntityGroupUid;
	}

	/** Returns the comparative Z depth for this renderer. See RendererZTable for a list of relative values. */
	public int ZDepth() {
		return 0;
	}

	public boolean isLoaded() {
		return mResourcesLoaded;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public void isActive(boolean newValue) {
		mIsActive = newValue;
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

	public BaseRenderer(final RendererManager rendererManager, final String rendererName, final int entityGroupUid) {
		if (rendererManager == null || rendererName == null || rendererName.length() == 0)
			throw new RuntimeException("Renderer names cannot be null or empty!");

		mRendererId = rendererManager.getNewRendererId();

		mRendererManager = rendererManager;
		mRendererName = rendererName;

		mIsManagedDraw = true;

		if (rendererManager != null) {
			rendererManager.addRenderer(this);
		}

		mEntityGroupUid = entityGroupUid;

		isActive(true);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public abstract boolean isInitialized();

	public abstract void initialize(LintfordCore core);

	public void loadResources(ResourceManager resourceManager) {
		Debug.debugManager().logger().i(TAG, "Loading GL Content (" + getClass().getSimpleName() + ")");
		mResourcesLoaded = true;
	}

	public void unloadResources() {
		Debug.debugManager().logger().i(TAG, "Unloading GL Content: " + getClass().getSimpleName());
		mResourcesLoaded = false;
	}

	public boolean handleInput(LintfordCore core) {
		return false;
	}

	public void update(LintfordCore core) {
		if(mInputTimer > 0)
			mInputTimer -= core.gameTime().elapsedTimeMilli();
		return;
	}

	public abstract void draw(LintfordCore core);

	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0.f;
	}

	public void resetCoolDownTimer() {
		mInputTimer = INPUT_COOLDOWN_TIME;
	}

	public boolean allowKeyboardInput() {
		return false;
	}

	public boolean allowGamepadInput() {
		return false;
	}

	public boolean allowMouseInput() {
		return false;
	}

}
