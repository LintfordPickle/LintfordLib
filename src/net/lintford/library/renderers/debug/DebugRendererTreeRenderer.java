package net.lintford.library.renderers.debug;

import net.lintford.library.controllers.debug.DebugRendererTreeController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.fonts.FontUnit.WrapType;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
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
	private SpriteSheetDefinition mCoreSpritesheet;
	private SpriteBatch mSpriteBatch;
	private DebugRendererTreeController mDebugRendererTree;
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

	public void isOpen(boolean isOpen) {
		mIsOpen = isOpen;
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

	public DebugRendererTreeRenderer(final Debug debugManager) {
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
			getRendererManagerInstance(core);
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
					final var lComponentTree = mDebugRendererTree.treeComponents();
					final var lControllerWidgetCount = lComponentTree.size();
					for (int i = 0; i < lControllerWidgetCount; i++) {
						final var lControllerWidget = lComponentTree.get(i);

						if (lMouseY > lControllerWidget.y() + lControllerWidget.height()) {
							continue;
						}

						if (lControllerWidget.baseRenderer != null) {
							lControllerWidget.baseRenderer.isActive(!lControllerWidget.baseRenderer.isActive());
						}

						break;
					}
				}
			}

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
			getRendererManagerInstance(core);
			return;
		}

		{
			final var lRendererList = mDebugRendererTree.treeComponents();
			final var lNumberRenderers = lRendererList.size();
			final var lDisplayManager = core.config().display();

			final var windowHeight = lDisplayManager.windowHeight();
			final var consoleHeight = Debug.debugManager().console().openHeight();
			mOpenHeight = windowHeight - consoleHeight - 60f;

			// Update the bounds of the window view
			mX = lDisplayManager.windowWidth() * 0.5f - mOpenWidth - 5f;
			final float lConsoleYOffset = Debug.debugManager().console().isOpen() ? consoleHeight : 5f;
			mY = -lDisplayManager.windowHeight() * 0.5f + lConsoleYOffset + 5f + 25f;
			mW = mOpenWidth;
			mH = mOpenHeight;

			// *** UPDATE THE COMPONENT WIDGETS ***

			float lPosY = mY + mScrollBar.currentYPos();

			for (int i = 0; i < lNumberRenderers; i++) {
				final var lControllerWidget = lRendererList.get(i);

				lControllerWidget.update(core);
				lControllerWidget.set(mX, lPosY, mW, ENTRY_HEIGHT);

				lPosY += lControllerWidget.height();

			}

			final int lMaxNumLines = (int) (mH / ENTRY_HEIGHT) + 1;
			mLowerBound = (int) -((mScrollBar.currentYPos()) / ENTRY_HEIGHT);
			mUpperBound = mLowerBound + lMaxNumLines;

			fullContentArea().setCenter(mX, mY, mW - mScrollBar.width(), lNumberRenderers * ENTRY_HEIGHT);

			mScrollBar.update(core);
		}
	}

	public void draw(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!isInitialized()) {
			getRendererManagerInstance(core);
			return;
		}

		if (!mIsOpen)
			return;

		mSpriteBatch.begin(core.HUD());
		mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY - 25f, mOpenWidth, 25f, -0.03f, ColorConstants.MenuPanelPrimaryColor);
		mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mOpenWidth, mOpenHeight, -0.03f, ColorConstants.MenuPanelSecondaryColor);
		mSpriteBatch.end();

		mConsoleFont.begin(core.HUD());
		mConsoleFont.drawText("Renderers", mX + 5f, mY - 25f, -0.01f, ColorConstants.WHITE, 1, -1);
		mConsoleFont.end();

		// Getting list of ControllerItems to render
		final var lControllerList = mDebugRendererTree.treeComponents();

		// The lControllerList contains a flat list of all <BaseControllerWidget> which we created for the controllers
		final var lNumTreeComponents = lControllerList.size();

		if (lNumTreeComponents == 0) {
			mConsoleFont.drawText("No BaseRenderers on Screen", mX + 5f, mY + 5f, -0.02f, ColorConstants.WHITE, 1, -1);
			return;
		}

		if (mH < mContentRectangle.height())
			mContentRectangle.preDraw(core, mSpriteBatch, mCoreSpritesheet);

		mConsoleFont.setWrapType(WrapType.WordWrapTrim);
		mConsoleFont.begin(core.HUD());
		mSpriteBatch.begin(core.HUD());

		for (int i = mLowerBound; i < mUpperBound; i++) {
			if (i < 0)
				continue;
			if (i >= lNumTreeComponents)
				break;

			final var lBaseRendererWidget = lControllerList.get(i);
			final var lRendererName = lBaseRendererWidget.displayName;

			float lPosX = 10f + lBaseRendererWidget.rendererLevel * 30f;

			mConsoleFont.drawText(lRendererName, lBaseRendererWidget.x() + lPosX, lBaseRendererWidget.y() + lBaseRendererWidget.height() * .5f - mConsoleFont.fontHeight() * .5f, -0.02f, ColorConstants.WHITE, 1, mOpenWidth - 64);

			final float lActiveIconX = mX + mOpenWidth - 64;
			final float lActiveIconY = lBaseRendererWidget.y();

			if (lBaseRendererWidget == null || !lBaseRendererWidget.isRendererActive) {
				mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_CROSS, lActiveIconX, lActiveIconY, 32, 32, -0.01f, ColorConstants.WHITE);
			} else {
				mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_TICK, lActiveIconX, lActiveIconY, 32, 32, -0.01f, ColorConstants.WHITE);
			}
		}

		mSpriteBatch.end();
		mConsoleFont.end();

		if (mH < mContentRectangle.height())
			mContentRectangle.postDraw(core);

		mSpriteBatch.begin(core.HUD());
		mScrollBar.draw(core, mSpriteBatch, mCoreSpritesheet, -0.02f);
		mSpriteBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void getRendererManagerInstance(LintfordCore core) {
		mDebugRendererTree = (DebugRendererTreeController) core.controllerManager().getControllerByName(DebugRendererTreeController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
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