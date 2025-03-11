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
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.input.IInputClickedFocusTracker;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.core.rendering.RenderStage;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.IResizeListener;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

// TODO: The RendererManager need to be sharing ALOT more of its resources with other render managers ..

public class RendererManager implements IInputClickedFocusManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_WINDOW_INDEX = -1;
	public static final int WINDOW_ALREADY_REGISTERED = -2;

	// --------------------------------------
	// variables
	// --------------------------------------

	/** Used both as a ControllerGroupID and RendererGroupID */
	protected final int mEntityGroupUid;

	private LintfordCore mCore;

	private DisplayManager mDisplayConfig;

	/** Tracks the number of times a loadResources method is called using this renderManager/entityGroupId */
	private int mSharedGlContentCount;

	private HudLayoutController mUiStructureController;
	private List<BaseRenderer> mRenderers;

	protected float mTitleHeight;

	private boolean mIsinitialized;
	private boolean mResourcesLoaded;

	private List<RenderTarget> mRenderTargets;
	private List<RenderTarget> mRenderTargetAutoResize;
	private RenderTarget mCurrentTarget;

	private IResizeListener mResizeListener;
	private int mRendererIdCounter;

	protected SharedResources mSharedResources;

	protected IInputClickedFocusTracker mTrackedInputControl;

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

	public RendererManager(LintfordCore core, int entityGroupUid) {
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

		mSharedResources.loadResources(resourceManager);
		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Unloading Resources for all renderers");

//		final int lRendererCount = mRenderers.size();
//		for (int i = 0; i < lRendererCount; i++) {
//			mRenderers.get(i).unloadResources();
//		}

		mDisplayConfig.removeResizeListener(mResizeListener);
		mDisplayConfig = null;

		mResourcesLoaded = false;
	}

	public boolean handleInput(LintfordCore core) {
		// Handle the base renderer input
		final int lNumRenderers = mRenderers.size();
		for (int i = lNumRenderers - 1; i >= 0; i--) {
			mRenderers.get(i).handleInput(core);

			// TODO: Window renderers need to be processed first, and can have 'exclusive' input (onHandledInput return).

		}

		return false;
	}

	public void update(LintfordCore core) {
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			final var lRenderer = mRenderers.get(i);
			if (!lRenderer.isActive())
				continue;

			lRenderer.update(core);
		}
	}

	public void draw(LintfordCore core) {
		// TODO: automatic drawing or not will be added when the StructuredRendererManager is available (then its just a flat list of renderers).
	}

	// -- NEW

	public void drawStageHierarchy(LintfordCore core) {

		// TODO: render all stages and passes
		final int lNumStages = mRenderStages.size();
		for (int i = 0; i < lNumStages; i++) {
			final var lStage = mRenderStages.get(i);
			final var lRenderPass = lStage.renderPass();

			if (lStage.renderTarget() != null) {
				lStage.renderTarget().bind();

				GL11.glClearColor(0.06f, 0.18f, 0.31f, 1.0f);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

				if (!lStage.hasSharedDepthBuffer()) {
					// parent stages clears depth buffer
					GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				}
			} else {
				if (mActiveRenderTarget != null) {
					mActiveRenderTarget.bind();
				}
			}

			final var lRenderers = lStage.renderers();
			final var lNumRenderers = lRenderers.size();
			for (int j = 0; j < lNumRenderers; j++) {
				final var lRenderer = lRenderers.get(j);

				lRenderer.draw(core, lRenderPass);
			}

			if (lStage.renderTarget() != null) {
				lStage.renderTarget().unbind();

				//
				if (mActiveRenderTarget != null) {
					mActiveRenderTarget.bind();
				}
			}
		}
	}

	public void drawStageIndex(LintfordCore core, int stageIndex) {
		// TODO: render desired stage (retreived by index)
	}

	/** draws the given rt (by uid) into the currently bound texture buffer. */
	public void drawStageRtByUid(LintfordCore core, int stageUid) {
		final var lDesiredStage = getRenderStageByUid(stageUid);
		if (lDesiredStage == null)
			return;

		final var lRenderTarget = lDesiredStage.renderTarget();
		if (lRenderTarget == null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "RenderStage '" + lDesiredStage.stageName + "' does not have a dedicated render target. Nothing will be rendered!");
			return;
		}

		final var rtw = mActiveRenderTarget.width() * .8f;
		final var rth = mActiveRenderTarget.height() * .8f;
		final var dx = -rtw * .5f;
		final var dy = -rth * .5f;

		// TODO: remove dependency on debug drawer ffs
		Debug.debugManager().drawers().drawRenderTargetImmediate(core, new Rectangle(dx, dy, rtw, rth), 0.01f, mActiveRenderTarget);
	}

	/** draws the given rt (by uid) into the currently bound texture buffer. */
	public void drawStageByUid(LintfordCore core, int stageUid) {
		final var lDesiredStage = getRenderStageByUid(stageUid);
		if (lDesiredStage == null)
			return;

		final var lRenderPass = lDesiredStage.renderPass();
		final var lRenderers = lDesiredStage.renderers();
		final var lNumRenderers = lRenderers.size();
		for (int j = 0; j < lNumRenderers; j++) {
			final var lRenderer = lRenderers.get(j);

			lRenderer.draw(core, lRenderPass);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private List<RenderStage> mRenderStages = new ArrayList<>();
	private RenderTarget mActiveRenderTarget; // this is what will be drawn into the back-buffer

	public void setRenderTarget(RenderTarget rt) {
		mActiveRenderTarget = rt;
	}

	public RenderStage getRenderStageByUid(int stageUid) {
		final var lNumStages = mRenderStages.size();
		for (int i = 0; i < lNumStages; i++) {
			final var lStage = mRenderStages.get(i);
			if (lStage.stageUid == stageUid)
				return lStage;
		}

		return null;
	}

	// RenderStageType type
	public RenderStage addRenderStage(String name, RenderPass pass, int stageUid) {
		final var lNewStage = new RenderStage(name, pass, stageUid);

		// TODO: sort the list by Z-Order.
		// TODO: ensure no duplicate names.
		// TODO: ensure no duplicate uids.
		mRenderStages.add(lNewStage);
		return lNewStage;
	}

	public void addRenderTargetToStage(RenderStage stage, int width, int height, Integer sharedDepthId) {
		final var rt = createRenderTarget(stage.stageName, width, height, 1.f, GL11.GL_LINEAR, true, sharedDepthId);
		stage.renderTarget(rt);
	}

	public void addToStage(BaseRenderer renderer, RenderPass renderPass) {

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

	/** Unloads all {@link BaseRenderer} instances registered to this {@link RendererManager} which have the specified entityGroupUid. */
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

	public void setRenderTarget(String name) {
		if (name == null) {
			if (mCurrentTarget != null) {
				mCurrentTarget.unbind();
				return;
			}
		}

		final var lResult = getRenderTarget(name);
		if (lResult != null) {
			if (mCurrentTarget != null) {
				mCurrentTarget.unbind();
			}
		}

		lResult.bind();
	}

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
}