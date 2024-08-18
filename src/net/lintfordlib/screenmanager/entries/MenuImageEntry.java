package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.RendererManager;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.layouts.BaseLayout;

public class MenuImageEntry extends MenuEntry {

	private static final long serialVersionUID = 4053035578493366108L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BaseLayout mParentLayout;
	private float mForceHeight;
	private float mFittedWidth;
	private float mFittedHeight;
	private int mMaximumWidth;
	private FontUnit mUiFont;
	private Texture mMainTexture;
	private SpriteSheetDefinition mMissingTextureSpritesheet;
	private int mMissingTextureSpriteFrameIndex;
	private boolean mShowMissingTextureText;
	private String mMissingTextureText;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setMissingTexturetext(String missingTextureText) {
		if (missingTextureText == null || missingTextureText.length() == 0) {
			mMissingTextureText = "";
			mShowMissingTextureText = false;

			return;
		}

		mMissingTextureText = missingTextureText;
		mShowMissingTextureText = true;
	}

	public void setMaximumImageWidth(int widthLimit) {
		mMaximumWidth = widthLimit;
	}

	@Override
	public float height() {
		if (forceHeight() < 0)
			return mFittedHeight;

		return mFittedHeight;
	}

	public void forceHeight(float forceHeight) {
		mForceHeight = forceHeight;
	}

	public float forceHeight() {
		return mForceHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuImageEntry(ScreenManager screenManager, BaseLayout parentLayout, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mParentLayout = parentLayout;
		mText = "Add your message";

		mCanHaveFocus = false;

		mMaximumWidth = 2048;

		mLeftMargin = 20;
		mRightMargin = 20;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mUiFont = resourceManager.fontManager().getFontUnit(RendererManager.UI_FONT_TEXT_BOLD_NAME);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mUiFont = null;
		mCoreSpritesheet = null;
	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		mH = mMainTexture != null ? mMainTexture.getTextureHeight() : mH;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		final var lParentLayoutCropped = mParentLayout.cropPaddingBottom() + mParentLayout.cropPaddingTop();
		
		final var lAvailableHeight = mParentLayout.height() - mParentLayout.marginBottom() - mParentLayout.marginTop() - lParentLayoutCropped - mParentLayout.titleBarSize();
		final var lAvailableWidth = MathHelper.clamp(mParentLayout.width() - mParentLayout.marginLeft() - mParentLayout.marginRight(), 120, mMaximumWidth);

		if (mMainTexture != null) {
			mFittedWidth = mMainTexture.getTextureWidth();
			mFittedHeight = mMainTexture.getTextureHeight();

		} else {
			mFittedWidth = 640;
			mFittedHeight = 480;
		}

		if (mFittedWidth > lAvailableWidth)
			reduceWidthToFit(lAvailableWidth);

		if (mFittedHeight > lAvailableHeight) {
			reduceHeightToFit(lAvailableHeight);
		}

		mX = mParentLayout.x() + mParentLayout.width() / 2f - mFittedWidth / 2;
		mH = mFittedHeight;
	}

	private void reduceWidthToFit(float availableWidth) {
		final var lNewProposedWidth = availableWidth;
		final var lScale = lNewProposedWidth / mFittedWidth;

		final var lNewProposedHeight = mFittedHeight * lScale;

		if (lNewProposedWidth < mFittedWidth && lNewProposedHeight < mFittedHeight) {
			mFittedWidth = lNewProposedWidth;
			mFittedHeight = lNewProposedHeight;
		}
	}

	private void reduceHeightToFit(float availableHeight) {
		if (mFittedHeight < availableHeight)
			return;

		final var lNewProposedHeight = availableHeight;
		final var lScale = lNewProposedHeight / mFittedHeight;

		final var lNewProposedWidth = mFittedWidth * lScale;

		if (lNewProposedWidth < mFittedWidth && lNewProposedHeight < mFittedHeight) {
			mFittedWidth = lNewProposedWidth;
			mFittedHeight = lNewProposedHeight;
		}
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var lParentScreen = mParentLayout.parentScreen;
		final var lSpriteBatch = lParentScreen.spriteBatch();

		lSpriteBatch.begin(core.HUD());

		final var lScreenOffset = screen.screenPositionOffset();

		if (mMainTexture != null) {
			final int lTextureWidth = mMainTexture.getTextureWidth();
			final int lTextureHeight = mMainTexture.getTextureHeight();

			lSpriteBatch.draw(mMainTexture, 0, 0, lTextureWidth, lTextureHeight, lScreenOffset.x + mX, lScreenOffset.y + mY, mFittedWidth, mFittedHeight, parentZDepth + .1f, entryColor);

		}

		else if (mShowMissingTextureText) {
			final float lTextWidth = mUiFont.getStringWidth(mMissingTextureText);
			mUiFont.begin(core.HUD());
			mUiFont.drawText(mMissingTextureText, lScreenOffset.x + mX + mFittedWidth / 2f - lTextWidth / 2f, lScreenOffset.y + (int) (mY + mFittedHeight / 2), parentZDepth + .1f, ColorConstants.WHITE, 1f);
			mUiFont.end();
		}

		else if (mMissingTextureSpritesheet != null) {
			lSpriteBatch.draw(mMissingTextureSpritesheet, mMissingTextureSpriteFrameIndex, lScreenOffset.x + mX, lScreenOffset.y + mY, mFittedWidth, mFittedHeight, parentZDepth + .1f, entryColor);
		}

		else if (mCoreSpritesheet != null) {
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mFittedWidth, mFittedHeight, parentZDepth + .1f, entryColor);
		}

		lSpriteBatch.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, 1.f);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, 1.f);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTexture(Texture texture) {
		mMainTexture = texture;
	}

	public void setDefaultImage(SpriteSheetDefinition spritesheetDefinition, int apriteFrameIndex) {
		mMissingTextureSpritesheet = spritesheetDefinition;
		mMissingTextureSpriteFrameIndex = apriteFrameIndex;
	}
}
