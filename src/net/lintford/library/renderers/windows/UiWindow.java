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
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
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

	protected float mWindowMarginLeft;
	protected float mWindowMarginRight;
	protected float mWindowMarginTop;
	protected float mWindowMarginBottom;

	protected float mWindowPaddingLeft;
	protected float mWindowPaddingRight;
	protected float mWindowPaddingTop;
	protected float mWindowPaddingBottom;

	protected int mWindowIconSpriteIndex;
	protected String mIconName;

	protected boolean mCanCaptureMouse;
	protected boolean mIsMouseOver;

	/** Stores the window area of this renderer window */
	protected Rectangle mWindowArea;
	protected SpriteSheetDefinition mCoreSpritesheet;
	protected SpriteSheetDefinition mHudSpritesheet;

	/** If true, this base renderer consumes input and ends the handleInput invocation chain. */
	protected boolean mExclusiveHandleInput = true;
	protected boolean mIsDebugWindow;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void paddingLeft(float newValue) {
		mWindowPaddingLeft = newValue;
	}

	public float paddingLeft() {
		return mWindowPaddingLeft;
	}

	public void paddingRight(float newValue) {
		mWindowPaddingRight = newValue;
	}

	public float paddingRight() {
		return mWindowPaddingRight;
	}

	public void paddingTop(float newValue) {
		mWindowPaddingTop = newValue;
	}

	public float paddingTop() {
		return mWindowPaddingTop;
	}

	public void paddingBottom(float newValue) {
		mWindowPaddingBottom = newValue;
	}

	public float paddingBottom() {
		return mWindowPaddingBottom;
	}

	public void marginLeft(float newValue) {
		mWindowMarginLeft = newValue;
	}

	public float marginLeft() {
		return mWindowMarginLeft;
	}

	public void marginRight(float newValue) {
		mWindowMarginRight = newValue;
	}

	public float marginRight() {
		return mWindowMarginRight;
	}

	public void marginTop(float newValue) {
		mWindowMarginTop = newValue;
	}

	public float marginTop() {
		return mWindowMarginTop;
	}

	public void marginBottom(float newValue) {
		mWindowMarginBottom = newValue;
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

	public int iconSpriteIndex() {
		return mWindowIconSpriteIndex;
	}

	public boolean exclusiveHandleInput() {
		return mExclusiveHandleInput;
	}

	/** @return true if this window is open, false if closed or minimised. */
	public boolean isOpen() {
		return mIsOpen;
	}

	public void isOpen(boolean newValue) {
		mIsOpen = newValue;

		if (mIsOpen) {
			onWindowOpened();

		} else {
			onWindowClosed();
		}
	}

	public String windowTitle() {
		return mWindowTitle;
	}

	public void windowTitle(String newTitle) {
		mWindowTitle = newTitle;
	}

	public float getTitleBarHeight() {
		return mTitleBarHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiWindow(final RendererManager rendererManager, final String rendererName, final int entityGroupUid) {
		super(rendererManager, rendererName, entityGroupUid);

		mComponents = new ArrayList<>();

		mWindowIconSpriteIndex = -1;
		mWindowArea = new Rectangle();
		mContentDisplayArea = new Rectangle();

		mWindowArea.set(-160, -120, 320, 240);

		mWindowAlpha = 1.0f;

		mFullContentRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mFullContentRectangle);

		mFullContentRectangle.set(mWindowArea.x(), mWindowArea.y() + BASE_TITLEBAR_HEIGHT, 0, 0);

		// sane default
		mWindowTitle = rendererName;

		mIsWindowMoveable = false;
		mUiInputFromUiManager = true;

		final float lDefaultPadding = 2.f;
		mWindowPaddingTop = lDefaultPadding;
		mWindowPaddingBottom = lDefaultPadding;
		mWindowPaddingLeft = lDefaultPadding;
		mWindowPaddingRight = lDefaultPadding;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mUiStructureController = (UiStructureController) core.controllerManager().getControllerByName(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();
		mHudSpritesheet = resourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_HUD", entityGroupID());

		mContentDisplayArea.y(mWindowArea.y() + getTitleBarHeight());
		mContentDisplayArea.height(mWindowArea.height() - +getTitleBarHeight());

		mResourcesLoaded = true;
	}

	public boolean handleInput(LintfordCore core) {
		if (!isActive() || !isOpen())
			return false;

		final boolean lMouseOverWindow = mWindowArea.intersectsAA(core.HUD().getMouseCameraSpace());
		final boolean lMouseLeftClick = core.input().mouse().isMouseLeftClick(hashCode());

		if (lMouseOverWindow) {
			if (core.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				final float scrollAccelerationAmt = core.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(scrollAccelerationAmt);
			}
		}

		// 1. Check if the scroll bar has been used
		if (mScrollBar.handleInput(core, rendererManager())) {
			return true;
		}

		// Update the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			if (mComponents.get(i).handleInput(core)) {
				return true;
			}
		}

		if (mIsWindowMoving) {
			// check if user has stopped dragging the window (worst case we skip this frame)
			if (!core.input().mouse().isMouseLeftClick(hashCode())) {
				mIsWindowMoving = false;
				mMouseDownLastUpdate = false;

				return false;
			}

			float lDifferenceX = (core.input().mouse().mouseWindowCoords().x - dx);
			float lDifferenceY = (core.input().mouse().mouseWindowCoords().y - dy);

			mWindowArea.x(mWindowArea.x() + lDifferenceX);
			mWindowArea.y(mWindowArea.y() + lDifferenceY);

			// update the delta
			dx = core.input().mouse().mouseWindowCoords().x;
			dy = core.input().mouse().mouseWindowCoords().y;

			return true;
		}

		// 2. window captures mouse clicks even if not dragging
		if (mIsWindowMoveable && !mIsWindowMoving && lMouseOverWindow) {

			// Only acquire lock when we are ready to move ...
			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				if (!mMouseDownLastUpdate) {
					mMouseDownLastUpdate = true;
					dx = core.input().mouse().mouseWindowCoords().x;
					dy = core.input().mouse().mouseWindowCoords().y;

				}

				final float nx = core.input().mouse().mouseWindowCoords().x;
				final float ny = core.input().mouse().mouseWindowCoords().y;

				final int MINIMUM_TOLERENCE = 3;

				if (Math.abs(nx - dx) > MINIMUM_TOLERENCE || Math.abs(ny - dy) > MINIMUM_TOLERENCE) {
					// Now we can try to acquire the lock, and if we get it, start dragging the window
					if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
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

			if (lMouseLeftClick) {
				core.input().mouse().tryAcquireMouseLeftClick(hashCode());
			}
		} else {
			mIsMouseOver = false;
		}

		return mIsMouseOver;
	}

	public void update(LintfordCore core) {
		if (!isOpen())
			return;

		if (!mIsWindowMoveable) {
			updateWindowPosition(core);
		} else {
			keepWindowOnScreen(core.HUD());
		}

		updateWindowScales(core);

		if (mMouseClickTimer >= 0) {
			mMouseClickTimer -= core.appTime().elapsedTimeMilli();
		}

		// Update the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).update(core);
		}

		if (mFullContentRectangle.height() < mContentDisplayArea.height()) {
			mFullContentRectangle.height(mContentDisplayArea.height());
		}

		mScrollBar.update(core);
	}

	@Override
	public void draw(LintfordCore core) {
		if (!isOpen())
			return;

		if (mUiStructureController == null) {
			mUiStructureController = (UiStructureController) core.controllerManager().getControllerByName(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
			if (mUiStructureController == null)
				return;
		}

		mWindowAlpha = 0.95f;

		final var lUiHeaderFont = mRendererManager.uiHeaderFont();
		final var lTextFont = mRendererManager.uiTextFont();
		final var lSpritebatch = mRendererManager.uiSpriteBatch();
		final var lWindowColor = ColorConstants.getWhiteWithAlpha(mWindowAlpha);

		// Draw the window background
		lSpritebatch.begin(core.HUD());
		TextureBatch9Patch.draw9Patch(lSpritebatch, mCoreSpritesheet, 32, mWindowArea.x(), mWindowArea.y(), mWindowArea.width(), mWindowArea.height(), Z_DEPTH, lWindowColor);
		lSpritebatch.end();

		// Draw the title bar
		lSpritebatch.begin(core.HUD());

		float lTitleX = mWindowArea.x() + WINDOW_CONTENT_PADDING_X;
		float lTitleY = mWindowArea.y();

		lUiHeaderFont.begin(core.HUD());
		lUiHeaderFont.drawText(mWindowTitle, lTitleX, lTitleY + getTitleBarHeight() * .5f - lUiHeaderFont.fontHeight() * .5f + 3.f, Z_DEPTH, ColorConstants.TextHeadingColor, 1f);

		if (mFullContentRectangle.height() - contentDisplayArea().height() > 0) {
			mScrollBar.draw(core, lSpritebatch, mCoreSpritesheet, Z_DEPTH);
		}

		lTextFont.begin(core.HUD());

		// Draw the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).draw(core, lSpritebatch, mCoreSpritesheet, lTextFont, ZLayers.LAYER_GAME_UI + ((float) i * 0.001f));
		}

		lTextFont.end();
		lSpritebatch.end();
		lUiHeaderFont.end();

		if (ConstantsApp.getBooleanValueDef("DRAW_UI_BOUNDS", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mWindowArea);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateWindowPosition(LintfordCore core) {
		final var lHUDBoundingRect = core.HUD().boundingRectangle();

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
		final var lW = lHUDBoundingRect.width() * 0.5f - lWindowPaddingX - lScreenPaddingX;
		final var lH = lHUDBoundingRect.height() / 2 - lWindowPaddingY - lScreenPaddingY;

		mWindowArea.set(lX, lY, lW, lH);
	}

	public void keepWindowOnScreen(ICamera hud) {
		if (mWindowArea.x() < hud.boundingRectangle().left())
			mWindowArea.x(hud.boundingRectangle().left());

		if (mWindowArea.y() < hud.boundingRectangle().top())
			mWindowArea.y(hud.boundingRectangle().top());

		if (mWindowArea.x() + mWindowArea.width() > hud.boundingRectangle().right())
			mWindowArea.x(hud.boundingRectangle().right() - mWindowArea.width());

		if (mWindowArea.y() + mWindowArea.height() > hud.boundingRectangle().bottom())
			mWindowArea.y(hud.boundingRectangle().bottom() - mWindowArea.height());
	}

	protected void updateWindowScales(LintfordCore core) {

		if (mUiStructureController == null) {
			mUiStructureController = (UiStructureController) core.controllerManager().getControllerByName(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
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
	public Rectangle contentDisplayArea() {
		return mContentDisplayArea;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mFullContentRectangle;
	}

	public void addComponent(UIWidget component) {
		if (!mComponents.contains(component)) {
			mComponents.add(component);
		}
	}

	public void removeComponent(UIWidget component) {
		if (mComponents.contains(component)) {
			mComponents.remove(component);
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