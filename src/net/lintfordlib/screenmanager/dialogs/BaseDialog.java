package net.lintfordlib.screenmanager.dialogs;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

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

	protected BaseDialog(ScreenManager screenManager, Screen parentScreen, String dialogMessage) {
		super(screenManager, "");

		mParentScreen = parentScreen;

		mShowBackgroundScreens = true;
		mDrawBackground = true;
		mDarkenBackground = true;

		mMessageString = dialogMessage;

		mTransitionOn = null;
		mTransitionOff = null;
		screenColor.a = 1.f;

		mScreenPaddingTop = DIALOG_HEIGHT / 2.f - 64.f;

		mIsPopup = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);

		// block elements under the dialog from acquiring a mouse over event
		core.input().mouse().tryAcquireMouseOverThisComponent(hashCode());

	}

	@Override
	public void updateLayoutSize(LintfordCore core) {
		super.updateLayoutSize(core);

		final int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lBaseLayout = mLayouts.get(i);
			lBaseLayout.set(-DIALOG_WIDTH * 0.5f, mScreenPaddingTop, DIALOG_WIDTH, DIALOG_HEIGHT);
			lBaseLayout.updateStructure();
		}
	}

	@Override
	public void draw(LintfordCore core) {
		if (mScreenState != ScreenState.ACTIVE || mScreenState == ScreenState.TRANSITION_STARTING || mScreenState == ScreenState.TRANSITION_SLEEPING)
			return;

		if (!mResourcesLoaded)
			return;

		final float lZDepth = ZLayers.LAYER_SCREENMANAGER + 0.05f;
		final float lWindowWidth = core.HUD().boundingRectangle().width();
		final float lWindowHeight = core.HUD().boundingRectangle().height();

		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDarkenBackground) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorRGBA(0.f, 0.f, 0.f, .6f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, -lWindowWidth * 0.5f, -lWindowHeight * 0.5f, lWindowWidth, lWindowHeight, ZLayers.LAYER_SCREENMANAGER);
			lSpriteBatch.end();
		}

		final float TILE_SIZE = 32f;
		if (mDrawBackground) {
			final float x = -DIALOG_WIDTH / 2.f;
			final float y = -DIALOG_HEIGHT / 2.f;
			final float w = DIALOG_WIDTH;
			final float h = DIALOG_HEIGHT;

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x, y, TILE_SIZE, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, x + TILE_SIZE, y, w - 64, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, x + w - 32, y, TILE_SIZE, TILE_SIZE, lZDepth);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT, x, y + 32, TILE_SIZE, h - 64, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, x + TILE_SIZE, y + 32, w - 64, h - 64, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT, x + w - 32, y + 32, TILE_SIZE, h - 64, lZDepth);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, x, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth);
			lSpriteBatch.end();
		}

		final boolean lDrawIcon = mIconSpriteFrameIndex != -1 && mIconSpritesheet != null;
		if (lDrawIcon) {
			final float x = -DIALOG_WIDTH / 2.f;
			final float y = -DIALOG_HEIGHT / 2.f;

			final var lSpriteFrame = mIconSpritesheet.getSpriteFrame(mIconSpriteFrameIndex);
			final var lIconWidth = lSpriteFrame.width();
			final var lIconHeight = lSpriteFrame.height();

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
			lSpriteBatch.draw(mIconSpritesheet, lSpriteFrame, x + 15.f, y + TILE_SIZE + 15.f, lIconWidth, lIconHeight, lZDepth);
			lSpriteBatch.end();
		}

		if (mMenuTitle != null && mMenuTitle.length() > 0) {
			final float lHorizontalOffsetX = (lDrawIcon) ? 5.f : 0.f;

			mMenuFont.begin(core.HUD());
			mMenuFont.setTextColor(screenColor);
			mMenuFont.drawText(mMenuTitle, -DIALOG_WIDTH / 2f + TEXT_HORIZONTAL_PADDING + lHorizontalOffsetX, -DIALOG_HEIGHT / 2f + mMenuFont.fontHeight(), lZDepth, 1.f);
			mMenuFont.end();
		}

		mMenuFont.begin(core.HUD());
		mMenuFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		mMenuFont.drawText(mMessageString, -DIALOG_WIDTH * 0.5f + 15.f * 2.f + 64.f, -DIALOG_HEIGHT * 0.5f + 48f, lZDepth, 1f, DIALOG_WIDTH - 120.f);
		mMenuFont.end();

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(core, lZDepth + (i * 0.001f));
		}
	}
}
