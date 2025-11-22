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

	public static final int DEFAULT_DIALOG_WIDTH = 500;
	public static final int DEFAULT_DIALOG_HEIGHT = 250;

	public static final float TEXT_HORIZONTAL_PADDING = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final Rectangle mDialogArea = new Rectangle();

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

	public void setDisplayAreaDimensions(float width, float height) {
		final var origCenterX = mDialogArea.centerX();
		final var origCenterY = mDialogArea.centerY();

		setDisplayArea(mDialogArea.x(), mDialogArea.y(), width, height);

		// recenter around previous point with updated dimensions
		mDialogArea.setCenterPosition(origCenterX, origCenterY);
	}

	public void setDisplayAreaPosition(float x, float y) {
		setDisplayArea(x, y, mDialogArea.width(), mDialogArea.height());
	}

	public void setDisplayArea(float x, float y, float width, float height) {
		mDialogArea.set(x, y, width, height);

		// TODO: The padding top (used for positioning the first of the menu entries) should take the height of the message into account. 
		mScreenPaddingTop = mDialogArea.centerY();
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

		mIsPopup = true;

		setDisplayArea(-DEFAULT_DIALOG_WIDTH / 2, -DEFAULT_DIALOG_HEIGHT / 2, DEFAULT_DIALOG_WIDTH, DEFAULT_DIALOG_HEIGHT);

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

		// Also, because the BaseDialog inherits from MenuScreen, it can have many layouts, but we I am assigning them all the same area.
		// The BaseDialogs and derived classes should use a floating layout (with will have the same 'area' as the dialog) and manage
		// the positioning and the size of the menu entries themselves. I.E. multiple list layout's wont work here.

		final int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var baseLayout = mLayouts.get(i);

			final var x = mDialogArea.x();
			final var y = mDialogArea.y();
			final var width = mDialogArea.width();
			final var height = mDialogArea.height();

			baseLayout.set(x, y, width, height);
			baseLayout.updateStructure();
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
			final float x = mDialogArea.left();
			final float y = mDialogArea.top();
			final float w = mDialogArea.width();
			final float h = mDialogArea.height();

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
			final float x = mDialogArea.left();
			final float y = mDialogArea.top();

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
			mMenuFont.drawText(mMenuTitle, mDialogArea.left() + TEXT_HORIZONTAL_PADDING + lHorizontalOffsetX, mDialogArea.top() + mMenuFont.fontHeight(), lZDepth, 1.f);
			mMenuFont.end();
		}

		mMenuFont.begin(core.HUD());
		mMenuFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		mMenuFont.drawText(mMessageString, mDialogArea.left() + 15.f * 2.f + 64.f, mDialogArea.top() + 48f, lZDepth, 1f, DEFAULT_DIALOG_WIDTH - 120.f);
		mMenuFont.end();

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(core, lZDepth + (i * 0.001f));
		}
	}
}
