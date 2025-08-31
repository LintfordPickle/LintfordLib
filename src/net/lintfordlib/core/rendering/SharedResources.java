package net.lintfordlib.core.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontMetaData;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.geometry.TexturedQuad_PT;
import net.lintfordlib.core.graphics.linebatch.LineBatch;
import net.lintfordlib.core.graphics.pointbatch.PointBatch;
import net.lintfordlib.core.graphics.polybatch.PolyBatchPCT;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PT;

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

	private PointBatch mPointBatch;
	private SpriteBatch mSpriteBatch;
	private LineBatch mLineBatch;
	private PolyBatchPCT mPolyBatch;

	private FontUnit mHudTextBoldSmallFont;
	private FontUnit mUiTextFont;
	private FontUnit mUiTextBoldFont;
	private FontUnit mUiHeaderFont;
	private FontUnit mUiTitleFont;

	private TexturedQuad_PT mTexturedQuad;
	private ShaderMVP_PT mBasicShader;

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

	public PointBatch pointBatch() {
		return mPointBatch;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SharedResources(int entityGroupUid) {
		mEntityGroupUid = entityGroupUid;

		mSpriteBatch = new SpriteBatch();
		mLineBatch = new LineBatch();
		mPolyBatch = new PolyBatchPCT();
		mPointBatch = new PointBatch();

		mTexturedQuad = new TexturedQuad_PT();

		mBasicShader = new ShaderMVP_PT("Basic Shader PT") {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inTexCoord");
			}
		};

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
		mPointBatch.loadResources(resourceManager);

		mBasicShader.loadResources(resourceManager);
		mTexturedQuad.loadResources(resourceManager);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mSpriteBatch.unloadResources();
		mLineBatch.unloadResources();
		mPolyBatch.unloadResources();
		mBasicShader.unloadResources();
		mTexturedQuad.unloadResources();
		mPointBatch.unloadResources();

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

	public void drawRenderTargetImmediate(LintfordCore core, float destinationX, float destinationY, float destinationWidth, float destinationHeight, float zDepth, RenderTarget renderTarget) {
		if (renderTarget == null)
			return;

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderTarget.colorTextureID());

		mBasicShader.projectionMatrix(core.HUD().projection());
		mBasicShader.viewMatrix(core.HUD().view());

		mTexturedQuad.createModelMatrixAbsolute(destinationX, destinationY, zDepth, destinationWidth, destinationHeight);
		mBasicShader.modelMatrix(mTexturedQuad.modelMatrix());

		mBasicShader.bind();
		mTexturedQuad.draw(core);
		mBasicShader.unbind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
