package net.lintford.library.core.graphics.polybatch;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PC;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

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

	public static final int MAX_LINES = 500;
	public static final int NUM_VERTS_PER_LINE = 2;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pc.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pc.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;

	private ICamera mCamera;
	private ShaderMVP_PC mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mResourcesLoaded;
	private int mLineMode;

	private boolean _countDebugStats = true;

	// --------------------------------------
	// Properties
	// ------------------------------------

	public void _countDebugStats(boolean enableStats) {
		_countDebugStats = enableStats;
	}

	/**
	 * Specifies what kind of primitives to render. Symbolic constants GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_LINE_STRIP_ADJACENCY, GL_LINES_ADJACENCY, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES,
	 * GL_TRIANGLE_STRIP_ADJACENCY, GL_TRIANGLES_ADJACENCY and GL_PATCHES are accepted.
	 */
	public void lineMode(int lineType) {
		mLineMode = lineType;
	}

	public boolean isDrawing() {
		return mIsDrawing;
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

		mLineMode = GL11.GL_LINE_STRIP;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mShader.loadResources(resourceManager);

		if (mVaoId == -1) {
			mVaoId = GL30.glGenVertexArrays();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenVertexArrays: " + mVaoId);
		}

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vbo " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_LINES * NUM_VERTS_PER_LINE * VertexDefinition.elementCount);

		initializeGlContent();

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
		mResourcesLoaded = true;
	}

	private void initializeGlContent() {
		GL30.glBindVertexArray(mVaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_LINES * NUM_VERTS_PER_LINE * VertexDefinition.stride, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDefinition.colorElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.colorByteOffset);

		GL30.glBindVertexArray(0);
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

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
		}

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteVertexArrays: " + mVaoId);
			mVaoId = -1;
		}

		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_BATCH_OBJECTS);
		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera camera) {
		if (camera == null)
			return;

		if (mIsDrawing)
			return;

		mCamera = camera;

		mBuffer.clear();
		mVertexCount = 0;
		mIsDrawing = true;
	}

	public void drawVertices(List<Vector2f> vertexArray, float zDepth, boolean closePolygon, float red, float green, float blue) {
		if (vertexArray == null)
			return;

		drawVertices(vertexArray, vertexArray.size(), zDepth, closePolygon, red, green, blue);
	}

	public void drawVertices(List<Vector2f> vertexArray, int numberVerts, float zDepth, boolean closePolygon, float red, float green, float blue) {
		if (!mIsDrawing || vertexArray == null || vertexArray.size() < 2 || numberVerts < 2)
			return;

		int lLastIndex = 1;

		final int lNumVerts = Math.min(vertexArray.size(), numberVerts);

		for (int i = 0; i < lNumVerts - 1; i++) {
			if (vertexArray.get(i) == null || vertexArray.get(i + 1) == null)
				continue;

			float px0 = vertexArray.get(i).x;
			float py0 = vertexArray.get(i).y;

			lLastIndex++;

			if (mLineMode == GL11.GL_LINES) {
				float px1 = vertexArray.get(i + 1).x;
				float py1 = vertexArray.get(i + 1).y;
				addEdge(px0, py0, px1, py1, zDepth, red, green, blue);
			} else {
				addPoint(px0, py0, zDepth, red, green, blue);
			}
		}

		if (mLineMode == GL11.GL_LINE_STRIP) {
			float px1 = vertexArray.get(lLastIndex - 1).x;
			float py1 = vertexArray.get(lLastIndex - 1).y;
			addPoint(px1, py1, zDepth, red, green, blue);
		}

		if (closePolygon) {
			if (vertexArray.get(0) != null && vertexArray.get(lLastIndex - 1) != null) {
				float px0 = vertexArray.get(0).x;
				float py0 = vertexArray.get(0).y;

				if (mLineMode == GL11.GL_LINES) {
					float px1 = vertexArray.get(lLastIndex - 1).x;
					float py1 = vertexArray.get(lLastIndex - 1).y;
					addEdge(px0, py0, px1, py1, zDepth, red, green, blue);
				} else {
					addPoint(px0, py0, zDepth, red, green, blue);
				}
			}
		}
	}

	public void addPoint(float point1X, float point1Y, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		if (mVertexCount / 2 >= MAX_LINES)
			flush();

		addVertToBuffer(point1X, point1Y, zDepth, 1f, red, green, blue, 1.f);
	}

	public void addEdge(float point1X, float point1Y, float point2X, float point2Y, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		if (mVertexCount / 2 >= MAX_LINES)
			flush();

		addVertToBuffer(point1X, point1Y, zDepth, 1f, red, green, blue, 1.f);
		addVertToBuffer(point2X, point2Y, zDepth, 1f, red, green, blue, 1.f);

	}

	private void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(r);
		mBuffer.put(g);
		mBuffer.put(b);
		mBuffer.put(a);

		mVertexCount++;
	}

	public void end() {
		if (!mIsDrawing)
			return;

		mIsDrawing = false;
		flush();
	}

	private void flush() {
		if (!mResourcesLoaded)
			return;

		if (mVertexCount == 0)
			return;

		mBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		if (_countDebugStats) {
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mVertexCount);
		}

		GL11.glDrawArrays(mLineMode, 0, mVertexCount);

		GL30.glBindVertexArray(0);

		mShader.unbind();

		mBuffer.clear();
	}
}