package net.lintfordlib.core.graphics.rendertarget;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.storage.FileUtils;

public class RenderTarget {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mTargetName;
	private int mColorTextureID;
	private int mDepthTextureID;
	private int mFramebufferID;
	private int mTextureFilter;
	private int mTextureWrapModeS;
	private int mTextureWrapModeT;
	private boolean mDepthBufferEnabled;
	private boolean mResourcesLoaded;

	private int mWidth;
	private int mHeight;
	private float mScale;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String targetName() {
		return mTargetName;
	}

	/**
	 * Sets the texture filter mode for mag. and min. (default: GL11.GL_LINEAR).
	 * 
	 * @param newValue The GL11 filter mode
	 */
	public void textureFilter(int newValue) {
		mTextureFilter = newValue;

		if (mResourcesLoaded) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mTextureFilter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mTextureFilter);
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		}
	}

	public int textureFilter() {
		return mTextureFilter;
	}

	public void textureWrapModeS(int newValue) {
		mTextureWrapModeS = newValue;

		if (mResourcesLoaded) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, mTextureWrapModeS);
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		}
	}

	public int textureWrapModeS() {
		return mTextureWrapModeS;
	}

	public void textureWrapModeT(int newValue) {
		mTextureWrapModeT = newValue;

		if (mResourcesLoaded) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, mTextureWrapModeT);
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		}
	}

	public int textureWrapModeT() {
		return mTextureWrapModeT;
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	public float scale() {
		return mScale;
	}

	public int colorTextureID() {
		return mColorTextureID;
	}

	public int depthTextureID() {
		return mDepthTextureID;
	}

	public int frameBufferID() {
		return mFramebufferID;
	}

	public boolean isLoaded() {
		return mResourcesLoaded;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RenderTarget(String renderTargetName) {
		mTargetName = renderTargetName;
		mTextureFilter = GL11.GL_LINEAR;
		mTextureWrapModeS = GL12.GL_CLAMP_TO_EDGE;
		mTextureWrapModeT = GL12.GL_CLAMP_TO_EDGE;

		mFramebufferID = -1;
		mColorTextureID = -1;

		mDepthBufferEnabled = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadResources(int width, int height, float scale) {
		if (width == 0 || height == 0)
			return;

		mScale = 1.f;
		mWidth = width;
		mHeight = height;

		final var lIntBuffer = MemoryUtil.memAllocInt(mWidth * mHeight * 4);

		initializeGl(lIntBuffer);
	}

	public void loadResourcesFromImage(String fileName) {
		var lBufferedImage = loadBufferedImage(fileName);

		if(lBufferedImage == null)
			return;
		
		mScale = 1.f;
		mWidth = lBufferedImage.getWidth();
		mHeight = lBufferedImage.getHeight();

		final var lPixelsARGB = lBufferedImage.getRGB(0, 0, mWidth, mHeight, null, 0, mWidth);

		final var lIntBuffer = MemoryUtil.memAllocInt(lPixelsARGB.length);
		lIntBuffer.put(lPixelsARGB);
		lIntBuffer.flip();

		initializeGl(lIntBuffer);

		MemoryUtil.memFree(lIntBuffer);
	}

	private void initializeGl(IntBuffer buffer) {

		if (mResourcesLoaded)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading RenderTarget: " + mTargetName);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "  GL texture filter mode enum: " + mTextureFilter);

		mFramebufferID = GL30.glGenFramebuffers(); // gen container for texture and optional depth buffer
		mColorTextureID = GL11.glGenTextures(); // gen texture to hold RGB data

		// Create and bind framebuffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);

		// Create and bind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mColorTextureID);

		// Create an empty texture
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, mWidth, mHeight, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);

		// Set the texture filtering mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mTextureFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mTextureFilter);

		// Set the texture wrap mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Configure the frame buffer
		GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, mColorTextureID, 0);

		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		// Depth buffer
		mDepthBufferEnabled = true;
		if (mDepthBufferEnabled) {
			mDepthTextureID = GL30.glGenRenderbuffers();
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, mDepthTextureID);
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, mWidth, mHeight);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, mDepthTextureID);
		}

		int lCreationStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (lCreationStatus != GL30.GL_FRAMEBUFFER_COMPLETE) {
			switch (lCreationStatus) {
			case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");

			case GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE:
				throw new RuntimeException("GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");

			case GL30.GL_FRAMEBUFFER_UNSUPPORTED:
				throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");

			case GL30.GL_FRAMEBUFFER_UNDEFINED:
				throw new RuntimeException("GL_FRAMEBUFFER_UNDEFINED");
			}
		}

		// unbind
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_RENDERTEXTURES);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		GL30.glDeleteFramebuffers(mFramebufferID);
		mFramebufferID = -1;

		GL11.glDeleteTextures(mColorTextureID);
		mColorTextureID = -1;

		if (mDepthBufferEnabled) {
			GL30.glDeleteFramebuffers(mDepthTextureID);
			mDepthTextureID = -1;
		}

		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_RENDERTEXTURES);

		mResourcesLoaded = false;
	}

	public void bind() {
		GL11.glViewport(0, 0, mWidth, mHeight);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);
	}

	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void resize(int newWidth, int newHeight) {
		if (newWidth == 0 || newHeight == 0)
			return;

		if (!mResourcesLoaded)
			return;

		mWidth = newWidth;
		mHeight = newHeight;

		final var lIntDataBuffer = createIntBuffer(mWidth, mHeight);

		if (ConstantsApp.getBooleanValueDef("DEBUG_RENDER_TARGET_RESIZE", false)) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading RenderTarget: " + mTargetName);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "  GL_TEXTURE_MAG_FILTER: " + mTextureFilter);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "  GL_TEXTURE_MIN_FILTER: " + mTextureFilter);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "  GL_TEXTURE_WRAP_S: " + GL12.GL_CLAMP_TO_EDGE);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "  GL_TEXTURE_WRAP_T: " + GL12.GL_CLAMP_TO_EDGE);
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mColorTextureID);

		// Set the texture filtering mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mTextureFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mTextureFilter);

		// Set the texture wrap mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Create an empty texture
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, mWidth, mHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lIntDataBuffer);

		if (mDepthBufferEnabled) {
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, mDepthTextureID);
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, mWidth, mHeight);
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		MemoryUtil.memFree(lIntDataBuffer);

	}

	private IntBuffer createIntBuffer(int width, int height) {
		final int lNewSize = mWidth * mHeight * 4;

		final var lIntBuffer = MemoryUtil.memAllocInt(lNewSize);
		lIntBuffer.flip();

		return lIntBuffer;
	}

	private BufferedImage loadBufferedImage(String filePath) {
		final var lBufferedImage = loadImageFromFile(filePath);
		if (lBufferedImage == null) {
			return null;
		}

		return lBufferedImage;

	}

	private BufferedImage loadImageFromFile(String filename) {
		final var lCleanFilename = FileUtils.cleanFilename(filename);

		try {
			final var lImageFile = new File(lCleanFilename);

			if (lImageFile.exists() == false) {
				Debug.debugManager().logger().e(Texture.class.getSimpleName(), "FileNotFoundException: Error loading texture from file (" + filename + "). File doesn't exist.");
				return null;
			}

			final var lImage = ImageIO.read(lImageFile);

			Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Loaded texture from file: " + filename);

			return lImage;

		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "FileNotFoundException: Error loading texture from file (" + filename + ").");
		} catch (IIOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "IIOException: Error loading texture from file (" + filename + ").");
		} catch (IOException e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "IOException: Error loading texture from file (" + filename + ").");
		}

		return null;
	}

	public void saveTextureToFile(String pPathname) {
		int lWidth = mWidth;
		int lHeight = mHeight;

		final var lColorARGB = new int[lWidth * lHeight];
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mColorTextureID);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lColorARGB);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		saveTextureToPngFile(lWidth, lHeight, lColorARGB, pPathname);
	}

	public static boolean saveTextureToPngFile(int width, int height, int[] argbData, String fileLocation) {
		final var lImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		int[] lTextureData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (argbData[i] & 0xff000000) >> 24;
			int r = (argbData[i] & 0xff0000) >> 16;
			int g = (argbData[i] & 0xff00) >> 8;
			int b = (argbData[i] & 0xff);

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
}