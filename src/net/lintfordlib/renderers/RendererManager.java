package net.lintfordlib.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.controllers.hud.HudLayoutController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontMetaData;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.linebatch.LineBatch;
import net.lintfordlib.core.graphics.polybatch.IndexedPolyBatchPCT;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.input.IInputClickedFocusTracker;
import net.lintfordlib.core.rendering.RenderState;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.IResizeListener;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.interfaces.UIWindowChangeListener;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

public class RendererManager implements IInputClickedFocusManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final FontMetaData RendererManagerFonts = new FontMetaData();

	public static final String HUD_FONT_TEXT_BOLD_SMALL_NAME = "HUD_FONT_TEXT_BOLD_SMALL_NAME";

	public static final String UI_FONT_TEXT_NAME = "UI_FONT_TEXT_NAME";
	public static final String UI_FONT_TEXT_BOLD_NAME = "UI_FONT_TEXT_BOLD_NAME";
	public static final String UI_FONT_HEADER_NAME = "UI_FONT_HEADER_NAME";
	public static final String UI_FONT_TITLE_NAME = "UI_FONT_TITLE_NAME";

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
	private ResourceManager mResourceManager;
	private DisplayManager mDisplayConfig;

	/** Tracks the number of times a loadResources method is called using this renderManager/entityGroupId */
	private int mSharedGlContentCount;

	private HudLayoutController mUiStructureController;
	private List<BaseRenderer> mRenderers;
	private List<UiWindow> mWindowRenderers;

	protected FontUnit mHudTextBoldSmallFont;

	protected FontUnit mUiTextFont;
	protected FontUnit mUiTextBoldFont;
	protected FontUnit mUiHeaderFont;
	protected FontUnit mUiTitleFont;
	protected float mTitleHeight;

	private boolean mIsinitialized;
	private boolean mResourcesLoaded;

	private List<UIWindowChangeListener> mListeners;

	private SpriteBatch mSpriteBatch;
	private LineBatch mLineBatch;
	private IndexedPolyBatchPCT mPolyBatch;

	private List<RenderTarget> mRenderTargets;
	private List<RenderTarget> mRenderTargetAutoResize;
	private RenderTarget mCurrentTarget;

	private IResizeListener mResizeListener;
	private int mRendererIdCounter;

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

	public FontUnit hudTextBoldSmallFont() {
		return mHudTextBoldSmallFont;
	}

	public FontUnit uiTextFont() {
		return mUiTextFont;
	}

	public float textFontHeight() {
		if (mUiTextFont == null)
			return 0.f;
		return mUiTextFont.fontHeight();
	}

	public FontUnit uiTextBoldFont() {
		return mUiTextBoldFont;
	}

	public float textBoldFontHeight() {
		if (mUiTextBoldFont == null)
			return 0.f;
		return mUiTextBoldFont.fontHeight();
	}

	public FontUnit uiHeaderFont() {
		return mUiHeaderFont;
	}

	public float headerFontHeight() {
		if (mUiHeaderFont == null)
			return 0.f;
		return mUiHeaderFont.fontHeight();
	}

	public FontUnit uiTitleFont() {
		return mUiTitleFont;
	}

	public float titleFontHeight() {
		if (mUiTitleFont == null)
			return 0.f;
		return mUiTitleFont.fontHeight();
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

	public RenderState currentRenderState() {
		return mCore.renderState();
	}

	public DisplayManager displayConfig() {
		return mDisplayConfig;
	}

	public SpriteBatch uiSpriteBatch() {
		return mSpriteBatch;
	}

	public IndexedPolyBatchPCT uiPolyBatch() {
		return mPolyBatch;
	}

	public LineBatch uiLineBatch() {
		return mLineBatch;
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

		mSpriteBatch = new SpriteBatch();
		mLineBatch = new LineBatch();
		mPolyBatch = new IndexedPolyBatchPCT();

		mListeners = new ArrayList<>();

		mIsinitialized = false;
		mResourcesLoaded = false;

		RendererManagerFonts.AddIfNotExists(HUD_FONT_TEXT_BOLD_SMALL_NAME, "/res/fonts/fontCoreText.json");

		RendererManagerFonts.AddIfNotExists(UI_FONT_TEXT_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_TEXT_BOLD_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_HEADER_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_TITLE_NAME, "/res/fonts/fontCoreText.json");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		mUiStructureController = (HudLayoutController) mCore.controllerManager().getControllerByNameRequired(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			mRenderers.get(i).initialize(mCore);
		}

		mIsinitialized = true;
	}

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading Resources for all registered renderers");

		mResourceManager = resourceManager;
		mResourceManager.increaseReferenceCounts(mEntityGroupUid);

		mHudTextBoldSmallFont = resourceManager.fontManager().getFontUnit(HUD_FONT_TEXT_BOLD_SMALL_NAME);

		mUiTextFont = resourceManager.fontManager().getFontUnit(UI_FONT_TEXT_NAME);
		mUiTextBoldFont = resourceManager.fontManager().getFontUnit(UI_FONT_TEXT_BOLD_NAME);
		mUiHeaderFont = resourceManager.fontManager().getFontUnit(UI_FONT_HEADER_NAME);
		mUiTitleFont = resourceManager.fontManager().getFontUnit(UI_FONT_TITLE_NAME);

		mSpriteBatch.loadResources(resourceManager);
		mLineBatch.loadResources(resourceManager);
		mPolyBatch.loadResources(resourceManager);

		mDisplayConfig = resourceManager.config().display();

		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			if (!mRenderers.get(i).isLoaded() && mResourcesLoaded) {
				mRenderers.get(i).loadResources(resourceManager);
			}
		}

		{ // Register a window resize listener so we can reload the RenderTargets when the window size changes
			mResizeListener = new IResizeListener() {
				@Override
				public void onResize(final int pWidth, final int pHeight) {
					reloadRenderTargets(pWidth, pHeight);
				}
			};
			mDisplayConfig.addResizeListener(mResizeListener);
		}

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

		mSpriteBatch.unloadResources();
		mLineBatch.unloadResources();
		mPolyBatch.unloadResources();

		mUiTextFont = null;
		mUiTextBoldFont = null;
		mUiHeaderFont = null;
		mUiTitleFont = null;

		mDisplayConfig.removeResizeListener(mResizeListener);
		mDisplayConfig = null;

		mResourceManager.decreaseReferenceCounts(mEntityGroupUid);
		mResourceManager = null;

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
			drawRenderers(core);
		}

		if (RENDER_UI_WINDOWS) {
			drawWindowRenderers(core);
		}
	}

	public void drawRenderers(LintfordCore core) {
		final int lNumBaseRenderers = mRenderers.size();
		for (int i = 0; i < lNumBaseRenderers; i++) {
			final var lRenderer = mRenderers.get(i);
			if (!lRenderer.isActive() || !lRenderer.isManagedDraw())
				continue;

			lRenderer.draw(core);
		}
	}

	public void drawWindowRenderers(LintfordCore core) {
		final int lNumWindowRenderers = mWindowRenderers.size();
		for (int i = 0; i < lNumWindowRenderers; i++) {
			final var lWindow = mWindowRenderers.get(i);
			if (!lWindow.isActive() || !lWindow.isOpen())
				continue;

			lWindow.draw(core);
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
			Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget with name '" + name + "' already exists. No new RendreTarget will be created.");
			return lRenderTarget;
		}

		lRenderTarget = new RenderTarget(name);
		lRenderTarget.textureFilter(filterMode);
		lRenderTarget.loadResources(width, height, scale);
		lRenderTarget.initialiszeGl(width, height, scale);

		mRenderTargets.add(lRenderTarget);

		final int lNumRenderTargets = mRenderTargets.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget '" + name + "' added. Currently have " + lNumRenderTargets + " rendertargets.");

		if (resizeWithWindow) {
			mRenderTargetAutoResize.add(lRenderTarget);
		}

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