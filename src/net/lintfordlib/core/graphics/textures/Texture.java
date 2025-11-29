package net.lintfordlib.core.graphics.textures;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

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
	 * Some textures, like textures generated from system fonts, do not need to be reloaded when checking for 
	 * changes to textures on the harddisk. Setting this Boolean to false will skip the texture reload requests on this texture.
	 */
	private boolean mReloadable;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entityUid() {
		return mEntityUId;
	}

	public int[] ARGBColorData() {
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

	static Texture loadTextureFromFile(String textureName, String filePath, int filter, int wrapModeS, int wrapModeT) {
		if (filePath == null || filePath.length() == 0) {
			return null;
		}

		final var textureFile = new File(filePath);
		if (!textureFile.exists()) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "File not found: " + filePath);
			return null;
		}

		Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Loading texture from file: " + filePath);

		final var fileSize = textureFile.length();

		try (final var stack = MemoryStack.stackPush()) {

			final var w = stack.mallocInt(1);
			final var h = stack.mallocInt(1);
			final var comp = stack.mallocInt(1);

			ByteBuffer imageData = null;
			try {

				STBImage.stbi_set_flip_vertically_on_load(true);

				imageData = STBImage.stbi_load(filePath, w, h, comp, 4);

				if (imageData == null) {
					Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Failed to load texture: " + STBImage.stbi_failure_reason());
					return null;
				}

				int width = w.get(0);
				int height = h.get(0);

				final int texID = GL11.glGenTextures();

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapModeS);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapModeT);

				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				final var newTexture = new Texture(textureName, texID, filePath, width, height, filter);

				newTexture.mARGBColorData = byteBufferToIntArray(imageData, width, height);

				newTexture.mTextureFilterMode = filter;
				newTexture.mWrapModeS = wrapModeS;
				newTexture.mWrapModeT = wrapModeT;

				if (newTexture != null) {
					newTexture.fileSizeOnLoad(fileSize);
					newTexture.reloadable(true);
					Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Loaded texture from file: " + filePath);
				}

				return newTexture;

			} finally {
				if (imageData != null) {
					STBImage.stbi_image_free(imageData);
				}
			}

		} catch (Exception e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + filePath + ")");
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), e.getMessage());
		}

		return null;
	}

	static Texture loadTextureFromResource(String textureName, String filename, int filter) {
		if (filename == null || filename.length() == 0) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Couldn't load texture from resource. Filename is invalid!");
			return null;
		}

		final var inputStream = Texture.class.getResourceAsStream(filename);
		if (inputStream == null) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Couldn't open InputStream: " + filename);
			return null;
		}

		ByteBuffer imageBuffer = null;
		try (MemoryStack stack = MemoryStack.stackPush()) {

			final var w = stack.mallocInt(1);
			final var h = stack.mallocInt(1);
			final var comp = stack.mallocInt(1);

			imageBuffer = readInputStreamToBuffer(inputStream);

			if (imageBuffer == null) {
				Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Failed to read resource stream: " + filename);
				return null;
			}

			ByteBuffer imageData = null;
			try {

				STBImage.stbi_set_flip_vertically_on_load(true);

				imageData = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 4);

				if (imageData == null) {
					Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Failed to load texture from resource: " + STBImage.stbi_failure_reason());
					return null;
				}

				int width = w.get(0);
				int height = h.get(0);

				final int texID = GL11.glGenTextures();

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				final var newTexture = new Texture(textureName, texID, filename, width, height, filter);

				//newTexture.mARGBColorData = byteBufferToIntArray(imageData, width, height);
				//imageData.rewind();
				newTexture.mTextureFilterMode = filter;
				newTexture.mWrapModeS = GL11.GL_REPEAT;
				newTexture.mWrapModeT = GL11.GL_REPEAT;

				if (newTexture != null) {
					newTexture.reloadable(false);
					Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Loaded texture from resource: " + filename);
				}

				return newTexture;

			} finally {
				if (imageData != null) {
					STBImage.stbi_image_free(imageData);
				}
			}

		} catch (IOException e) {
			Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Failed to load image data: " + e.getMessage());
		} finally {
			if (imageBuffer != null) {
				MemoryUtil.memFree(imageBuffer);
			}
		}

		return null;

	}

	public void saveTextureToFile(String fileName) {
		final var width = mTextureWidth;
		final var height = mTextureHeight;

		final var colorRGB = new int[width * height];
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, colorRGB);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		final var convertedRGB = changeBGRAtoARGB(colorRGB, width, height);

		saveTextureToFile(width, height, convertedRGB, fileName);
	}

	public static boolean saveTextureToFile(int width, int height, int[] argbData, String fileLocation) {
		ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);

		try {
			// Convert ARGB to RGBA for STB
			for (int i = 0; i < width * height; i++) {
				int a = (argbData[i] & 0xff000000) >>> 24;
				int r = (argbData[i] & 0xff0000) >> 16;
				int g = (argbData[i] & 0xff00) >> 8;
				int b = (argbData[i] & 0xff);

				buffer.put((byte) r);
				buffer.put((byte) g);
				buffer.put((byte) b);
				buffer.put((byte) a);
			}

			buffer.flip();

			// Flip the image vertically for correct orientation
			ByteBuffer flippedBuffer = flipImageVertically(buffer, width, height, 4);

			boolean success = STBImageWrite.stbi_write_png(fileLocation, width, height, 4, flippedBuffer, width * 4);

			MemoryUtil.memFree(flippedBuffer);

			if (!success) {
				Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error saving png to disk : " + fileLocation);
				return false;
			}

			return true;

		} finally {
			MemoryUtil.memFree(buffer);
		}
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
	 * Creates an OpenGL {@link Texture} from pixel data.
	 */
	static Texture createTexture(String textureName, String textureLocation, int[] pixelsARGB, int width, int height, int filter) {
		return createTexture(textureName, textureLocation, pixelsARGB, width, height, filter, GL11.GL_REPEAT, GL11.GL_REPEAT);
	}

	/**
	 * Creates an OpenGL Texture from RGB data.
	 */
	static Texture createTexture(String textureName, String textureLocation, int[] pixelsARGB, int width, int height, int filter, int wrapModeS, int wrapModeT) {
		// Safety check
		if (pixelsARGB == null || pixelsARGB.length != width * height) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Invalid pixel data for texture: " + textureName);
			return null;
		}

		final int texID = GL11.glGenTextures();

		ByteBuffer colorBuffer = null;
		try {
			colorBuffer = MemoryUtil.memAlloc(pixelsARGB.length * 4);

			// Check if allocation succeeded
			if (colorBuffer == null) {
				Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Failed to allocate native memory for texture: " + textureName);
				return null;
			}

			// Verify buffer capacity
			if (colorBuffer.capacity() < pixelsARGB.length) {
				Debug.debugManager().logger().e(Texture.class.getSimpleName(), String.format("Buffer capacity mismatch: allocated %d, need %d", colorBuffer.capacity(), pixelsARGB.length));
				return null;
			}

			for (int i = 0; i < pixelsARGB.length; i++) {
				int a = (pixelsARGB[i] & 0xff000000) >>> 24;
				int r = (pixelsARGB[i] & 0xff0000) >> 16;
				int g = (pixelsARGB[i] & 0xff00) >> 8;
				int b = (pixelsARGB[i] & 0xff);

				colorBuffer.put((byte) r);
				colorBuffer.put((byte) g);
				colorBuffer.put((byte) b);
				colorBuffer.put((byte) a);
			}

			colorBuffer.flip();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapModeS);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapModeT);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, colorBuffer);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		} catch (Exception e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Exception during texture creation: " + e.getMessage());
			Debug.debugManager().logger().printException(Texture.class.getSimpleName(), e);
			return null;
		} finally {
			// Always free native memory
			if (colorBuffer != null) {
				MemoryUtil.memFree(colorBuffer);
			}
		}

		final var newTexture = new Texture(textureName, texID, textureLocation, width, height, filter);

		newTexture.mARGBColorData = pixelsARGB;
		newTexture.mTextureFilterMode = filter;
		newTexture.mWrapModeS = wrapModeS;
		newTexture.mWrapModeT = wrapModeT;

		return newTexture;
	}

	public static int getNewTextureEntityId() {
		return mTextureEntityId++;
	}

	public void reloadTexture(String textureFilename) {
		final var cleanFilename = FileUtils.cleanFilename(textureFilename);

		// TODO: Check about marking the resources (and other core textures) with !reloadable.
		if (textureFilename != null && textureFilename.startsWith("/")) {
			// cannot reload textures embedded in resources.
			return;
		}

		try {
			final var lTextureFile = new File(cleanFilename);
			final var lFileSize = lTextureFile.length();

			// Enable vertical flipping for OpenGL coordinate system
			STBImage.stbi_set_flip_vertically_on_load(true);

			try (MemoryStack stack = MemoryStack.stackPush()) {
				final var w = stack.mallocInt(1);
				final var h = stack.mallocInt(1);
				final var comp = stack.mallocInt(1);

				final var imageData = STBImage.stbi_load(cleanFilename, w, h, comp, 4);

				if (imageData == null) {
					Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Failed to reload texture: " + STBImage.stbi_failure_reason());
					return;
				}

				final var lWidth = w.get(0);
				final var lHeight = h.get(0);

				// Convert ByteBuffer (RGBA) to int[] (ARGB)
				final var lPixels = byteBufferToIntArray(imageData, lWidth, lHeight);

				STBImage.stbi_image_free(imageData);

				updateGLTextureData(lPixels, lWidth, lHeight);

				mTextureWidth = lWidth;
				mTextureHeight = lHeight;

				mTextureLocation = cleanFilename;
				fileSizeOnLoad(lFileSize);
				reloadable(true);

				Debug.debugManager().logger().i(getClass().getSimpleName(), "Reloaded texture: " + mTextureLocation);
			}
		} catch (Exception e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + textureFilename + ") from location: " + mTextureLocation);
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

	/**
	 * Converts STB's RGBA ByteBuffer to ARGB int array
	 */
	private static int[] byteBufferToIntArray(ByteBuffer buffer, int width, int height) {
		int pixelCount = width * height;
		int expectedBytes = pixelCount * 4;

		// Make sure position/limit are correct
		buffer.rewind();
		int available = buffer.remaining();
		if (available < expectedBytes) {
			throw new IllegalStateException("Image buffer underflow: available=" + available + ", expected=" + expectedBytes);
		}

		byte[] raw = new byte[expectedBytes];
		buffer.get(raw, 0, expectedBytes); // copies exactly expectedBytes into Java heap

		int[] pixels = new int[pixelCount];
		int idx = 0;
		for (int i = 0; i < pixelCount; i++) {
			int r = raw[idx++] & 0xFF;
			int g = raw[idx++] & 0xFF;
			int b = raw[idx++] & 0xFF;
			int a = raw[idx++] & 0xFF;
			pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
		}

		buffer.rewind();
		return pixels;
	}

	/**
	 * Reads an InputStream into a native ByteBuffer for STB
	 */
	private static ByteBuffer readInputStreamToBuffer(InputStream inputStream) throws IOException {
		byte[] bytes = inputStream.readAllBytes();
		ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		return buffer;
	}

	/**
	 * Flips an image vertically in a ByteBuffer
	 */
	private static ByteBuffer flipImageVertically(ByteBuffer image, int width, int height, int channels) {
		ByteBuffer flipped = MemoryUtil.memAlloc(image.capacity());
		int stride = width * channels;

		for (int y = 0; y < height; y++) {
			int srcPos = (height - 1 - y) * stride;
			int dstPos = y * stride;

			for (int x = 0; x < stride; x++) {
				flipped.put(dstPos + x, image.get(srcPos + x));
			}
		}

		return flipped;
	}

	public static int[] changeARGBtoABGR(int[] input, int width, int height) {
		int[] lReturnData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (input[i] & 0xff000000) >>> 24;
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
}
