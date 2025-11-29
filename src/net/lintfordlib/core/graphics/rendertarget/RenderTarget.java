package net.lintfordlib.core.graphics.rendertarget;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.graphics.textures.Texture;

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
	private Integer mSharedDepthBufferId;

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

	public boolean isSharedDepthBuffer() {
		return mSharedDepthBufferId != null;
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
		mSharedDepthBufferId = null;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadResources(int width, int height, float scale) {
		if (width == 0 || height == 0)
			return;

		loadResources(width, height, scale, null);
	}

	public void loadResources(int width, int height, float scale, Integer sharedDepthBufferId) {
		if (width == 0 || height == 0)
			return;

		mScale = 1.f;
		mWidth = width;
		mHeight = height;
		mSharedDepthBufferId = sharedDepthBufferId;

		final var lIntBuffer = MemoryUtil.memAlloc(mWidth * mHeight * 4);

		initializeGl(lIntBuffer);
	}

	public void loadResourcesFromImage(String filePath) {
		if (filePath == null || filePath.length() == 0) {
			return;
		}

		final var textureFile = new File(filePath);
		if (!textureFile.exists()) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "File not found: " + filePath);
			return;
		}

		Debug.debugManager().logger().v(Texture.class.getSimpleName(), "Loading render texture from file: " + filePath);

		try {
			final var fileSize = textureFile.length();

			STBImage.stbi_set_flip_vertically_on_load(true);
			ByteBuffer imageData = null;
			try (final var stack = MemoryStack.stackPush()) {

				final var w = stack.mallocInt(1);
				final var h = stack.mallocInt(1);
				final var comp = stack.mallocInt(1);

				imageData = STBImage.stbi_load(filePath, w, h, comp, 4);

				if (imageData == null) {
					Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Failed to load texture: " + STBImage.stbi_failure_reason());
					return;
				}

				mWidth = w.get(0);
				mHeight = h.get(0);

				initializeGl(imageData);

			} finally {
				STBImage.stbi_image_free(imageData);
			}

		} catch (Exception e) {
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), "Error loading texture from file (" + filePath + ")");
			Debug.debugManager().logger().e(Texture.class.getSimpleName(), e.getMessage());
		}

	}

	private void initializeGl(ByteBuffer buffer) {

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
			if (mSharedDepthBufferId != null)
				mDepthTextureID = mSharedDepthBufferId;
			else
				mDepthTextureID = GL30.glGenRenderbuffers();

			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, mDepthTextureID);
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, mWidth, mHeight);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, mDepthTextureID);
		}

		int lCreationStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (lCreationStatus != GL30.GL_FRAMEBUFFER_COMPLETE) {
			switch (lCreationStatus) {
			case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT'");
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT'");
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER'");
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");

			case GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE'");
				throw new RuntimeException("GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER'");
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");

			case GL30.GL_FRAMEBUFFER_UNSUPPORTED:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_UNSUPPORTED'");
				throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE'");
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");

			case GL30.GL_FRAMEBUFFER_UNDEFINED:
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create Fbo. Error: 'GL_FRAMEBUFFER_UNDEFINED'");
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

	public void saveTextureToPngFile(String filepath) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Allocate buffer for pixel data (RGBA format)
			ByteBuffer imageBuffer = stack.malloc(mWidth * mHeight * 4);

			// Bind the texture and read its contents
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mColorTextureID);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

			// Flip the image vertically (OpenGL uses bottom-left origin, images use top-left)
			ByteBuffer flippedBuffer = flipImageVertically(imageBuffer, mWidth, mHeight, 4);

			// Write to PNG file using STB
			if (!STBImageWrite.stbi_write_png(filepath, mWidth, mHeight, 4, flippedBuffer, mWidth * 4)) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to write texture to file: " + filepath);
			} else {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Successfully saved texture to: " + filepath);
			}
		}
	}

	private ByteBuffer flipImageVertically(ByteBuffer image, int width, int height, int channels) {
		int stride = width * channels;
		ByteBuffer flipped = MemoryUtil.memAlloc(width * height * channels);

		for (int y = 0; y < height; y++) {
			int srcPos = (height - 1 - y) * stride;
			int dstPos = y * stride;

			for (int x = 0; x < stride; x++) {
				flipped.put(dstPos + x, image.get(srcPos + x));
			}
		}

		return flipped;
	}
}