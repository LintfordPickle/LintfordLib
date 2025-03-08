package net.lintfordlib.core.rendering;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontMetaData;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.linebatch.LineBatch;
import net.lintfordlib.core.graphics.polybatch.PolyBatchPCT;

public final class SharedResources {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final FontMetaData RendererManagerFonts = new FontMetaData();

	public static final String HUD_FONT_TEXT_BOLD_SMALL_NAME = "HUD_FONT_TEXT_BOLD_SMALL_NAME";

	// TODO: recheck how this is set to work, they seem to be set in a lot of places.

	public static final String UI_FONT_TEXT_NAME = "UI_FONT_TEXT_NAME";
	public static final String UI_FONT_TEXT_BOLD_NAME = "UI_FONT_TEXT_BOLD_NAME";
	public static final String UI_FONT_HEADER_NAME = "UI_FONT_HEADER_NAME";
	public static final String UI_FONT_TITLE_NAME = "UI_FONT_TITLE_NAME";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourceManager mResourceManager;

	private SpriteBatch mSpriteBatch;
	private LineBatch mLineBatch;
	private PolyBatchPCT mPolyBatch;

	private FontUnit mHudTextBoldSmallFont;
	private FontUnit mUiTextFont;
	private FontUnit mUiTextBoldFont;
	private FontUnit mUiHeaderFont;
	private FontUnit mUiTitleFont;

	private int mEntityGroupUid;
	private boolean mResourcesLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public FontUnit hudTextBoldSmallFont() {
		return mHudTextBoldSmallFont;
	}

	public FontUnit uiTextFont() {
		return mUiTextFont;
	}

	public float textFontHeight() {
		if (mUiTextFont == null)
			return 0.f;
		return mUiTextFont.fontHeight();
	}

	public FontUnit uiTextBoldFont() {
		return mUiTextBoldFont;
	}

	public float textBoldFontHeight() {
		if (mUiTextBoldFont == null)
			return 0.f;
		return mUiTextBoldFont.fontHeight();
	}

	public FontUnit uiHeaderFont() {
		return mUiHeaderFont;
	}

	public float headerFontHeight() {
		if (mUiHeaderFont == null)
			return 0.f;
		return mUiHeaderFont.fontHeight();
	}

	public FontUnit uiTitleFont() {
		return mUiTitleFont;
	}

	public float titleFontHeight() {
		if (mUiTitleFont == null)
			return 0.f;
		return mUiTitleFont.fontHeight();
	}

	public SpriteBatch uiSpriteBatch() {
		return mSpriteBatch;
	}

	public PolyBatchPCT uiPolyBatch() {
		return mPolyBatch;
	}

	public LineBatch uiLineBatch() {
		return mLineBatch;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SharedResources(int entityGroupUid) {
		mEntityGroupUid = entityGroupUid;

		mSpriteBatch = new SpriteBatch();
		mLineBatch = new LineBatch();
		mPolyBatch = new PolyBatchPCT();

		RendererManagerFonts.AddIfNotExists(HUD_FONT_TEXT_BOLD_SMALL_NAME, "/res/fonts/fontCoreText.json");

		RendererManagerFonts.AddIfNotExists(UI_FONT_TEXT_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_TEXT_BOLD_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_HEADER_NAME, "/res/fonts/fontCoreText.json");
		RendererManagerFonts.AddIfNotExists(UI_FONT_TITLE_NAME, "/res/fonts/fontCoreText.json");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mResourceManager = resourceManager;
		mResourceManager.increaseReferenceCounts(mEntityGroupUid);

		mHudTextBoldSmallFont = resourceManager.fontManager().getFontUnit(HUD_FONT_TEXT_BOLD_SMALL_NAME);

		mUiTextFont = resourceManager.fontManager().getFontUnit(UI_FONT_TEXT_NAME);
		mUiTextBoldFont = resourceManager.fontManager().getFontUnit(UI_FONT_TEXT_BOLD_NAME);
		mUiHeaderFont = resourceManager.fontManager().getFontUnit(UI_FONT_HEADER_NAME);
		mUiTitleFont = resourceManager.fontManager().getFontUnit(UI_FONT_TITLE_NAME);

		mSpriteBatch.loadResources(resourceManager);
		mLineBatch.loadResources(resourceManager);
		mPolyBatch.loadResources(resourceManager);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mSpriteBatch.unloadResources();
		mLineBatch.unloadResources();
		mPolyBatch.unloadResources();

		mUiTextFont = null;
		mUiTextBoldFont = null;
		mUiHeaderFont = null;
		mUiTitleFont = null;

		mResourceManager.decreaseReferenceCounts(mEntityGroupUid);
		mResourceManager = null;

		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------
}
