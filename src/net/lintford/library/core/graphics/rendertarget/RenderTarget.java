package net.lintford.library.core.graphics.rendertarget;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class RenderTarget {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String targetName;
	private int mColorTextureID;
	private int mDepthTextureID;
	private int mFramebufferID;
	private int mTextureFilter;
	private int mTextureWrapModeS;
	private int mTextureWrapModeT;
	private boolean mDepthBufferEnabled;
	private boolean mIsLoaded;

	private int mWidth;
	private int mHeight;
	private float mScale;

	private FloatBuffer mTextureBufferData;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Sets the texture filter mode for mag. and min. (default: GL11.GL_LINEAR).
	 * 
	 * @param pParam The GL11 filter mode
	 */
	public void textureFilter(int pParam) {
		mTextureFilter = pParam;
	}

	public int textureFilter() {
		return mTextureFilter;
	}

	public void textureWrapModeS(int pParam) {
		mTextureWrapModeS = pParam;
	}

	public int textureWrapModeS() {
		return mTextureWrapModeS;
	}

	public void textureWrapModeT(int pParam) {
		mTextureWrapModeT = pParam;
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
		return mIsLoaded;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RenderTarget() {

		mTextureFilter = GL11.GL_LINEAR;
		mTextureWrapModeS = GL12.GL_CLAMP_TO_EDGE;
		mTextureWrapModeT = GL12.GL_CLAMP_TO_EDGE;

		mFramebufferID = -1;
		mColorTextureID = -1;

		mDepthBufferEnabled = false;
		targetName = "unnamed";

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadGLContent(int pWidth, int pHeight, float pScale) {
		if (pWidth == 0 || pHeight == 0)
			return;

		if (mIsLoaded)
			return;

		mWidth = pWidth;
		mHeight = pHeight;
		mScale = pScale;

		createFloatBuffer();

		mFramebufferID = GL30.glGenFramebuffers(); // gen container for texture and optional depth buffer
		mColorTextureID = GL11.glGenTextures(); // gen texture to hold RGB data

		// Create and bind framebuffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);

		// Create and bind geometry texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mColorTextureID);

		// Create an empty texture
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, mWidth, mHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mTextureBufferData);

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
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, mDepthTextureID); //
		}

		int lCreationStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (lCreationStatus != GL30.GL_FRAMEBUFFER_COMPLETE) {
			switch (lCreationStatus) {
			case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT ");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER ");

			case GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE:
				throw new RuntimeException("GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE ");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER  ");

			case GL30.GL_FRAMEBUFFER_UNSUPPORTED:
				throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED  ");

			case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE   ");

			case GL30.GL_FRAMEBUFFER_UNDEFINED:
				throw new RuntimeException("GL_FRAMEBUFFER_UNDEFINED    ");

			}

		}

		// unbind
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		GL15.glDeleteBuffers(mFramebufferID);
		mFramebufferID = -1;

		GL11.glDeleteTextures(mColorTextureID);
		mColorTextureID = -1;

		if (mDepthBufferEnabled) {
			GL15.glDeleteBuffers(mDepthTextureID);
			mDepthTextureID = -1;
		}

		mIsLoaded = false;
	}

	public void bind() {
		GL11.glViewport(0, 0, mWidth, mHeight);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);
	}

	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void resize(int pWidth, int pHeight) {
		if (pWidth == 0 || pHeight == 0)
			return;

		if (!mIsLoaded)
			return;

		mWidth = pWidth;
		mHeight = pHeight;

		createFloatBuffer();

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mFramebufferID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mColorTextureID);

		// Set the texture filtering mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mTextureFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mTextureFilter);

		// Set the texture wrap mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Create an empty texture
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, mWidth, mHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mTextureBufferData);

		if (mDepthBufferEnabled) {
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, mDepthTextureID);
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, mWidth, mHeight);

		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

	}

	private void createFloatBuffer() {
		final int lNewSize = mWidth * mHeight * 4;
		if (mTextureBufferData != null) {
			if (mTextureBufferData.capacity() != lNewSize) {
				MemoryUtil.memFree(mTextureBufferData);
				mTextureBufferData = MemoryUtil.memAllocFloat(lNewSize);
			}

			mTextureBufferData.clear();

		} else {
			mTextureBufferData = MemoryUtil.memAllocFloat(lNewSize);

		}
	}

}