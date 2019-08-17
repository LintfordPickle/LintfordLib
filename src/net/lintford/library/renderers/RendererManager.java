package net.lintford.library.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.hud.UIHUDStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.options.IResizeListener;
import net.lintford.library.renderers.windows.UIWindow;
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

	public static final String WINDOWS_TEXT_FONT_NAME = "WindowTextFont";
	public static final String WINDOWS_TITLE_FONT_NAME = "WindowTitleFont";

	// --------------------------------------
	// variables
	// --------------------------------------

	private LintfordCore mCore;
	private int mEntityGroupID;
	private ResourceManager mResourceManager;
	private DisplayManager mDisplayConfig;

	private UIHUDStructureController mUIHUDController;

	/** Allows us to track where each RendererManager is created from */
	private String mOwnerIdentifier;
	private List<BaseRenderer> mRenderers;
	private List<UIWindow> mWindowRenderers;

	private boolean mIsinitialized;
	private boolean mIsLoaded;

	// Stuff from the UI Manager
	private List<UIWindowChangeListener> mListeners;
	private float mUIScale;
	private float mUITextScale;

	// Maybe put these in a kind of RendererResourcePool
	private FontUnit mWindowTitleFont;
	private FontUnit mWindowTextFont;

	private SpriteBatch mSpriteBatch;
	private TextureBatch mTextureBatch;
	private LineBatch mLineBatch;

	// TODO: Make a dedicated RenderTargetManager
	private List<RenderTarget> mRenderTargets;
	private List<RenderTarget> mRenderTargetAutoResize;
	private RenderTarget mCurrentTarget;

	private IResizeListener mResizeListener;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public float getUIScale() {
		return mUIScale;
	}

	public float getUITextScale() {
		return mUITextScale;
	}

	public DisplayManager displayConfig() {
		return mDisplayConfig;
	}

	public TextureBatch uiTextureBatch() {
		return mTextureBatch;
	}

	public SpriteBatch uiSpriteBatch() {
		return mSpriteBatch;
	}

	public LineBatch uiLineBatch() {
		return mLineBatch;
	}

	public List<UIWindow> windows() {
		return mWindowRenderers;
	}

	public UIHUDStructureController uiHUDController() {
		return mUIHUDController;
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
		mTextureBatch = new TextureBatch();
		mLineBatch = new LineBatch();

		mListeners = new ArrayList<>();

		// TODO: This should be controlled in the options menu later
		mUIScale = 1f;

		mIsinitialized = false;
		mIsLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

		mUIHUDController = (UIHUDStructureController) mCore.controllerManager().getControllerByNameRequired(UIHUDStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
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

		String lPath = "res//fonts//Rajdhani-Bold.ttf";
		mWindowTitleFont = pResourceManager.fontManager().loadNewFont(WINDOWS_TITLE_FONT_NAME, lPath, 18, mEntityGroupID);
		mWindowTextFont = pResourceManager.fontManager().loadNewFont(WINDOWS_TEXT_FONT_NAME, lPath, 16, mEntityGroupID);

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

		mWindowTextFont.unloadGLContent();
		mWindowTitleFont.unloadGLContent();

		mDisplayConfig.removeResizeListener(mResizeListener);
		mDisplayConfig = null;

		mResourceManager.decreaseReferenceCounts(mEntityGroupID);
		mResourceManager = null;
		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		final int NUM_WINDOW_RENDERERS = mWindowRenderers.size();

		if (pCore.input().keyDown(GLFW.GLFW_KEY_F7)) {
			final int lCount = mWindowRenderers.size();
			System.out.printf("RenderManager %d count: %d\n", hashCode(), lCount);
			for (int i = 0; i < lCount; i++) {
				System.out.printf("  %d: %s\n", i, mWindowRenderers.get(i).getClass().getSimpleName());

			}

		}

		// We handle the input to the UI Windows in the game with priority.
		for (int i = 0; i < NUM_WINDOW_RENDERERS; i++) {
			final UIWindow WINDOW = mWindowRenderers.get(i);
			boolean lResult = WINDOW.handleInput(pCore);
			if (lResult && WINDOW.exclusiveHandleInput()) {
				return true;

			}

		}

		final int NUM_RENDERERS = mRenderers.size();

		// Handle the base renderer input
		for (int i = NUM_RENDERERS - 1; i >= 0; i--) {
			mRenderers.get(i).handleInput(pCore);

		}

		return false;

	}

	/** Checks to make sure that all active {@link BaseRenderer} instances have been properly loaded, and loads them if not. */
	public void update(LintfordCore pCore) {
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (!mRenderers.get(i).isActive())
				continue;

			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Reloading content in Update() (BaseRenderer) ");
				mRenderers.get(i).loadGLContent(mResourceManager);

			}

			// Update the renderer
			mRenderers.get(i).update(pCore);

		}

		final int WINDOW_RENDERER_COUNT = mWindowRenderers.size();
		for (int i = 0; i < WINDOW_RENDERER_COUNT; i++) {
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
			final int RENDERER_COUNT = mRenderers.size();
			for (int i = 0; i < RENDERER_COUNT; i++) {
				if (!mRenderers.get(i).isActive())
					continue;

				if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), mOwnerIdentifier + "Reloading content in Update() (BaseRenderer) ");
					mRenderers.get(i).loadGLContent(mResourceManager);

				}

				// Update the renderer
				mRenderers.get(i).draw(pCore);

			}

		}

		if (RENDER_UI_WINDOWS) {
			final int WINDOW_RENDERER_COUNT = mWindowRenderers.size();
			for (int i = 0; i < WINDOW_RENDERER_COUNT; i++) {
				if (!mWindowRenderers.get(i).isActive())
					continue;

				if (!mWindowRenderers.get(i).isLoaded() && mIsLoaded) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Reloading content in Update() (UIWindow) ");
					mWindowRenderers.get(i).loadGLContent(mResourceManager);
				}

				// Update the renderer
				mWindowRenderers.get(i).draw(pCore);

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
			if (pRenderer instanceof UIWindow) {
				mWindowRenderers.add((UIWindow) pRenderer);

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
		final List<UIWindow> WINDOW_UPDATE_LIST = new ArrayList<>();
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
		// First check to see if the render target exists
		RenderTarget lResult = getRenderTarget(pName);

		if (lResult != null)
			return lResult;

		lResult = new RenderTarget();
		lResult.loadGLContent(pWidth, pHeight, pScale);
		lResult.targetName = pName;

		mRenderTargets.add(lResult);

		if (pResizeWithWindow) {
			mRenderTargetAutoResize.add(lResult);

		}

		return lResult;

	}

	public void releaseRenderTarget(String pName) {
		RenderTarget lResult = getRenderTarget(pName);

		if (lResult != null) {
			if (mRenderTargetAutoResize.contains(lResult)) {
				mRenderTargetAutoResize.remove(lResult);

			}

			lResult.unbind();
			lResult.unloadGLContent();

			mRenderTargets.remove(lResult);

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

	public UIWindow openWindow(String pWindowName) {
		UIWindow lWindow = (UIWindow) getRenderer(pWindowName);

		// Check to see if this window is already open, and if it is, close it

		if (lWindow != null) {
			lWindow.isOpen(true);
		}

		return lWindow;
	}

	public void closeWindow(final UIWindow pUIWindow) {
		final int NUM_WINDOW_LISTENERS = mListeners.size();
		for (int i = 0; i < NUM_WINDOW_LISTENERS; i++) {
			mListeners.get(i).onWindowClosed(pUIWindow);

		}
	}

}
