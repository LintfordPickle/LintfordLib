package net.lintford.library.renderers.debug;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.DebugTreeController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;

public class DebugControllerRenderer extends Rectangle implements IScrollBarArea, IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1937162238791885253L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Debug mDebugManager;
	private Texture mCoreTexture;
	private TextureBatch mTextureBatch;
	private StringBuilder mStringBuilder;
	private ControllerManager mControllerManager;
	private DebugTreeController mDebugControllerTree;
	private transient FontUnit mConsoleFont;
	private boolean mIsOpen;

	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBar mScrollBar;
	private transient float mScrollYPosition;
	private boolean mScrollBarEnabled;
	private transient int mLowerBound;
	private transient int mUpperBound;
	private transient int mConsoleLineHeight;

	private float mOpenWidth = 400;
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
		return mControllerManager != null && mDebugControllerTree != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugControllerRenderer(final Debug pDebugManager) {
		mDebugManager = pDebugManager;
		mStringBuilder = new StringBuilder();

		if (pDebugManager.debugManagerEnabled()) {
			mTextureBatch = new TextureBatch();

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
		mConsoleFont = pResourceManager.fontManager().systemFont();

		mTextureBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats unloading GL content");

		mConsoleFont.unloadGLContent();
		mTextureBatch.unloadGLContent();
		mCoreTexture = null;

	}

	public void handleInput(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mIsOpen)
			return;

		if (!isInitialized()) {
			getControllerManagerInstance(pCore);
			return;
		}

		// *** Control the selection of ControllerWidgets ***

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				final float lMouseX = pCore.HUD().getMouseCameraSpace().x;
				final float lMouseY = pCore.HUD().getMouseCameraSpace().y;

				//
				if (lMouseX > x && lMouseX < x + w - mScrollBar.w) {
					final var lComponentTree = mDebugControllerTree.treeComponents();
					final var lControllerWidgetCount = lComponentTree.size();
					for (int i = 0; i < lControllerWidgetCount; i++) {
						final var lControllerWidget = lComponentTree.get(i);

						if (lMouseY > lControllerWidget.y + lControllerWidget.h) {
							continue;

						}

						lControllerWidget.isExpanded = !lControllerWidget.isExpanded;

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
			mConsoleLineHeight = (int) (mConsoleFont.bitmap().getStringHeight(" ") + 1);
			mScrollYPosition += mConsoleLineHeight;

			if (mScrollYPosition > 0)
				mScrollYPosition = 0;

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN)) {
			mConsoleLineHeight = (int) (mConsoleFont.bitmap().getStringHeight(" ") + 1);
			mScrollYPosition -= mConsoleLineHeight;

			if (mScrollYPosition < mScrollBar.getScrollYBottomPosition())
				mScrollYPosition = mScrollBar.getScrollYBottomPosition() - mConsoleLineHeight;
		}

		if (mScrollBar.handleInput(pCore)) {
			return;

		}

	}

	public void update(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;
		
		mIsOpen = mDebugManager.stats().isOpen();

		if (!mIsOpen)
			return;

		if (!isInitialized()) {
			getControllerManagerInstance(pCore);
			return;
		}

		{
			if (mConsoleFont.bitmap() == null)
				mConsoleFont = pCore.resources().fontManager().systemFont();

			// Update the window content
			// 'ConsoleLineHeight' should actually be the 6
			final var lControllerList = mDebugControllerTree.treeComponents();
			final var lNumberComponents = lControllerList.size();

			DisplayManager lDisplay = pCore.config().display();
			// Update the bounds of the window view
			x = -lDisplay.windowWidth() * 0.5f + 5f;
			y = -lDisplay.windowHeight() * 0.5f + 5f;
			w = mOpenWidth;
			h = mOpenHeight;

			// fit the upper bound around the lower bounds
			mLowerBound = (int) -((mScrollYPosition) / mConsoleLineHeight);
//			if (mLowerBound + MAX_NUM_LINES > lNumberComponents) {
//				mLowerBound = lNumberComponents - MAX_NUM_LINES;
//				mUpperBound = lNumberComponents;
//
//				// but the lower bound can never be lower than 0 (indexed)
//				if (mLowerBound < 0)
//					mLowerBound = 0;
//			} else {
//				mUpperBound = mLowerBound + MAX_NUM_LINES;
//
//			}

			{

				// *** UPDATE THE COMPONENT WIDGETS ***

				float lPosY = y + mScrollYPosition;
				float lTotalControllerHeight = 0;

				final var lComponentTree = mDebugControllerTree.treeComponents();
				final var lControllerWidgetCount = lComponentTree.size();
				for (int i = 0; i < lControllerWidgetCount; i++) {
					final var lControllerWidget = lComponentTree.get(i);

					lControllerWidget.update(pCore);
					lControllerWidget.w = w;
					lControllerWidget.x = x;
					lControllerWidget.y = lPosY;

					lPosY += lControllerWidget.h;
					lTotalControllerHeight += lControllerWidget.h;

				}

				mContentRectangle.h = (int) lTotalControllerHeight;
				fullContentArea().setCenter(x, y, w - mScrollBar.w, lNumberComponents * 25);

			}

			mScrollBarEnabled = h < mContentRectangle.h;

			if (mScrollBarEnabled) {
				mScrollBar.update(pCore);

			}

		}

	}

	public void draw(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!isInitialized()) {
			getControllerManagerInstance(pCore);
			return;
		}

		if (!mIsOpen)
			return;

		// Get the positional informationt from the parent object (inline with console and renderer widget display).

		mTextureBatch.begin(pCore.HUD());

		final var lTop = y;// lHUDRectangle.top() + lHeightOffset + 5f;
		final var lLeft = x;// lHUDRectangle.left() + 5f;

		mTextureBatch.draw(mCoreTexture, 0, 0, 32, 32, lLeft, lTop, mOpenWidth, 500, -0.03f, 0.21f, 0.17f, 0.25f, 0.95f);
		mTextureBatch.end();

		mConsoleFont.begin(pCore.HUD());

		// Getting list of ControllerItems to render
		final var lControllerList = mDebugControllerTree.treeComponents();

		final var lNumTreeComponents = lControllerList.size();
		int lPosY = 5;
		for (int i = mLowerBound; i < mUpperBound; i++) {
			if (i < 0)
				continue;
			if (i >= lNumTreeComponents)
				break;

			final var lBaseControllerWidget = lControllerList.get(i);
			final var lControllerName = lBaseControllerWidget.displayName;

			int lPosX = lBaseControllerWidget.controllerLevel * 30;

			mConsoleFont.draw(lControllerName, lBaseControllerWidget.x + lPosX, lBaseControllerWidget.y, -0.02f, 1, 1, 1, 1, 1, -1);
			lPosY += lBaseControllerWidget.h;
		}

		mConsoleFont.end();

		if (mScrollBarEnabled) {
			mScrollBar.draw(pCore, mTextureBatch, mCoreTexture, -0.02f);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------
	
	private void getControllerManagerInstance(LintfordCore pCore) {
		mControllerManager = pCore.controllerManager();
		mDebugControllerTree = (DebugTreeController) mControllerManager.getControllerByNameRequired(DebugTreeController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

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
