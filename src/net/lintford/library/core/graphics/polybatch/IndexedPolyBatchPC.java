package net.lintford.library.core.graphics.polybatch;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PC;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePC;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

public class IndexedPolyBatchPC {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_TRIS = 2048;
	public static final int NUM_VERTS_PER_TRI = 3;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pc.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pc.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mVaoId = -1;
	private int mVioId = -1;
	private int mVboId = -1;
	private int mIndexCount = 0;
	private int mVertexCount = 0;
	public float r, g, b, a;

	private ICamera mCamera;
	private ShaderMVP_PC mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private IntBuffer mIndexBuffer;
	private boolean mIsDrawing;
	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// ------------------------------------

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public IndexedPolyBatchPC() {
		mShader = new ShaderMVP_PC(ShaderMVP_PC.SHADER_NAME, VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		a = r = g = b = 1f;

		mModelMatrix = new Matrix4f();
		mIsLoaded = false;

	}

	// --------------------------------------1
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		mShader.loadGLContent(pResourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVioId == -1)
			mVioId = GL15.glGenBuffers();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		// TODO: Make sure this is the new / correct way to allocation memory for a buffer
		// TODO: We are not benefiting from the indexed nature (with regards to reduced vert count)
		mBuffer = MemoryUtil.memAllocFloat(MAX_TRIS * NUM_VERTS_PER_TRI * VertexDataStructurePC.stride);
		mIndexBuffer = MemoryUtil.memAllocInt(MAX_TRIS * NUM_VERTS_PER_TRI);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		mShader.unloadGLContent();

		if (mVaoId > -1)
			GL30.glDeleteVertexArrays(mVaoId);

		if (mVboId > -1)
			GL15.glDeleteBuffers(mVboId);

		mVaoId = -1;
		mVboId = -1;

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);

		}

		if (mIndexBuffer != null) {
			mIndexBuffer.clear();
			MemoryUtil.memFree(mIndexBuffer);

		}

		mIsLoaded = false;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return;

		mCamera = pCamera;

		mBuffer.clear();
		mIndexBuffer.clear();

		mIndexCount = 0;
		mVertexCount = 0;
		mIsDrawing = true;

	}

	public void drawRect(List<Vector2f> pVertexArray, float pZ, boolean pClose, float pR, float pG, float pB) {
		if (pVertexArray == null)
			return;

		final var lRectVerts = pVertexArray;
		if (lRectVerts.size() < 4)
			return;

		for (int i = 0; i < 4; i++) {
			addVertToBuffer(lRectVerts.get(i).x, lRectVerts.get(i).y, pZ, 1f, pR, pG, pB, a);

		}

		// Index the triangle
		mIndexBuffer.put(mVertexCount - 4);
		mIndexBuffer.put(mVertexCount - 3);
		mIndexBuffer.put(mVertexCount - 2);

		mIndexBuffer.put(mVertexCount - 3);
		mIndexBuffer.put(mVertexCount - 1);
		mIndexBuffer.put(mVertexCount - 2);

		mIndexCount += 6;

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

		if (mIndexCount == 0)
			return;

		mBuffer.flip();
		mIndexBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		// Bind vertices

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePC.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePC.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.colorByteOffset);

		// Bind indices

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mIndexCount * 3);

		}

		// glDrawElements for index buffer
		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		GLDebug.checkGLErrorsException();

		GL30.glBindVertexArray(0);

		mShader.unbind();

		mIndexCount = 0;

	}

	public void changeColorNormalized(float pR, float pG, float pB, float pA) {
		// if (mCurNumSprites > 0) {
		// flush();
		// mCurNumSprites = 0;
		// }

		r = pR;
		g = pG;
		b = pB;
		a = pA;

	}

}
