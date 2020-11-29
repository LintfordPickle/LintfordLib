package net.lintford.library.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.controllers.hud.UiStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.graphics.fonts.FontManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.polybatch.PolyBatch;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.options.IResizeListener;
import net.lintford.library.renderers.windows.UiWindow;
import net.lintford.library.renderers.windows.UIWindowChangeListener;

public class RendererManager {

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

	private LintfordCore mCore;
	private int mEntityGroupID;
	private ResourceManager mResourceManager;
	private DisplayManager mDisplayConfig;

	private UiStructureController mUiStructureController;

	/** Allows us to track where each RendererManager is created from */
	private String mOwnerIdentifier;
	private List<BaseRenderer> mRenderers;
	private List<UiWindow> mWindowRenderers;

	private boolean mIsinitialized;
	private boolean mIsLoaded;

	// Stuff from the UI Manager
	private List<UIWindowChangeListener> mListeners;

	// Maybe put these in a kind of RendererResourcePool
	private FontUnit mWindowTitleFont;
	private FontUnit mWindowTextFont;

	private SpriteBatch mSpriteBatch;
	private TextureBatchPCT mTextureBatch;
	private LineBatch mLineBatch;
	private PolyBatch mPolyBatch;

	private List<RenderTarget> mRenderTargets;
	private List<RenderTarget> mRenderTargetAutoResize;
	private RenderTarget mCurrentTarget;

	private IResizeListener mResizeListener;
	private int mRendererIdCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getNewRendererId() {
		return mRendererIdCounter++;
	}

	public String ownerName() {
		return mOwnerIdentifier;
	}

	public LintfordCore core() {
		return mCore;
	}

	public RenderState currentRenderState() {
		return mCore.renderState();
	}

	public boolean isinitialized() {
		return mIsinitialized;

	}

	public boolean isLoaded() {
		return mIsLoaded;

	}

	/** A shared font for rendering message text into the UI windows */
	public FontUnit textFont() {
		return mWindowTextFont;
	}

	/** A shared font for rendering title text into the UI windows */
	public FontUnit titleFont() {
		return mWindowTitleFont;
	}

	public DisplayManager displayConfig() {
		return mDisplayConfig;
	}

	public TextureBatchPCT uiTextureBatch() {
		return mTextureBatch;
	}

	public SpriteBatch uiSpriteBatch() {
		return mSpriteBatch;
	}

	public PolyBatch uiPolyBatch() {
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

	public UiStructureController uiStructureController() {
		return mUiStructureController;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RendererManager(LintfordCore pCore, String pOwnerName, int pEntityGroupID) {
		mOwnerIdentifier = pOwnerName;
		mCore = pCore;
		mEntityGroupID = pEntityGroupID;

		mRenderers = new ArrayList<>();
		mWindowRenderers = new ArrayList<>();
		mRenderTargets = new ArrayList<>();
		mRenderTargetAutoResize = new ArrayList<>();

		mSpriteBatch = new SpriteBatch();
		mTextureBatch = new TextureBatchPCT();
		mLineBatch = new LineBatch();
		mPolyBatch = new PolyBatch();

		mListeners = new ArrayList<>();

		mIsinitialized = false;
		mIsLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		mUiStructureController = (UiStructureController) mCore.controllerManager().getControllerByNameRequired(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			mRenderers.get(i).initialize(mCore);

		}

		mIsinitialized = true;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), mOwnerIdentifier + "Loading GL content for all registered renderers");

		mResourceManager = pResourceManager;
		mResourceManager.increaseReferenceCounts(mEntityGroupID);

		mSpriteBatch.loadGLContent(pResourceManager);
		mTextureBatch.loadGLContent(pResourceManager);
		mLineBatch.loadGLContent(pResourceManager);
		mPolyBatch.loadGLContent(pResourceManager);

		mWindowTitleFont = pResourceManager.fontManager().getFont(FontManager.FONT_FONTNAME_TITLE);
		mWindowTextFont = pResourceManager.fontManager().getFont(FontManager.FONT_FONTNAME_TEXT);

		// Some windows will use this to orientate themselves to the window
		mDisplayConfig = pResourceManager.config().display();

		// Load all of the renderers that have been added so far
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				mRenderers.get(i).loadGLContent(pResourceManager);

			}

		}

		mResizeListener = new IResizeListener() {

			@Override
			public void onResize(final int pWidth, final int pHeight) {
				reloadRenderTargets(pWidth, pHeight);

			}

		};

		// Register a window resize listener so we can reload the RenderTargets when the window size changes
		mDisplayConfig.addResizeListener(mResizeListener);

		GLDebug.checkGLErrorsException(getClass().getSimpleName());

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), mOwnerIdentifier + "Unloading GL content for all renderers");

		// Unloaded each of the renderers
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			mRenderers.get(i).unloadGLContent();

			GLDebug.checkGLErrorsException(getClass().getSimpleName());

		}

		mSpriteBatch.unloadGLContent();
		mTextureBatch.unloadGLContent();
		mLineBatch.unloadGLContent();
		mPolyBatch.unloadGLContent();

		mWindowTextFont = null;
		mWindowTitleFont = null;

		mDisplayConfig.removeResizeListener(mResizeListener);
		mDisplayConfig = null;

		mResourceManager.decreaseReferenceCounts(mEntityGroupID);
		mResourceManager = null;
		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		final int lNumWindowRenderers = mWindowRenderers.size();

		// We handle the input to the UI Windows in the game with priority.
		for (int i = 0; i < lNumWindowRenderers; i++) {
			final var lWindow = mWindowRenderers.get(i);
			final var lResult = lWindow.handleInput(pCore);
			if (lResult && lWindow.exclusiveHandleInput()) {
				// return true;

			}

		}

		// Handle the base renderer input
		final int lNumRenderers = mRenderers.size();
		for (int i = lNumRenderers - 1; i >= 0; i--) {
			mRenderers.get(i).handleInput(pCore);

		}

		return false;

	}

	/** Checks to make sure that all active {@link BaseRenderer} instances have been properly loaded, and loads them if not. */
	public void update(LintfordCore pCore) {
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			if (!mRenderers.get(i).isActive())
				continue;

			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Reloading content in Update() (BaseRenderer) ");
				mRenderers.get(i).loadGLContent(mResourceManager);

			}

			// Update the renderer
			mRenderers.get(i).update(pCore);

		}

		final int lWindowRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lWindowRendererCount; i++) {
			if (!mWindowRenderers.get(i).isActive())
				continue;

			if (!mWindowRenderers.get(i).isLoaded() && mIsLoaded) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Reloading content in Update() (UIWindow) ");
				mWindowRenderers.get(i).loadGLContent(mResourceManager);
			}

			// Update the renderer
			mWindowRenderers.get(i).update(pCore);

		}

	}

	public void draw(LintfordCore pCore) {
		if (RENDER_GAME_RENDERABLES) {
			final int lNumBaseRenderers = mRenderers.size();
			for (int i = 0; i < lNumBaseRenderers; i++) {
				final var lRenderer = mRenderers.get(i);
				if (!lRenderer.isActive() || !lRenderer.isManagedDraw())
					continue;

				if (!lRenderer.isLoaded() && mIsLoaded) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), mOwnerIdentifier + "Reloading content in Update() (BaseRenderer) ");
					lRenderer.loadGLContent(mResourceManager);

				}

				// Update the renderer
				lRenderer.draw(pCore);

			}

		}

		if (RENDER_UI_WINDOWS) {
			final int lNumWindowRenderers = mWindowRenderers.size();
			for (int i = 0; i < lNumWindowRenderers; i++) {
				final var lWindow = mWindowRenderers.get(i);
				if (!lWindow.isActive() || !lWindow.isOpen())
					continue;

				if (!lWindow.isLoaded() && mIsLoaded) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Reloading content in Update() (UIWindow) ");
					lWindow.loadGLContent(mResourceManager);
				}

				// Update the renderer
				lWindow.draw(pCore);

			}
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public BaseRenderer getRenderer(String pRendererName) {
		if (pRendererName == null || pRendererName.length() == 0) {
			return null;
		}

		// First check for windows
		final int NUM_WINDOWS = mWindowRenderers.size();
		for (int i = 0; i < NUM_WINDOWS; i++) {
			if (mWindowRenderers.get(i).rendererName().equals(pRendererName)) {
				return mWindowRenderers.get(i);
			}
		}

		// IF not, check for renderers
		final int NUM_RENDERERS = mRenderers.size();
		for (int i = 0; i < NUM_RENDERERS; i++) {
			if (mRenderers.get(i).rendererName().equals(pRendererName)) {
				return mRenderers.get(i);
			}
		}

		return null;

	}

	public void addRenderer(BaseRenderer pRenderer) {
		// Only renderers with valid names can be added
		if (getRenderer(pRenderer.rendererName()) == null) {
			if (pRenderer instanceof UiWindow) {
				mWindowRenderers.add((UiWindow) pRenderer);

				// Re-order the WindowRenderers
				Collections.sort(mWindowRenderers, new ZLayerComparator());

			}

			else {
				mRenderers.add(pRenderer);

				// Re-order the BaseRenderers
				Collections.sort(mRenderers, new ZLayerComparator());

			}

		} else {
			// Output this as an error so that it is visible in the debuglog for corrective action.
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot add the same renderer twice! (" + pRenderer.getClass().getSimpleName() + "/" + pRenderer.mRendererName + ")");

		}

	}

	public void removeRenderer(BaseRenderer pRenderer) {
		if (mWindowRenderers.contains(pRenderer)) {
			mWindowRenderers.remove(pRenderer);

		}

		if (mRenderers.contains(pRenderer)) {
			mRenderers.remove(pRenderer);

		}

	}

	public void removeAllRenderers() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "RendererManager: Removing all renderers");

		mWindowRenderers.clear();
		mRenderers.clear();

	}

	public void addChangeListener(UIWindowChangeListener pListener) {
		if (!mListeners.contains(pListener)) {
			mListeners.add(pListener);

		}

	}

	public void removeListener(UIWindowChangeListener pListener) {
		if (mListeners.contains(pListener)) {
			mListeners.remove(pListener);

		}

	}

	public void removeAllListeners() {
		mListeners.clear();
	}

	/** Unloads all {@link BaseRenderer} instances registered to this {@link RendererManager} which have the given gorup ID assigned to them. */
	public void removeRendererGroup(final int pEntityGroupID) {
		// Heap assignment
		final List<UiWindow> WINDOW_UPDATE_LIST = new ArrayList<>();
		final int WINDOW_COUNT = mWindowRenderers.size();
		for (int i = 0; i < WINDOW_COUNT; i++) {
			WINDOW_UPDATE_LIST.add(mWindowRenderers.get(i));

		}

		for (int i = 0; i < WINDOW_COUNT; i++) {
			if (WINDOW_UPDATE_LIST.get(i).entityGroupID() == pEntityGroupID) {
				// Unload this BaseRenderer instance
				WINDOW_UPDATE_LIST.get(i).unloadGLContent();

				// Remove the BaseRenderer instance from the mWindowRenderers list
				mWindowRenderers.remove(WINDOW_UPDATE_LIST.get(i));

			}
		}

		// Heap assignment
		final List<BaseRenderer> RENDERER_UPDATE_LIST = new ArrayList<>();
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			RENDERER_UPDATE_LIST.add(mRenderers.get(i));

		}

		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (RENDERER_UPDATE_LIST.get(i).entityGroupID() == pEntityGroupID) {
				// Unload this BaseRenderer instance
				RENDERER_UPDATE_LIST.get(i).unloadGLContent();

				// Remove the BaseRenderer instance from the mWindowRenderers list
				mRenderers.remove(RENDERER_UPDATE_LIST.get(i));

			}

		}

	}

	public void setRenderTarget(String pName) {
		if (pName == null) {
			if (mCurrentTarget != null) {
				mCurrentTarget.unbind();
				return;
			}
		}

		RenderTarget lResult = getRenderTarget(pName);

		if (lResult != null) {
			if (mCurrentTarget != null) {
				mCurrentTarget.unbind();

			}

		}

		lResult.bind();

	}

	public RenderTarget createRenderTarget(String pName, int pWidth, int pHeight, boolean pResizeWithWindow) {
		return createRenderTarget(pName, pWidth, pHeight, 1f, pResizeWithWindow);
	}

	public RenderTarget createRenderTarget(String pName, int pWidth, int pHeight, float pScale, boolean pResizeWithWindow) {
		return createRenderTarget(pName, pWidth, pHeight, 1f, GL11.GL_LINEAR, pResizeWithWindow);
	}

	public RenderTarget createRenderTarget(String pName, int pWidth, int pHeight, float pScale, int pFilterMode, boolean pResizeWithWindow) {
		var lRenderTarget = getRenderTarget(pName);

		if (lRenderTarget != null) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget with name '" + pName + "' already exists. No new RendreTarget will be created.");
			return lRenderTarget;

		}

		lRenderTarget = new RenderTarget();
		lRenderTarget.targetName = pName;
		lRenderTarget.textureFilter(pFilterMode);
		lRenderTarget.loadGLContent(pWidth, pHeight, pScale);

		mRenderTargets.add(lRenderTarget);

		final int lNumRenderTargets = mRenderTargets.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), "RenderTarget '" + pName + "' added. Currently have " + lNumRenderTargets + " rendertargets.");

		if (pResizeWithWindow) {
			mRenderTargetAutoResize.add(lRenderTarget);

		}

		return lRenderTarget;

	}

	public void unloadRenderTarget(RenderTarget pRenderTarget) {
		if (pRenderTarget == null)
			return;

		if (mRenderTargetAutoResize.contains(pRenderTarget)) {
			mRenderTargetAutoResize.remove(pRenderTarget);

		}

		if (mRenderTargets.contains(pRenderTarget)) {
			mRenderTargets.remove(pRenderTarget);

		}

		pRenderTarget.unbind();
		pRenderTarget.unloadGLContent();

	}

	public void releaseRenderTargetByName(String pName) {
		final var lResult = getRenderTarget(pName);
		if (lResult != null) {
			unloadRenderTarget(lResult);

		}
	}

	public RenderTarget getRenderTarget(String pName) {
		final int RENDER_TARGET_COUNT = mRenderTargets.size();
		for (int i = 0; i < RENDER_TARGET_COUNT; i++) {
			if (mRenderTargets.get(i).targetName.equals(pName)) {
				return mRenderTargets.get(i);

			}

		}

		return null;
	}

	public void reloadRenderTargets(final int pWidth, final int pHeight) {
		final int RENDER_TARGET_COUNT = mRenderTargetAutoResize.size();
		for (int i = 0; i < RENDER_TARGET_COUNT; i++) {
			mRenderTargetAutoResize.get(i).resize(pWidth, pHeight);

		}

	}

	// --------------------------------------
	// UIWindow Methods
	// --------------------------------------

	public UiWindow openWindow(String pWindowName) {
		UiWindow lWindow = (UiWindow) getRenderer(pWindowName);

		// Check to see if this window is already open, and if it is, close it

		if (lWindow != null) {
			lWindow.isOpen(true);
		}

		return lWindow;
	}

	public void closeWindow(final UiWindow pUIWindow) {
		final int NUM_WINDOW_LISTENERS = mListeners.size();
		for (int i = 0; i < NUM_WINDOW_LISTENERS; i++) {
			mListeners.get(i).onWindowClosed(pUIWindow);

		}
	}

}
