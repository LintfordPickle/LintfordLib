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
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.core.rendering.RenderState;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.IResizeListener;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.interfaces.UIWindowChangeListener;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

// TODO: The RendererManager need to be sharing ALOT more of its resources with other render managers ..

public class RendererManager implements IInputClickedFocusManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** This refers to the BaseRenderers responsible for rendering the game components. */
	public static final boolean RENDER_GAME_RENDERABLES = true;

	/** This refers to the BaseRenderers responsible for rendering the UI components. */
	public static final boolean RENDER_UI_WINDOWS = true;

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
	private List<UiWindow> mWindowRenderers; // TODO: remove this in the new version (using the structure)

	// START SHARE - move to SharedResources class and share amoungst all RendererManagers

	// END SHARE

	protected float mTitleHeight;

	private boolean mIsinitialized;
	private boolean mResourcesLoaded;

	private List<UIWindowChangeListener> mListeners;

	private List<RenderTarget> mRenderTargets;
	private List<RenderTarget> mRenderTargetAutoResize;
	private RenderTarget mCurrentTarget;

	private IResizeListener mResizeListener;
	private int mRendererIdCounter;

	protected final RenderState mRenderState = new RenderState();
	protected SharedResources mSharedResources;

	protected IInputClickedFocusTracker mTrackedInputControl;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public RenderState renderState() {
		return mRenderState;
	}

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

	public List<UiWindow> windows() {
		return mWindowRenderers;
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
		mWindowRenderers = new ArrayList<>();
		mRenderTargets = new ArrayList<>();
		mRenderTargetAutoResize = new ArrayList<>();

		mListeners = new ArrayList<>();

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

		// TODO: these are the same, separated by stage/pass
		final int lUiRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lUiRendererCount; i++) {
			mWindowRenderers.get(i).initialize(mCore);
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

		// TODO: these are the same, separated by stage/pass
		final int lUiRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lUiRendererCount; i++) {
			if (!mWindowRenderers.get(i).isLoaded()) {
				mWindowRenderers.get(i).loadResources(resourceManager);
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

		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			mRenderers.get(i).unloadResources();
		}

		final int lWindowRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lWindowRendererCount; i++) {
			mWindowRenderers.get(i).unloadResources();
		}

		mDisplayConfig.removeResizeListener(mResizeListener);
		mDisplayConfig = null;

		mResourcesLoaded = false;
	}

	public boolean handleInput(LintfordCore core) {
		final int lNumWindowRenderers = mWindowRenderers.size();

		// We handle the input to the UI Windows in the game with priority.
		for (int i = 0; i < lNumWindowRenderers; i++) {
			final var lWindow = mWindowRenderers.get(i);
			final var lResult = lWindow.handleInput(core);
			if (lResult && lWindow.exclusiveHandleInput()) {
				// return true;
			}
		}

		// Handle the base renderer input
		final int lNumRenderers = mRenderers.size();
		for (int i = lNumRenderers - 1; i >= 0; i--) {
			mRenderers.get(i).handleInput(core);
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

		final int lWindowRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lWindowRendererCount; i++) {
			final var lWindowRenderer = mWindowRenderers.get(i);
			if (!lWindowRenderer.isActive())
				continue;

			lWindowRenderer.update(core);
		}
	}

	public void draw(LintfordCore core) {
		if (RENDER_GAME_RENDERABLES) {
			drawRenderersAllPasses(core);
		}

		if (RENDER_UI_WINDOWS) {
			drawWindowRenderers(core);
		}
	}

	public void drawRenderersWithPass(LintfordCore core, RenderPass renderPass) {
		final int lNumBaseRenderers = mRenderers.size();

		final var lRenderPassTypeIndex = renderPass.passTypeIndex();

		for (int j = 0; j < lNumBaseRenderers; j++) {
			final var lRenderer = mRenderers.get(j);
			if (!lRenderer.isActive() || !lRenderer.isManagedDraw())
				continue;

			if (!lRenderer.isRegisteredForPass(lRenderPassTypeIndex))
				continue;

			lRenderer.draw(core, renderPass);
		}
	}

	public void drawRenderersAllPasses(LintfordCore core) {
		final var lRenderPasses = mRenderState.renderPasses();
		final int lNumBaseRenderers = mRenderers.size();

		final var lNumRenderPasses = lRenderPasses.size();
		for (int i = 0; i < lNumRenderPasses; i++) {
			final var lRenderPass = lRenderPasses.get(i);
			final var lRenderPassTypeIndex = lRenderPass.passTypeIndex();

			for (int j = 0; j < lNumBaseRenderers; j++) {
				final var lRenderer = mRenderers.get(j);
				if (!lRenderer.isActive() || !lRenderer.isManagedDraw())
					continue;

				if (!lRenderer.isRegisteredForPass(lRenderPassTypeIndex))
					continue;

				lRenderer.draw(core, lRenderPass);
			}
		}
	}

	public void drawWindowRenderers(LintfordCore core) {
		final int lNumWindowRenderers = mWindowRenderers.size();
		for (int i = 0; i < lNumWindowRenderers; i++) {
			final var lWindow = mWindowRenderers.get(i);
			if (!lWindow.isActive() || !lWindow.isOpen())
				continue;

			lWindow.draw(core, RenderPass.DefaultRenderPass);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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

		final int lNumWindows = mWindowRenderers.size();
		for (int i = 0; i < lNumWindows; i++) {
			if (mWindowRenderers.get(i).rendererName().equals(rendererName)) {
				return mWindowRenderers.get(i);
			}
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
			if (renderer instanceof UiWindow) {
				mWindowRenderers.add((UiWindow) renderer);
				Collections.sort(mWindowRenderers, new ZLayerComparator());
			}

			else {
				mRenderers.add(renderer);
				Collections.sort(mRenderers, new ZLayerComparator());
			}

		} else {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot add the same renderer twice! (" + renderer.getClass().getSimpleName() + "/" + renderer.mRendererName + ")");
		}
	}

	public void removeRenderer(BaseRenderer renderer) {
		if (mWindowRenderers.contains(renderer)) {
			mWindowRenderers.remove(renderer);
		}

		if (mRenderers.contains(renderer)) {
			mRenderers.remove(renderer);
		}
	}

	public void removeAllRenderers() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "RendererManager: Removing all renderers");

		mWindowRenderers.clear();
		mRenderers.clear();
	}

	public void addChangeListener(UIWindowChangeListener listener) {
		if (!mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}

	public void removeListener(UIWindowChangeListener listener) {
		if (mListeners.contains(listener)) {
			mListeners.remove(listener);
		}
	}

	public void removeAllListeners() {
		mListeners.clear();
	}

	/** Unloads all {@link BaseRenderer} instances registered to this {@link RendererManager} which have the given gorup ID assigned to them. */
	public void removeRendererGroup(final int entityGroupID) {
		final var lWindowUpdateList = new ArrayList<UiWindow>();
		final int lNumWindows = mWindowRenderers.size();
		for (int i = 0; i < lNumWindows; i++) {
			lWindowUpdateList.add(mWindowRenderers.get(i));
		}

		for (int i = 0; i < lNumWindows; i++) {
			if (lWindowUpdateList.get(i).entityGroupID() == entityGroupID) {
				lWindowUpdateList.get(i).unloadResources();

				mWindowRenderers.remove(lWindowUpdateList.get(i));
			}
		}

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
		return createRenderTarget(name, width, height, 1f, GL11.GL_LINEAR, resizeWithWindow);
	}

	public RenderTarget createRenderTarget(String name, int width, int height, float scale, int filterMode, boolean resizeWithWindow) {
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
		lRenderTarget.loadResources(width, height, scale);

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