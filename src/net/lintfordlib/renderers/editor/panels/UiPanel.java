package net.lintfordlib.renderers.editor.panels;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.ConstantsEditor;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.controllers.editor.IBrushModeCallback;
import net.lintfordlib.controllers.hud.HudLayoutController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.data.editor.EditorLayerBrush;
import net.lintfordlib.data.editor.HudTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.renderers.windows.components.UIWidget;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.renderers.windows.components.interfaces.IUiWidgetInteractions;
import net.lintfordlib.renderers.windows.components.interfaces.UIWindowChangeListener;

public abstract class UiPanel implements IScrollBarArea, UIWindowChangeListener, IInputProcessor, IUiWidgetInteractions, IBrushModeCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int BUTTON_SET_LAYER = 500;
	protected static final int BUTTON_SHOW_LAYER = 501;
	protected static final int BUTTON_EXPANDED = 502;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected UiWindow mParentWindow;
	protected int mEntityGroupUid;
	protected boolean mRenderPanelTitle;
	protected String mPanelTitle;
	protected boolean mPanelTitleUnderline;
	protected boolean mDrawBackground;
	protected float mPanelBarHeight;
	protected float mMouseClickTimer;

	protected boolean mIsExpandable;
	protected final Rectangle mExpandRectangle = new Rectangle();
	protected boolean mIsPanelOpen; // expanded

	protected HudLayoutController mUiStructureController;
	protected EditorBrushController mEditorBrushController;

	protected SpriteSheetDefinition mCoreSpritesheet;
	protected SpriteSheetDefinition mHudSpritesheet;

	protected Rectangle mPanelArea;

	protected float mPaddingTop;
	protected float mPaddingLeft;
	protected float mVerticalSpacing;
	protected float mHorizontalSpacing;
	protected float mPaddingBottom;
	protected float mPaddingRight;

	protected boolean mShowShowLayerButton;
	private final Rectangle mShowLayerButtonRect = new Rectangle();
	private boolean mIsLayerVisibleToggleOn;

	protected boolean mShowActiveLayerButton;
	private final Rectangle mActiveLayerButtonRect = new Rectangle();
	private boolean mIsLayerActiveToggleOn;
	protected int mEditorActiveLayerUid = EditorLayerBrush.NO_LAYER_UID;

	protected final List<UIWidget> mWidgets = new ArrayList<>();
	protected UiPanel mNestedPanel;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean drawPanelBackground() {
		return mDrawBackground;
	}

	public void drawPanelBackground(boolean newValue) {
		mDrawBackground = newValue;
	}

	public void isLayerActive(boolean newValue) {
		mIsLayerActiveToggleOn = newValue;
	}

	public boolean isLayerActive() {
		return mIsLayerActiveToggleOn;
	}

	public void isLayerVisible(boolean newValue) {
		mIsLayerVisibleToggleOn = newValue;
	}

	public boolean isLayerVisible() {
		return mIsLayerVisibleToggleOn;
	}

	public HudLayoutController uiStructureController() {
		return mUiStructureController;
	}

	public void addWidget(UIWidget newWidget) {
		mWidgets.add(newWidget);
	}

	public Rectangle panelArea() {
		return mPanelArea;
	}

	public float getTitleBarHeight() {
		return 32.f;
	}

	public float getPanelFullHeight() {
		if (mIsPanelOpen == false)
			return 32.f; // standard panel title height

		float totalHeight = mPaddingTop;

		if (mRenderPanelTitle || mIsExpandable) {
			totalHeight += getTitleBarHeight();
			totalHeight += mVerticalSpacing;
		}

		final int lNumWidgets = mWidgets.size();
		for (int i = 0; i < lNumWidgets; i++) {
			final var lWidget = mWidgets.get(i);
			totalHeight += lWidget.marginTop();
			totalHeight += lWidget.height();
			totalHeight += lWidget.marginBottom();
			if (i < lNumWidgets - 1) {
				totalHeight += mVerticalSpacing;
			}
		}

		if (mNestedPanel != null) {
			totalHeight += mNestedPanel.getPanelFullHeight();
		}

		return totalHeight + mPaddingBottom;
	}

	public boolean isOpen() {
		return mIsPanelOpen;
	}

	public void isOpen(boolean isOpen) {
		mIsPanelOpen = isOpen;
	}

	public void panelTitle(String panelTitle) {
		mPanelTitle = panelTitle;
	}

	public String panelTitle() {
		return mPanelTitle;
	}

	public void underlineTitle(boolean underlineTitle) {
		mPanelTitleUnderline = underlineTitle;
	}

	public boolean underlineTitle() {
		return mPanelTitleUnderline;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiPanel(UiWindow parentWindow, String panelTitle, int entityGroupUid) {
		mParentWindow = parentWindow;
		mPanelTitle = panelTitle;
		mEntityGroupUid = entityGroupUid;

		mPanelArea = new Rectangle();

		mIsPanelOpen = false;
		mDrawBackground = true;

		mIsExpandable = true;
		mShowActiveLayerButton = true;
		mShowShowLayerButton = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(LintfordCore core) {
		mUiStructureController = (HudLayoutController) core.controllerManager().getControllerByName(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
		mEditorBrushController = (EditorBrushController) core.controllerManager().getControllerByName(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);

		final int lNumWidgets = mWidgets.size();
		for (int i = 0; i < lNumWidgets; i++) {
			final var lWidget = mWidgets.get(i);

			lWidget.initialize();
		}
	}

	public void loadResources(ResourceManager resourceManager) {
		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();
		mHudSpritesheet = resourceManager.spriteSheetManager().getSpriteSheet("SPRITESHEET_HUD", ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);

		final int lNumWidgets = mWidgets.size();
		for (int i = 0; i < lNumWidgets; i++) {
			final var lWidget = mWidgets.get(i);

			lWidget.loadResources(resourceManager);
		}
	}

	public void unloadResources() {
		final int lNumWidgets = mWidgets.size();
		for (int i = 0; i < lNumWidgets; i++) {
			final var lWidget = mWidgets.get(i);

			lWidget.unloadResources();
		}
	}

	public boolean handleInput(LintfordCore core) {
		final float lMouseX = core.HUD().getMouseWorldSpaceX();
		final float lMouseY = core.HUD().getMouseWorldSpaceY();

		if (mShowActiveLayerButton) {
			if (mActiveLayerButtonRect.intersectsAA(lMouseX, lMouseY))
				if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					handleSetLayerToggle(core);
					return true;
				}
		}

		if (mShowShowLayerButton) {
			if (mShowLayerButtonRect.intersectsAA(lMouseX, lMouseY))
				if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mIsLayerVisibleToggleOn = !mIsLayerVisibleToggleOn;

					widgetOnClick(core.input(), BUTTON_SHOW_LAYER);
					return true;
				}
		}

		if (mIsExpandable) {
			if (mExpandRectangle.intersectsAA(lMouseX, lMouseY))
				if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mIsPanelOpen = !mIsPanelOpen;
					widgetOnClick(core.input(), BUTTON_EXPANDED);
					return true;
				}
		}

		boolean result = false;
		if (mIsPanelOpen) {
			final int lNumWidgets = mWidgets.size();
			for (int i = 0; i < lNumWidgets; i++) {
				final var lWidget = mWidgets.get(i);

				final var tr = lWidget.handleInput(core);
				result = result | tr;
			}

			if (mNestedPanel != null) {
				if (mNestedPanel.handleInput(core)) {
					return true;
				}
			}

		}

		return result;
	}

	public void update(LintfordCore core) {
		if (mMouseClickTimer > 0)
			mMouseClickTimer -= core.gameTime().elapsedTimeMilli();

		mPaddingLeft = 5.f;
		mHorizontalSpacing = 5.f;
		mPaddingRight = 5.f;

		mPaddingTop = 5.f;
		mVerticalSpacing = 5.f;
		mPaddingBottom = 5.f;

		final float lCanvasScale = 1.0f;
		final float lTitleButtonSize = 32.f;
		final float lInsetSize = (lTitleButtonSize - lTitleButtonSize) / 2.f;

		// @formatter:off
		int lButtonCounter = 1;
		if(mIsExpandable) {
			mExpandRectangle.set(
					mPanelArea.x() + mPanelArea.width() - lInsetSize - lTitleButtonSize * lCanvasScale, 
					mPanelArea.y() + lInsetSize, 
					lTitleButtonSize,
					lTitleButtonSize);
			lButtonCounter++;
		}
		
		mIsLayerActiveToggleOn = mEditorActiveLayerUid != EditorLayerBrush.NO_LAYER_UID && mEditorBrushController.isLayerActive(mEditorActiveLayerUid);
		if(mShowActiveLayerButton) {
			mActiveLayerButtonRect.set(
					mPanelArea.x() + mPanelArea.width() - lInsetSize * lButtonCounter - (lTitleButtonSize * lCanvasScale) * lButtonCounter, 
					mPanelArea.y() + lInsetSize, 
					lTitleButtonSize,
					lTitleButtonSize);
			lButtonCounter++;
		}
		
		if(mShowShowLayerButton) {
		mShowLayerButtonRect.set(
				mPanelArea.x() + mPanelArea.width() - lInsetSize * lButtonCounter - (lTitleButtonSize * lCanvasScale) * lButtonCounter, 
				mPanelArea.y() + lInsetSize, 
				lTitleButtonSize,
				lTitleButtonSize);
		lButtonCounter++;
		}
		// @formatter:on

		if (mIsPanelOpen) {
			arrangeWidgets(core);

			final int lNumWidgets = mWidgets.size();
			for (int i = 0; i < lNumWidgets; i++) {
				final var lWidget = mWidgets.get(i);

				lWidget.update(core);
			}

			if (mNestedPanel != null)
				mNestedPanel.update(core);
		}

	}

	public void draw(LintfordCore core) {
		final var rendererManager = mParentWindow.rendererManager();

		final var lFontUnit = rendererManager.uiTextFont();
		final var lSpriteBatch = rendererManager.uiSpriteBatch();
		final var mCoreSpriteSheet = mParentWindow.coreSpritesheet();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mPanelArea, -0.01f, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}

		if (mDrawBackground)
			drawBackground(core, lSpriteBatch, true, -0.02f);

		lSpriteBatch.begin(core.HUD());
		if (mRenderPanelTitle) {
			mPanelBarHeight = 20.f;
			lFontUnit.begin(core.HUD());
			final var lTextWidth = lFontUnit.getStringWidth(mPanelTitle);
			lFontUnit.drawText(mPanelTitle, mPanelArea.x() + 5.f, mPanelArea.y() + 5.f, -0.01f, 1.f);
			if (mIsPanelOpen && mPanelTitleUnderline)
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mPanelArea.x() + 5.f, mPanelArea.y() + lFontUnit.fontHeight() + 5.f, lTextWidth, 1.f, -0.01f, ColorConstants.WHITE);
			lFontUnit.end();
		}

		if (mShowShowLayerButton) {
			if (mIsLayerVisibleToggleOn) {
				lSpriteBatch.draw(mHudSpritesheet, HudTextureNames.TEXTURE_SHOW_LAYER, mShowLayerButtonRect, -0.01f, ColorConstants.WHITE);
			} else {
				lSpriteBatch.draw(mHudSpritesheet, HudTextureNames.TEXTURE_HIDE_LAYER, mShowLayerButtonRect, -0.01f, ColorConstants.WHITE);
			}
		}

		if (mShowActiveLayerButton) {
			if (mIsLayerActiveToggleOn) {
				lSpriteBatch.draw(mHudSpritesheet, HudTextureNames.TEXTURE_SET_LAYER_ON, mActiveLayerButtonRect, -0.01f, ColorConstants.WHITE);
			} else {
				lSpriteBatch.draw(mHudSpritesheet, HudTextureNames.TEXTURE_SET_LAYER_OFF, mActiveLayerButtonRect, -0.01f, ColorConstants.WHITE);
			}
		}

		if (mIsExpandable) {
			if (mIsPanelOpen) {
				lSpriteBatch.draw(mCoreSpriteSheet, CoreTextureNames.TEXTURE_EXPAND, mExpandRectangle, -0.01f, ColorConstants.WHITE);
			} else {
				lSpriteBatch.draw(mCoreSpriteSheet, CoreTextureNames.TEXTURE_COLLAPSE, mExpandRectangle, -0.01f, ColorConstants.WHITE);
			}
		}
		lSpriteBatch.end();

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		float zDepth = -0.01f;
		if (mIsPanelOpen) {
			final int lNumWidgets = mWidgets.size();
			for (int i = 0; i < lNumWidgets; i++) {
				final var lWidget = mWidgets.get(i);
				lWidget.draw(core, lSpriteBatch, mCoreSpriteSheet, lFontUnit, zDepth -= 0.03f);
			}

			if (mNestedPanel != null)
				mNestedPanel.draw(core);

		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private void drawBackground(LintfordCore core, SpriteBatch spriteBatch, boolean withTitlebar, float componentDepth) {
		final var lSpriteSheetCore = core.resources().spriteSheetManager().coreSpritesheet();

		final int lTileSize = 32;
		final int posX = (int) mPanelArea.x();
		final int posY = (int) mPanelArea.y();
		final int width = (int) mPanelArea.width();
		final int height = (int) mPanelArea.height();
		final var layoutColor = ColorConstants.WHITE;

		spriteBatch.begin(core.HUD());
		if (mIsPanelOpen == false) {
			spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X1_LEFT, posX, posY, lTileSize, lTileSize, componentDepth, layoutColor);
			spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X1_MID, posX + lTileSize, posY, width - lTileSize * 2, lTileSize, componentDepth, layoutColor);
			spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X1_RIGHT, posX + width - lTileSize, posY, lTileSize, lTileSize, componentDepth, layoutColor);
		} else {
			if (height < 64) {
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, posX, posY, lTileSize, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, posX + lTileSize, posY, width - lTileSize * 2, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, posX + width - lTileSize, posY, lTileSize, lTileSize, componentDepth, layoutColor);

				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, posX, posY + height - lTileSize, lTileSize, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, posX + lTileSize, posY + height - lTileSize, width - lTileSize * 2, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, posX + width - lTileSize, posY + height - lTileSize, lTileSize, lTileSize, componentDepth, layoutColor);
			} else {
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, posX, posY, lTileSize, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, posX + lTileSize, posY, width - lTileSize * 2 + 1, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, posX + width - lTileSize, posY, lTileSize, lTileSize, componentDepth, layoutColor);

				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT, posX, posY + lTileSize, lTileSize, height - lTileSize * 2 + 1, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER, posX + lTileSize, posY + lTileSize, width - lTileSize * 2 + 1, height - 64 + 1, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT, posX + width - lTileSize, posY + lTileSize, lTileSize, height - lTileSize * 2 + 1, componentDepth, layoutColor);

				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, posX, posY + height - lTileSize, lTileSize, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, posX + lTileSize, posY + height - lTileSize, width - lTileSize * 2 + 1, lTileSize, componentDepth, layoutColor);
				spriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, posX + width - lTileSize, posY + height - lTileSize, lTileSize, lTileSize, componentDepth, layoutColor);
			}
		}

		spriteBatch.end();
	}

	// --------------------------------------

	public abstract int layerOwnerHashCode();

	private void handleSetLayerToggle(LintfordCore core) {
		final var lIsLayerActive = mEditorActiveLayerUid != EditorLayerBrush.NO_LAYER_UID && mEditorBrushController.isLayerActive(mEditorActiveLayerUid);
		if (lIsLayerActive) {
			mEditorBrushController.clearActiveLayer(layerOwnerHashCode());
		} else {
			mEditorBrushController.setActiveLayer(this, mEditorActiveLayerUid, layerOwnerHashCode());
		}

		isLayerActive(mEditorBrushController.isLayerActive(mEditorActiveLayerUid));
		mIsLayerActiveToggleOn = mEditorActiveLayerUid != EditorLayerBrush.NO_LAYER_UID && mEditorBrushController.isLayerActive(mEditorActiveLayerUid);
	}

	protected void arrangeWidgets(LintfordCore core) {
		float lCurPositionX = mPanelArea.x() + mPaddingLeft;
		float lCurPositionY = mPanelArea.y() + mPaddingTop;

		float lVSpacing = mVerticalSpacing;

		if (mRenderPanelTitle || mIsExpandable) {
			lCurPositionY += getTitleBarHeight();
		}

		final float lWidthDefaultWidth = mPanelArea.width() - mPaddingLeft - mPaddingRight;

		final int lNumWidgets = mWidgets.size();
		for (int i = 0; i < lNumWidgets; i++) {
			final var lWidget = mWidgets.get(i);

			lWidget.setPosition(lCurPositionX, lCurPositionY);
			lWidget.width(lWidthDefaultWidth);

			final var lDesiredHeight = lWidget.desiredHeight();

			if (lDesiredHeight <= 0) {
				final var lNewHeight = lWidget.isDoubleHeight() ? UIWidget.DefaultWidthHeight * 2.f : UIWidget.DefaultWidthHeight;
				lWidget.height(lNewHeight);
			} else {
				lWidget.height(lDesiredHeight);
			}

			UIWidget lNextWidget = null;
			if (i + 1 < mWidgets.size()) {
				lNextWidget = mWidgets.get(i + 1);
			}

			lCurPositionY = increaseYPosition(lCurPositionY, lWidget, lNextWidget) + lVSpacing;
		}

		if (mNestedPanel != null) {
			mNestedPanel.panelArea().setPosition(lCurPositionX, lCurPositionY);
			mNestedPanel.panelArea().width(lWidthDefaultWidth);
		}

	}

	// --------------------------------------
	// Inherited Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseClickTimer <= 0.f;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mMouseClickTimer = cooldownInMs;
	}

	@Override
	public void onWindowOpened() {

	}

	@Override
	public void onWindowClosed() {

	}

	@Override
	public Rectangle contentDisplayArea() {
		return null;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return null;
	}

	protected float increaseYPosition(float currentY, UIWidget currentWidget, UIWidget nextWidget) {
		if (currentWidget != null) {
			currentY += currentWidget.height();
			currentY += currentWidget.marginBottom();
		}

		if (nextWidget != null) {
			currentY += nextWidget.marginTop();
		}

		return currentY;
	}

	@Override
	public void onLayerDeselected() {

	}

	@Override
	public void onLayerSelected() {

	}

	@Override
	public void onActionDeselected() {

	}

	@Override
	public void onActionSelected() {

	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowKeyboardInput() {
		return false;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}

}
