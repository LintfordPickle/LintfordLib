package net.lintfordlib.core.graphics.geometry;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.maths.Matrix4f;

public class TexturedQuad_PT {

	private class VertexDefinition {

		public static final int ELEMNT_BYTES = 4;

		public static final int POSITION_ELEMENT_COUNT = 4;
		public static final int TEXTURE_ELEMENT_COUNT = 2;

		public static final int ELEMENT_COUNT = POSITION_ELEMENT_COUNT + TEXTURE_ELEMENT_COUNT;

		public static final int POSITION_BYTES_COUNT = POSITION_ELEMENT_COUNT * ELEMNT_BYTES;
		public static final int TEXTURE_BYTES_COUNT = TEXTURE_ELEMENT_COUNT * ELEMNT_BYTES;

		public static final int POSITION_BYTE_OFFSET = 0;
		public static final int TEXTURE_BYTE_OFFSET = POSITION_BYTE_OFFSET + POSITION_BYTES_COUNT;

		public static final int STRIDE = POSITION_BYTES_COUNT + TEXTURE_BYTES_COUNT;
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

	/** Returns the Z Depth this {@link TexturedQuad_PT}s modelmatrix will be translated to upon request. */
	public float zDepth() {
		return mZDepth;
	}

	/** Sets a new ZDepth value for this {@link TexturedQuad_PT}. */
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

	public TexturedQuad_PT() {
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

		mBuffer = MemoryUtil.memAllocFloat(6 * VertexDefinition.ELEMENT_COUNT);
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

		GL20.glVertexAttribPointer(0, VertexDefinition.POSITION_ELEMENT_COUNT, GL11.GL_FLOAT, false, VertexDefinition.STRIDE, VertexDefinition.POSITION_BYTE_OFFSET);
		GL20.glVertexAttribPointer(1, VertexDefinition.TEXTURE_ELEMENT_COUNT, GL11.GL_FLOAT, false, VertexDefinition.STRIDE, VertexDefinition.TEXTURE_BYTE_OFFSET);

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

		mBuffer.put(x1).put(y1).put(z1).put(w1).put(u1).put(v1);
		mBuffer.put(x0).put(y0).put(z0).put(w0).put(u0).put(v0);
		mBuffer.put(x2).put(y2).put(z2).put(w2).put(u2).put(v2);

		mBuffer.put(x2).put(y2).put(z2).put(w2).put(u2).put(v2);
		mBuffer.put(x0).put(y0).put(z0).put(w0).put(u0).put(v0);
		mBuffer.put(x3).put(y3).put(z3).put(w3).put(u3).put(v3);

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

		if (Debug.debugManager().debugManagerEnabled()) {
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
