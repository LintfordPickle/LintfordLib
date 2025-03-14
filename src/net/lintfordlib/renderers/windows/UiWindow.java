package net.lintfordlib.renderers.windows;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.hud.HudLayoutController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.batching.TextureBatch9Patch;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.renderers.windows.components.UIWidget;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.renderers.windows.components.interfaces.UIWindowChangeListener;

public class UiWindow extends BaseRenderer implements IScrollBarArea, UIWindowChangeListener, IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final float Z_DEPTH = 2.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mPanelSizeWidth;
	protected float mPanelSizeHeight;

	protected List<UIWidget> mComponents;
	protected boolean mIsOpen;

	protected boolean mRenderWindowTitle;
	protected String mWindowTitle;
	protected float mTitleBarHeight;

	protected boolean mMouseDownLastUpdate;

	// This is the area within which any scrollable content will be displayed. Scrollbars are only visible if the
	// height of the mContentDisplayArea is smaller than the height of the mContentRectangle (below).
	protected Rectangle mContentDisplayArea;

	// This is the area that the content would take up, if not limited by the window bounds (i.e. the area of the 'content' visualisation).
	protected ScrollBarContentRectangle mFullContentRectangle;
	protected HudLayoutController mUiStructureController;

	protected boolean mUiInputFromUiManager;
	protected boolean mIsWindowMoveable;
	protected boolean mIsWindowMoving;
	protected float dx;
	protected float dy;
	protected float mWindowAlpha;

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
	protected boolean mDrawWindowBackground;

	/** Stores the window area of this renderer window */
	protected Rectangle mWindowArea;
	protected SpriteSheetDefinition mCoreSpritesheet;

	/** If true, this base renderer consumes input and ends the handleInput invocation chain. */
	protected boolean mExclusiveHandleInput = true;
	protected boolean mIsDebugWindow;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean drawWindowBackground() {
		return mDrawWindowBackground;
	}

	public void drawWindowBackground(boolean drawWindowBackground) {
		mDrawWindowBackground = drawWindowBackground;
	}

	public HudLayoutController uiStructureController() {
		return mUiStructureController;
	}

	public SpriteSheetDefinition coreSpritesheet() {
		return mCoreSpritesheet;
	}

	public boolean renderWindowTitle() {
		return mRenderWindowTitle;
	}

	public void renderWindowTitle(boolean renderTitle) {
		mRenderWindowTitle = renderTitle;
	}

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

	public UiWindow(RendererManagerBase rendererManager, String rendererName, int entityGroupUid) {
		super(rendererManager, rendererName, entityGroupUid);

		mComponents = new ArrayList<>();

		mWindowIconSpriteIndex = -1;
		mWindowArea = new Rectangle();
		mContentDisplayArea = new Rectangle();

		mWindowArea.set(-160, -120, 320, 240);

		mWindowAlpha = 1.0f;

		mFullContentRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mFullContentRectangle);

		mFullContentRectangle.set(mWindowArea.x(), mWindowArea.y() + ConstantsUi.UI_WINDOW_REFERENCE_TITLEBAR_HEIGHT, 0, 0);

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
		mUiStructureController = (HudLayoutController) core.controllerManager().getControllerByName(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();

		mContentDisplayArea.y(mWindowArea.y() + getTitleBarHeight());
		mContentDisplayArea.height(mWindowArea.height() - +getTitleBarHeight());

		mResourcesLoaded = true;
	}

	@Override
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

		if (mScrollBar.handleInput(core, rendererManager()))
			return true;

		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			if (mComponents.get(i).handleInput(core)) {
				return true;
			}
		}

		if (mIsWindowMoving) {
			if (!core.input().mouse().isMouseLeftClick(hashCode())) {
				mIsWindowMoving = false;
				mMouseDownLastUpdate = false;

				return false;
			}

			float lDifferenceX = (core.input().mouse().mouseWindowCoords().x - dx);
			float lDifferenceY = (core.input().mouse().mouseWindowCoords().y - dy);

			mWindowArea.x(mWindowArea.x() + lDifferenceX);
			mWindowArea.y(mWindowArea.y() + lDifferenceY);

			dx = core.input().mouse().mouseWindowCoords().x;
			dy = core.input().mouse().mouseWindowCoords().y;

			return true;
		}

		if (mIsWindowMoveable && lMouseOverWindow) {
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

	@Override
	public void update(LintfordCore core) {
		if (!isOpen())
			return;

		if (!mIsWindowMoveable)
			updateWindowPosition(core);
		else
			keepWindowOnScreen(core.HUD());

		updateWindowScales(core);

		if (mInputTimer >= 0)
			mInputTimer -= core.appTime().elapsedTimeMilli();

		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).update(core);
		}

		if (mFullContentRectangle.height() < mContentDisplayArea.height())
			mFullContentRectangle.height(mContentDisplayArea.height());

		mScrollBar.update(core);
	}

	@Override
	public void draw(LintfordCore core, RenderPass pass) {
		if (!isOpen())
			return;

		if (mUiStructureController == null) {
			mUiStructureController = (HudLayoutController) core.controllerManager().getControllerByName(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
			if (mUiStructureController == null)
				return;
		}

		mWindowAlpha = 0.95f;

		final var lSharedResources = mRendererManager.sharedResources();
		final var lUiHeaderFont = lSharedResources.uiHeaderFont();
		final var lTextFont = lSharedResources.uiTextFont();
		final var lSpritebatch = lSharedResources.uiSpriteBatch();

		final var x = (int) mWindowArea.x();
		final var y = (int) mWindowArea.y();
		final var w = (int) mWindowArea.width();
		final var h = (int) mWindowArea.height();

		// Draw the window background
		if (mDrawWindowBackground) {
			lSpritebatch.begin(core.HUD());
			lSpritebatch.setColorRGBA(1.f, 1.f, 1.f, mWindowAlpha);
			TextureBatch9Patch.drawBackground(lSpritebatch, mCoreSpritesheet, 32, x, y, w, h, false, .01f);
			lSpritebatch.end();
		}

		final var lTitleX = mWindowArea.x() + 5.f;
		final var lTitleY = mWindowArea.y() + 5.f;

		if (mWindowTitle != null) {
			lUiHeaderFont.begin(core.HUD());
			lUiHeaderFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			lUiHeaderFont.drawText(mWindowTitle, lTitleX, lTitleY + getTitleBarHeight() * .5f - lUiHeaderFont.fontHeight() * .5f + 3.f, .01f, 1f);
			lUiHeaderFont.end();
		}

		if (mScrollBar.scrollBarEnabled() && mScrollBar.areaNeedsScrolling()) {
			mScrollBar.scrollBarAlpha(mWindowAlpha);

			lSpritebatch.begin(core.HUD());
			mScrollBar.draw(core, lSpritebatch, mCoreSpritesheet, Z_DEPTH);
			lSpritebatch.end();
		}

		// Draw the window components
		final int lComponentCount = mComponents.size();
		for (int i = 0; i < lComponentCount; i++) {
			mComponents.get(i).draw(core, lSharedResources, mCoreSpritesheet, lTextFont, ZLayers.LAYER_GAME_UI + (i * 0.001f));
		}

		if (ConstantsApp.getBooleanValueDef("DRAW_UI_BOUNDS", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mWindowArea);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateWindowPosition(LintfordCore core) {
		final var lHUDBoundingRect = core.HUD().boundingRectangle();

		if (lHUDBoundingRect == null)
			return;

		final float lScreenPaddingX = ConstantsUi.UI_WINDOW_PADDING_X;
		final float lScreenPaddingY = ConstantsUi.UI_WINDOW_PADDING_Y;

		final float lWindowPaddingX = (paddingLeft() + paddingRight());
		final float lWindowPaddingY = (paddingTop() + paddingBottom());

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
		if (mUiStructureController == null)
			mUiStructureController = (HudLayoutController) core.controllerManager().getControllerByName(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		final var lUiScaleFactor = mUiStructureController.uiScaleFactor();

		final var MAX_TITLEBAR_HEIGHT = 32.f;
		final var lUiWindowReferenceWidth = ConstantsUi.UI_WINDOW_REFERENCE_WIDTH;
		final var lUiWindowReferenceHeight = ConstantsUi.UI_WINDOW_REFERENCE_HEIGHT;
		final var lUiWindowTitlebarHeight = ConstantsUi.UI_WINDOW_REFERENCE_TITLEBAR_HEIGHT;

		mTitleBarHeight = MathHelper.clamp(lUiWindowTitlebarHeight * lUiScaleFactor, lUiWindowTitlebarHeight, MAX_TITLEBAR_HEIGHT);
		mPanelSizeWidth = lUiWindowReferenceWidth * lUiScaleFactor;
		mPanelSizeHeight = lUiWindowReferenceHeight * lUiScaleFactor;
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
		// ignore
	}

	@Override
	public void onWindowOpened() {
		// ignore
	}

	// --------------------------------------
	// IProcessMouseInput Inherited Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer < 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = cooldownInMs;
	}

	@Override
	public boolean allowGamepadInput() {
		return true;
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}
}