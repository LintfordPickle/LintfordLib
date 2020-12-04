package net.lintford.library.core.graphics.fonts;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.storage.AppStorage;
import net.lintford.library.core.storage.FileUtils;

// http://forum.lwjgl.org/index.php?topic=5573.0
// https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Fonts
public class BitmapFont {

	// --------------------------------------
	// Constants
	// --------------------------------------

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

	public static final int NO_WORD_WRAP = -1;
	public static final int NO_WIDTH_CAP = -1;
	
	public static final boolean SAVE_BITMAP_TEXTURES_ON_CREATION = false;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Font mFont;
	private String mFontFileLocation;
	private String mFontName;
	private Texture mFontTexture;
	private float mPointSize;
	private boolean mIsLoaded;
	private boolean mAntiAlias;

	private float mFontHeight;

	private Map<Character, Glyph> mGlyphs = new HashMap<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BitmapFont(String pName, String pFontFileLocation, float pPointSize, boolean pAntiAlias) {
		mFontName = pName;
		mAntiAlias = pAntiAlias;
		mFontFileLocation = pFontFileLocation;
		mPointSize = pPointSize;
		mIsLoaded = false;
		mFontHeight = pPointSize;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {

		if (mFontFileLocation.charAt(0) == '/') {
			// Load from file
			try {
				InputStream lFontInputStream = FileUtils.class.getResourceAsStream(mFontFileLocation);
				mFont = Font.createFont(Font.TRUETYPE_FONT, lFontInputStream).deriveFont(mPointSize);

			} catch (FontFormatException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Font format exception with font resource: " + mFontFileLocation);
				Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			} catch (IOException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Specified font resource not found: " + mFontFileLocation);
				Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			}

		} else {
			// Load from file
			try {
				File lFontFile = new File(mFontFileLocation);
				if (!lFontFile.exists()) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Font file not found at location: " + mFontFileLocation);
					return;
				}

				InputStream lFontInputStream = new FileInputStream(lFontFile);

				mFont = Font.createFont(Font.TRUETYPE_FONT, lFontInputStream).deriveFont(mPointSize);

			} catch (FontFormatException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Font format exception with font file: " + mFontFileLocation);
				Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			} catch (IOException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Specified font file not found: " + mFontFileLocation);
				Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			}

		}

		int imageWidth = 0;
		int imageHeight = 0;

		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage charImage = createCharImage(mFont, c, mAntiAlias);
			if (charImage == null)
				continue;

			imageWidth += charImage.getWidth();
			imageHeight = Math.max(imageHeight, charImage.getHeight());

		}

		// Create a new buffered image with the font height and the sum of the width
		BufferedImage lFontImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D lGraphics = lFontImage.createGraphics();

		int x = 0;
		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage charImage = createCharImage(mFont, c, mAntiAlias);
			if (charImage == null)
				continue;

			int charWidth = charImage.getWidth();
			int charHeight = charImage.getHeight();

			Glyph lNewGlyph = new Glyph(charWidth, charHeight, x, lFontImage.getHeight() - charHeight);

			lGraphics.drawImage(charImage, x, 0, null);

			x += lNewGlyph.width;
			mGlyphs.put(c, lNewGlyph);

		}

		// Save test out
		final int[] lPixels = new int[imageWidth * imageHeight];
		lFontImage.getRGB(0, 0, imageWidth, imageHeight, lPixels, 0, imageWidth);

		// Create a new font texture and add it to our EntityGroup
		mFontTexture = pResourceManager.textureManager().createFontTexture(mFontName, lFontImage, GL11.GL_LINEAR, LintfordCore.CORE_ENTITY_GROUP_ID);
		
		if(SAVE_BITMAP_TEXTURES_ON_CREATION) {
			final var lDebugFilename = AppStorage.getGameDataDirectory() + AppStorage.FILE_SEPERATOR + mFontName + mPointSize + ".png";

			try {
				ImageIO.write(lFontImage, "png", new File(lDebugFilename));
			} catch (IOException e) {
			}
			
		}

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		mFontTexture = null;
		mIsLoaded = false;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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
		int charWidth = lFontMetrics.charWidth((int) c);
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
		return getStringHeight(pText, 1f, -1) * pScaleFactor;
	}

	public float getStringHeight(String pText, float pScale, float pWordWrapWidth) {
		if (!mIsLoaded)
			return 0.f;

		if (pText == null || pText.length() == 0)
			return 0.f;

		float lResult = mFontHeight * pScale;
		float lWrapWidth = 0;

		for (int i = 0; i < pText.length(); i++) {
			char ch = pText.charAt(i);

			if (ch == '\n') {
				// Line feed
				lResult += mFontHeight * pScale;
				continue;
			}
			if (ch == '\r') {
				// Carriage return
				lResult += mFontHeight * pScale;
				continue;
			}

			// word wrapping works on words
			if (pWordWrapWidth != NO_WORD_WRAP) {
				if (ch == ' ') {
					for (int j = i + 1; j < pText.length(); j++) {
						char ch_m = pText.charAt(j);

						Glyph lCharGlyph = glyphs().get(ch_m);

						if (lCharGlyph == null)
							continue;
						lWrapWidth += lCharGlyph.width * pScale;

						if (ch_m == ' ') {
							break;
						}

						if (lWrapWidth >= pWordWrapWidth) {
							lResult += mFontHeight * pScale + 5f;
							lWrapWidth = 0;
						}
					}
				}
			}

		}

		return lResult;
	}

}