package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class BaseDialog extends MenuScreen {

	public class DialogIcon {

		// --------------------------------------
		// Variables
		// --------------------------------------

		public boolean mEnabled;
		protected Texture mIconTexture;
		protected final Rectangle mSrcRectangle = new Rectangle();

		// --------------------------------------
		// Properties
		// --------------------------------------

		public boolean enabled() {
			return mEnabled;
		}

		// --------------------------------------
		// Constaructor
		// --------------------------------------

		public DialogIcon() {

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void setDialogIcon() {
			setDialogIcon(null, 0.f, 0.f, 0.f, 0.f);
		}

		public void setDialogIcon(Texture pTexture) {
			setDialogIcon(pTexture, 0.f, 0.f, 0.f, 0.f);
		}

		public void setDialogIcon(Texture pTexture, float pSrcX, float pSrcY, float pSrcW, float pSrcH) {
			if (pTexture == null) {
				mIconTexture = null;
				mEnabled = false;
				return;

			}

			mEnabled = true;
			mIconTexture = pTexture;
			mSrcRectangle.set(pSrcX, pSrcY, pSrcW, pSrcH);

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DIALOG_WIDTH = 600;
	public static final int DIALOG_HEIGHT = 250;

	public static final float TEXT_HORIZONTAL_PADDING = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mMessageString;
	protected Screen mParentScreen;
	protected boolean mDrawBackground;
	protected boolean mDarkenBackground;

	protected SpriteSheetDefinition mIconSpritesheet;
	protected int mIconSpriteFrameIndex;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setDialogIcon(SpriteSheetDefinition pSpritesheetDefinition, int pSpriteFrameIndex) {
		mIconSpritesheet = pSpritesheetDefinition;
		mIconSpriteFrameIndex = pSpriteFrameIndex;
	}

	public boolean drawBackground() {
		return mDrawBackground;
	}

	public void drawBackground(boolean pNewValue) {
		mDrawBackground = pNewValue;
	}

	public boolean darkenBackground() {
		return mDarkenBackground;
	}

	public void darkenBackground(boolean pNewValue) {
		mDarkenBackground = pNewValue;
	}

	public String dialogTitle() {
		return mMenuTitle;
	}

	public void dialogTitle(String pNewMenuTitle) {
		mMenuTitle = pNewMenuTitle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseDialog(ScreenManager pScreenManager, Screen pParentScreen, String pDialogMessage) {
		super(pScreenManager, "");

		mParentScreen = pParentScreen;

		mShowBackgroundScreens = true;
		mDrawBackground = true;
		mDarkenBackground = true;

		mMessageString = pDialogMessage;

		mTransitionOn = null;
		mTransitionOff = null;
		screenColor.a = 1.f;

		mPaddingTopNormalized = 0;

		mIsPopup = true; // don't hide underlying screens
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void updateLayoutSize(LintfordCore pCore) {
		super.updateLayoutSize(pCore);

		final int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lBaseLayout = mLayouts.get(i);

			lBaseLayout.set(-DIALOG_WIDTH * 0.4f, mPaddingTopNormalized, DIALOG_WIDTH * 0.8f, DIALOG_HEIGHT);
			lBaseLayout.updateStructure();
		}
	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		final float lZDepth = ZLayers.LAYER_SCREENMANAGER + 0.05f;
		final float lWindowWidth = pCore.HUD().boundingRectangle().w();
		final float lWindowHeight = pCore.HUD().boundingRectangle().h();

		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDarkenBackground) {
			final var lColor = ColorConstants.getBlackWithAlpha(.6f);
			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, -lWindowWidth * 0.5f, -lWindowHeight * 0.5f, lWindowWidth, lWindowHeight, ZLayers.LAYER_SCREENMANAGER - 0.1f, lColor);
			lSpriteBatch.end();
		}

		if (mDrawBackground) {
			final float TILE_SIZE = 32f;
			final float x = -DIALOG_WIDTH / 2;
			final float y = -DIALOG_HEIGHT / 2;
			final float w = DIALOG_WIDTH;
			final float h = DIALOG_HEIGHT;

			final var lColor = ColorConstants.getWhiteWithAlpha(1.f);

			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_TOP_LEFT, x, y, TILE_SIZE, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_TOP_MID, x + TILE_SIZE, y, w - 64, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_TOP_RIGHT, x + w - 32, y, TILE_SIZE, TILE_SIZE, lZDepth, lColor);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_MID_LEFT, x, y + 32, TILE_SIZE, h - 64, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_MID_CENTER, x + TILE_SIZE, y + 32, w - 64, h - 64, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_MID_RIGHT, x + w - 32, y + 32, TILE_SIZE, h - 64, lZDepth, lColor);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_BOTTOM_LEFT, x, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_BOTTOM_MID, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_BOTTOM_RIGHT, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.end();
		}

		final boolean lDrawIcon = mIconSpriteFrameIndex != -1 && mIconSpritesheet != null;
		if (lDrawIcon) {
			final float x = -DIALOG_WIDTH / 2;
			final float y = -DIALOG_HEIGHT / 2;

			final var lSpriteFrame = mIconSpritesheet.getSpriteFrame(mIconSpriteFrameIndex);
			final float lIconWidth = lSpriteFrame.w();
			final float lIconHeight = lSpriteFrame.h();

			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mIconSpritesheet, lSpriteFrame, x + 15.f, y + 15.f, lIconWidth, lIconHeight, lZDepth, ColorConstants.WHITE);
			lSpriteBatch.end();
		}

		final float lHeaderFontHeight = mMenuHeaderFont.fontHeight();

		// Render the menu title if there is one
		if (mMenuTitle != null && mMenuTitle.length() > 0) {
			mMenuFont.begin(pCore.HUD());
			final float lHorizontalOffsetX = (lDrawIcon) ? 74.f : 0.f;
			mMenuFont.drawText(mMenuTitle, -DIALOG_WIDTH / 2f + TEXT_HORIZONTAL_PADDING + lHorizontalOffsetX, -DIALOG_HEIGHT / 2f + TEXT_HORIZONTAL_PADDING, lZDepth, screenColor, 1.f);
			mMenuFont.end();
		}

		mMenuFont.begin(pCore.HUD());
		mMenuFont.drawText(mMessageString, -DIALOG_WIDTH * 0.5f + TEXT_HORIZONTAL_PADDING, -DIALOG_HEIGHT * 0.5f + lHeaderFontHeight + 15f, lZDepth, ColorConstants.WHITE, 1f, DIALOG_WIDTH - 70);
		mMenuFont.end();

		// Draw each layout in turn.
		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, lZDepth + (i * 0.001f));
		}
	}
}
