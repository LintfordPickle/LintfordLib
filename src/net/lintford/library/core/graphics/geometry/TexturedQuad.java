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

		public static final int elementCount = positionElementCount + textureElementCount;

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
	private boolean mAreGlContainersInitialized = false;

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
		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vbo " + mVboId);
		}

		loadGLGeometry();

		mResourcesLoaded = true;

		if (resourceManager.isMainOpenGlThread())
			initializeGlContainers();
	}

	private void initializeGlContainers() {
		if (!mResourcesLoaded)
			return;

		if (mAreGlContainersInitialized)
			return;

		if (mVaoId == -1) {
			mVaoId = GL30.glGenVertexArrays();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenVertexArrays: " + mVaoId);
		}

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);

		GL30.glBindVertexArray(0);

		mAreGlContainersInitialized = true;
	}

	private void loadGLGeometry() {
		// vert 0
		final var x0 = -.5f;
		final var y0 = .5f;
		final var z0 = 0.f;
		final var w0 = 1.f;
		final var u0 = 0.f;
		final var v0 = 0.f;

		// vert 1
		final var x1 = -.5f;
		final var y1 = -.5f;
		final var z1 = 0.f;
		final var w1 = 1.f;
		final var u1 = 0.f;
		final var v1 = 1.f;

		// vert 2
		final var x2 = .5f;
		final var y2 = -.5f;
		final var z2 = 0.f;
		final var w2 = 1.f;
		final var u2 = 1.f;
		final var v2 = 1.f;

		// vert 4
		final var x3 = .5f;
		final var y3 = .5f;
		final var z3 = 0.f;
		final var w3 = 1.f;
		final var u3 = 1.f;
		final var v3 = 0.f;

		mBuffer.put(x0).put(y0).put(z0).put(w0).put(u0).put(v0);
		mBuffer.put(x1).put(y1).put(z1).put(w1).put(u1).put(v1);
		mBuffer.put(x2).put(y2).put(z2).put(w2).put(u2).put(v2);

		mBuffer.put(x2).put(y2).put(z2).put(w2).put(u2).put(v2);
		mBuffer.put(x3).put(y3).put(z3).put(w3).put(u3).put(v3);
		mBuffer.put(x0).put(y0).put(z0).put(w0).put(u0).put(v0);

		mBuffer.flip();
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
		mAreGlContainersInitialized = false;
	}

	public void cleanup() {
		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteVertexArrays: " + mVaoId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteBuffers VboId: " + mVboId);
			mVboId = -1;
		}
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core) {
		if (!mAreGlContainersInitialized)
			initializeGlContainers();

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
		createModelMatrixCentered(0, 0);
	}

	public void createModelMatrixCentered(float positionX, float positionY) {
		createModelMatrixCentered(positionX, positionY, mZDepth);
	}

	public void createModelMatrixAbsolute(float positionX, float positionY) {
		createModelMatrixAbsolute(positionX, positionY, mZDepth);
	}

	public void createModelMatrixCentered(float positionX, float positionY, float positionZ) {
		createModelMatrixCentered(positionX, positionY, positionZ, mWidth, mHeight);
	}

	public void createModelMatrixAbsolute(float positionX, float positionY, float positionZ) {
		createModelMatrixAbsolute(positionX, positionY, positionZ, mWidth, mHeight);
	}

	public void createModelMatrixCentered(float positionX, float positionY, float positionZ, float width, float height) {
		mModelMatrix.setIdentity();
		mModelMatrix.scale(width, height, 1f);
		mModelMatrix.translate(positionX, positionY, positionZ);
	}

	public void createModelMatrixAbsolute(float positionX, float positionY, float positionZ, float width, float height) {
		mModelMatrix.setIdentity();
		mModelMatrix.scale(width, height, 1f);
		mModelMatrix.translate(positionX + width * .5f, positionY + height * .5f, positionZ);
	}
}
