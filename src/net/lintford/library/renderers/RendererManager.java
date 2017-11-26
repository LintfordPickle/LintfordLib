package net.lintford.library.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.options.IResizeListener;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.renderers.windows.UIWindowChangeListener;

public class RendererManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_WINDOW_INDEX = -1;
	public static final int WINDOW_ALREADY_REGISTERED = -2;

	// --------------------------------------
	// variables
	// --------------------------------------

	private ResourceManager mResourceManager;
	private List<BaseRenderer> mRenderers;
	private List<UIWindow> mWindowRenderers;

	private boolean mIsInitialised;
	private boolean mIsLoaded;

	// Stuff from the UI Manager
	private List<UIWindowChangeListener> mListeners;
	private float mUIScale;

	// Maybe put these in a kind of RendererResourcePool
	private FontUnit mWindowTitleFont;
	private FontUnit mWindowTextFont;

	private TextureBatch mSpriteBatch;
	private DisplayConfig mDisplayConfig;

	// TODO: Make a dedicated RenderTargetManager
	private List<RenderTarget> mRenderTargets;
	private List<RenderTarget> mRenderTargetAutoResize;
	private RenderTarget mCurrentTarget;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInitialised() {
		return mIsInitialised;

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

	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	public TextureBatch uiSpriteBatch() {
		return mSpriteBatch;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RendererManager() {
		mRenderers = new ArrayList<>();
		mWindowRenderers = new ArrayList<>();
		mRenderTargets = new ArrayList<>();
		mRenderTargetAutoResize = new ArrayList<>();

		mSpriteBatch = new TextureBatch();

		mListeners = new ArrayList<>();

		// TODO: This should be controlled in the options menu later
		mUIScale = 1f;

		mIsInitialised = false;
		mIsLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

		mIsInitialised = true;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Loading GL content for all rendererins");

		mResourceManager = pResourceManager;

		mSpriteBatch.loadGLContent(pResourceManager);

		// TODO: We should add a more concise method for getting fonts which are already loaded...
		mWindowTitleFont = pResourceManager.fontManager().loadNewFont("WindowTitleFont", "res/fonts/monofonto.ttf", 22);
		mWindowTextFont = pResourceManager.fontManager().loadNewFont("WindowTextFont", "res/fonts/monofonto.ttf", 18);

		// Some windows will use this to orientate themselves to the window
		mDisplayConfig = pResourceManager.masterConfig().displayConfig();

		// Load all of the renderers that have been added so far
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				mRenderers.get(i).loadGLContent(pResourceManager);
			}

		}

		// Register a window resize listener so we can reload the RenderTargets when the window size changes
		mDisplayConfig.addResizeListener(new IResizeListener() {

			@Override
			public void onResize(final int pWidth, final int pHeight) {
				reloadRenderTargets(pWidth, pHeight);

			}

		});

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Unloading GL content for all renderers");

		// Unloaded each of the renderers
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			mRenderers.get(i).unloadGLContent();

		}

		mSpriteBatch.unloadGLContent();

		mIsLoaded = false;

	}

	public boolean handleInput(InputState pInputState) {

		final int NUM_WINDOW_RENDERERS = mWindowRenderers.size();

		// We handle the input to the UI Windows in the game with priority.
		for (int i = 0; i < NUM_WINDOW_RENDERERS; i++) {
			final UIWindow WINDOW = mWindowRenderers.get(i);
			boolean lResult = WINDOW.handleInput(pInputState);
			if (lResult && WINDOW.exclusiveHandleInput()) {
				return true;

			}

		}

		final int NUM_RENDERERS = mRenderers.size();

		// Handle the base renderer input
		for (int i = 0; i < NUM_RENDERERS; i++) {
			mRenderers.get(i).handleInput(pInputState);

		}

		return false;

	}

	/** Checks to make sure that all active {@link BaseRenderer} instances have been properly loaded, and loads them if not. */
	public void update(GameTime pGameTime) {
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (!mRenderers.get(i).isActive())
				continue;

			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Reloading contenet in Update() (BaseRenderer) ");
				mRenderers.get(i).loadGLContent(mResourceManager);

			}

			// Update the renderer
			mRenderers.get(i).update(pGameTime);

		}

		final int WINDOW_RENDERER_COUNT = mWindowRenderers.size();
		for (int i = 0; i < WINDOW_RENDERER_COUNT; i++) {
			if (!mWindowRenderers.get(i).isActive())
				continue;

			if (!mWindowRenderers.get(i).isLoaded() && mIsLoaded) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Reloading contenet in Update() (UIWindow) ");
				mWindowRenderers.get(i).loadGLContent(mResourceManager);
			}

			// Update the renderer
			mWindowRenderers.get(i).update(pGameTime);

		}

	}

	public void draw(RenderState pRenderState) {
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (!mRenderers.get(i).isActive())
				continue;

			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Reloading contenet in Update() (BaseRenderer) ");
				mRenderers.get(i).loadGLContent(mResourceManager);

			}

			// Update the renderer
			mRenderers.get(i).draw(pRenderState);

		}

		final int WINDOW_RENDERER_COUNT = mWindowRenderers.size();
		for (int i = 0; i < WINDOW_RENDERER_COUNT; i++) {
			if (!mWindowRenderers.get(i).isActive())
				continue;

			if (!mWindowRenderers.get(i).isLoaded() && mIsLoaded) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Reloading contenet in Update() (UIWindow) ");
				mWindowRenderers.get(i).loadGLContent(mResourceManager);
			}

			// Update the renderer
			mWindowRenderers.get(i).draw(pRenderState);

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
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Cannot add the same renderer twice! (" + pRenderer.getClass().getSimpleName() + "/" + pRenderer.mRendererName + ")");

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
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "RendererManager: Removing all renderers");

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
	public void removeRendererGroup(final int pGroupID) {
		// Heap assignment
		final List<UIWindow> WINDOW_UPDATE_LIST = new ArrayList<>();
		final int WINDOW_COUNT = mWindowRenderers.size();
		for (int i = 0; i < WINDOW_COUNT; i++) {
			WINDOW_UPDATE_LIST.add(mWindowRenderers.get(i));

		}

		for (int i = 0; i < WINDOW_COUNT; i++) {
			if (WINDOW_UPDATE_LIST.get(i).groupID() == pGroupID) {
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
			if (RENDERER_UPDATE_LIST.get(i).groupID() == pGroupID) {
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
		// First check to see if the render target exists
		RenderTarget lResult = getRenderTarget(pName);

		if (lResult != null)
			return lResult;

		lResult = new RenderTarget();
		lResult.loadGLContent(pWidth, pHeight);
		lResult.targetName = pName;

		mRenderTargets.add(lResult);

		System.out.println("Rendertargets size: " + mRenderTargets.size());

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
