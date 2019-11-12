
package net.lintford.library.renderers.windows;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsTable;
import net.lintford.library.controllers.hud.UIHUDStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch9Patch;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.renderers.windows.components.UIWidget;

public class UIWindow extends BaseRenderer implements IScrollBarArea, UIWindowChangeListener, IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final float Z_DEPTH = -1.0f;

	// The default sie of the title bar for a window
	protected static final float DEFAULT_TITLEBAR_HEIGHT = 32;

	// The padding between the windows and the edge of the UI bounds.
	protected static final float SCREEN_PADDING = 100;

	// The padding between windows in the UI
	protected static final float WINDOW_PADDING = 10;

	// The padding between the window and the window content (displayed in the window)
	public static final float WINDOW_CONTENT_PADDING_X = 10;
	public static final float WINDOW_CONTENT_PADDING_Y = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<UIWidget> mComponents;
	protected String mWindowTitle;
	protected boolean mIsOpen;

	protected boolean mMouseDownLastUpdate;

	// This is the area within which any scrollable content will be displayed. Scrollbars are only visible if the
	// height of the mContentDisplayArea is smaller than the height of the mContentRectangle (below).
	protected Rectangle mContentDisplayArea;

	// This is the area that the content would take up, if not limited by the window bounds (i.e. the area of the 'content' visualisation).
	protected ScrollBarContentRectangle mFullContentRectangle;
	protected UIHUDStructureController mUIHUDGameController;

	protected boolean mUIInputFromUIManager;
	protected boolean mIsWindowMoveable;
	protected boolean mIsWindowMoving;
	protected float dx, dy;
	protected float mWindowAlpha;

	protected float mMouseClickTimer;

	protected ScrollBar mScrollBar;
	protected float mYScrollVal;
	protected float mZScrollAcceleration;
	protected float mZScrollVelocity;

	protected Rectangle mIconSrcRectangle;
	protected String mIconName;

	/** Stores the window area of this renderer window */
	protected Rectangle mWindowArea;
	private Texture mCoreTexture;

	/** If true, this base renderer consumes input and ends the handleInput invocation chain. */
	protected boolean mExclusiveHandleInput = true;

	protected boolean mIsDebugWindow;

	protected float mTitleR, mTitleG, mTitleB;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setTitleColor(float pR, float pG, float pB) {
		mTitleR = pR;
		mTitleG = pG;
		mTitleB = pB;

	}

	public boolean isDebugWindow() {
		return mIsDebugWindow;
	}

	public Rectangle iconSrcRectangle() {
		return mIconSrcRectangle;
	}

	public boolean exclusiveHandleInput() {
		return mExclusiveHandleInput;
	}

	/** @return true if this window is open, false if closed or minimised. */
	public boolean isOpen() {
		return mIsOpen;
	}

	public void isOpen(boolean pNewValue) {
		mIsOpen = pNewValue;
	}

	public String windowTitle() {
		return mWindowTitle;
	}

	public void windowTitle(String pNewTitle) {
		mWindowTitle = pNewTitle;
	}

	public float getTitleBarHeight() {
		return DEFAULT_TITLEBAR_HEIGHT * mRendererManager.getUIScale();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIWindow(final RendererManager pRendererManager, final String pRendererName, final int pEntityGroupID) {
		super(pRendererManager, pRendererName, pEntityGroupID);

		mComponents = new ArrayList<>();

		mWindowArea = new Rectangle();
		mIconSrcRectangle = new Rectangle();
		mContentDisplayArea = new Rectangle();

		// Set some sane defaults
		mWindowArea.x = 10;
		mWindowArea.y = 10;
		mWindowArea.w = 320;
		mWindowArea.h = 240;

		mWindowAlpha = 1.0f;
		mTitleR = 1.0f;
		mTitleG = 1.0f;
		mTitleB = 1.0f;

		mFullContentRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mFullContentRectangle);

		mFullContentRectangle.x = mWindowArea.x;
		mFullContentRectangle.y = mWindowArea.y + DEFAULT_TITLEBAR_HEIGHT;
		mFullContentRectangle.w = 0;
		mFullContentRectangle.h = 0;

		// sane default
		mWindowTitle = "<unnamed>";

		mIsWindowMoveable = false;
		mUIInputFromUIManager = true; // UIManager will call HandleInput (as oppose to some other controller)
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mUIHUDGameController = (UIHUDStructureController) pCore.controllerManager().getControllerByName(UIHUDStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mCoreTexture = pResourceManager.textureManager().textureCore();

		mContentDisplayArea.y = mWindowArea.y + getTitleBarHeight();
		mContentDisplayArea.h = mWindowArea.h - +getTitleBarHeight();

		mIsLoaded = true;

	}

	public void unloadGLContent() {

	}

	public boolean handleInput(LintfordCore pCore) {
		if (!isOpen())
			return false;

		// 1. Check if the scroll bar has been used
		if (mScrollBar.handleInput(pCore)) {
			return true;
		}

		// Update the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			if (mComponents.get(i).handleInput(pCore)) {
				return true;

			}

		}

		if (mIsWindowMoving) {
			// check if user has stopped dragging the window (worst case we skip this frame)
			if (!pCore.input().mouse().isMouseLeftClick(hashCode())) {
				mIsWindowMoving = false;
				mMouseDownLastUpdate = false;

				return false;

			}

			mWindowArea.x += (pCore.input().mouse().mouseWindowCoords().x - dx);
			mWindowArea.y += (pCore.input().mouse().mouseWindowCoords().y - dy);

			// update the delta
			dx = pCore.input().mouse().mouseWindowCoords().x;
			dy = pCore.input().mouse().mouseWindowCoords().y;

			return true;

		}

		// 2. window captures mouse clicks even if not dragging
		if (mIsWindowMoveable && !mIsWindowMoving && mWindowArea.intersectsAA(pCore.HUD().getMouseCameraSpace())) {

			// Only acquire lock when we are ready to move ...
			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				if (!mMouseDownLastUpdate) {
					mMouseDownLastUpdate = true;
					dx = pCore.input().mouse().mouseWindowCoords().x;
					dy = pCore.input().mouse().mouseWindowCoords().y;

				}

				float nx = pCore.input().mouse().mouseWindowCoords().x;
				float ny = pCore.input().mouse().mouseWindowCoords().y;

				final int MINIMUM_TOLERENCE = 3;

				if (Math.abs(nx - dx) > MINIMUM_TOLERENCE || Math.abs(ny - dy) > MINIMUM_TOLERENCE) {
					// Now we can try to acquire the lock, and if we get it, start dragging the window
					if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
						mIsWindowMoving = true;

					}

				}

			}

		}

		if (!pCore.input().mouse().isMouseLeftClick(hashCode())) {
			mIsWindowMoving = false;
			mMouseDownLastUpdate = false;
		}

		// This is needed because when the mouse is over a component
		if (mWindowArea.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
			mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

			// If the mouse was clicked within the window, then we need to process the click anyway
			// return pCore.input().mouse().tryAcquireMouseLeftClick(hashCode());
		}

		return false;

	}

	public void update(LintfordCore pCore) {
		if (!isOpen())
			return;

		if (mMouseClickTimer >= 0) {
			mMouseClickTimer -= pCore.time().elapseGameTimeMilli();

		}

		// Update the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).update(pCore);

		}

		if (mFullContentRectangle.h < mContentDisplayArea.h) {
			mFullContentRectangle.h = mContentDisplayArea.h;

		}

		if (mFullContentRectangle.h - contentDisplayArea().h > 0) {
			mScrollBar.update(pCore);

		}

		final var lDeltaTime = (float) pCore.time().elapseGameTimeSeconds();
		var lScrollSpeedFactor = mYScrollVal;

		mZScrollVelocity += mZScrollAcceleration;
		lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
		mZScrollVelocity *= 0.85f;
		mZScrollAcceleration = 0.0f;

		// Constrain
		mYScrollVal = lScrollSpeedFactor;
		if (mYScrollVal > 0)
			mYScrollVal = 0;
		if (mYScrollVal < -(mFullContentRectangle.h - mContentDisplayArea.h))
			mYScrollVal = -(mFullContentRectangle.h - mContentDisplayArea.h);

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isOpen())
			return;

		if (mUIHUDGameController == null) {
			mUIHUDGameController = (UIHUDStructureController) pCore.controllerManager().getControllerByName(UIHUDStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
			if (mUIHUDGameController == null)
				return;
		}

		updateWindowPosition(pCore);

		mWindowAlpha = 0.95f;

		final TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();
		final FontUnit lTextFont = mRendererManager.textFont();

		// Draw the window background
		lTextureBatch.begin(pCore.HUD());
		TextureBatch9Patch.draw9Patch(lTextureBatch, mCoreTexture, 32, mWindowArea.x, mWindowArea.y + getTitleBarHeight() + 5, mWindowArea.w, mWindowArea.h - getTitleBarHeight() - 5, Z_DEPTH, 1f);
		lTextureBatch.end();

		// Draw the title bar
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mCoreTexture, 448, 0, 32, 32, mWindowArea.x, mWindowArea.y, 32, getTitleBarHeight(), Z_DEPTH, 1f, 1f, 1f, mWindowAlpha);
		lTextureBatch.draw(mCoreTexture, 480, 0, 32, 32, mWindowArea.x + 32, mWindowArea.y, mWindowArea.w - 64, getTitleBarHeight(), Z_DEPTH, 1f, 1f, 1f, mWindowAlpha);
		lTextureBatch.draw(mCoreTexture, 512, 0, 32, 32, mWindowArea.x + mWindowArea.w - 32, mWindowArea.y, 32, getTitleBarHeight(), Z_DEPTH, 1f, 1f, 1f, mWindowAlpha);

		float lTitleX = mWindowArea.x + WINDOW_CONTENT_PADDING_X;
		float lTitleY = mWindowArea.y;

		// Render the icons from the game ui texture
		if (mIconSrcRectangle != null && !mIconSrcRectangle.isEmpty()) {
			lTextureBatch.draw(mCoreTexture, mIconSrcRectangle.x, mIconSrcRectangle.y, mIconSrcRectangle.w, mIconSrcRectangle.h, lTitleX, lTitleY, getTitleBarHeight(), getTitleBarHeight(), Z_DEPTH, 1f, 1f, 1f, mWindowAlpha);

			lTitleX += 32 + WINDOW_CONTENT_PADDING_X;

		}

		lTextureBatch.end();

		// Draw the window title
		FontUnit lTitleFontUnit = mRendererManager.titleFont();
		lTitleFontUnit.begin(pCore.HUD());
		lTitleFontUnit.draw(mWindowTitle, lTitleX, lTitleY + 16f - lTitleFontUnit.fontPointSize() * 0.5f, Z_DEPTH, mTitleR, mTitleG, mTitleB, 1f, 1f);
		lTitleFontUnit.end();

		if (mFullContentRectangle.h - contentDisplayArea().h > 0) {
			mScrollBar.draw(pCore, lTextureBatch, mCoreTexture, Z_DEPTH);

		}

		// Draw the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).draw(pCore, lTextureBatch, mCoreTexture, lTextFont, ZLayers.LAYER_GAME_UI + ((float) i * 0.001f));

		}

		if (ConstantsTable.getBooleanValueDef("DRAW_UI_BOUNDS", false)) {
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mWindowArea);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateWindowPosition(LintfordCore pCore) {
		final Rectangle lHUDBoundingRect = pCore.HUD().boundingRectangle();

		if (lHUDBoundingRect == null) {
			return;

		}

		mWindowArea.x = lHUDBoundingRect.left() + SCREEN_PADDING;
		mWindowArea.y = lHUDBoundingRect.top() + 50;
		mWindowArea.w = lHUDBoundingRect.w * 0.5f - WINDOW_PADDING - SCREEN_PADDING;
		mWindowArea.h = lHUDBoundingRect.h / 2 - WINDOW_PADDING - 50;

	}

	public void keepWindowOnScreen(ICamera pHUD) {

	}

	// --------------------------------------
	// IScrolBarArea Inherited Methods
	// --------------------------------------

	@Override
	public float currentYPos() {
		return mYScrollVal;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mYScrollVal += pAmt;

	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mYScrollVal = pValue;

	}

	@Override
	public Rectangle contentDisplayArea() {
		return mContentDisplayArea;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mFullContentRectangle;
	}

	public void addComponent(UIWidget pComponent) {
		if (!mComponents.contains(pComponent)) {
			mComponents.add(pComponent);

		}

	}

	public void removeComponent(UIWidget pComponent) {
		if (mComponents.contains(pComponent)) {
			mComponents.remove(pComponent);

		}

	}

	public final void closeWindow() {
		mIsOpen = false;

	}

	@Override
	public void onWindowClosed(UIWindow pUIWindow) {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// IProcessMouseInput Inherited Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseClickTimer < 0;

	}

	@Override
	public void resetCoolDownTimer() {
		mMouseClickTimer = 200;

	}

}
