package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuImageEntry extends MenuEntry {

	private static final long serialVersionUID = 4053035578493366108L;

	private static final float DEFAULT_ASPECT_RATIO = 480f / 640f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BaseLayout mParentLayout;
	private float mForceHeight;
	private float mFittedWidth;
	private float mFittedHeight;
	private boolean mScaleToFitParent;
	private int mMaximumWidth = 640;
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

		mScaleToFitParent = true;

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
		if (mMainTexture != null) {
			float lAR = (float) mMainTexture.getTextureHeight() / (float) mMainTexture.getTextureWidth();

			final float lParentLayoutCropped = mParentLayout.cropPaddingBottom() + mParentLayout.cropPaddingTop();
			final float lAvailableHeight = mParentLayout.height() - mParentLayout.marginBottom() - mParentLayout.marginTop() - lParentLayoutCropped - mParentLayout.titleBarSize();

			fitWidthToHeight(lAvailableHeight, lAR);
		} else {
			final float lParentLayoutCropped = mParentLayout.cropPaddingBottom() + mParentLayout.cropPaddingTop();
			final float lAvailableHeight = mParentLayout.height() - mParentLayout.marginBottom() - mParentLayout.marginTop() - lParentLayoutCropped - mParentLayout.titleBarSize();

			fitWidthToHeight(lAvailableHeight, DEFAULT_ASPECT_RATIO);
		}

		mX = mParentLayout.x() + mParentLayout.width() / 2f - mFittedWidth / 2;
		mH = mFittedHeight;
	}

	// limited by the amount of height available
	// so fit the width based on the asepct ratio
	private void fitWidthToHeight(float availableHeightInParentContainer, float aspectRatio) {
		float maxAvailableWidth = mMaxWidth;
		float thMaxHeight = MathHelper.clamp(availableHeightInParentContainer, 0.f, mMaxHeight);

		if (mScaleToFitParent && aspectRatio != 0)
			maxAvailableWidth = (thMaxHeight - marginTop() - marginBottom()) / aspectRatio;

		if (mMainTexture != null)
			mFittedWidth = mMainTexture.getTextureWidth();
		else
			mFittedWidth = maxAvailableWidth;

		if (mFittedWidth > maxAvailableWidth)
			mFittedWidth = maxAvailableWidth;

		if (mFittedWidth > mMaximumWidth)
			mFittedWidth = mMaximumWidth;

		mFittedHeight = mFittedWidth * aspectRatio;
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

		} else if (mShowMissingTextureText) {
			final float lTextWidth = mUiFont.getStringWidth(mMissingTextureText);
			mUiFont.begin(core.HUD());
			mUiFont.drawText(mMissingTextureText, lScreenOffset.x + mX + mFittedWidth / 2f - lTextWidth / 2f, lScreenOffset.y + (int) (mY + mFittedHeight / 2), parentZDepth + .1f, ColorConstants.WHITE, 1f);
			mUiFont.end();
		} else if (mMissingTextureSpritesheet != null) {
			lSpriteBatch.draw(mMissingTextureSpritesheet, mMissingTextureSpriteFrameIndex, lScreenOffset.x + mX, lScreenOffset.y + mY, mFittedWidth, mFittedHeight, parentZDepth + .1f, entryColor);
		} else if (mCoreSpritesheet != null) {
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
