package net.lintfordlib.renderers.debug;

import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.controllers.debug.DebugControllerTreeController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.BitmapFontManager;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.renderers.windows.components.IScrollBarArea;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;

public class DebugControllerTreeRenderer extends Rectangle implements IScrollBarArea, IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5939573295360447130L;

	public static final float ENTRY_HEIGHT = 32;
	public static final float MAX_PANE_HEIGHT = 600;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Debug mDebugManager;
	private SpriteSheetDefinition mCoreSpritesheet;
	private SpriteBatch mSpriteBatch;
	private ControllerManager mControllerManager;
	private DebugControllerTreeController mDebugControllerTree;
	private transient FontUnit mConsoleFont;
	private boolean mIsOpen;

	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBar mScrollBar;
	private transient int mLowerBound;
	private transient int mUpperBound;

	private float mOpenWidth = 400;
	private float mOpenHeight = 500;
	private float mClickTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void isOpen(boolean newValue) {
		mIsOpen = newValue;
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

	public DebugControllerTreeRenderer(final Debug debugManager) {
		mDebugManager = debugManager;

		if (debugManager.debugManagerEnabled()) {
			mSpriteBatch = new SpriteBatch();
			mContentRectangle = new ScrollBarContentRectangle(this);
			mScrollBar = new ScrollBar(this, mContentRectangle);
		}
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats loading GL content");

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();
		mConsoleFont = resourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CONSOLE_NAME);

		mSpriteBatch.loadResources(resourceManager);
	}

	public void unloadResources() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats unloading GL content");

		mSpriteBatch.unloadResources();

		mConsoleFont = null;
		mCoreSpritesheet = null;
	}

	public void handleInput(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mIsOpen)
			return;

		if (!isInitialized()) {
			getControllerManagerInstance(core);
			return;
		}

		// *** Control the selection of ControllerWidgets ***
		if (mScrollBar.handleInput(core, null)) {
			return;
		}

		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				final float lMouseX = core.HUD().getMouseCameraSpace().x;
				final float lMouseY = core.HUD().getMouseCameraSpace().y;

				if (lMouseX > mX && lMouseX < mX + mW - mScrollBar.width()) {
					final var lComponentTree = mDebugControllerTree.treeComponents();
					final var lControllerWidgetCount = lComponentTree.size();
					for (int i = 0; i < lControllerWidgetCount; i++) {
						final var lControllerWidget = lComponentTree.get(i);

						if (lMouseY > lControllerWidget.y() + lControllerWidget.height()) {
							continue;
						}

						lControllerWidget.isExpanded = !lControllerWidget.isExpanded;
						if (lControllerWidget.baseController != null) {
							lControllerWidget.baseController.isActive(!lControllerWidget.baseController.isActive());
						}

						// Once we have handled, then we can exit the loop
						break;
					}
				}
			}

			// If the mouse is over this window, then don't let clicks fal-through.
			core.input().mouse().mouseHoverOverHash(hashCode());
		}
	}

	public void update(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mIsOpen = mDebugManager.console().isOpen();

		if (!mIsOpen)
			return;

		if (mClickTimer >= 0) {
			mClickTimer -= core.appTime().elapsedTimeMilli();
		}

		if (!isInitialized()) {
			getControllerManagerInstance(core);
			return;
		}

		{
			final var lControllerList = mDebugControllerTree.treeComponents();
			final var lNumberComponents = lControllerList.size();
			final var lDisplayManager = core.config().display();

			final var windowHeight = lDisplayManager.windowHeight();
			final var consoleHeight = Debug.debugManager().console().openHeight();
			mOpenHeight = windowHeight - consoleHeight - 60f;

			// Update the bounds of the window view
			mX = -lDisplayManager.windowWidth() * 0.5f + 5f;
			final float lConsoleYOffset = Debug.debugManager().console().isOpen() ? Debug.debugManager().console().openHeight() : 5f;
			mY = -lDisplayManager.windowHeight() * 0.5f + lConsoleYOffset + 5f + 25f;
			mW = mOpenWidth;
			mH = mOpenHeight;

			final int lMaxNumLines = (int) (mH / ENTRY_HEIGHT) + 1;
			mLowerBound = (int) -((mScrollBar.currentYPos()) / ENTRY_HEIGHT);
			mUpperBound = mLowerBound + lMaxNumLines;

			{
				// *** UPDATE THE COMPONENT WIDGETS ***

				float lPosY = mY + mScrollBar.currentYPos();

				final var lComponentTree = mDebugControllerTree.treeComponents();
				final var lControllerWidgetCount = lComponentTree.size();
				for (int i = 0; i < lControllerWidgetCount; i++) {
					final var lControllerWidget = lComponentTree.get(i);

					lControllerWidget.update(core);
					lControllerWidget.set(mX, lPosY, mW, ENTRY_HEIGHT);

					lPosY += lControllerWidget.height();
				}

				fullContentArea().setCenter(mX, mY, mW - mScrollBar.width(), lNumberComponents * ENTRY_HEIGHT);
			}

			mScrollBar.update(core);
		}
	}

	public void draw(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!isInitialized()) {
			getControllerManagerInstance(core);
			return;
		}

		if (!mIsOpen)
			return;

		mSpriteBatch.begin(core.HUD());
		mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY - 25f, mOpenWidth, 25f, -0.03f, ColorConstants.MenuPanelPrimaryColor);
		mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mOpenWidth, mOpenHeight, -0.03f, ColorConstants.MenuPanelSecondaryColor);
		mSpriteBatch.end();

		mConsoleFont.begin(core.HUD());
		mConsoleFont.drawText("Controllers", mX, mY - 25f, -0.02f, ColorConstants.WHITE, 1, -1);
		mConsoleFont.end();

		if (mH < mContentRectangle.height())
			mContentRectangle.preDraw(core, mSpriteBatch, mCoreSpritesheet);

		mSpriteBatch.begin(core.HUD());
		mConsoleFont.begin(core.HUD());

		// Getting list of ControllerItems to render
		final var lControllerList = mDebugControllerTree.treeComponents();

		// The lControllerList contains a flat list of all <BaseControllerWidget> which we created for the controllers
		final var lNumTreeComponents = lControllerList.size();
		for (int i = mLowerBound; i < mUpperBound; i++) {
			if (i < 0)
				continue;
			if (i >= lNumTreeComponents)
				break;

			final var lBaseControllerWidget = lControllerList.get(i);
			final var lControllerName = lBaseControllerWidget.displayName;

			int lPosX = lBaseControllerWidget.controllerLevel * 30;

			mConsoleFont.drawText(lControllerName, lBaseControllerWidget.x() + lPosX, lBaseControllerWidget.y() + lBaseControllerWidget.height() * .5f - mConsoleFont.fontHeight() * .5f, -0.02f, ColorConstants.WHITE, 1, -1);

			final float lActiveIconX = mX + mOpenWidth - 64;
			final float lActiveIconY = lBaseControllerWidget.y();

			if (lBaseControllerWidget == null || !lBaseControllerWidget.isControllerActive) {
				mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_TICK, lActiveIconX, lActiveIconY, 32, 32, -0.01f, ColorConstants.WHITE);
			} else {
				mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_CROSS, lActiveIconX, lActiveIconY, 32, 32, -0.01f, ColorConstants.WHITE);
			}
		}

		mSpriteBatch.end();
		mConsoleFont.end();

		if (mH < mContentRectangle.height())
			mContentRectangle.postDraw(core);

		mSpriteBatch.begin(core.HUD());
		mScrollBar.draw(core, mSpriteBatch, mCoreSpritesheet, -0.01f, 1.f);
		mSpriteBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void getControllerManagerInstance(LintfordCore core) {
		mControllerManager = core.controllerManager();
		mDebugControllerTree = (DebugControllerTreeController) mControllerManager.getControllerByNameRequired(DebugControllerTreeController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
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

	@Override
	public boolean allowGamepadInput() {
		return false;
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
