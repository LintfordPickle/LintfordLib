package net.lintford.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

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
	private transient int mEntityGroupID;

	/** The height of the largest glyph is taken to be the height of the font. */
	public float fontHeight;

	/** The unique name given to this {@link BitmapFontDefinition}. */
	public boolean reloadable;
	public boolean useSubPixelRendering;

	/** The name of the {@link Texture} associated to this {@link BitmapFontDefinition} */
	protected String textureName;
	protected String textureFilepath;

	/** The {@link Texture} instance associated with this {@link SpriteSheetDBitmapFontDefinitionefinition} */
	private transient Texture texture;

	/**
	 * In order to detect changes to the SpriteSheet when trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	private long mFileSizeOnLoad;

	/** A collection of {@link ISprite} instances (glyphs) contained within this {@link BitmapFontDefinition} */
	public Map<Integer, SpriteFrame> glyphMap;

	public String spriteGraphNodeName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public long fileSizeOnLoad() {
		return mFileSizeOnLoad;
	}

	public void fileSizeOnLoad(long v) {
		mFileSizeOnLoad = v;
	}

	/** Returns true if this {@link SpriteSheetDefinition}'s GL resources have been laoded, false otherwise. */
	public boolean isLoaded() {
		return texture != null;
	}

	/** Returns the texture loaded for this {@link SpriteSheetDefinition}. */
	public Texture texture() {
		return texture;
	}

	public boolean reloadable() {
		return reloadable;
	}

	public void reloadable(boolean pNewValue) {
		reloadable = pNewValue;
	}

	public float getStringWidth(String pText) {
		return getStringWidth(pText, 1.0f);
	}

	public float getStringWidth(String pText, float pTextScale) {
		if (pText == null || pText.length() == 0)
			return 0f;

		float lWidth = 0.f;
		final int lCharCount = pText.length();
		for (int i = 0; i < lCharCount; i++) {
			final var lGlyph = getGlyphFrame((int) pText.charAt(i));
			if (lGlyph == null)
				continue;
			lWidth += lGlyph.w() * pTextScale;
		}

		return lWidth;
	}

	public float getFontHeight() {
		return fontHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BitmapFontDefinition() {
		glyphMap = new HashMap<>();
		useSubPixelRendering = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		loadResources(pResourceManager, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	/** Loads the associated texture. */
	public void loadResources(ResourceManager pResourceManager, int pEntityGroupID) {
		if (textureName == null || textureName.length() == 0) {
			System.err.println("BitmapFontDefinition texture name and filename cannot be null!");
			return;

		}

		mEntityGroupID = pEntityGroupID;

		final var lTextureManager = pResourceManager.textureManager();
		final int lFilteringMode = useSubPixelRendering ? GL11.GL_LINEAR : GL11.GL_NEAREST;
		texture = lTextureManager.getTextureOrLoad(textureName, textureFilepath, lFilteringMode, mEntityGroupID);

		if (lTextureManager.isTextureLoaded(texture) == false) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot locate texture " + textureName);
		}

		if (fontHeight == 0) {
			int lFontHeight = 0;
			for (Map.Entry<Integer, SpriteFrame> entry : glyphMap.entrySet()) {
				final var lSpriteFrame = entry.getValue();
				if (lSpriteFrame.h() > lFontHeight)
					lFontHeight = (int) lSpriteFrame.h();
			}
			fontHeight = lFontHeight;
		}
	}

	/** unloads the GL Content created by this SpriteSheet. */
	public void unloadResources() {
		texture = null;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteFrame getGlyphFrame(final int pFrameIndex) {
		return glyphMap.get(pFrameIndex);
	}
}
