package net.lintford.library.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.debug.DebugManager;

public class Texture {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mName;
	private final int mTextureID;
	private final String mTextureLocation;
	private int mTextureWidth;
	private int mTextureHeight;
	private int mFilter;

	/**
	 * In order to detect changes to the texture wher trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	private long mFileSizeOnLoad;

	/**
	 * Some textures, like textures generated from system fonts, do not need to be reloaded when checking for changes to textures on the harddisk. Setting this Boolean to false will skip the texture reload requests on this texture.
	 */
	private boolean mReloadable;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mName;
	}

	public boolean reloadable() {
		return mReloadable;
	}

	public void reloadable(boolean v) {
		mReloadable = v;
	}

	public long fileSizeOnLoad() {
		return mFileSizeOnLoad;
	}

	public void fileSizeOnLoad(long v) {
		mFileSizeOnLoad = v;
	}

	public String textureLocation() {
		return mTextureLocation;
	}

	public int getTextureID() {
		return mTextureID;
	}

	public int getTextureWidth() {
		return mTextureWidth;
	}

	public int getTextureHeight() {
		return mTextureHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Texture(String pName, int pTextureID, String pFilename, int pWidth, int pHeight, int pFilter) {
		mName = pName;
		mTextureID = pTextureID;
		mTextureLocation = pFilename;
		mTextureWidth = pWidth;
		mTextureHeight = pHeight;
		mFilter = pFilter;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// package access (textures should be loaded using the texture manager.
	static Texture loadTextureFromFile(String pName, String pFilename, int pFilter) {
		if (pFilename == null || pFilename.length() == 0) {
			return TextureManager.TEXTURE_NOT_FOUND;

		}

		String lCleanFilename = cleanFilename(pFilename);

		BufferedImage lImage = null;
		long lFileSize = 0;

		// 1. load the image
		try {
			// Load the file from the path, ignoring whitespace, tabs and new lines from the path string.
			File lTextureFile = new File(lCleanFilename);
			lFileSize = lTextureFile.length();

			lImage = ImageIO.read(lTextureFile);

			Texture lNewTexture = createTexture(pName, pFilename, lImage, pFilter);

			lNewTexture.fileSizeOnLoad(lFileSize);
			lNewTexture.reloadable(true);

			DebugManager.DEBUG_MANAGER.logger().i(Texture.class.getSimpleName(), "Loaded texture from file: " + pFilename);

			return lNewTexture;

		} catch (FileNotFoundException e) {
			DebugManager.DEBUG_MANAGER.logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + pFilename + ")");

			return TextureManager.TEXTURE_NOT_FOUND;

		} catch (IOException e) {
			System.err.println("Error loading texture from " + pFilename);
			System.err.println(e.getMessage());

			return null;
		}

	}

	static Texture loadTextureFromResource(String pName, String pFilename, int pFilter) {
		if (pFilename == null || pFilename.length() == 0) {
			return TextureManager.TEXTURE_NOT_FOUND;

		}

		BufferedImage lImage = null;
		long lFileSize = 0;

		// 1. load the image
		try {
			// Load the file from the path, ignoring whitespace, tabs and new lines from the path string.
			InputStream lInputStream = Texture.class.getResourceAsStream(pFilename);
			if (lInputStream == null) {
				throw new FileNotFoundException();
			}

			// Resource paths don't allow double slashes
			pFilename = pFilename.replace("//", "/");

			lImage = ImageIO.read(lInputStream);

			Texture lNewTexture = createTexture(pName, pFilename, lImage, pFilter);

			lNewTexture.fileSizeOnLoad(lFileSize);

			// Don't attempt to reload textures loaded from the embedded resources
			lNewTexture.reloadable(false);

			DebugManager.DEBUG_MANAGER.logger().i(Texture.class.getSimpleName(), "Loaded texture from resource: " + pFilename);

			return lNewTexture;

		} catch (FileNotFoundException e) {
			DebugManager.DEBUG_MANAGER.logger().e(Texture.class.getSimpleName(), "Error loading texture from resource (" + pFilename + " )");

			return TextureManager.TEXTURE_NOT_FOUND;

		} catch (IOException e) {
			System.err.println("Error loading texture from " + pFilename);
			System.err.println(e.getMessage());

			return null;
		}

	}

	public void saveTextureToFile(String pPathname) {
		int pWidth = mTextureWidth;
		int pHeight = mTextureHeight;

		int[] colorRGB = new int[pWidth * pHeight];
		GL11.glGetTexImage(mTextureID, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, colorRGB);

		TextureManager.textureManager().saveTextureToFile(pWidth, pHeight, colorRGB, pPathname);

	}

	static void unloadTexture(Texture pTexture) {
		if (pTexture == null)
			return;

		GL11.glDeleteTextures(pTexture.mTextureID);
		pTexture = null;

	}

	private static String cleanFilename(String pFilename) {
		return pFilename.replaceAll("\\s+", "");
	}

	/**
	 * Creates an OpenGL {@link Texture} from a {@link BufferedImage}.
	 */
	static Texture createTexture(String pName, String pTextureLocation, BufferedImage pImage, int pFilter) {
		final int lWidth = pImage.getWidth();
		final int lHeight = pImage.getHeight();

		// Get the pixels from the buffered image
		final int[] lPixels = new int[lWidth * lHeight];
		pImage.getRGB(0, 0, lWidth, lHeight, lPixels, 0, lWidth);

		// 2. change channel order
		int[] lTextureData = new int[lWidth * lHeight];
		for (int i = 0; i < lWidth * lHeight; i++) {
			int a = (lPixels[i] & 0xff000000) >> 24;
			int r = (lPixels[i] & 0xff0000) >> 16;
			int g = (lPixels[i] & 0xff00) >> 8;
			int b = (lPixels[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		return createTexture(pName, pTextureLocation, lTextureData, lWidth, lHeight, pFilter);
	}

	/**
	 * Creates an OpenGL Texture from RGB data.
	 */
	static Texture createTexture(String pName, String mTextureLocation, int[] pPixels, int pWidth, int pHeight, int pFilter) {
		final int lTexID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, lTexID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, pFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, pFilter);

		IntBuffer lBuffer = ByteBuffer.allocateDirect(pPixels.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		lBuffer.put(pPixels);
		lBuffer.flip();

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, pWidth, pHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return new Texture(pName, lTexID, mTextureLocation, pWidth, pHeight, pFilter);
	}

	public void reload(int[] pColorData, int pWidth, int pHeight) {
		if (mTextureWidth != pWidth || mTextureHeight != pHeight)
			return;
		if (pColorData.length != pWidth * pHeight)
			return;

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mFilter);

		IntBuffer lBuffer = ByteBuffer.allocateDirect(pColorData.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		lBuffer.put(pColorData);
		lBuffer.flip();

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, pWidth, pHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

	}

	public void reload() {
		if (!mReloadable) {
			return;

		}

		BufferedImage lImage = null;
		int[] lPixels = null;
		int lTexWidth = 0;
		int lTexHeight = 0;

		// 1. load the image
		try {
			File lTextureFile = new File(mTextureLocation);

			// Check the file size to see if the texture has changed
			long lLatestSize = lTextureFile.length();
			if (lLatestSize == mFileSizeOnLoad) {
				return;

			}

			mFileSizeOnLoad = lLatestSize;

			lImage = ImageIO.read(lTextureFile);

			lTexWidth = lImage.getWidth();
			lTexHeight = lImage.getHeight();

			lPixels = new int[lTexWidth * lTexHeight];
			lImage.getRGB(0, 0, lTexWidth, lTexHeight, lPixels, 0, lTexWidth);

		} catch (FileNotFoundException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Error reloading texture (File not found at " + mTextureLocation + " )");

			return;

		} catch (IOException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Error reloading texture from " + mTextureLocation);

			return;
		}

		// 2. change channel order
		int[] lTextureData = new int[lTexWidth * lTexHeight];
		for (int i = 0; i < lTexWidth * lTexHeight; i++) {
			int a = (lPixels[i] & 0xff000000) >> 24;
			int r = (lPixels[i] & 0xff0000) >> 16;
			int g = (lPixels[i] & 0xff00) >> 8;
			int b = (lPixels[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		// 3. Create OpenGL texture and return ID
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mFilter);

		IntBuffer lBuffer = ByteBuffer.allocateDirect(lTextureData.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		lBuffer.put(lTextureData);
		lBuffer.flip();

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, lTexWidth, lTexHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
			DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Reloaded texture: " + mTextureLocation);

		}

	}

}