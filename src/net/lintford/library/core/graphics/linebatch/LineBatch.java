package net.lintford.library.core.graphics.linebatch;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePC;
import net.lintford.library.core.maths.Matrix4f;

public class LineBatch {

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
	private int mCurNumLines;
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

	public LineBatch() {
		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		a = r = g = b = 1f;

		mBuffer = BufferUtils.createFloatBuffer(MAX_LINES * NUM_VERTS_PER_LINE * VertexDataStructurePC.stride);

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

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (mIsLoaded)
			return;

		mShader.unloadGLContent();

		if (mVaoId > -1)
			GL30.glDeleteVertexArrays(mVaoId);

		if (mVboId > -1)
			GL15.glDeleteBuffers(mVboId);

		mVaoId = -1;
		mVboId = -1;

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
		mCurNumLines = 0;
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

		final float lModWidth = pRect.width() * pScale;
		final float lModHeight = pRect.height() * pScale;

		final float lModX = pRect.left() - pOX * pScale;
		final float lModY = pRect.top() - pOY * pScale;

		draw(lModX, lModY, lModX + lModWidth, lModY, pZ, 1f, 1f, 1f); // top
		draw(lModX, lModY + lModHeight, lModX + lModWidth, lModY + lModHeight, pZ, 1f, 1f, 1f); // bottom
		draw(lModX, lModY, lModX, lModY + lModHeight, pZ, 1f, 1f, 1f); // left
		draw(lModX + lModWidth, lModY, lModX + lModWidth, lModY + lModHeight, pZ, 1f, 1f, 1f); // right

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

	public void draw(float pP1X, float pP1Y, float pP2X, float pP2Y, float pZ, float pR, float pG, float pB) {

		if (!mIsDrawing)
			return;

		if (mCurNumLines >= MAX_LINES) {
			flush();
		}

		// Add both vertices to the buffer
		addVertToBuffer(pP1X, pP1Y, pZ, 1f, pR, pG, pB, a);
		addVertToBuffer(pP2X, pP2Y, pZ, 1f, pR, pG, pB, a);

		mCurNumLines++;

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

		GL11.glDrawArrays(GL11.GL_LINES, 0, mVertexCount);

		GL30.glBindVertexArray(0);

		mShader.unbind();

		mBuffer.clear();

		mCurNumLines = 0;
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
