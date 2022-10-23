package net.lintford.library.core.graphics.geometry;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.maths.Matrix4f;

public class TexturedQuad {

	private class VertexDefinition {

		public static final int elementBytes = 4;

		public static final int positionElementCount = 4;
		public static final int textureElementCount = 2;
		public static final int textureIndexElementCount = 1;

		public static final int elementCount = positionElementCount + textureElementCount + textureIndexElementCount;

		public static final int positionBytesCount = positionElementCount * elementBytes;
		public static final int textureBytesCount = textureElementCount * elementBytes;

		public static final int positionByteOffset = 0;
		public static final int textureByteOffset = positionByteOffset + positionBytesCount;

		public static final int stride = positionBytesCount + textureBytesCount;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Matrix4f mModelMatrix;
	protected int mVaoId = -1;
	protected int mVboId = -1;
	protected boolean mResourcesLoaded;
	protected float mWidth;
	protected float mHeight;
	protected float mZDepth;
	protected FloatBuffer mBuffer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the Z Depth this {@link TexturedQuad}s modelmatrix will be translated to upon request. */
	public float zDepth() {
		return mZDepth;
	}

	/** Sets a new ZDepth value for this {@link TexturedQuad}. */
	public void zDepth(float zDepth) {
		mZDepth = zDepth;
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	public void modelMatrix(Matrix4f modelMatrix) {
		mModelMatrix = modelMatrix;
	}

	public void width(float width) {
		if (width < 0) {
			width = 0;
		}
		mWidth = width;
	}

	public void height(float height) {
		if (height < 0) {
			height = 0;
		}
		mHeight = height;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TexturedQuad() {
		mModelMatrix = new Matrix4f();
		createModelMatrix();

		mWidth = 320.f;
		mHeight = 240.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mBuffer = MemoryUtil.memAllocFloat(6 * VertexDefinition.elementCount);

		initializeGlContent();

		mResourcesLoaded = true;
	}

	private void initializeGlContent() {
		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		// @formatter:off
		addVertToBuffer(-.5f,  .5f, 0.f, 1.f, 0.f, 0.f);
		addVertToBuffer(-.5f, -.5f, 0.f, 1.f, 0.f, 1.f);
		addVertToBuffer( .5f, -.5f, 0.f, 1.f, 1.f, 1.f);
		addVertToBuffer( .5f,  .5f, 0.f, 1.f, 1.f, 0.f);
		// @formatter:on

		mBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);

		GL30.glBindVertexArray(0);
	}

	protected void addVertToBuffer(float x, float y, float z, float w, float u, float v) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(u);
		mBuffer.put(v);
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		cleanup();

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		mResourcesLoaded = false;
	}

	public void cleanup() {
		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "TexturedQuad: Unloading VboId = " + mVboId);
			mVaoId = -1;
		}

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "TexturedQuad: Unloading VaoId = " + mVaoId);
			mVboId = -1;
		}
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core) {
		GL30.glBindVertexArray(mVaoId);

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, 6);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, 2);
		}

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

		GL30.glBindVertexArray(0);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void createModelMatrix() {
		createModelMatrix(0, 0);
	}

	public void createModelMatrix(float positionX, float positionY) {
		createModelMatrix(positionX, positionY, mZDepth);
	}

	public void createModelMatrix(float positionX, float positionY, float positionZ) {
		createModelMatrix(positionX, positionY, positionZ, mWidth, mHeight);
	}

	public void createModelMatrix(float positionX, float positionY, float positionZ, float width, float height) {
		mModelMatrix.setIdentity();
		mModelMatrix.scale(width, height, 1f);
		mModelMatrix.translate(positionX, positionY, positionZ);
	}
}
