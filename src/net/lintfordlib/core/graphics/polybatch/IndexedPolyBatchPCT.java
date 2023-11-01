package net.lintfordlib.core.graphics.polybatch;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.batching.TextureSlotBatch;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PCT;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.maths.Matrix4f;
import net.lintfordlib.core.maths.Vector2f;

public class IndexedPolyBatchPCT {

	private class VertexDefinition {

		public static final int elementBytes = 4;

		public static final int positionElementCount = 4;
		public static final int colorElementCount = 4;
		public static final int textureElementCount = 2;
		public static final int textureIndexElementCount = 1;

		public static final int elementCount = positionElementCount + colorElementCount + textureElementCount + textureIndexElementCount;

		public static final int positionBytesCount = positionElementCount * elementBytes;
		public static final int colorBytesCount = colorElementCount * elementBytes;
		public static final int textureBytesCount = textureElementCount * elementBytes;
		public static final int textureIndexBytesCount = textureIndexElementCount * elementBytes;

		public static final int positionByteOffset = 0;
		public static final int colorByteOffset = positionByteOffset + positionBytesCount;
		public static final int textureByteOffset = colorByteOffset + colorBytesCount;
		public static final int textureIndexByteOffset = textureByteOffset + textureBytesCount;

		public static final int stride = positionBytesCount + colorBytesCount + textureBytesCount + textureIndexBytesCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 10000;
	protected static final int NUM_VERTICES_PER_SPRITE = 4;
	protected static final int NUM_INDICES_PER_SPRITE = 6;

	protected static final int MAX_VERTEX_COUNT = MAX_SPRITES * NUM_VERTICES_PER_SPRITE;
	protected static final int MAX_INDEX_COUNT = MAX_SPRITES * NUM_INDICES_PER_SPRITE;

	private static final String VERT_FILENAME = "/res/shaders/shader_batch_pct.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_batch_pct.frag";

	private static IntBuffer mIndexBuffer;

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

	protected final TextureSlotBatch mTextureSlots = new TextureSlotBatch();

	protected ICamera mCamera;
	protected ShaderMVP_PCT mShader;
	protected ShaderMVP_PCT mCustomShader;

	protected FloatBuffer mBuffer;

	protected Matrix4f mModelMatrix;
	protected int mVaoId = -1;
	protected int mVioId = -1;
	protected int mVboId = -1;

	protected int mCurrentTexID;

	protected boolean mResourcesLoaded;
	protected boolean mAreGlContainersInitialized = false;
	protected boolean mIsDrawing;

	protected int mIndexCount = 0;

	// --------------------------------------
	// Properties
	// ------------------------------------

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public IndexedPolyBatchPCT() {
		mShader = new ShaderMVP_PCT(ShaderMVP_PCT.SHADER_NAME, VERT_FILENAME, FRAG_FILENAME);

		mModelMatrix = new Matrix4f();
		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mShader.loadResources(resourceManager);

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDefinition.elementCount);
		getIndexBuffer();

		if (mVioId == -1)
			mVioId = GL15.glGenBuffers();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		mResourcesLoaded = true;

		if (resourceManager.isMainOpenGlThread())
			initializeGlContainers();

	}

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
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDefinition.stride, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, VertexDefinition.colorElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.colorByteOffset);

		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);

		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, VertexDefinition.textureIndexElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureIndexByteOffset);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer(), GL15.GL_STATIC_DRAW);

		GL30.glBindVertexArray(0);
		mAreGlContainersInitialized = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading VaoId = " + mVaoId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading VboId = " + mVboId);
			mVboId = -1;
		}

		if (mVioId > -1) {
			GL15.glDeleteBuffers(mVioId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading mVioId = " + mVioId);
			mVioId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		if (mIndexBuffer != null) {
			mIndexBuffer.clear();
			MemoryUtil.memFree(mIndexBuffer);
			mIndexBuffer = null;
		}

		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		begin(pCamera, mShader);
	}

	public void begin(ICamera pCamera, ShaderMVP_PCT pCustomShader) {
		if (pCamera == null)
			return;

		if (!mResourcesLoaded)
			return;

		if (mIsDrawing)
			return;

		mCamera = pCamera;

		mBuffer.clear();
		mIndexBuffer.clear();

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

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mIndexBuffer);

		if (mCurrentTexID != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);
		} else {
			return;
		}

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);

			final int lNumQuads = mIndexCount / NUM_INDICES_PER_SPRITE;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, lNumQuads * 4);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumQuads * 2);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		mShader.unbind();

		GL30.glBindVertexArray(0);
	}

	public void drawRect(Texture texture, Rectangle sourceRect, List<Vector2f> vertexArray, float zDepth, boolean closePolygon) {
	}

	public void drawRect(Texture texture, Rectangle sourceRect, List<Vector2f> vertexArray, float zDepth, boolean closePolygon, float red, float green, float blue) {

	}

	protected void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v, float texIndex) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(r);
		mBuffer.put(g);
		mBuffer.put(b);
		mBuffer.put(a);

		mBuffer.put(u);
		mBuffer.put(v);

		mBuffer.put(texIndex);
	}
}