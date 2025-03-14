package net.lintfordlib.renderers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.hud.HudLayoutController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.input.IInputClickedFocusTracker;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.IResizeListener;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

public abstract class RendererManagerBase implements IInputClickedFocusManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final int mEntityGroupUid;
	protected float mTitleHeight;
	protected boolean mIsinitialized;
	protected boolean mResourcesLoaded;
	protected int mRendererIdCounter;

	protected LintfordCore mCore;
	protected DisplayManager mDisplayConfig;
	protected int mSharedGlContentCount;
	protected SharedResources mSharedResources;

	// TODO: Make this more general (i.e. not just the hud, but screen-layout or something).
	protected HudLayoutController mUiStructureController;
	protected IResizeListener mResizeListener;
	protected IInputClickedFocusTracker mTrackedInputControl;

	protected List<BaseRenderer> mRenderers;
	protected List<RenderTarget> mRenderTargets;
	protected List<RenderTarget> mRenderTargetAutoResize;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entityGroupUid() {
		return mEntityGroupUid;
	}

	public IInputClickedFocusTracker getTrackedClickedFocusControl() {
		return mTrackedInputControl;
	}

	public void setTrackedClickedFocusControl(IInputClickedFocusTracker controlToTrack) {
		mTrackedInputControl = controlToTrack;
	}

	public void increaseGlContentCount() {
		mSharedGlContentCount++;
	}

	public boolean decreaseGlContentCount() {
		mSharedGlContentCount--;
		return mSharedGlContentCount <= 0;
	}

	public boolean isinitialized() {
		return mIsinitialized;
	}

	public boolean isLoaded() {
		return mResourcesLoaded;
	}

	public int getNewRendererId() {
		return mRendererIdCounter++;
	}

	public LintfordCore core() {
		return mCore;
	}

	public DisplayManager displayConfig() {
		return mDisplayConfig;
	}

	public List<BaseRenderer> baseRenderers() {
		return mRenderers;
	}

	public HudLayoutController uiStructureController() {
		return mUiStructureController;
	}

	public SharedResources sharedResources() {
		return mSharedResources;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RendererManagerBase(LintfordCore core, int entityGroupUid) {
		mCore = core;
		mEntityGroupUid = entityGroupUid;

		mRenderers = new ArrayList<>();
		mRenderTargets = new ArrayList<>();
		mRenderTargetAutoResize = new ArrayList<>();
		mIsinitialized = false;
		mResourcesLoaded = false;
		mSharedResources = core.sharedResources();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		mUiStructureController = (HudLayoutController) mCore.controllerManager().getControllerByNameRequired(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		mIsinitialized = true;
	}

	public void initializeRenderers() {
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			mRenderers.get(i).initialize(mCore);
		}
	}

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading Resources for all registered renderers");

		mDisplayConfig = resourceManager.config().display();

		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			if (!mRenderers.get(i).isLoaded()) {
				mRenderers.get(i).loadResources(resourceManager);
			}
		}

		// Register a window resize listener so we can reload the RenderTargets when the window size changes
		mResizeListener = this::reloadRenderTargets;
		mDisplayConfig.addResizeListener(mResizeListener);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Unloading Resources for all renderers");

		// TODO: Need to check if the renderers are not being used in other renderermanager.
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			mRenderers.get(i).unloadResources();
		}

		mDisplayConfig.removeResizeListener(mResizeListener);
		mDisplayConfig = null;

		mResourcesLoaded = false;
	}

	public abstract boolean handleInput(LintfordCore core);

	public void update(LintfordCore core) {
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			final var lRenderer = mRenderers.get(i);
			if (!lRenderer.isActive())
				continue;

			lRenderer.update(core);
		}
	}

	public abstract void draw(LintfordCore core);

	// --------------------------------------
	// Methods
	// --------------------------------------

	public RenderTarget createRenderTarget(String name, int width, int height, boolean resizeWithWindow) {
		return createRenderTarget(name, width, height, 1f, resizeWithWindow);
	}

	public RenderTarget createRenderTarget(String name, int width, int height, float scale, boolean resizeWithWindow) {
		return createRenderTarget(name, width, height, 1f, GL11.GL_LINEAR, resizeWithWindow, null);
	}

	public RenderTarget createRenderTarget(String name, int width, int height, float scale, int filterMode, boolean resizeWithWindow, Integer sharedDepthBufferId) {
		var lRenderTarget = getRenderTarget(name);

		if (lRenderTarget != null) {
			if (lRenderTarget.width() != width || lRenderTarget.height() != height) {
				lRenderTarget.resize(width, height);
				Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget with name '" + name + "' already exists. It will be resized.");
			} else {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget with name '" + name + "' already exists. It will be reused.");
			}

			return lRenderTarget;
		}

		lRenderTarget = new RenderTarget(name);
		lRenderTarget.textureFilter(filterMode);
		lRenderTarget.loadResources(width, height, scale, sharedDepthBufferId);

		mRenderTargets.add(lRenderTarget);

		if (resizeWithWindow) {
			mRenderTargetAutoResize.add(lRenderTarget);
		}

		final int lNumRenderTargets = mRenderTargets.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget '" + name + "' added. Currently have " + lNumRenderTargets + " rendertargets.");

		return lRenderTarget;
	}

	public RenderTarget createRenderTargetFromImage(String name, File filename, int filterMode, boolean resizeWithWindow) {
		var lRenderTarget = getRenderTarget(name);

		if (lRenderTarget != null) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget with name '" + name + "' already exists. No new RendreTarget will be created.");
			return lRenderTarget;
		}

		lRenderTarget = new RenderTarget(name);
		lRenderTarget.textureFilter(filterMode);
		lRenderTarget.loadResourcesFromImage(filename.getAbsolutePath());

		mRenderTargets.add(lRenderTarget);

		if (resizeWithWindow) {
			mRenderTargetAutoResize.add(lRenderTarget);
		}

		final int lNumRenderTargets = mRenderTargets.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget '" + name + "' added. Currently have " + lNumRenderTargets + " rendertargets.");

		return lRenderTarget;
	}

	public void unloadRenderTarget(RenderTarget renderTarget) {
		if (renderTarget == null)
			return;

		if (mRenderTargetAutoResize.contains(renderTarget)) {
			mRenderTargetAutoResize.remove(renderTarget);
		}

		if (mRenderTargets.contains(renderTarget)) {
			mRenderTargets.remove(renderTarget);
		}

		renderTarget.unbind();
		renderTarget.unloadResources();
	}

	public void releaseRenderTargetByName(String name) {
		final var lResult = getRenderTarget(name);
		if (lResult != null) {
			unloadRenderTarget(lResult);
		}
	}

	public RenderTarget getRenderTarget(String name) {
		final int lRenderTargetCount = mRenderTargets.size();
		for (int i = 0; i < lRenderTargetCount; i++) {
			if (mRenderTargets.get(i).targetName().equals(name))
				return mRenderTargets.get(i);
		}

		return null;
	}

	public void reloadRenderTargets(final int width, final int height) {
		final int lRenderTargetCount = mRenderTargetAutoResize.size();
		for (int i = 0; i < lRenderTargetCount; i++) {
			mRenderTargetAutoResize.get(i).resize(width, height);
		}
	}

	public BaseRenderer getRendererRequired(String rendererName) {
		final var lRenderer = getRenderer(rendererName);

		if (lRenderer == null) {
			throw new RuntimeException("Required Renderer not found: " + rendererName + ". Check you are using the correct GroupEntityUid");
		}

		return lRenderer;
	}

	public BaseRenderer getRenderer(String rendererName) {
		if (rendererName == null || rendererName.length() == 0) {
			return null;
		}

		final int lNumRenderers = mRenderers.size();
		for (int i = 0; i < lNumRenderers; i++) {
			if (mRenderers.get(i).rendererName().equals(rendererName)) {
				return mRenderers.get(i);
			}
		}

		Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot find renderer with the name: '" + rendererName + "'.");
		return null;
	}

	/** Adds a renderer to the manager. This automatically re-orders the renderers to take into consideration their relative z-depths. */
	public void addRenderer(BaseRenderer renderer) {
		if (getRenderer(renderer.rendererName()) == null) {
			mRenderers.add(renderer);
			Collections.sort(mRenderers, new ZLayerComparator());

		} else {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot add the same renderer twice! (" + renderer.getClass().getSimpleName() + "/" + renderer.mRendererName + ")");
		}
	}

	public void removeRenderer(BaseRenderer renderer) {
		if (mRenderers.contains(renderer)) {
			mRenderers.remove(renderer);
		}
	}

	public void removeAllRenderers() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "RendererManager: Removing all renderers");

		mRenderers.clear();
	}

	/** Unloads all {@link BaseRenderer} instances registered to this {@link SimpleRendererManager} which have the specified entityGroupUid. */
	public void removeRendererGroup(final int entityGroupID) {
		final var lRendererUpdateList = new ArrayList<BaseRenderer>();
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			lRendererUpdateList.add(mRenderers.get(i));
		}

		for (int i = 0; i < lRendererCount; i++) {
			if (lRendererUpdateList.get(i).entityGroupID() == entityGroupID) {
				lRendererUpdateList.get(i).unloadResources();

				mRenderers.remove(lRendererUpdateList.get(i));
			}
		}
	}

}
