package net.ld.library.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.camera.HUD;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.fonts.FontUnit;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.renderers.windows.UIWindow;
import net.ld.library.renderers.windows.UIWindowChangeListener;

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

	private Camera mCamera;
	private HUD mHUDCamera;

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

	public Camera gameCamera() {
		return mCamera;
	}

	public HUD HUDCamera() {
		return mHUDCamera;
	}

	public float getUIScale() {
		return mUIScale;
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

	public void initialise(final Camera pCamera, final HUD pHUD) {
		mCamera = pCamera;
		mHUDCamera = pHUD;

		mIsInitialised = true;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		System.out.println("RendererManager loading GL content");

		mResourceManager = pResourceManager;

		mSpriteBatch.loadGLContent(pResourceManager);

		// TODO: We should add a more concise method for getting fonts which are already loaded...
		mWindowTitleFont = pResourceManager.fontManager().loadFontFromFile("WindowTitleFont", "res/fonts/pixel.ttf", 35);
		mWindowTextFont = pResourceManager.fontManager().loadFontFromFile("WindowTextFont", "res/fonts/pixel.ttf", 28);

		// Load all of the renderers that have been added so far
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				mRenderers.get(i).loadGLContent(pResourceManager);
			}

		}

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		System.out.println("RendererManager unloading GL content");

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

	/**
	 * Checks to make sure that all active {@link BaseRenderer} instances have been properly loaded, and loads them if not.
	 */
	public void update(GameTime pGameTime) {
		final int RENDERER_COUNT = mRenderers.size();
		for (int i = 0; i < RENDERER_COUNT; i++) {
			if (!mRenderers.get(i).isActive())
				continue;

			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				System.out.println("Reloading content in update (BaseRenderer)");
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
				System.out.println("Reloading content in update (UIWindow)");
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

			// Update the renderer
			mRenderers.get(i).draw(pRenderState);

		}

		final int WINDOW_RENDERER_COUNT = mWindowRenderers.size();
		for (int i = 0; i < WINDOW_RENDERER_COUNT; i++) {
			if (!mWindowRenderers.get(i).isActive())
				continue;

			// Update the renderer
			mWindowRenderers.get(i).draw(pRenderState);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public BaseRenderer getRenderer(String pRendererName) {
		if (pRendererName == null || pRendererName.length() == 0) {
			System.out.println("Renderer requested but no identifier given");

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
			// TODO: Is this really the case, there are times when two renderers
			// of the same type would be benefical ...
			System.err.println("Cannot add the same renderer twice! " + pRenderer.mRendererName);
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
		System.out.println("RendererManager: Removing all renderers");

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

	// --------------------------------------
	// UIWindow Methods
	// --------------------------------------

	public void closeWindow(final UIWindow pUIWindow) {
		System.out.println("RendererManager: closeWindow (" + pUIWindow.getClass().getSimpleName() + ")");

		final int NUM_WINDOW_LISTENERS = mListeners.size();
		for (int i = 0; i < NUM_WINDOW_LISTENERS; i++) {
			mListeners.get(i).onWindowClosed(pUIWindow);

		}
	}

}
