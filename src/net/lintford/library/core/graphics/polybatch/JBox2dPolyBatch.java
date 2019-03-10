package net.lintford.library.core.graphics.polybatch;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

public class JBox2dPolyBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_VERTS = 2048;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	// The number of bytes an element has (all elements are floats here)
	protected static final int elementBytes = 4;

	// Elements per parameter
	protected static final int positionElementCount = 4;
	protected static final int colorElementCount = 4;
	protected static final int textureElementCount = 2;

	// Bytes per parameter
	protected static final int positionBytesCount = positionElementCount * elementBytes;
	protected static final int colorBytesCount = colorElementCount * elementBytes;
	protected static final int textureBytesCount = textureElementCount * elementBytes;

	// Byte offsets per parameter
	protected static final int positionByteOffset = 0;
	protected static final int colorByteOffset = positionByteOffset + positionBytesCount;
	protected static final int textureByteOffset = colorByteOffset + colorBytesCount;

	// The amount of elements that a vertex has
	protected static final int elementCount = positionElementCount + colorElementCount + textureElementCount;

	// The size of a vertex in bytes (sizeOf())
	protected static final int stride = positionBytesCount + colorBytesCount + textureBytesCount;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mCurrentTexID;
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
	protected boolean mUseCheckerPattern;
	protected ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// ------------------------------------

	public boolean useCheckerPattern() {
		return mUseCheckerPattern;
	}

	public void useCheckerPattern(boolean pNewValue) {
		mUseCheckerPattern = pNewValue;
	}

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dPolyBatch() {
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
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		mResourceManager = pResourceManager;

		mShader.loadGLContent(pResourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().i(getClass().getSimpleName(), "glGenBuffers: " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_VERTS * stride);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (mIsLoaded)
			return;

		mShader.unloadGLContent();

		if (mVboId != -1)
			GL15.glDeleteBuffers(mVboId);

		if (mVaoId != -1)
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
		if (!mIsLoaded)
			return;

		if (pCamera == null)
			return;

		if (mIsDrawing)
			return;

		mCamera = pCamera;

		mBuffer.clear();
		mVertexCount = 0;
		mIsDrawing = true;

	}

	public void drawPolygon(Texture pTexture, Vector2f[] pVertexArray, float pSX, float pSY, float pSW, float pSH, float pZ, float pR, float pG, float pB, float pA) {
		if (!mIsLoaded || !mIsDrawing)
			return;

		// TODO: Only supports polygons with 4 vertices so far (we don't actually need more for now) ...
		if (pVertexArray == null || pVertexArray.length < 4)
			return;

		if (pTexture == null) {
			// Resolve to use a default texture, or the 'MISSING_TEXTURE'
			if (TextureManager.USE_DEBUG_MISSING_TEXTURES) {
				pTexture = mResourceManager.textureManager().textureNotFound();
				if (pTexture == null)
					return;

			} else {
				return;

			}
		}

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mUseCheckerPattern) {
			pTexture = mResourceManager.textureManager().checkerIndexedTexture();

		}

		// Vertex 0
		float x0 = pVertexArray[0].x;
		float y0 = pVertexArray[0].y;
		float u0 = (pSX + pSW) / pTexture.getTextureWidth();
		float v0 = pSY / pTexture.getTextureHeight();

		// Vertex 1
		float x1 = pVertexArray[1].x;
		float y1 = pVertexArray[1].y;
		float u1 = (pSX) / pTexture.getTextureWidth();
		float v1 = (pSY) / pTexture.getTextureHeight();

		// Vertex 2
		float x2 = pVertexArray[2].x;
		float y2 = pVertexArray[2].y;
		float u2 = pSX / pTexture.getTextureWidth();
		float v2 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = pVertexArray[3].x;
		float y3 = pVertexArray[3].y;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		// -- CCW 102123
		// CW 120132
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2

	}

	private void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v) {
		if (mVertexCount >= MAX_VERTS) {
			flush();
		}

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
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, positionElementCount, GL11.GL_FLOAT, false, stride, positionByteOffset);
		GL20.glVertexAttribPointer(1, colorElementCount, GL11.GL_FLOAT, false, stride, colorByteOffset);
		GL20.glVertexAttribPointer(2, textureElementCount, GL11.GL_FLOAT, false, stride, textureByteOffset);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		int_redraw();

		mBuffer.clear();
		mVertexCount = 0;

	}

	public void redraw() {
		if (mVertexCount == 0)
			return;

		GL30.glBindVertexArray(mVaoId);

		int_redraw();

	}

	private void int_redraw() {
		if (mCurrentTexID != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		}

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mShader.unbind();
	}

}
