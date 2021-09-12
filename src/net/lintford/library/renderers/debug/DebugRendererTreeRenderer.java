package net.lintford.library.renderers.debug;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.debug.DebugRendererTreeController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;

public class DebugRendererTreeRenderer extends Rectangle implements IScrollBarArea, IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1937162238791885253L;

	public static final float ENTRY_HEIGHT = 32;
	public static final float MAX_PANE_HEIGHT = 600;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Debug mDebugManager;
	private Texture mCoreTexture;
	private TextureBatchPCT mTextureBatch;
	private DebugRendererTreeController mDebugRendererTree;
	private transient FontUnit mConsoleFont;
	private boolean mIsOpen;

	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBar mScrollBar;
	private transient float mScrollYPosition;
	protected float mZScrollAcceleration;
	protected float mZScrollVelocity;
	private boolean mScrollBarEnabled;
	private transient int mLowerBound;
	private transient int mUpperBound;

	private float mOpenWidth = 350;
	private float mOpenHeight = 500;
	private float mClickTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void isOpen(boolean pNewValue) {
		mIsOpen = pNewValue;
	}

	public boolean isOpen() {
		return mIsOpen;
	}

	public boolean isInitialized() {
		return mDebugRendererTree != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugRendererTreeRenderer(final Debug pDebugManager) {
		mDebugManager = pDebugManager;

		if (pDebugManager.debugManagerEnabled()) {
			mTextureBatch = new TextureBatchPCT();

			mContentRectangle = new ScrollBarContentRectangle(this);
			mScrollBar = new ScrollBar(this, mContentRectangle);

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats loading GL content");

		mCoreTexture = pResourceManager.textureManager().textureCore();
		mConsoleFont = pResourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CONSOLE_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		mTextureBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats unloading GL content");

		mTextureBatch.unloadGLContent();

		mConsoleFont = null;
		mCoreTexture = null;

	}

	public void handleInput(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mIsOpen)
			return;

		if (!isInitialized()) {
			getRendererManagerInstance(pCore);
			return;
		}

		// *** Control the selection of ControllerWidgets ***
		if (mScrollBar.handleInput(pCore)) {
			return;

		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {

			if (pCore.input().mouse().tryAcquireMouseMiddle(hashCode())) {
				mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

			}

			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				final float lMouseX = pCore.HUD().getMouseCameraSpace().x;
				final float lMouseY = pCore.HUD().getMouseCameraSpace().y;

				//
				if (lMouseX > x && lMouseX < x + w - mScrollBar.w()) {
					final var lComponentTree = mDebugRendererTree.treeComponents();
					final var lControllerWidgetCount = lComponentTree.size();
					for (int i = 0; i < lControllerWidgetCount; i++) {
						final var lControllerWidget = lComponentTree.get(i);

						if (lMouseY > lControllerWidget.y() + lControllerWidget.h()) {
							continue;

						}

						if (lControllerWidget.baseRenderer != null) {
							lControllerWidget.baseRenderer.isActive(!lControllerWidget.baseRenderer.isActive());

						}

						// Once we have handled, then we can exit the loop
						break;

					}

				}
			}

			// If the mouse is over this window, then don't let clicks fal-through.
			pCore.input().mouse().mouseHoverOverHash(hashCode());

		}

		// *** Control the scroll bar positioning ***
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP)) {
			mScrollYPosition += ENTRY_HEIGHT;

			if (mScrollYPosition > 0)
				mScrollYPosition = 0;

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN)) {
			mScrollYPosition -= ENTRY_HEIGHT;

			if (mScrollYPosition < mScrollBar.getScrollYBottomPosition())
				mScrollYPosition = mScrollBar.getScrollYBottomPosition() - ENTRY_HEIGHT;
		}

	}

	public void update(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		final float lDeltaTime = (float) pCore.appTime().elapsedTimeMilli() / 1000f;

		mIsOpen = mDebugManager.console().isOpen();

		if (!mIsOpen)
			return;

		if (mClickTimer >= 0) {
			mClickTimer -= pCore.appTime().elapsedTimeMilli();

		}

		if (!isInitialized()) {
			getRendererManagerInstance(pCore);
			return;
		}

		{
			final var lRendererList = mDebugRendererTree.treeComponents();
			final var lNumberRenderers = lRendererList.size();
			final var lDisplayManager = pCore.config().display();

			final var windowHeight = lDisplayManager.windowHeight();
			final var consoleHeight = Debug.debugManager().console().openHeight();
			mOpenHeight = windowHeight - consoleHeight - 60f;

			// Update the bounds of the window view
			x = lDisplayManager.windowWidth() * 0.5f - mOpenWidth - 5f;
			final float lConsoleYOffset = Debug.debugManager().console().isOpen() ? consoleHeight : 5f;
			y = -lDisplayManager.windowHeight() * 0.5f + lConsoleYOffset + 5f + 25f;
			w = mOpenWidth;
			h = mOpenHeight;

			// *** UPDATE THE COMPONENT WIDGETS ***

			float lPosY = y + mScrollYPosition;

			for (int i = 0; i < lNumberRenderers; i++) {
				final var lControllerWidget = lRendererList.get(i);

				lControllerWidget.update(pCore);
				lControllerWidget.set(x, lPosY, w, ENTRY_HEIGHT);

				lPosY += lControllerWidget.h();

			}

			final int lMaxNumLines = (int) (h / ENTRY_HEIGHT) + 1;
			mLowerBound = (int) -((mScrollYPosition) / ENTRY_HEIGHT);
			mUpperBound = mLowerBound + lMaxNumLines;

			fullContentArea().setCenter(x, y, w - mScrollBar.w(), lNumberRenderers * ENTRY_HEIGHT);

			float lScrollSpeedFactor = mScrollYPosition;

			mZScrollVelocity += mZScrollAcceleration;
			lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
			mZScrollVelocity *= 0.85f;
			mZScrollAcceleration = 0.0f;

			mScrollYPosition = lScrollSpeedFactor;

			// Constrain
			if (mScrollYPosition > 0)
				mScrollYPosition = 0;
			if (mScrollYPosition < -(mContentRectangle.h() - this.h)) {
				mScrollYPosition = -(mContentRectangle.h() - this.h);
			}

			mScrollBarEnabled = h < mContentRectangle.h();

			if (mScrollBarEnabled) {
				mScrollBar.update(pCore);

			}

		}

	}

	public void draw(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!isInitialized()) {
			getRendererManagerInstance(pCore);
			return;
		}

		if (!mIsOpen)
			return;

		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(mCoreTexture, 0, 0, 32, 32, x, y - 25f, mOpenWidth, 25f, -0.03f, ColorConstants.MenuPanelPrimaryColor);
		mTextureBatch.draw(mCoreTexture, 0, 0, 32, 32, x, y, mOpenWidth, mOpenHeight, -0.03f, ColorConstants.MenuPanelSecondaryColor);
		mTextureBatch.end();

		mConsoleFont.begin(pCore.HUD());
		mConsoleFont.drawText("Renderers", x + 5f, y - 25f, -0.01f, ColorConstants.WHITE, 1, -1);
		mConsoleFont.end();

		// Getting list of ControllerItems to render
		final var lControllerList = mDebugRendererTree.treeComponents();

		// The lControllerList contains a flat list of all <BaseControllerWidget> which we created for the controllers
		final var lNumTreeComponents = lControllerList.size();

		if (lNumTreeComponents == 0) {
			mConsoleFont.drawText("No BaseRenderers on Screen", x + 5f, y + 5f, -0.02f, ColorConstants.TextHeadingColor, 1, -1);
			return;
		}

		if (h < mContentRectangle.h())
			mContentRectangle.preDraw(pCore, mTextureBatch, mCoreTexture);

		mConsoleFont.begin(pCore.HUD());
		mTextureBatch.begin(pCore.HUD());

		for (int i = mLowerBound; i < mUpperBound; i++) {
			if (i < 0)
				continue;
			if (i >= lNumTreeComponents)
				break;

			final var lBaseRendererWidget = lControllerList.get(i);
			final var lRendererName = lBaseRendererWidget.displayName;

			float lPosX = 10f + lBaseRendererWidget.rendererLevel * 30f;

			mConsoleFont.drawText(lRendererName, lBaseRendererWidget.x() + lPosX, lBaseRendererWidget.y(), -0.02f, ColorConstants.TextHeadingColor, 1, -1);

			final float lActiveIconX = x + mOpenWidth - 64;
			final float lActiveIconY = lBaseRendererWidget.y();

			if (lBaseRendererWidget == null || !lBaseRendererWidget.isRendererActive) {
				mTextureBatch.draw(mCoreTexture, 32, 128, 32, 32, lActiveIconX, lActiveIconY, 32, 32, -0.01f, ColorConstants.WHITE);

			} else {
				mTextureBatch.draw(mCoreTexture, 64, 128, 32, 32, lActiveIconX, lActiveIconY, 32, 32, -0.01f, ColorConstants.WHITE);

			}

		}

		mTextureBatch.end();
		mConsoleFont.end();

		if (h < mContentRectangle.h())
			mContentRectangle.postDraw(pCore);

		mTextureBatch.begin(pCore.HUD());

		if (mScrollBarEnabled) {
			mScrollBar.draw(pCore, mTextureBatch, mCoreTexture, -0.02f);

		}

		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void getRendererManagerInstance(LintfordCore pCore) {
		mDebugRendererTree = (DebugRendererTreeController) pCore.controllerManager().getControllerByName(DebugRendererTreeController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	@Override
	public float currentYPos() {
		return mScrollYPosition;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mScrollYPosition += pAmt;
	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mScrollYPosition = pValue;
	}

	@Override
	public Rectangle contentDisplayArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentRectangle;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mClickTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mClickTimer = 200;

	}

}
