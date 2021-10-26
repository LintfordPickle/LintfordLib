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
import net.lintford.library.core.graphics.fonts.FontMetaData;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.polybatch.PolyBatch;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.options.IResizeListener;
import net.lintford.library.renderers.windows.UIWindowChangeListener;
import net.lintford.library.renderers.windows.UiWindow;

public class RendererManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final FontMetaData RendererManagerFonts = new FontMetaData();

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
	public final int entityGroupID;

	private LintfordCore mCore;
	private ResourceManager mResourceManager;
	private DisplayManager mDisplayConfig;

	/** Tracks the number of times a LoadGlContent method is called using this renderManager/entityGroupId*/
	private int mSharedGlContentCount;

	private UiStructureController mUiStructureController;
	private List<BaseRenderer> mRenderers;
	private List<UiWindow> mWindowRenderers;

	protected FontUnit mUiTextFont;
	protected FontUnit mUiTextBoldFont;
	protected FontUnit mUiHeaderFont;
	protected FontUnit mUiTitleFont;

	private boolean mIsinitialized;
	private boolean mIsLoaded;

	private List<UIWindowChangeListener> mListeners;

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

	public void increaseGlContentCount() {
		mSharedGlContentCount++;
	}

	public boolean decreaseGlContentCount() {
		mSharedGlContentCount--;
		return mSharedGlContentCount <= 0;
	}

	public FontUnit uiTextFont() {
		return mUiTextFont;
	}

	public FontUnit uiTextBoldFont() {
		return mUiTextBoldFont;
	}

	public FontUnit uiHeaderFont() {
		return mUiHeaderFont;
	}

	public FontUnit uiTitleFont() {
		return mUiTitleFont;
	}

	public boolean isinitialized() {
		return mIsinitialized;

	}

	public boolean isLoaded() {
		return mIsLoaded;

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

	public RendererManager(LintfordCore pCore, int pEntityGroupID) {
		mCore = pCore;
		entityGroupID = pEntityGroupID;

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

		RendererManagerFonts.AddIfNotExists(UI_FONT_TEXT_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_TEXT_BOLD_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_HEADER_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_TITLE_NAME, "/res/fonts/fontCoreText.json");
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

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading GL content for all registered renderers");

		mResourceManager = pResourceManager;
		mResourceManager.increaseReferenceCounts(entityGroupID);

		mUiTextFont = pResourceManager.fontManager().getFontUnit(UI_FONT_TEXT_NAME);
		mUiTextBoldFont = pResourceManager.fontManager().getFontUnit(UI_FONT_TEXT_BOLD_NAME);
		mUiHeaderFont = pResourceManager.fontManager().getFontUnit(UI_FONT_HEADER_NAME);
		mUiTitleFont = pResourceManager.fontManager().getFontUnit(UI_FONT_TITLE_NAME);

		mSpriteBatch.loadGLContent(pResourceManager);
		mTextureBatch.loadGLContent(pResourceManager);
		mLineBatch.loadGLContent(pResourceManager);
		mPolyBatch.loadGLContent(pResourceManager);

		mDisplayConfig = pResourceManager.config().display();

		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			if (!mRenderers.get(i).isLoaded() && mIsLoaded) {
				mRenderers.get(i).loadGLContent(pResourceManager);
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

		GLDebug.checkGLErrorsException(getClass().getSimpleName());

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Unloading GL content for all renderers");

		// Unloaded each of the renderers
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			mRenderers.get(i).unloadGLContent();

			GLDebug.checkGLErrorsException(getClass().getSimpleName());
		}

		mSpriteBatch.unloadGLContent();
		mTextureBatch.unloadGLContent();
		mLineBatch.unloadGLContent();
		mPolyBatch.unloadGLContent();

		mUiTextFont = null;
		mUiTextBoldFont = null;
		mUiHeaderFont = null;
		mUiTitleFont = null;

		mDisplayConfig.removeResizeListener(mResizeListener);
		mDisplayConfig = null;

		mResourceManager.decreaseReferenceCounts(entityGroupID);
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

	public void update(LintfordCore pCore) {
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			final var lRenderer = mRenderers.get(i);
			if (!lRenderer.isActive())
				continue;

			if (!lRenderer.isLoaded() && mIsLoaded) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), lRenderer.getClass().getSimpleName());
				lRenderer.loadGLContent(mResourceManager);
			}

			lRenderer.update(pCore);
		}

		final int lWindowRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lWindowRendererCount; i++) {
			final var lWindowRenderer = mWindowRenderers.get(i);
			if (!lWindowRenderer.isActive())
				continue;

			if (!lWindowRenderer.isLoaded() && mIsLoaded) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), lWindowRenderer.getClass().getSimpleName());
				lWindowRenderer.loadGLContent(mResourceManager);
			}

			lWindowRenderer.update(pCore);
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
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Reloading content in Update() (BaseRenderer) ");
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

		final int lNumWindows = mWindowRenderers.size();
		for (int i = 0; i < lNumWindows; i++) {
			if (mWindowRenderers.get(i).rendererName().equals(pRendererName)) {
				return mWindowRenderers.get(i);
			}
		}

		final int lNumRenderers = mRenderers.size();
		for (int i = 0; i < lNumRenderers; i++) {
			if (mRenderers.get(i).rendererName().equals(pRendererName)) {
				return mRenderers.get(i);
			}
		}

		return null;
	}

	/** Adds a renderer to the manager. This automatically re-orders the renderers to take into consideration their relative z-depths.*/
	public void addRenderer(BaseRenderer pRenderer) {
		if (getRenderer(pRenderer.rendererName()) == null) {
			if (pRenderer instanceof UiWindow) {
				mWindowRenderers.add((UiWindow) pRenderer);
				Collections.sort(mWindowRenderers, new ZLayerComparator());
			}

			else {
				mRenderers.add(pRenderer);
				Collections.sort(mRenderers, new ZLayerComparator());
			}

		} else {
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
		final var lWindowUpdateList = new ArrayList<UiWindow>();
		final int lNumWindows = mWindowRenderers.size();
		for (int i = 0; i < lNumWindows; i++) {
			lWindowUpdateList.add(mWindowRenderers.get(i));
		}

		for (int i = 0; i < lNumWindows; i++) {
			if (lWindowUpdateList.get(i).entityGroupID() == pEntityGroupID) {
				lWindowUpdateList.get(i).unloadGLContent();

				mWindowRenderers.remove(lWindowUpdateList.get(i));
			}
		}

		final var lRendererUpdateList = new ArrayList<BaseRenderer>();
		final int lRendererCount = mRenderers.size();
		for (int i = 0; i < lRendererCount; i++) {
			lRendererUpdateList.add(mRenderers.get(i));
		}

		for (int i = 0; i < lRendererCount; i++) {
			if (lRendererUpdateList.get(i).entityGroupID() == pEntityGroupID) {
				lRendererUpdateList.get(i).unloadGLContent();

				mRenderers.remove(lRendererUpdateList.get(i));
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
}