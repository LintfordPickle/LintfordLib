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
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PC;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePC;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

public class PolyBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_LINES = 16384;
	public static final int NUM_VERTS_PER_LINE = 2;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pc.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pc.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	public float r, g, b, a;

	private ICamera mCamera;
	private ShaderMVP_PC mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mResourcesLoaded;
	private int mLineMode;

	// --------------------------------------
	// Properties
	// ------------------------------------

	/**
	 * Specifies what kind of primitives to render. Symbolic constants GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES, GL_LINE_STRIP_ADJACENCY, GL_LINES_ADJACENCY, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES,
	 * GL_TRIANGLE_STRIP_ADJACENCY, GL_TRIANGLES_ADJACENCY and GL_PATCHES are accepted.
	 */
	public void lineMode(int pNewLineType) {
		mLineMode = pNewLineType;

	}

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PolyBatch() {
		mShader = new ShaderMVP_PC(ShaderMVP_PC.SHADER_NAME, VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		a = r = g = b = 1f;

		mModelMatrix = new Matrix4f();
		mResourcesLoaded = false;

		mLineMode = GL11.GL_LINE_STRIP;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		if (mResourcesLoaded)
			return;

		mShader.loadResources(pResourceManager);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v("OpenGL", "PolyBatch: VboId = " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_LINES * NUM_VERTS_PER_LINE * VertexDataStructurePC.stride);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "PolyBatch: Unloading VaoId = " + mVaoId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "PolyBatch: Unloading VboId = " + mVboId);
			mVboId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);

		}

		mResourcesLoaded = false;
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

	public void drawRect(Rectangle pRect, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing || pRect == null)
			return;

		List<Vector2f> verts = pRect.getVertices();

		draw(verts.get(0).x, verts.get(0).y, verts.get(1).x, verts.get(1).y, pZ, pR, pG, pB);
		draw(verts.get(0).x, verts.get(0).y, verts.get(2).x, verts.get(2).y, pZ, pR, pG, pB);
		draw(verts.get(2).x, verts.get(2).y, verts.get(3).x, verts.get(3).y, pZ, pR, pG, pB);
		draw(verts.get(1).x, verts.get(1).y, verts.get(3).x, verts.get(3).y, pZ, pR, pG, pB);

	}

	public void drawRect(List<Vector2f> pVertexArray, float pZ, boolean pClose, float pR, float pG, float pB) {
		if (pVertexArray == null)
			return;

		drawRect(pVertexArray, pVertexArray.size(), pZ, pClose, pR, pG, pB);

	}

	public void drawRect(List<Vector2f> pVertexArray, int pAmt, float pZ, boolean pClose, float pR, float pG, float pB) {
		if (!mIsDrawing || pVertexArray == null || pVertexArray.size() < 2 || pAmt < 2)
			return;

		int lLastIndex = 1;

		final int ARRAY_SIZE = pAmt; // pVertexArray.length;
		for (int i = 0; i < ARRAY_SIZE - 1; i++) {
			if (pVertexArray.get(i) == null || pVertexArray.get(i + 1) == null)
				continue;
			float px0 = pVertexArray.get(i).x;
			float py0 = pVertexArray.get(i).y;
			float px1 = pVertexArray.get(i + 1).x;
			float py1 = pVertexArray.get(i + 1).y;

			lLastIndex++;

			draw(px0, py0, px1, py1, pZ, pR, pG, pB); // top
		}

		if (pClose) {
			if (pVertexArray.get(0) != null && pVertexArray.get(lLastIndex - 1) != null) {
				float px0 = pVertexArray.get(0).x;
				float py0 = pVertexArray.get(0).y;
				float px1 = pVertexArray.get(lLastIndex - 1).x;
				float py1 = pVertexArray.get(lLastIndex - 1).y;

				draw(px0, py0, px1, py1, pZ, pR, pG, pB); // top

			}

		}

	}

	public void draw(float pP1X, float pP1Y, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;

		if (mVertexCount / 2 >= MAX_LINES) {
			flush();
		}

		// Add both vertices to the buffer
		addVertToBuffer(pP1X, pP1Y, pZ, 1f, pR, pG, pB, a);

	}

	public void draw(float pP1X, float pP1Y, float pP2X, float pP2Y, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;

		if (mVertexCount / 2 >= MAX_LINES) {
			flush();
		}

		// Add both vertices to the buffer
		addVertToBuffer(pP1X, pP1Y, pZ, 1f, pR, pG, pB, a);
		addVertToBuffer(pP2X, pP2Y, pZ, 1f, pR, pG, pB, a);

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

			if (mLineMode == GL11.GL_TRIANGLES || mLineMode == GL11.GL_TRIANGLE_STRIP)
				Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, mVertexCount - 2);

		}

		GL11.glDrawArrays(mLineMode, 0, mVertexCount);

		GL30.glBindVertexArray(0);

		mShader.unbind();

		mBuffer.clear();
		mVertexCount = 0;
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