package net.lintfordlib.core.graphics.textures;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public class Texture {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static int mTextureEntityId = 0;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mName;
	private int mTextureId;
	private final int mEntityUId;
	private String mTextureLocation;
	private int mTextureWidth;
	private int mTextureHeight;
	private int mTextureFilterMode;
	private int mWrapModeS;
	private int mWrapModeT;
	private int[] mARGBColorData;

	/**
	 * In order to detect changes to the texture when trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	private long mFileSizeOnLoad;

	/**
	 * Some textures, like textures generated from system fonts, do not need to be reloaded when checking for changes to textures on the harddisk. Setting this Boolean to false will skip the texture reload requests on this texture.
	 */
	private boolean mReloadable;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entityUid() {
		return mEntityUId;
	}

	public int[] RGBColorData() {
		return mARGBColorData;
	}

	public String name() {
		return mName;
	}

	public boolean reloadable() {
		return mReloadable;
	}

	public void reloadable(boolean isReloadable) {
		mReloadable = isReloadable;
	}

	public long fileSizeOnLoad() {
		return mFileSizeOnLoad;
	}

	public void fileSizeOnLoad(long filesize) {
		mFileSizeOnLoad = filesize;
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

	private Texture(String textureName, int textureId, String filename, int width, int height, int filter) {
		mName = textureName;
		mEntityUId = getNewTextureEntityId();
		mTextureId = textureId;
		mTextureLocation = filename;
		mTextureWidth = width;
		mTextureHeight = height;
		mTextureFilterMode = filter;
		mReloadable = true;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// package access (textures should be loaded using the texture manager.
	static Texture loadTextureFromFile(String ptextureName, String filename, int filter, int wrapModeS, int wrapModeT) {
		if (filename == null || filename.length() == 0) {
			return null;
		}

		final var lCleanFilename = FileUtils.cleanFilename(filename);

		try {
			final var lWorkspaceFile = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);

			// NOTE: loading a texture from an absolue path that is not within the games subdirectories (res/) will not work.
			// is this an issue?
			File textureFile;
			if (lCleanFilename.startsWith(lWorkspaceFile)) {
				textureFile = new File(lCleanFilename);
			} else {
				textureFile = new File(lWorkspaceFile, lCleanFilename);
			}

			final var lFileSize = textureFile.length();
			final var lImage = createFlipped(ImageIO.read(textureFile));

			final var lNewTexture = createTexture(ptextureName, filename, lImage, filter, wrapModeS, wrapModeT);
			lNewTexture.fileSizeOnLoad(lFileSize);
			lNewTexture.reloadable(true);

			Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Loaded texture from file: " + filename);

			return lNewTexture;

		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "FileNotFoundException: Error loading texture from file (" + filename + ")");
		} catch (IIOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "IIOException: Error loading texture from file (" + filename + ")");
		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "IOException: Error loading texture from file (" + filename + ")");
		}

		return null;
	}

	static Texture loadTextureFromResource(String textureName, String filename, int filter) {
		if (filename == null || filename.length() == 0) {
			return null;
		}

		try {
			InputStream lInputStream = Texture.class.getResourceAsStream(filename);
			if (lInputStream == null) {
				Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Couldn't open InputStream: " + filename);
				return null;
			}

			filename = filename.replace("//", "/");

			final var lImage = createFlipped(ImageIO.read(lInputStream));

			final var lNewTexture = createTexture(textureName, filename, lImage, filter);
			lNewTexture.fileSizeOnLoad(0);
			lNewTexture.reloadable(false);

			Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Loaded texture from resource: " + filename);

			return lNewTexture;
		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from resource (" + filename + " )");
			Debug.debugManager().logger().printException(Texture.class.getSimpleName(), e);
		}

		return null;
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

	public static boolean saveTextureToFile(int width, int height, int[] pData, String fileLocation) {
		final var lImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		int[] lTextureData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (pData[i] & 0xff000000) >> 24;
			int r = (pData[i] & 0xff0000) >> 16;
			int g = (pData[i] & 0xff00) >> 8;
			int b = (pData[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		lImage.setRGB(0, 0, width, height, lTextureData, 0, width);

		try {
			ImageIO.write(lImage, "png", new File(fileLocation));
		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error saving png to disk : " + fileLocation);
			return false;
		}

		return true;
	}

	static void unloadTexture(Texture texture) {
		if (texture == null)
			return;

		if (texture.name() != null && texture.name().equals(TextureManager.TEXTURE_NOT_FOUND_NAME))
			return;

		GL11.glDeleteTextures(texture.mTextureId);
		texture.mTextureId = -1;
		texture.mTextureLocation = null;
		texture.mTextureWidth = 0;
		texture.mTextureHeight = 0;
	}

	/**
	 * Creates an OpenGL {@link Texture} from a {@link BufferedImage}.
	 */
	static Texture createTexture(String textureName, String textureLocation, BufferedImage image, int filter) {
		return createTexture(textureName, textureLocation, image, filter, GL11.GL_REPEAT, GL11.GL_REPEAT);
	}

	/**
	 * Creates an OpenGL {@link Texture} from a {@link BufferedImage}.
	 */
	static Texture createTexture(String textureName, String textureLocation, BufferedImage image, int filter, int wrapModeS, int wrapModeT) {
		final int lWidth = image.getWidth();
		final int lHeight = image.getHeight();

		final var lPixelsARGB = image.getRGB(0, 0, lWidth, lHeight, null, 0, lWidth);

		return createTexture(textureName, textureLocation, lPixelsARGB, lWidth, lHeight, filter, wrapModeS, wrapModeT);
	}

	/**
	 * Creates an OpenGL Texture from RGB data.
	 */
	static Texture createTexture(String textureName, String textureLocation, int[] pixelsARGB, int width, int height, int filter, int wrapModeS, int wrapModeT) {
		final int lTexID = GL11.glGenTextures();

		var lIntBuffer = MemoryUtil.memAllocInt(pixelsARGB.length);
		lIntBuffer.put(pixelsARGB);
		lIntBuffer.flip();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, lTexID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapModeS);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapModeT);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, lIntBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		MemoryUtil.memFree(lIntBuffer);

		final var lNewTexture = new Texture(textureName, lTexID, textureLocation, width, height, filter);

		lNewTexture.mARGBColorData = pixelsARGB;
		lNewTexture.mTextureFilterMode = filter;
		lNewTexture.mWrapModeS = wrapModeS;
		lNewTexture.mWrapModeT = wrapModeT;

		return lNewTexture;
	}

	public static int getNewTextureEntityId() {
		return mTextureEntityId++;
	}

	public void reloadTexture(String textureFilename) {
		String lCleanFilename = FileUtils.cleanFilename(textureFilename);

		try {
			final var lTextureFile = new File(lCleanFilename);
			final var lFileSize = lTextureFile.length();

			final var lImage = ImageIO.read(lTextureFile);

			final int lWidth = lImage.getWidth();
			final int lHeight = lImage.getHeight();

			final int[] lPixels = new int[lWidth * lHeight];
			lImage.getRGB(0, 0, lWidth, lHeight, lPixels, 0, lWidth);

			updateGLTextureData(changeARGBtoABGR(lPixels, lWidth, lHeight), lWidth, lHeight);

			mTextureWidth = lWidth;
			mTextureHeight = lHeight;

			mTextureLocation = lCleanFilename;
			fileSizeOnLoad(lFileSize);
			reloadable(true);

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Reloaded texture: " + mTextureLocation);
		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + textureFilename + ")");
			Debug.debugManager().logger().printException(Texture.class.getSimpleName(), e);
		}
	}

	public void reload() {
		if (!mReloadable) {
			return;
		}

		reloadTexture(mTextureLocation);
	}

	void updateGLTextureData(int[] pixelsARGB, int width, int height) {
		if (pixelsARGB.length == 0 || pixelsARGB.length != width * height)
			return;

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mTextureFilterMode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mTextureFilterMode);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, mWrapModeS);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, mWrapModeT);

		final var lIntBuffer = MemoryUtil.memAllocInt(pixelsARGB.length);
		lIntBuffer.put(pixelsARGB);
		lIntBuffer.flip();

		mARGBColorData = pixelsARGB;
		mTextureWidth = width;
		mTextureHeight = height;

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, lIntBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		MemoryUtil.memFree(lIntBuffer);
	}

	// --------------------------------------
	// Helpers
	// --------------------------------------

	public static int[] changeARGBtoABGR(int[] input, int width, int height) {
		int[] lReturnData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (input[i] & 0xff000000) >> 24;
			int r = (input[i] & 0xff0000) >> 16;
			int g = (input[i] & 0xff00) >> 8;
			int b = (input[i] & 0xff);

			lReturnData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		return lReturnData;
	}

	public static int[] changeABGRtoARGB(int[] input, int width, int height) {
		int[] lReturnData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (input[i] & 0xff000000) >> 24;
			int b = (input[i] & 0xff0000) >> 16;
			int g = (input[i] & 0xff00) >> 8;
			int r = (input[i] & 0xff);

			lReturnData[i] = a << 24 | r << 16 | g << 8 | b;
		}

		return lReturnData;
	}

	public static int[] changeBGRAtoARGB(int[] input, int width, int height) {
		int[] lReturnData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int b = (input[i] & 0xff000000) >> 24;
			int g = (input[i] & 0xff0000) >> 16;
			int r = (input[i] & 0xff00) >> 8;
			int a = (input[i] & 0xff);

			lReturnData[i] = a << 24 | r << 16 | g << 8 | b;
		}

		return lReturnData;
	}

	public static int[] changeARGBAtoRGBA(int[] input, int width, int height) {
		int[] lReturnData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (input[i] & 0xff000000) >> 24;
			int r = (input[i] & 0xff0000) >> 16;
			int g = (input[i] & 0xff00) >> 8;
			int b = (input[i] & 0xff);

			lReturnData[i] = r << 24 | g << 16 | b << 8 | a;
		}

		return lReturnData;
	}

	public static int[] changeRGBAtoARGB(int[] input, int width, int height) {
		int[] lReturnData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int r = (input[i] & 0xff000000) >> 24;
			int g = (input[i] & 0xff0000) >> 16;
			int b = (input[i] & 0xff00) >> 8;
			int a = (input[i] & 0xff);

			lReturnData[i] = a << 24 | r << 16 | g << 8 | b;
		}

		return lReturnData;
	}

	private static BufferedImage createFlipped(BufferedImage image) {
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}

	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
		final var newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final var g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
}
