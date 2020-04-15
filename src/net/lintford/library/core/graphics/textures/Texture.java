package net.lintford.library.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.lintford.library.core.debug.Debug;

public class Texture {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mName;
	private int mTextureId;
	public final int entityId;
	private String mTextureLocation;
	private int mTextureWidth;
	private int mTextureHeight;
	private int mFilter;
	private int[] mColorData;

	/**
	 * In order to detect changes to the texture when trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	private long mFileSizeOnLoad;

	/**
	 * Some textures, like textures generated from system fonts, do not need to be reloaded when checking for changes to textures on the harddisk. Setting this Boolean to false will
	 * skip the texture reload requests on this texture.
	 */
	private boolean mReloadable;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int[] RGBColorData() {
		return mColorData;
	}

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
		return mTextureId;
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

	private Texture(String pName, int pTextureId, String pFilename, int pWidth, int pHeight, int pFilter) {
		mName = pName;
		entityId = getNewTextureEntityId();
		mTextureId = pTextureId;
		mTextureLocation = pFilename;
		mTextureWidth = pWidth;
		mTextureHeight = pHeight;
		mFilter = pFilter;
		mReloadable = true;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// package access (textures should be loaded using the texture manager.
	static Texture loadTextureFromFile(String pName, String pFilename, int pFilter) {
		if (pFilename == null || pFilename.length() == 0) {
			return null;

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

			Debug.debugManager().logger().i(Texture.class.getSimpleName(), "Loaded texture from file: " + pFilename);

			return lNewTexture;

		} catch (IIOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + pFilename + ")");
			return null;

		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + pFilename + ")");
			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + pFilename + ")");
			return null;

		}

	}

	static Texture loadTextureFromResource(String pName, String pFilename, int pFilter) {
		if (pFilename == null || pFilename.length() == 0) {
			return null;

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

			Debug.debugManager().logger().i(Texture.class.getSimpleName(), "Loaded texture from resource: " + pFilename);

			return lNewTexture;

		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from resource (" + pFilename + " )");
			Debug.debugManager().logger().printException(Texture.class.getSimpleName(), e);

			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from resource (" + pFilename + " )");
			Debug.debugManager().logger().printException(Texture.class.getSimpleName(), e);

			return null;
		}

	}

	public void saveTextureToFile(String pPathname) {
		int lWidth = mTextureWidth;
		int lHeight = mTextureHeight;

		int[] colorRGB = new int[lWidth * lHeight];
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, colorRGB);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		int[] convertedRGB = changeBGRAtoARGB(colorRGB, lWidth, lHeight);

		// needs ARGB
		saveTextureToFile(lWidth, lHeight, convertedRGB, pPathname);

	}

	public static boolean saveTextureToFile(int pWidth, int pHeight, int[] pData, String pFileLocation) {
		BufferedImage lImage = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_ARGB);

		// Convert our ARGB to output ABGR
		int[] lTextureData = new int[pWidth * pHeight];
		for (int i = 0; i < pWidth * pHeight; i++) {
			int a = (pData[i] & 0xff000000) >> 24;
			int r = (pData[i] & 0xff0000) >> 16;
			int g = (pData[i] & 0xff00) >> 8;
			int b = (pData[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		lImage.setRGB(0, 0, pWidth, pHeight, lTextureData, 0, pWidth);

		File outputfile = new File(pFileLocation);
		try {
			ImageIO.write(lImage, "png", outputfile);
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}

		return true;

	}

	static void unloadTexture(Texture pTexture) {
		if (pTexture == null)
			return;

		GL11.glDeleteTextures(pTexture.mTextureId);
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

		return createTexture(pName, pTextureLocation, changeARGBtoABGR(lPixels, lWidth, lHeight), lWidth, lHeight, pFilter);
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
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, pWidth, pHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		lBuffer = null;

		final var lNewTexture = new Texture(pName, lTexID, mTextureLocation, pWidth, pHeight, pFilter);
		lNewTexture.mColorData = pPixels;
		return lNewTexture;
	}

	private static int mTextureEntityId = 0;

	public static int getNewTextureEntityId() {
		return mTextureEntityId++;
	}

	public void reloadTexture(String pFilename) {
		// If a new filename is explicitly passed in, then ignore the reloadable flag
		String lCleanFilename = cleanFilename(pFilename);

		BufferedImage lImage = null;
		long lFileSize = 0;

		try {
			// Load the file from the path, ignoring whitespace, tabs and new lines from the path string.
			File lTextureFile = new File(lCleanFilename);
			lFileSize = lTextureFile.length();

			lImage = ImageIO.read(lTextureFile);

			final int lWidth = lImage.getWidth();
			final int lHeight = lImage.getHeight();

			// Get the pixels from the buffered image
			final int[] lPixels = new int[lWidth * lHeight];
			lImage.getRGB(0, 0, lWidth, lHeight, lPixels, 0, lWidth);

			updateGLTextureData(changeARGBtoABGR(lPixels, lWidth, lHeight), lWidth, lHeight);

			mTextureWidth = lWidth;
			mTextureHeight = lHeight;

			mTextureLocation = lCleanFilename;
			fileSizeOnLoad(lFileSize);
			reloadable(true);

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Reloaded texture: " + mTextureLocation);

			return;

		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + pFilename + ")");
			Debug.debugManager().logger().printException(Texture.class.getSimpleName(), e);

			return;

		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + pFilename + ")");
			Debug.debugManager().logger().printException(Texture.class.getSimpleName(), e);

			return;

		}

	}

	public void reload() {
		if (!mReloadable) {
			return;

		}

		reloadTexture(mTextureLocation);

	}

	void updateGLTextureData(int[] pColorData, int pWidth, int pHeight) {
		if (pColorData.length != pWidth * pHeight)
			return;

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mFilter);

		IntBuffer lBuffer = ByteBuffer.allocateDirect(pColorData.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		lBuffer.put(pColorData);
		lBuffer.flip();

		mColorData = pColorData;

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, pWidth, pHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

	}

	static int[] changeARGBtoABGR(int[] pInput, int pWidth, int pHeight) {
		int[] lReturnData = new int[pWidth * pHeight];
		for (int i = 0; i < pWidth * pHeight; i++) {
			int a = (pInput[i] & 0xff000000) >> 24;
			int r = (pInput[i] & 0xff0000) >> 16;
			int g = (pInput[i] & 0xff00) >> 8;
			int b = (pInput[i] & 0xff);

			lReturnData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		return lReturnData;
	}

	static int[] changeABGRtoARGB(int[] pInput, int pWidth, int pHeight) {
		int[] lReturnData = new int[pWidth * pHeight];
		for (int i = 0; i < pWidth * pHeight; i++) {
			int a = (pInput[i] & 0xff000000) >> 24;
			int b = (pInput[i] & 0xff0000) >> 16;
			int g = (pInput[i] & 0xff00) >> 8;
			int r = (pInput[i] & 0xff);

			lReturnData[i] = a << 24 | r << 16 | g << 8 | b;
		}

		return lReturnData;
	}

	static int[] changeBGRAtoARGB(int[] pInput, int pWidth, int pHeight) {
		int[] lReturnData = new int[pWidth * pHeight];
		for (int i = 0; i < pWidth * pHeight; i++) {
			int b = (pInput[i] & 0xff000000) >> 24;
			int g = (pInput[i] & 0xff0000) >> 16;
			int r = (pInput[i] & 0xff00) >> 8;
			int a = (pInput[i] & 0xff);

			lReturnData[i] = a << 24 | r << 16 | g << 8 | b;
		}

		return lReturnData;
	}

	static int[] changeRGBtoARGB(int[] pInput, int pWidth, int pHeight) {
		int[] lReturnData = new int[pWidth * pHeight];
		for (int i = 0; i < pWidth * pHeight; i++) {
			int b = (pInput[i] & 0xff000000) >> 24;
			int g = (pInput[i] & 0xff0000) >> 16;
			int r = (pInput[i] & 0xff00) >> 8;
			int a = (pInput[i] & 0xff);

			lReturnData[i] = a << 24 | r << 16 | g << 8 | b;
		}

		return lReturnData;
	}

}
