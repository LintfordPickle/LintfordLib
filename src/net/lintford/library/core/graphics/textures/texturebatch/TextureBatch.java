package net.lintford.library.core.graphics.textures.texturebatch;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.geometry.Circle;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector4f;

// TODO: Non of the Batch rendering classes are using indices I notice...
// TODO: The SpriteBatch doesn't actually allow to cache buffers between frames if there is no change (no vertex + transformations).
// TODO: Add Batch types (call, texture, Z-Order).
public class TextureBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 2048;

	protected static final String VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	protected static final int NUM_VERTS_PER_SPRITE = 6;

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

	protected int mVaoId = -1;
	protected int mVboId = -1;
	protected int mVertexCount = 0;
	protected int mCurrentTexID;

	protected ICamera mCamera;
	protected ShaderMVP_PT mShader;
	protected Matrix4f mModelMatrix;
	protected FloatBuffer mBuffer;
	protected int mCurNumSprites;

	protected Vector4f mTempVector;

	protected boolean mIsLoaded;
	protected boolean mIsDrawing;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isDrawing() {
		return mIsDrawing;
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public void modelMatrix(Matrix4f pNewMatrix) {
		if (pNewMatrix == null) {
			mModelMatrix = new Matrix4f();
			mModelMatrix.setIdentity();
		} else {
			mModelMatrix = pNewMatrix;
		}
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TextureBatch() {
		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
				GL20.glBindAttribLocation(pShaderID, 2, "inTexCoord");
			}
		};

		mModelMatrix = new Matrix4f();
		mTempVector = new Vector4f();

		mBuffer = BufferUtils.createFloatBuffer(MAX_SPRITES * NUM_VERTS_PER_SPRITE * stride);
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
		if (!mIsLoaded)
			return;

		mShader.unloadGLContent();

		if (mVaoId > -1)
			GL30.glDeleteVertexArrays(mVaoId);

		if (mVboId > -1)
			GL15.glDeleteBuffers(mVboId);

		mIsLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return; // already drawing, don't want to flush too early

		mCurrentTexID = -1;
		mCamera = pCamera;

		mBuffer.clear();
		mVertexCount = 0;
		mCurNumSprites = 0;
		mIsDrawing = true;

	}

	public void draw(Texture pTexture, AARectangle pSrcRect, Rectangle pDestRect, float pZ, float pR, float pG, float pB, float pA) {
		draw(pTexture, pSrcRect.x, pSrcRect.y, pSrcRect.w, pSrcRect.h, pDestRect, pZ, pR, pG, pB, pA);
	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float pZ, float pR, float pG, float pB, float pA) {
		if (!mIsLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null)
			return;

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		// Vertex 0
		float x0 = pDestRect.getVertices()[0].x;
		float y0 = pDestRect.getVertices()[0].y;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = pSY / pTexture.getTextureHeight();

		// Vertex 1
		float x1 = pDestRect.getVertices()[1].x;
		float y1 = pDestRect.getVertices()[1].y;
		float u1 = (pSX + pSW) / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 2
		float x2 = pDestRect.getVertices()[2].x;
		float y2 = pDestRect.getVertices()[2].y;
		float u2 = pSX / pTexture.getTextureWidth();
		float v2 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = pDestRect.getVertices()[3].x;
		float y3 = pDestRect.getVertices()[3].y;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		// CCW 102123
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3

		mCurNumSprites++;
	}
	
	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ, float pR, float pG, float pB, float pA) {
		if (!mIsDrawing)
			return;

		if (pTexture == null)
			return;

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		
		// Vertex 0
		float x0 = pDX;
		float y0 = pDY;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = pSY / (float) pTexture.getTextureHeight();

		// Vertex 1
		float x1 = pDX + pDW;
		float y1 = pDY;
		float u1 = (pSX + pSW)  / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 2
		float x2 = pDX;
		float y2 = pDY + pDH;
		float u2 = pSX / pTexture.getTextureWidth();
		float v2 = (pSY + pSH)  / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		// CCW 102203
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3

		mCurNumSprites++;

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Circle dstCircle, float pZ, float pR, float pG, float pB, float pA) {
		if (!mIsLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null)
			return;

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		final int POINTS = 12;

		float angle = 0;
		float intervalSize = (float) (Math.PI * 2 / POINTS);
		for (int i = 0; i < POINTS; i++) {
			// Vertex 0
			float x0 = dstCircle.centerX();
			float y0 = dstCircle.centerY();
			float u0 = 0.5f;
			float v0 = 0.5f;

			// Vertex 1
			float x1 = dstCircle.centerX() + (float) Math.cos(angle + dstCircle.rotate()) * dstCircle.radius();
			float y1 = dstCircle.centerY() + (float) Math.sin(angle + dstCircle.rotate()) * dstCircle.radius();
			float u1 = 0.5f + ((float) Math.cos(angle) * 0.5f);
			float v1 = 0.5f + ((float) Math.sin(angle) * 0.5f);

			angle += intervalSize;

			// Vertex 2
			float x2 = dstCircle.centerX() + (float) Math.cos(angle + dstCircle.rotate()) * dstCircle.radius();
			float y2 = dstCircle.centerY() + (float) Math.sin(angle + dstCircle.rotate()) * dstCircle.radius();
			float u2 = 0.5f + ((float) Math.cos(angle) * 0.5f);
			float v2 = 0.5f + ((float) Math.sin(angle) * 0.5f);

			addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
			addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
			addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		}

		mCurNumSprites++;

	}

	private void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v) {
		// If the buffer is already full, we need to draw what is currently in the buffer and start a new one.
		if (mCurNumSprites >= MAX_SPRITES * NUM_VERTS_PER_SPRITE - 1) {
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

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, positionElementCount, GL11.GL_FLOAT, false, stride, positionByteOffset);
		GL20.glVertexAttribPointer(1, colorElementCount, GL11.GL_FLOAT, false, stride, colorByteOffset);
		GL20.glVertexAttribPointer(2, textureElementCount, GL11.GL_FLOAT, false, stride, textureByteOffset);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mShader.unbind();

		mBuffer.clear();

		mCurNumSprites = 0;
		mVertexCount = 0;

	}

}