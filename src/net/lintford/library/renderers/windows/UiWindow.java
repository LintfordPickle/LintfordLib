package net.lintford.library.renderers.windows;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsApp;
import net.lintford.library.controllers.hud.UiStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch9Patch;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.renderers.windows.components.UIWidget;

public class UiWindow extends BaseRenderer implements IScrollBarArea, UIWindowChangeListener, IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final float Z_DEPTH = -2.f;

	/** Define the base size of the panels and title bar. These are re-calculated on a per-frame basis using the current window size (see bnelow in UiWindow class) */
	protected static final float BASE_TITLEBAR_HEIGHT = 32.f;
	protected static final float BASE_PANEL_WIDTH = 32.f;
	protected static final float BASE_PANEL_HEIGHT = 32.f;

	protected static final float SCREEN_PADDING_X = 50.f;
	protected static final float SCREEN_PADDING_Y = 50.f;

	// Inter-content padding
	protected static final float WINDOW_CONTENT_PADDING_X = 16.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mTitleBarHeight;
	protected float mPanelSizeWidth;
	protected float mPanelSizeHeight;

	protected List<UIWidget> mComponents;
	protected String mWindowTitle;
	protected boolean mIsOpen;

	protected boolean mMouseDownLastUpdate;

	// This is the area within which any scrollable content will be displayed. Scrollbars are only visible if the
	// height of the mContentDisplayArea is smaller than the height of the mContentRectangle (below).
	protected Rectangle mContentDisplayArea;

	// This is the area that the content would take up, if not limited by the window bounds (i.e. the area of the 'content' visualisation).
	protected ScrollBarContentRectangle mFullContentRectangle;
	protected UiStructureController mUiStructureController;

	protected boolean mUiInputFromUiManager;
	protected boolean mIsWindowMoveable;
	protected boolean mIsWindowMoving;
	protected float dx, dy;
	protected float mWindowAlpha;

	protected float mMouseClickTimer;

	protected ScrollBar mScrollBar;
	protected float mYScrollVal;
	protected float mZScrollAcceleration;
	protected float mZScrollVelocity;

	protected float mWindowMarginLeft;
	protected float mWindowMarginRight;
	protected float mWindowMarginTop;
	protected float mWindowMarginBottom;

	protected float mWindowPaddingLeft;
	protected float mWindowPaddingRight;
	protected float mWindowPaddingTop;
	protected float mWindowPaddingBottom;

	protected Rectangle mIconSrcRectangle;
	protected String mIconName;

	protected boolean mCanCaptureMouse;
	protected boolean mIsMouseOver;

	/** Stores the window area of this renderer window */
	protected Rectangle mWindowArea;
	protected Texture mUiCoreTexture;
	protected Texture mHudTexture;

	/** If true, this base renderer consumes input and ends the handleInput invocation chain. */
	protected boolean mExclusiveHandleInput = true;
	protected boolean mIsDebugWindow;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void paddingLeft(float pNewValue) {
		mWindowPaddingLeft = pNewValue;
	}

	public float paddingLeft() {
		return mWindowPaddingLeft;
	}

	public void paddingRight(float pNewValue) {
		mWindowPaddingRight = pNewValue;
	}

	public float paddingRight() {
		return mWindowPaddingRight;
	}

	public void paddingTop(float pNewValue) {
		mWindowPaddingTop = pNewValue;
	}

	public float paddingTop() {
		return mWindowPaddingTop;
	}

	public void paddingBottom(float pNewValue) {
		mWindowPaddingBottom = pNewValue;
	}

	public float paddingBottom() {
		return mWindowPaddingBottom;
	}

	public void marginLeft(float pNewValue) {
		mWindowMarginLeft = pNewValue;
	}

	public float marginLeft() {
		return mWindowMarginLeft;
	}

	public void marginRight(float pNewValue) {
		mWindowMarginRight = pNewValue;
	}

	public float marginRight() {
		return mWindowMarginRight;
	}

	public void marginTop(float pNewValue) {
		mWindowMarginTop = pNewValue;
	}

	public float marginTop() {
		return mWindowMarginTop;
	}

	public void marginBottom(float pNewValue) {
		mWindowMarginBottom = pNewValue;
	}

	public float marginBottom() {
		return mWindowMarginBottom;
	}

	@Override
	public boolean isInitialized() {
		return true;

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

		if (mIsOpen) {
			onWindowOpened();

		} else {
			onWindowClosed();

		}
	}

	public String windowTitle() {
		return mWindowTitle;
	}

	public void windowTitle(String pNewTitle) {
		mWindowTitle = pNewTitle;
	}

	public float getTitleBarHeight() {
		return mTitleBarHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiWindow(final RendererManager pRendererManager, final String pRendererName, final int pEntityGroupID) {
		super(pRendererManager, pRendererName, pEntityGroupID);

		mComponents = new ArrayList<>();

		mWindowArea = new Rectangle();
		mIconSrcRectangle = new Rectangle();
		mContentDisplayArea = new Rectangle();

		mWindowArea.set(-160, -120, 320, 240);

		mWindowAlpha = 1.0f;

		mFullContentRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mFullContentRectangle);

		mFullContentRectangle.set(mWindowArea.x(), mWindowArea.y() + BASE_TITLEBAR_HEIGHT, 0, 0);

		// sane default
		mWindowTitle = pRendererName;

		mIsWindowMoveable = false;
		mUiInputFromUiManager = true;

		mWindowPaddingTop = 5.f;
		mWindowPaddingBottom = 5.f;
		mWindowPaddingLeft = 5.f;
		mWindowPaddingRight = 5.f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mUiStructureController = (UiStructureController) pCore.controllerManager().getControllerByName(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUiCoreTexture = pResourceManager.textureManager().textureCore();
		mHudTexture = pResourceManager.textureManager().getTexture("TEXTURE_HUD", entityGroupID());

		mContentDisplayArea.y(mWindowArea.y() + getTitleBarHeight());
		mContentDisplayArea.h(mWindowArea.h() - +getTitleBarHeight());

		mIsLoaded = true;

	}

	public void unloadGLContent() {

	}

	public boolean handleInput(LintfordCore pCore) {
		if (!isActive() || !isOpen())
			return false;

		final boolean lMouseOverWindow = mWindowArea.intersectsAA(pCore.HUD().getMouseCameraSpace());
		final boolean lMouseLeftClick = pCore.input().mouse().isMouseLeftClick(hashCode());

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

			float lDifferenceX = (pCore.input().mouse().mouseWindowCoords().x - dx);
			float lDifferenceY = (pCore.input().mouse().mouseWindowCoords().y - dy);

			mWindowArea.x(mWindowArea.x() + lDifferenceX);
			mWindowArea.y(mWindowArea.y() + lDifferenceY);

			// update the delta
			dx = pCore.input().mouse().mouseWindowCoords().x;
			dy = pCore.input().mouse().mouseWindowCoords().y;

			return true;

		}

		// 2. window captures mouse clicks even if not dragging
		if (mIsWindowMoveable && !mIsWindowMoving && lMouseOverWindow) {

			// Only acquire lock when we are ready to move ...
			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				if (!mMouseDownLastUpdate) {
					mMouseDownLastUpdate = true;
					dx = pCore.input().mouse().mouseWindowCoords().x;
					dy = pCore.input().mouse().mouseWindowCoords().y;

				}

				final float nx = pCore.input().mouse().mouseWindowCoords().x;
				final float ny = pCore.input().mouse().mouseWindowCoords().y;

				final int MINIMUM_TOLERENCE = 3;

				if (Math.abs(nx - dx) > MINIMUM_TOLERENCE || Math.abs(ny - dy) > MINIMUM_TOLERENCE) {
					// Now we can try to acquire the lock, and if we get it, start dragging the window
					if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
						mIsWindowMoving = true;

					}

				}

			}

		}

		if (!lMouseLeftClick) {
			mIsWindowMoving = false;
			mMouseDownLastUpdate = false;

		}

		// This is needed because when the mouse is over a component
		if (lMouseOverWindow) {
			mIsMouseOver = true;

			// TODO: This isn't working. even though you click on a window, you still get clicks registerd in the game.
			if (lMouseLeftClick) {
				pCore.input().mouse().tryAcquireMouseLeftClick(hashCode());
			}

			if (mCanCaptureMouse && pCore.input().mouse().tryAcquireMouseOverThisComponent(hashCode()) && pCore.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

			}

		} else {
			mIsMouseOver = false;

		}

		return mIsMouseOver;

	}

	public void update(LintfordCore pCore) {
		if (!isOpen())
			return;

		if (!mIsWindowMoveable) {
			updateWindowPosition(pCore);

		} else {
			keepWindowOnScreen(pCore.HUD());

		}

		updateWindowScales(pCore);

		if (mMouseClickTimer >= 0) {
			mMouseClickTimer -= pCore.appTime().elapsedTimeMilli();

		}

		// Update the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).update(pCore);

		}

		if (mFullContentRectangle.h() < mContentDisplayArea.h()) {
			mFullContentRectangle.h(mContentDisplayArea.h());

		}

		if (mFullContentRectangle.h() - contentDisplayArea().h() > 0) {
			mScrollBar.update(pCore);

		}

		final var lDeltaTime = (float) pCore.appTime().elapsedTimeSeconds();
		var lScrollSpeedFactor = mYScrollVal;

		mZScrollVelocity += mZScrollAcceleration;
		lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
		mZScrollVelocity *= 0.85f;
		mZScrollAcceleration = 0.0f;

		// Constrain
		mYScrollVal = lScrollSpeedFactor;
		if (mYScrollVal > 0)
			mYScrollVal = 0;
		if (mYScrollVal < -(mFullContentRectangle.h() - mContentDisplayArea.h()))
			mYScrollVal = -(mFullContentRectangle.h() - mContentDisplayArea.h());

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isOpen())
			return;

		if (mUiStructureController == null) {
			mUiStructureController = (UiStructureController) pCore.controllerManager().getControllerByName(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
			if (mUiStructureController == null)
				return;
		}

		mWindowAlpha = 0.95f;

		final var lTextureBatch = mRendererManager.uiTextureBatch();
		final var lTextFont = mRendererManager.textFont();
		final var lWindowColor = ColorConstants.getWhiteWithAlpha(mWindowAlpha);

		// Draw the window background
		lTextureBatch.begin(pCore.HUD());
		TextureBatch9Patch.draw9Patch(lTextureBatch, mUiCoreTexture, 32, mWindowArea.x(), mWindowArea.y() + getTitleBarHeight() + 5, mWindowArea.w(), mWindowArea.h() - getTitleBarHeight() - 5, Z_DEPTH, lWindowColor);
		lTextureBatch.end();

		final var lWindowTitleColor = ColorConstants.getWhiteWithAlpha(0.6f);

		// Draw the title bar
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mUiCoreTexture, 0, 256, 32, 32, mWindowArea.x(), mWindowArea.y(), 32, getTitleBarHeight(), Z_DEPTH, lWindowTitleColor);
		lTextureBatch.draw(mUiCoreTexture, 32, 256, 32, 32, mWindowArea.x() + 32, mWindowArea.y(), mWindowArea.w() - 64, getTitleBarHeight(), Z_DEPTH, lWindowTitleColor);
		lTextureBatch.draw(mUiCoreTexture, 128, 256, 32, 32, mWindowArea.x() + mWindowArea.w() - 32, mWindowArea.y(), 32, 32.f, Z_DEPTH, lWindowTitleColor);

		float lTitleX = mWindowArea.x();
		float lTitleY = mWindowArea.y();

		// Render the icons from the game ui texture
		if (mIconSrcRectangle != null && !mIconSrcRectangle.isEmpty() && mHudTexture != null) {
			lTextureBatch.draw(mHudTexture, mIconSrcRectangle, lTitleX, lTitleY, getTitleBarHeight(), getTitleBarHeight(), Z_DEPTH, lWindowColor);
			lTitleX += 32 + WINDOW_CONTENT_PADDING_X;

		} else {
			lTitleX += 8.f; // offset when no icon in title bar
		}

		// Draw the window title
		final var lTitleFontUnit = mRendererManager.titleFont();
		lTitleFontUnit.begin(pCore.HUD());
		lTitleFontUnit.drawText(mWindowTitle, lTitleX, lTitleY, Z_DEPTH, ColorConstants.TextHeadingColor, 1.15f);

		if (mFullContentRectangle.h() - contentDisplayArea().h() > 0) {
			mScrollBar.draw(pCore, lTextureBatch, mUiCoreTexture, Z_DEPTH);
		}

		lTextFont.begin(pCore.HUD());

		// Draw the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).draw(pCore, lTextureBatch, mUiCoreTexture, lTextFont, ZLayers.LAYER_GAME_UI + ((float) i * 0.001f));

		}

		lTextFont.end();
		lTextureBatch.end();
		lTitleFontUnit.end();

		if (ConstantsApp.getBooleanValueDef("DRAW_UI_BOUNDS", false)) {
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mWindowArea);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateWindowPosition(LintfordCore pCore) {
		final var lHUDBoundingRect = pCore.HUD().boundingRectangle();

		if (lHUDBoundingRect == null) {
			return;

		}

		final float lWindowScaleFactorX = mUiStructureController.windowAutoScaleFactorX();
		final float lWindowScaleFactorY = mUiStructureController.windowAutoScaleFactorY();

		final float lScreenPaddingX = SCREEN_PADDING_X * lWindowScaleFactorX;
		final float lScreenPaddingY = SCREEN_PADDING_Y * lWindowScaleFactorY;

		final float lWindowPaddingX = (paddingLeft() + paddingRight()) * lWindowScaleFactorX;
		final float lWindowPaddingY = (paddingTop() + paddingBottom()) * lWindowScaleFactorY;

		final var lX = lHUDBoundingRect.left() + lScreenPaddingX;
		final var lY = lHUDBoundingRect.top() + lScreenPaddingY;
		final var lW = lHUDBoundingRect.w() * 0.5f - lWindowPaddingX - lScreenPaddingX;
		final var lH = lHUDBoundingRect.h() / 2 - lWindowPaddingY - lScreenPaddingY;

		mWindowArea.set(lX, lY, lW, lH);

	}

	public void keepWindowOnScreen(ICamera pHUD) {
		if (mWindowArea.x() < pHUD.boundingRectangle().left())
			mWindowArea.x(pHUD.boundingRectangle().left());

		if (mWindowArea.y() < pHUD.boundingRectangle().top())
			mWindowArea.y(pHUD.boundingRectangle().top());

		if (mWindowArea.x() + mWindowArea.width() > pHUD.boundingRectangle().right())
			mWindowArea.x(pHUD.boundingRectangle().right() - mWindowArea.width());

		if (mWindowArea.y() + mWindowArea.height() > pHUD.boundingRectangle().bottom())
			mWindowArea.y(pHUD.boundingRectangle().bottom() - mWindowArea.height());

	}

	protected void updateWindowScales(LintfordCore pCore) {

		if (mUiStructureController == null) {
			mUiStructureController = (UiStructureController) pCore.controllerManager().getControllerByName(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
		}

		final float lUiScaleFactor = mUiStructureController.uiScaleFactor();
		final float lWindowScaleFactorX = mUiStructureController.windowAutoScaleFactorX() * lUiScaleFactor;
		final float lWindowScaleFactorY = mUiStructureController.windowAutoScaleFactorY() * lUiScaleFactor;

		final float MAX_TITLEBAR_HEIGHT = 32.f;

		mTitleBarHeight = MathHelper.clamp(BASE_TITLEBAR_HEIGHT * lWindowScaleFactorY * lWindowScaleFactorY, BASE_TITLEBAR_HEIGHT, MAX_TITLEBAR_HEIGHT);
		mPanelSizeWidth = BASE_PANEL_WIDTH * lWindowScaleFactorX * lWindowScaleFactorX;
		mPanelSizeHeight = BASE_PANEL_HEIGHT * lWindowScaleFactorY * lWindowScaleFactorY;

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
	public void onWindowClosed() {

	}

	@Override
	public void onWindowOpened() {

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
