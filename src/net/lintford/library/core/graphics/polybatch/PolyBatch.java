package net.lintford.library.core.graphics.polybatch;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePC;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

// TODO: Inherit from LineBatch
public class PolyBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_LINES = 2048;
	public static final int NUM_VERTS_PER_LINE = 2;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_col.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_col.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	public float r, g, b, a;

	private ICamera mCamera;
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mIsLoaded;

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
		mShader = new ShaderMVP_PT(ShaderMVP_PT.SHADER_NAME, VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		a = r = g = b = 1f;

		mModelMatrix = new Matrix4f();
		mIsLoaded = false;

		mLineMode = GL11.GL_LINE_STRIP;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		mShader.loadGLContent(pResourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().i(getClass().getSimpleName(), "glGenBuffers: " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_LINES * NUM_VERTS_PER_LINE * VertexDataStructurePC.stride);

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
		mVertexCount = 0;
		mIsDrawing = true;

	}

	public void drawRect(Rectangle pRect, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing || pRect == null)
			return;

		Vector2f[] verts = pRect.getVertices();

		draw(verts[0].x, verts[0].y, verts[1].x, verts[1].y, pZ, pR, pG, pB);
		draw(verts[0].x, verts[0].y, verts[2].x, verts[2].y, pZ, pR, pG, pB);
		draw(verts[2].x, verts[2].y, verts[3].x, verts[3].y, pZ, pR, pG, pB);
		draw(verts[1].x, verts[1].y, verts[3].x, verts[3].y, pZ, pR, pG, pB);

	}

	public void drawRect(Vector2f[] pVertexArray, float pZ, boolean pClose, float pR, float pG, float pB) {
		drawRect(pVertexArray, pVertexArray.length, pZ, pClose, pR, pG, pB);

	}

	public void drawRect(Vector2f[] pVertexArray, int pAmt, float pZ, boolean pClose, float pR, float pG, float pB) {
		if (!mIsDrawing || pVertexArray == null || pVertexArray.length < 2 || pAmt < 2)
			return;

		int lLastIndex = 1;

		final int ARRAY_SIZE = pAmt; // pVertexArray.length;
		for (int i = 0; i < ARRAY_SIZE - 1; i++) {
			if (pVertexArray[i] == null || pVertexArray[i + 1] == null)
				continue;
			float px0 = pVertexArray[i].x;
			float py0 = pVertexArray[i].y;
			float px1 = pVertexArray[i + 1].x;
			float py1 = pVertexArray[i + 1].y;

			lLastIndex++;

			draw(px0, py0, px1, py1, pZ, pR, pG, pB); // top
		}

		if (pClose) {
			if (pVertexArray[0] != null && pVertexArray[lLastIndex - 1] != null) {
				float px0 = pVertexArray[0].x;
				float py0 = pVertexArray[0].y;
				float px1 = pVertexArray[lLastIndex - 1].x;
				float py1 = pVertexArray[lLastIndex - 1].y;

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
		if (!mIsLoaded)
			return;

		if (mVertexCount == 0)
			return;

		mBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePC.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePC.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.colorByteOffset);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

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
