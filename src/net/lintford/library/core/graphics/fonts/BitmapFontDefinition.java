package net.lintford.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.SerializedName;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;

class BitmapFontDefinition {

	public static final int LINE_SPACING = 2;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Textures referenced within this spritesheet need to be referenced with the correct entity group Id. */
	protected transient int mEntityGroupUid;

	/** The height of the largest glyph is taken to be the height of the font. */
	protected float mFontHeight;

	/** The unique name given to this {@link BitmapFontDefinition}. */
	protected boolean mReloadable;

	@SerializedName(value = "useSubPixelRendering")
	protected boolean mUseSubPixelRendering;

	/** The name of the {@link Texture} associated to this {@link BitmapFontDefinition} */
	@SerializedName(value = "textureName")
	protected String mTextureName;

	@SerializedName(value = "textureFilepath")
	protected String mTextureFilepath;

	/** The {@link Texture} instance associated with this {@link SpriteSheetDBitmapFontDefinitionefinition} */
	protected transient Texture mTexture;

	/**
	 * In order to detect changes to the SpriteSheet when trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	protected long mFileSizeOnLoad;

	/** A collection of {@link ISprite} instances (glyphs) contained within this {@link BitmapFontDefinition} */
	@SerializedName(value = "glyphMap")
	protected Map<Integer, SpriteFrame> mGlyphMap;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public long fileSizeOnLoad() {
		return mFileSizeOnLoad;
	}

	public void fileSizeOnLoad(long fileSize) {
		mFileSizeOnLoad = fileSize;
	}

	/** Returns true if this {@link SpriteSheetDefinition}'s GL resources have been laoded, false otherwise. */
	public boolean isLoaded() {
		return mTexture != null;
	}

	/** Returns the texture loaded for this {@link SpriteSheetDefinition}. */
	public Texture texture() {
		return mTexture;
	}

	public boolean reloadable() {
		return mReloadable;
	}

	public void reloadable(boolean isReloadable) {
		mReloadable = isReloadable;
	}

	public float getStringWidth(String text) {
		return getStringWidth(text, 1.0f);
	}

	public float getStringWidth(String text, float textScale) {
		if (text == null || text.length() == 0)
			return 0f;

		float lWidth = 0.f;
		final int lCharCount = text.length();
		for (int i = 0; i < lCharCount; i++) {
			final var lGlyph = getGlyphFrame((int) text.charAt(i));
			if (lGlyph == null)
				continue;
			lWidth += lGlyph.width() * textScale;
		}

		return lWidth;
	}

	public float getFontHeight() {
		return mFontHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BitmapFontDefinition() {
		mGlyphMap = new HashMap<>();
		mUseSubPixelRendering = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		loadResources(resourceManager, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	/** Loads the associated texture. */
	public void loadResources(ResourceManager resourceManager, int entityGroupUid) {
		if (mTextureName == null || mTextureName.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "BitmapFontDefinition texture name and filename cannot be null!");
			return;
		}

		mEntityGroupUid = entityGroupUid;

		final var lTextureManager = resourceManager.textureManager();
		final int lFilteringMode = mUseSubPixelRendering ? GL11.GL_LINEAR : GL11.GL_NEAREST;
		mTexture = lTextureManager.getTextureOrLoad(mTextureName, mTextureFilepath, lFilteringMode, mEntityGroupUid);

		if (lTextureManager.isTextureLoaded(mTexture) == false) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot locate texture " + mTextureName);
		}

		if (mFontHeight == 0) {
			int lFontHeight = 0;
			for (Map.Entry<Integer, SpriteFrame> entry : mGlyphMap.entrySet()) {
				final var lSpriteFrame = entry.getValue();
				if (lSpriteFrame.height() > lFontHeight)
					lFontHeight = (int) lSpriteFrame.height();
			}
			mFontHeight = lFontHeight;
		}
	}

	/** unloads the GL Content created by this SpriteSheet. */
	public void unloadResources() {
		mTexture = null;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteFrame getGlyphFrame(final int frameIndex) {
		return mGlyphMap.get(frameIndex);
	}
}
