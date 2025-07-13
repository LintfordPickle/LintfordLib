package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.rendering.SharedResources;
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

	@Override
	public float height() {
		if (forceHeight() < 0)
			return mFittedHeight;

		return mFittedHeight;
	}

	@Override
	public float desiredHeight() {
		return height();
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

		mLeftMargin = 20;
		mRightMargin = 20;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mUiFont = resourceManager.fontManager().getFontUnit(SharedResources.UI_FONT_TEXT_BOLD_NAME);
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
		final var lAvailableWidth = mW;

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
		final var spriteBatch = lParentScreen.spriteBatch();

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(entryColor);

		final var lScreenOffset = screen.screenPositionOffset();

		if (mMainTexture != null) {
			final int lTextureWidth = mMainTexture.getTextureWidth();
			final int lTextureHeight = mMainTexture.getTextureHeight();

			if (mHasFocus)
				spriteBatch.setColorRGBA(1.f, 1.f, .1f, 1.f);
			else
				spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);

			spriteBatch.draw(mMainTexture, 0, 0, lTextureWidth, lTextureHeight, lScreenOffset.x + mX, lScreenOffset.y + mY, mFittedWidth, mFittedHeight, parentZDepth + .01f);

		}

		else if (mShowMissingTextureText) {
			final float lTextWidth = mUiFont.getStringWidth(mMissingTextureText);
			mUiFont.begin(core.HUD());
			mUiFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			mUiFont.drawText(mMissingTextureText, lScreenOffset.x + mX + mFittedWidth / 2f - lTextWidth / 2f, lScreenOffset.y + mY + mFittedHeight / 2, parentZDepth + .1f, 1f);
			mUiFont.end();
		}

		else if (mMissingTextureSpritesheet != null) {
			spriteBatch.draw(mMissingTextureSpritesheet, mMissingTextureSpriteFrameIndex, lScreenOffset.x + mX, lScreenOffset.y + mY, mFittedWidth, mFittedHeight, parentZDepth + .1f);
		}

		else if (mCoreSpritesheet != null) {
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mFittedWidth, mFittedHeight, parentZDepth - .01f);
		}

		spriteBatch.end();

		if (mHasFocus)
			renderHighlight(core, screen, spriteBatch);

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, 1.f);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, 1.f);

		drawDebugCollidableBounds(core, spriteBatch);
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
