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

		public void setDialogIcon(Texture texture) {
			setDialogIcon(texture, 0.f, 0.f, 0.f, 0.f);
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

	public void setDialogIcon(SpriteSheetDefinition spritesheetDefinition, int spriteFrameIndex) {
		mIconSpritesheet = spritesheetDefinition;
		mIconSpriteFrameIndex = spriteFrameIndex;
	}

	public boolean drawBackground() {
		return mDrawBackground;
	}

	public void drawBackground(boolean drawBackground) {
		mDrawBackground = drawBackground;
	}

	public boolean darkenBackground() {
		return mDarkenBackground;
	}

	public void darkenBackground(boolean darkenBackground) {
		mDarkenBackground = darkenBackground;
	}

	public String dialogTitle() {
		return mMenuTitle;
	}

	public void dialogTitle(String dialogTitle) {
		mMenuTitle = dialogTitle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseDialog(ScreenManager screenManager, Screen parentScreen, String dialogMessage) {
		super(screenManager, "");

		mParentScreen = parentScreen;

		mShowBackgroundScreens = true;
		mDrawBackground = true;
		mDarkenBackground = true;

		mMessageString = dialogMessage;

		mTransitionOn = null;
		mTransitionOff = null;
		screenColor.a = 1.f;

		mPaddingTopNormalized = DIALOG_HEIGHT / 2.f - 64.f;

		mIsPopup = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void updateLayoutSize(LintfordCore core) {
		super.updateLayoutSize(core);

		final int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lBaseLayout = mLayouts.get(i);
			lBaseLayout.set(-DIALOG_WIDTH * 0.4f, mPaddingTopNormalized, DIALOG_WIDTH * 0.8f, DIALOG_HEIGHT);
			lBaseLayout.updateStructure();
		}
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);
	}

	@Override
	public void draw(LintfordCore core) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		final float lZDepth = ZLayers.LAYER_SCREENMANAGER + 0.05f;
		final float lWindowWidth = core.HUD().boundingRectangle().width();
		final float lWindowHeight = core.HUD().boundingRectangle().height();

		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDarkenBackground) {
			final var lColor = ColorConstants.getBlackWithAlpha(.6f);
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, -lWindowWidth * 0.5f, -lWindowHeight * 0.5f, lWindowWidth, lWindowHeight, ZLayers.LAYER_SCREENMANAGER - 0.1f, lColor);
			lSpriteBatch.end();
		}

		final float TILE_SIZE = 32f;
		if (mDrawBackground) {
			final float x = -DIALOG_WIDTH / 2;
			final float y = -DIALOG_HEIGHT / 2;
			final float w = DIALOG_WIDTH;
			final float h = DIALOG_HEIGHT;

			final var lColor = ColorConstants.getWhiteWithAlpha(1.f);

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x, y, TILE_SIZE, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, x + TILE_SIZE, y, w - 64, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, x + w - 32, y, TILE_SIZE, TILE_SIZE, lZDepth, lColor);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT, x, y + 32, TILE_SIZE, h - 64, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, x + TILE_SIZE, y + 32, w - 64, h - 64, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT, x + w - 32, y + 32, TILE_SIZE, h - 64, lZDepth, lColor);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, x, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth, lColor);
			lSpriteBatch.end();
		}

		final boolean lDrawIcon = mIconSpriteFrameIndex != -1 && mIconSpritesheet != null;
		if (lDrawIcon) {
			final float x = -DIALOG_WIDTH / 2;
			final float y = -DIALOG_HEIGHT / 2;

			final var lSpriteFrame = mIconSpritesheet.getSpriteFrame(mIconSpriteFrameIndex);
			final float lIconWidth = lSpriteFrame.width();
			final float lIconHeight = lSpriteFrame.height();

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mIconSpritesheet, lSpriteFrame, x + 15.f, y + TILE_SIZE + 15.f, lIconWidth, lIconHeight, lZDepth, ColorConstants.WHITE);
			lSpriteBatch.end();
		}

		if (mMenuTitle != null && mMenuTitle.length() > 0) {
			mMenuFont.begin(core.HUD());
			final float lHorizontalOffsetX = (lDrawIcon) ? 5.f : 0.f;
			mMenuFont.drawText(mMenuTitle, -DIALOG_WIDTH / 2f + TEXT_HORIZONTAL_PADDING + lHorizontalOffsetX, -DIALOG_HEIGHT / 2f + 4.f, lZDepth, screenColor, 1.f);
			mMenuFont.end();
		}
		mMenuFont.begin(core.HUD());
		mMenuFont.drawText(mMessageString, -DIALOG_WIDTH * 0.5f + 15.f * 2.f + 64.f, -DIALOG_HEIGHT * 0.5f + 48f, lZDepth, ColorConstants.WHITE, 1f, DIALOG_WIDTH - 120);
		mMenuFont.end();

		// Draw each layout in turn.
		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(core, lZDepth + (i * 0.001f));
		}
	}
}
