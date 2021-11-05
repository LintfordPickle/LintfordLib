package net.lintford.library.core.graphics.pointbatch;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePC;
import net.lintford.library.core.maths.Matrix4f;

public class PointBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_POINTS = 16384;
	public static final int NUM_VERTS_PER_POINT = 1;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pc.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pc.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;

	private ICamera mCamera;
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PointBatch() {
		mShader = new ShaderMVP_PT("ShaderMVP_PT", VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		mModelMatrix = new Matrix4f();
		mIsLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		mShader.loadResources(pResourceManager);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v("OpenGL", "PointBatch: VboId = " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_POINTS * NUM_VERTS_PER_POINT * VertexDataStructurePC.stride);

		mIsLoaded = true;
	}

	public void unloadResources() {
		if (!mIsLoaded)
			return;

		mShader.unloadResources();

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "PointBatch: Unloading VboId = " + mVboId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "PointBatch: Unloading VboId = " + mVboId);
			mVboId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
		}

		mIsLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void initializeGlContent() {
		if (mVaoId == -1) {
			mVaoId = GL30.glGenVertexArrays();

			GL30.glBindVertexArray(mVaoId);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);

			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);

			GL20.glVertexAttribPointer(0, VertexDataStructurePC.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.positionByteOffset);
			GL20.glVertexAttribPointer(1, VertexDataStructurePC.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.colorByteOffset);
		}
	}

	public void begin(ICamera pCamera) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return;

		mCamera = pCamera;

		mBuffer.clear();
		mVertexCount = 0;
		mIsDrawing = true;

	}

	public void draw(float pP1X, float pP1Y, float pZ, float pR, float pG, float pB, float pA) {

		if (!mIsDrawing)
			return;

		if (mVertexCount >= MAX_POINTS) {
			flush();
		}

		// Add both vertices to the buffer
		addVertToBuffer(pP1X, pP1Y, pZ, 1f, pR, pG, pB, pA);

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
		if (!mIsLoaded)
			return;

		if (mVertexCount == 0)
			return;

		mBuffer.flip();

		initializeGlContent();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mVertexCount);
		}

		GL11.glDrawArrays(GL11.GL_POINTS, 0, mVertexCount);

		GL30.glBindVertexArray(0);

		mShader.unbind();

		mBuffer.clear();

		mVertexCount = 0;

	}
}