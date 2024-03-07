package net.lintfordlib.core.graphics.polybatch;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PC;
import net.lintfordlib.core.maths.Matrix4f;
import net.lintfordlib.core.maths.Vector2f;

public class PolyBatchPC {

	private class VertexDefinition {
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

	protected static final int MAX_SPRITES = 10000;
	protected static final int NUM_VERTICES_PER_SPRITE = 4;
	protected static final int NUM_INDICES_PER_SPRITE = 6;

	protected static final int MAX_VERTEX_COUNT = MAX_SPRITES * NUM_VERTICES_PER_SPRITE;
	protected static final int MAX_INDEX_COUNT = MAX_SPRITES * NUM_INDICES_PER_SPRITE;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pc.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pc.frag";

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
	private ShaderMVP_PC mShader;
	private ShaderMVP_PC mCustomShader;

	private FloatBuffer mBuffer;

	private boolean mBlendEnabled;
	private int mBlendFuncSrcFactor;
	private int mBlendFuncDstFactor;

	private Matrix4f mModelMatrix;
	protected int mVaoId = -1;
	protected int mVboId = -1;
	protected int mVioId = -1;

	protected ResourceManager mResourceManager;
	private boolean mResourcesLoaded;
	protected boolean mAreGlContainersInitialized = false;
	private boolean mIsDrawing;

	protected int mIndexCount = 0;
	private boolean _countDebugStats = true;

	// --------------------------------------
	// Properties
	// ------------------------------------

	public void _countDebugStats(boolean enableStats) {
		_countDebugStats = enableStats;
	}

	public boolean isDrawing() {
		return mIsDrawing;
	}

	public void setGlBlendEnabled(boolean blendEnabled) {
		mBlendEnabled = blendEnabled;
	}

	public void setGlBlendFactor(int sourceFactor, int destFactor) {
		mBlendFuncSrcFactor = sourceFactor;
		mBlendFuncDstFactor = destFactor;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PolyBatchPC() {
		mShader = new ShaderMVP_PC(ShaderMVP_PC.SHADER_NAME, VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		mModelMatrix = new Matrix4f();
		mResourcesLoaded = false;

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

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer(), GL15.GL_STATIC_DRAW);

		GL30.glBindVertexArray(0);
		mAreGlContainersInitialized = true;

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

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

		if (mVioId > -1) {
			GL15.glDeleteBuffers(mVioId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading mVioId = " + mVioId);
			mVioId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
		}

		if (mIndexBuffer != null) {
			mIndexBuffer.clear();
			MemoryUtil.memFree(mIndexBuffer);
			mIndexBuffer = null;
		}

		mResourcesLoaded = false;

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

		if (!mResourcesLoaded)
			return;

		if (mIsDrawing)
			return;

		mCamera = camera;
		mBuffer.clear();
		if (mIndexBuffer == null)
			getIndexBuffer();
		mIndexBuffer.clear();

		mIndexCount = 0;

		mIsDrawing = true;

	}

	public void drawQuadrilateral(List<Vector2f> localVertices, float wx, float wy, float zDepth, boolean closePolygon, Color color) {
		if (!mIsDrawing || localVertices == null || localVertices.size() < 2)
			return;

		final int lNumVerts = localVertices.size();

		for (int i = 0; i < lNumVerts; i++) {
			final var v_x = wx + localVertices.get(i).x;
			final var v_y = wy + localVertices.get(i).y;

			addVertToBuffer(v_x, v_y, zDepth, color.r, color.g, color.b, color.a);
		}
	}

	private void addVertToBuffer(float x, float y, float z, float r, float g, float b, float a) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(1.f);

		mBuffer.put(r);
		mBuffer.put(g);
		mBuffer.put(b);
		mBuffer.put(a);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	public void end() {
		if (!mIsDrawing)
			return;

		flush();
		mIsDrawing = false;
	}

	private void flush() {
		if (!mResourcesLoaded || !mIsDrawing)
			return;

		if (mIndexCount == 0)
			return;

		mBuffer.flip();

		if (!mAreGlContainersInitialized)
			initializeGlContainers();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId); // TODO: Check if this is needed (we bound the ao after all?
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		mBlendEnabled = mBlendFuncSrcFactor != GL11.GL_SRC_ALPHA || mBlendFuncDstFactor != GL11.GL_ONE_MINUS_SRC_ALPHA;
		if (mBlendEnabled) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(mBlendFuncSrcFactor, mBlendFuncDstFactor);
		} else {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		if (_countDebugStats) {
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);

			final int lNumQuads = mIndexCount / NUM_INDICES_PER_SPRITE;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, lNumQuads * 4);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumQuads * 2);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);
		GL30.glBindVertexArray(0);

		mShader.unbind();

		mBuffer.clear();
		mIndexCount = 0;

		if (mBlendEnabled) {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
}