package net.ld.library.core.graphics.fonts;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;

// http://forum.lwjgl.org/index.php?topic=5573.0
// https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Fonts
public class BitmapFont {

	public class Glyph {
		public final int width;
		public final int height;
		public final int x;
		public final int y;

		public Glyph(int width, int height, int x, int y) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
		}
	}

	// =============================================
	// Constants
	// =============================================

	public static String CHARACTER_GLYPHS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!\"§$%&/()=?<>,.-_*+[]{}#äöü~^°:@€| ";

	// =============================================
	// Variables
	// =============================================

	private String mFontFileLocation;
	private String mFontName;
	private Texture mFontTexture;
	private float mPointSize;
	private boolean mIsLoaded;

	private float mFontHeight;

	private Map<Character, Glyph> mGlyphs = new HashMap<>();

	// =============================================
	// Properties
	// =============================================

	public Map<Character, Glyph> glyphs() {
		return mGlyphs;
	}

	public String fontName() {
		return mFontName;
	}

	public float pointSize() {
		return mPointSize;
	}

	public Texture fontTexture() {
		return mFontTexture;
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public float fontHeight() {
		return mFontHeight;
	}

	// =============================================
	// Constructor
	// =============================================

	public BitmapFont(String pName, float pPointSize) {
		mFontName = pName;

		mPointSize = pPointSize;
		mIsLoaded = false;
		mFontHeight = pPointSize;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public boolean loadFontFromFile(String pFilepath) {
		mFontFileLocation = pFilepath;

		// Try and load the file
		File lFontFile = new File(mFontFileLocation);
		if (!lFontFile.exists()) {
			System.err.println("Cannot load font from file: " + pFilepath + ". File doesn't exit.");
			mIsLoaded = false;
			return false;
		}

		// Create new awt.Font instance, specifying the size and style attribs

		// This font looks really nice
		// mFont = new Font(Font.MONOSPACED, mBold ? Font.BOLD : Font.PLAIN, (int) mPointSize);
		try {
			Font lFont = Font.createFont(Font.TRUETYPE_FONT, lFontFile).deriveFont(mPointSize);

			loadBitmapFont(lFont);

		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}

	public boolean loadFontFromResource(String pFilepath) {
		mFontFileLocation = pFilepath;

		System.out.println("Loading font from Resource: " + pFilepath);

		// Try and load the file
		InputStream lInputStream = Texture.class.getResourceAsStream(pFilepath);

		if (lInputStream == null) {
			System.err.println("Cannot load font from resource: " + pFilepath + ". Resource doesn't exit  (InputStream null).");
			mIsLoaded = false;
			return false;
		}

		try {
			Font lFont = Font.createFont(Font.TRUETYPE_FONT, lInputStream).deriveFont(mPointSize);

			loadBitmapFont(lFont);

		} catch (FontFormatException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}

		return true;

	}

	private void loadBitmapFont(Font pFont) {
		int imageWidth = 0;
		int imageHeight = 0;

		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage charImage = createCharImage(pFont, c, true);
			if (charImage == null)
				continue;

			imageWidth += charImage.getWidth();
			imageHeight = Math.max(imageHeight, charImage.getHeight());

		}

		// Store the height of the largest glyph as the height of the font bitmap
		// mFontHeight = imageHeight;

		// Create a new buffered image with the font height and the sum of the width
		BufferedImage lFontImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D lGraphics = lFontImage.createGraphics();

		int x = 0;
		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage charImage = createCharImage(pFont, c, true);
			if (charImage == null)
				continue;

			int charWidth = charImage.getWidth();
			int charHeight = charImage.getHeight();

			Glyph lNewGlyph = new Glyph(charWidth, charHeight, x, lFontImage.getHeight() - charHeight);

			lGraphics.drawImage(charImage, x, 0, null);

			x += lNewGlyph.width;
			mGlyphs.put(c, lNewGlyph);

		}

		// TODO: Need to be able to specify if the texture loaded for a bitmap font is GL_NEAREST or GL_LINEAR
		mFontTexture = TextureManager.textureManager().createFontTexture(mFontName, lFontImage, GL11.GL_NEAREST);

		mIsLoaded = true;
	}

	public void loadGLContent(ResourceManager pResourceManager) {

	}

	public void unloadGLContent() {

		mIsLoaded = false;
	}

	// =============================================
	// Methods
	// =============================================

	private BufferedImage createCharImage(java.awt.Font font, char c, boolean antiAlias) {
		/* Creating temporary image to extract character size */
		BufferedImage lImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D lGraphics = lImage.createGraphics();
		if (antiAlias) {
			lGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		lGraphics.setFont(font);
		FontMetrics lFontMetrics = lGraphics.getFontMetrics();
		lGraphics.dispose();

		/* Get char charWidth and charHeight */
		int charWidth = lFontMetrics.charWidth(c);
		int charHeight = lFontMetrics.getHeight();

		/* Check if charWidth is 0 */
		if (charWidth == 0) {
			return null;
		}

		/* Create image for holding the char */
		lImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		lGraphics = lImage.createGraphics();
		if (antiAlias) {
			lGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		lGraphics.setFont(font);
		lGraphics.setPaint(java.awt.Color.WHITE);
		lGraphics.drawString(String.valueOf(c), 0, lFontMetrics.getAscent());
		lGraphics.dispose();
		return lImage;
	}

	public float getStringWidth(String pText) {
		return getStringWidth(pText, 1f);
	}

	public float getStringWidth(String pText, float pScaleFactor) {
		if (!mIsLoaded || pText == null)
			return 0f;

		float lResult = 0;
		float lTempResult = 0;

		for (int i = 0; i < pText.length(); i++) {
			char ch = pText.charAt(i);

			if (ch == '\n') {
				// Line feed
				lResult = Math.max(lResult, lTempResult);
				lTempResult = 0;
				continue;
			}
			if (ch == '\r') {
				// Carriage return
				lResult = Math.max(lResult, lTempResult);
				lTempResult = 0;
				continue;
			}

			Glyph lCharGlyph = glyphs().get(ch);
			if (lCharGlyph != null) {
				// Increment the temp result
				lTempResult += lCharGlyph.width * pScaleFactor;
			}
		}

		return Math.max(lResult, lTempResult);
	}

	public float getStringHeight(String pText) {
		return getStringHeight(pText, 1f);
	}

	public float getStringHeight(String pText, float pScaleFactor) {
		if (!mIsLoaded)
			return 0f;

		float lResult = mFontHeight * pScaleFactor;

		for (int i = 0; i < pText.length(); i++) {
			char ch = pText.charAt(i);

			if (ch == '\n') {
				// Line feed
				lResult += mFontHeight * pScaleFactor;
				continue;
			}
			if (ch == '\r') {
				// Carriage return
				lResult += mFontHeight * pScaleFactor;
				continue;
			}

		}

		return lResult;
	}

}
