package net.lintford.library.core.graphics.linebatch;

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
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePC;
import net.lintford.library.core.maths.Matrix4f;

public class LineBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_LINES = 2048;
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
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mIsLoaded;
	private int mGLLineType;
	private float mGLLineWidth;
	private boolean mAntiAliasing;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void lineAntialiasing(boolean pEnableSmoothing) {
		mAntiAliasing = pEnableSmoothing;
	}

	public boolean lineAntialiasing() {
		return mAntiAliasing;
	}

	public void lineWidth(float pNewWidth) {
		mGLLineWidth = pNewWidth;
	}

	public float lineWidth() {
		return mGLLineWidth;
	}

	/** Sets the line type to use by OpenGL. Choices are either GL11.GL_LINE_STRIP or GL11.GL_LINES */
	public void lineType(int pGLLineType) {
		mGLLineType = pGLLineType;

		if (mGLLineType != GL11.GL_LINE_STRIP && mGLLineType != GL11.GL_LINES && mGLLineType != GL11.GL_POINTS) {
			mGLLineType = GL11.GL_LINES;
		}

	}

	public int lineType() {
		return mGLLineType;

	}

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LineBatch() {
		mShader = new ShaderMVP_PT("ShaderMVP_PT", VERT_FILENAME, FRAG_FILENAME) {
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

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		mShader.loadGLContent(pResourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		mBuffer = MemoryUtil.memAllocFloat(MAX_LINES * NUM_VERTS_PER_LINE * VertexDataStructurePC.stride);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		mShader.unloadGLContent();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		if (mVboId > -1)
			GL15.glDeleteBuffers(mVboId);

		if (mVaoId > -1)
			GL30.glDeleteVertexArrays(mVaoId);

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

	public void drawRect(Rectangle pRect, float pZ) {
		if (!mIsDrawing)
			return;

		drawRect(pRect, 1f, pZ);

	}

	public void drawRect(Rectangle pRect, float pScale, float pZ) {
		if (!mIsDrawing)
			return;

		drawRect(pRect, 0f, 0f, pScale, pZ);
	}

	public void drawRect(Rectangle pRect, float pOX, float pOY, float pScale, float pZ) {
		if (!mIsDrawing)
			return;

		drawRect(pRect, pOX, pOY, pScale, pZ, 1f, 1f, 1f);

	}

	// FIXME: There is something wrong with the origin offsets
	public void drawRect(Rectangle pRect, float pOX, float pOY, float pScale, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;

		final float lModWidth = pRect.width() * pScale;
		final float lModHeight = pRect.height() * pScale;

		final float lModX = pRect.left() - pOX * pScale;
		final float lModY = pRect.top() - pOY * pScale;

		draw(lModX, lModY, lModX + lModWidth, lModY, pZ, pR, pG, pB); // top
		draw(lModX, lModY + lModHeight, lModX + lModWidth, lModY + lModHeight, pZ, pR, pG, pB); // bottom
		draw(lModX, lModY, lModX, lModY + lModHeight, pZ, pR, pG, pB); // left
		draw(lModX + lModWidth, lModY, lModX + lModWidth, lModY + lModHeight, pZ, pR, pG, pB); // right
	}

	public void drawRect(float pX, float pY, float pW, float pH, float pZ) {
		if (!mIsDrawing)
			return;
		draw(pX, pY, pX + pW, pY, pZ, r, g, b); // top
		draw(pX, pY + pH, pX + pW, pY + pH, pZ, r, g, b); // bottom

		draw(pX, pY, pX, pY + pH, pZ, r, g, b); // left
		draw(pX + pW, pY, pX + pW, pY + pH, pZ, r, g, b); // right
	}

	public void drawRect(float pX, float pY, float pW, float pH, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;
		draw(pX, pY, pX + pW, pY, pZ, pR, pG, pB); // top
		draw(pX, pY + pH, pX + pW, pY + pH, pZ, pR, pG, pB); // bottom

		draw(pX, pY, pX, pY + pH, pZ, pR, pG, pB); // left
		draw(pX + pW, pY, pX + pW, pY + pH, pZ, pR, pG, pB); // right
	}

	public void drawArrowDown(float pX, float pY, float pW, float pH, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;
		draw(pX, pY, pX + pW, pY, pZ, pR, pG, pB);
		draw(pX, pY, pX + pW * 0.5f, pY + pH, pZ, pR, pG, pB);
		draw(pX + pW, pY, pX + pW * 0.5f, pY + pH, pZ, pR, pG, pB);
	}

	public void drawArrowUp(float pX, float pY, float pW, float pH, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;
		draw(pX, pY + pH, pX + pW, pY + pH, pZ, pR, pG, pB);
		draw(pX, pY + pH, pX + pW * 0.5f, pY, pZ, pR, pG, pB);
		draw(pX + pW, pY + pH, pX + pW * 0.5f, pY, pZ, pR, pG, pB);
	}

	public void drawArrowLeft(float pX, float pY, float pW, float pH, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;
		draw(pX + pW, pY, pX + pW, pY + pH, pZ, pR, pG, pB);
		draw(pX, pY + pH * 0.5f, pX + pW, pY, pZ, pR, pG, pB);
		draw(pX, pY + pH * 0.5f, pX + pW, pY + pH, pZ, pR, pG, pB);
	}

	public void drawArrowRight(float pX, float pY, float pW, float pH, float pZ, float pR, float pG, float pB) {
		if (!mIsDrawing)
			return;
		draw(pX, pY, pX, pY + pH, pZ, pR, pG, pB);
		draw(pX + pW, pY + pH * 0.5f, pX, pY, pZ, pR, pG, pB);
		draw(pX + pW, pY + pH * 0.5f, pX, pY + pH, pZ, pR, pG, pB);
	}

	public void draw(float pP1X, float pP1Y, float pP2X, float pP2Y, float pZ, float pR, float pG, float pB) {

		if (!mIsDrawing)
			return;

		if (mVertexCount * 2 >= MAX_LINES) {
			flush();
		}

		draw(pP1X, pP1Y, pP2X, pP2Y, pZ, pR, pG, pB, a);

	}

	public void draw(float pP1X, float pP1Y, float pP2X, float pP2Y, float pZ, float pR, float pG, float pB, float pA) {

		if (!mIsDrawing)
			return;

		if (mVertexCount * 2 >= MAX_LINES) {
			flush();
		}

		// Add both vertices to the buffer
		draw(pP1X, pP1Y, pZ, pR, pG, pB, pA);
		draw(pP2X, pP2Y, pZ, pR, pG, pB, pA);

	}

	public void draw(float pP1X, float pP1Y, float pZ, float pR, float pG, float pB, float pA) {

		if (!mIsDrawing)
			return;

		if (mVertexCount * 2 >= MAX_LINES) {
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

	public void forceFlush() {
		flush();

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

		{

			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mVertexCount);

		}

		if (mAntiAliasing) {
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
		} else {
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		}

		GL11.glLineWidth(mGLLineWidth);
		GL11.glDrawArrays(mGLLineType, 0, mVertexCount);

		GL30.glBindVertexArray(0);

		mShader.unbind();

		mBuffer.clear();

		mVertexCount = 0;

	}

	public void changeColorNormalized(float pR, float pG, float pB, float pA) {
		r = pR;
		g = pG;
		b = pB;
		a = pA;

	}

}
