package net.lintfordlib.core.graphics.batching;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PC;
import net.lintfordlib.core.maths.Matrix4f;

//Note - we use half pixel correction to attempt to sample the correct texels when applying the Uvs.
//https://learn.microsoft.com/en-us/windows/win32/direct3d9/directly-mapping-texels-to-pixels?redirectedfrom=MSDN

public class TextureBatchPC {

	private static class VertexDataStructure {

		public static final int elementBytes = 4;

		public static final int positionElementCount = 4;
		public static final int colorElementCount = 4;

		public static final int elementCount = positionElementCount + colorElementCount;

		public static final int positionBytesCount = positionElementCount * elementBytes;
		public static final int colorBytesCount = colorElementCount * elementBytes;

		public static final int positionByteOffset = 0;
		public static final int colorByteOffset = positionByteOffset + positionBytesCount;

		public static final int stride = positionBytesCount + colorBytesCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 5000;
	protected static final int NUM_VERTICES_PER_SPRITE = 4;
	protected static final int NUM_INDICES_PER_SPRITE = 6;

	protected static final int MAX_VERTEX_COUNT = MAX_SPRITES * NUM_VERTICES_PER_SPRITE;
	protected static final int MAX_INDEX_COUNT = MAX_SPRITES * NUM_INDICES_PER_SPRITE;

	protected static final String VERT_FILENAME = "/res/shaders/shader_batch_pc.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_batch_pc.frag";

	private static IntBuffer mIndexBuffer;

	// @formatter:off
	//  1 ---- 2
	//  |      |
	//  |      |
	//  0------3
	// @formatter:on

	private static IntBuffer getIndexBuffer() {
		if (mIndexBuffer == null) {
			mIndexBuffer = MemoryUtil.memAllocInt(MAX_SPRITES * NUM_INDICES_PER_SPRITE);

			mIndexBuffer.clear();
			for (int i = 0; i < MAX_SPRITES; i++) {
				final int offset = i * NUM_VERTICES_PER_SPRITE;
				mIndexBuffer.put(offset + 1);
				mIndexBuffer.put(offset + 0);
				mIndexBuffer.put(offset + 2);

				mIndexBuffer.put(offset + 2);
				mIndexBuffer.put(offset + 0);
				mIndexBuffer.put(offset + 3);
			}
			mIndexBuffer.flip();
		}

		return mIndexBuffer;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ICamera mCamera;
	protected ShaderMVP_PC mShader;
	protected ShaderMVP_PC mCustomShader;

	protected FloatBuffer mBuffer;

	private boolean mBlendEnabled;
	private int mBlendFuncSrcFactor;
	private int mBlendFuncDstFactor;

	protected Matrix4f mModelMatrix;
	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVioId = -1;

	protected ResourceManager mResourceManager;
	private boolean mResourcesLoaded;
	private boolean mAreGlContainersInitialized = false;
	protected boolean mIsDrawing;

	protected int mIndexCount;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isDrawing() {
		return mIsDrawing;
	}

	public boolean isLoaded() {
		return mResourcesLoaded;
	}

	public void modelMatrix(Matrix4f modelMatrix) {
		if (modelMatrix == null) {
			mModelMatrix = new Matrix4f();
			mModelMatrix.setIdentity();
		} else {
			mModelMatrix = modelMatrix;
		}
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	public void setGlBlendEnabled(boolean blendEnabled) {
		mBlendEnabled = blendEnabled;
	}

	public void setGlBlendFactor(int srcFactor, int dtFactor) {
		mBlendFuncSrcFactor = srcFactor;
		mBlendFuncDstFactor = dtFactor;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TextureBatchPC() {
		mShader = new ShaderMVP_PC("TextureBatchShader", VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		mModelMatrix = new Matrix4f();

		mBlendEnabled = true;
		mBlendFuncSrcFactor = GL11.GL_SRC_ALPHA;
		mBlendFuncDstFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mResourceManager = resourceManager;

		mShader.loadResources(resourceManager);

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDataStructure.elementCount);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vbo " + mVboId);
		}

		if (mVioId == -1) {
			mVioId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vio " + mVioId);
		}

		mResourcesLoaded = true;

		if (resourceManager.isMainOpenGlThread())
			initializeGlContainers();

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	/**
	 * OpenGl container objects (Array objects/framebuffers/program pipeline/transform feedback) are not shared between OpenGl contexts and must be created on the main thread.
	 */
	private void initializeGlContainers() {
		if (!mResourcesLoaded) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Cannot create Gl containers until resources have been loaded");
			return;
		}

		if (mAreGlContainersInitialized)
			return;

		if (mVaoId == -1) {
			mVaoId = GL30.glGenVertexArrays();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenVertexArrays: " + mVaoId);
		}

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDataStructure.stride, GL15.GL_DYNAMIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer(), GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, VertexDataStructure.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructure.stride, VertexDataStructure.positionByteOffset);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, VertexDataStructure.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructure.stride, VertexDataStructure.colorByteOffset);

		GL30.glBindVertexArray(0);

		mAreGlContainersInitialized = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();
		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteBuffers VboId: " + mVboId);
			mVboId = -1;
		}

		if (mVioId > -1) {
			GL15.glDeleteBuffers(mVioId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteBuffers VioId: " + mVioId);
			mVioId = -1;
		}

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteVertexArrays: " + mVaoId);
			mVaoId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		mResourcesLoaded = false;
		mAreGlContainersInitialized = false;

		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera camera) {
		begin(camera, mShader);
	}

	public void begin(ICamera camera, ShaderMVP_PC customShader) {
		if (camera == null)
			return;

		if (mIsDrawing)
			return;

		if (customShader != null)
			mCustomShader = customShader;
		else
			mCustomShader = mShader;

		mCamera = camera;

		if (mBuffer != null)
			mBuffer.clear();

		mIndexCount = 0;
		mIsDrawing = true;
	}

	public void end() {
		if (!mIsDrawing)
			return;

		flush();
		mIsDrawing = false;
	}

	protected void flush() {
		if (!mResourcesLoaded || !mIsDrawing)
			return;

		if (mIndexCount == 0)
			return;

		mBuffer.flip();

		if (!mAreGlContainersInitialized)
			initializeGlContainers();

		GL30.glBindVertexArray(mVaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		if (mBlendEnabled) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(mBlendFuncSrcFactor, mBlendFuncDstFactor);
		} else {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		mCustomShader.projectionMatrix(mCamera.projection());
		mCustomShader.viewMatrix(mCamera.view());
		mCustomShader.modelMatrix(mModelMatrix);

		mCustomShader.bind();

		if (Debug.debugManager().debugManagerEnabled()) {
			final int lNumQuads = mIndexCount / NUM_INDICES_PER_SPRITE;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, lNumQuads * 4);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumQuads * 2);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);

		mCustomShader.unbind();

		mBuffer.clear();
		mIndexCount = 0;
	}

	// ---

	public void draw(Rectangle srcRect, Rectangle destRect, float zDepth, Color colorTint) {
		if (srcRect == null || destRect == null)
			return;

		draw(srcRect.x(), srcRect.y(), srcRect.width(), srcRect.height(), destRect, zDepth, colorTint);
	}

	public void draw(Rectangle srcRect, float dx, float dy, float dw, float dh, float zDepth, Color colorTint) {
		if (srcRect == null)
			return;

		draw(dx, dy, dw, dh, zDepth, colorTint);
	}

	public void draw(float sx, float sy, float sw, float sh, Rectangle destRect, float zDepth, Color colorTint) {
		if (destRect == null)
			return;

		draw(destRect.x(), destRect.y(), destRect.width(), destRect.height(), zDepth, colorTint);
	}

	public void draw(float dx, float dy, float dw, float dh, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();

		final var x0 = dx;
		final var y0 = dy + dh;

		final var x1 = dx;
		final var y1 = dy;

		final var x2 = dx + dw;
		final var y2 = dy;

		final var x3 = dx + dw;
		final var y3 = dy + dh;

		addVertToBuffer(x0, y0, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);
		addVertToBuffer(x1, y1, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);
		addVertToBuffer(x2, y2, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);
		addVertToBuffer(x3, y3, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	public void drawAroundCenter(Rectangle destRect, float zDepth, float rota, float rotx, float roty, float scale, Color colorTint) {
		if (destRect == null)
			return;

		drawAroundCenter(destRect.x(), destRect.y(), destRect.width(), destRect.height(), zDepth, rota, rotx, roty, scale, colorTint);
	}

	public void drawAroundCenter(float dx, float dy, float dw, float dh, float zDepth, float rota, float rotx, float roty, float scale, Color colorTint) {
		if (!mResourcesLoaded)
			return;

		if (!mIsDrawing)
			return;

		final var sin = (float) Math.sin(rota);
		final var cos = (float) Math.cos(rota);

		final var lHalfW = (dw * scale) / 2f;
		final var lHalfH = (dh * scale) / 2f;

		// define the origin of this sprite
		// note: the rotation origin is not scaled with the sprite (this should be performed before calling this function)
		final var originX = -rotx;
		final var originY = -roty;

		// Vertex 0 (bottom left)
		final var x0 = -(lHalfW - originX) * cos - (lHalfH + originY) * sin;
		final var y0 = -(lHalfW - originX) * sin + (lHalfH + originY) * cos;

		// Vertex 1 (top left)
		final var x1 = -(lHalfW - originX) * cos - (-lHalfH + originY) * sin;
		final var y1 = -(lHalfW - originX) * sin + (-lHalfH + originY) * cos;

		// Vertex 2 (top right)
		final var x2 = (lHalfW + originX) * cos - (-lHalfH + originY) * sin;
		final var y2 = (lHalfW + originX) * sin + (-lHalfH + originY) * cos;

		// Vertex 3 (bottom right)
		final var x3 = (lHalfW + originX) * cos - (lHalfH + originY) * sin;
		final var y3 = (lHalfW + originX) * sin + (lHalfH + originY) * cos;

		addVertToBuffer(dx + x0, dy + y0, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);
		addVertToBuffer(dx + x1, dy + y1, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);
		addVertToBuffer(dx + x2, dy + y2, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);
		addVertToBuffer(dx + x3, dy + y3, zDepth, 1f, colorTint.r, colorTint.g, colorTint.b, colorTint.a);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	protected void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(r);
		mBuffer.put(g);
		mBuffer.put(b);
		mBuffer.put(a);
	}
}