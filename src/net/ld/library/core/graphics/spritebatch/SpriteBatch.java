package net.ld.library.core.graphics.spritebatch;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.ld.library.core.camera.ICamera;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.shaders.ShaderMVP_PT;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Vector4f;

// TODO: Need to implement the spritebatch like TileSetRendererVBO, i.e. with separate shaders 
// TODO: Need to differentiate between batching and caching. Currently we are only batching sprites per frame, but not caching them!!
public class SpriteBatch {

	// =============================================
	// Constants
	// =============================================

	protected static final int MAX_SPRITES = 1000;

	// These default shaders are loaded from embedded resources
	protected static final String VERT_FILENAME = "/res/shaders/shader_basic.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_basic.frag";

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

	// =============================================
	// Variables
	// =============================================

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

	// =============================================
	// Properties
	// =============================================

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

	// =============================================
	// Constructor
	// =============================================

	public SpriteBatch() {
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

	// =============================================
	// Core-Methods
	// =============================================

	public void loadGLContent(ResourceManager pResourceManager) {
		mShader.loadGLContent();

		mVaoId = GL30.glGenVertexArrays();
		mVboId = GL15.glGenBuffers();

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		mShader.unloadGLContent();

		GL30.glDeleteVertexArrays(mVaoId);
		GL15.glDeleteBuffers(mVboId);

		mIsLoaded = false;
	}

	// =============================================
	// Methods
	// =============================================

	// FIXME: Need to make a nicer, more encompassing set of methods for the SpriteBatch rendering
	// FIXME(John): calling draw without first calling begin() doesn't fail gracefully - it crashes
	// TODO(John): The SpriteBatch doesn't actually allow to cache buffers between frames if there is no change.

	public void begin(ICamera pCamera) {
		if (mIsDrawing)
			return; // already drawing, don't want to flush too early

		mCurrentTexID = -1;
		mCamera = pCamera;

		mBuffer.clear();
		mVertexCount = 0;
		mCurNumSprites = 0;
		mIsDrawing = true;

	}

	public void draw(float pSX, float pSY, float pSW, float pSH, float x, float y, float pZ, float w, float h, float pScale, Texture pTexture) {
		draw(pSX, pSY, pSW, pSH, x, y, pZ, w, h, pScale, 1, 1, 1, 1, pTexture);
	}

	public void draw(float pSX, float pSY, float pSW, float pSH, float x, float y, float pZ, float w, float h, float pScale, float pAlpha, Texture pTexture) {
		draw(pSX, pSY, pSW, pSH, x, y, pZ, w, h, pScale, 1, 1, 1, pAlpha, pTexture);
	}

	public void draw(float pSX, float pSY, float pSW, float pSH, float x, float y, float pZ, float w, float h, float pScale, float pR, float pG, float pB, float pA, Texture pTexture) {
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
		float x0 = x;
		float y0 = y + h * pScale;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 1
		float x1 = x;
		float y1 = y;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 2
		float x2 = x + w * pScale;
		float y2 = y;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = x + w * pScale;
		float y3 = y + h * pScale;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0

		mCurNumSprites++;

	}

	public void draw(float pSX, float pSY, float pSW, float pSH, float x, float y, float pZ, float w, float h, float pR, float pG, float pB, float pA, float pRotation, float pROX, float pROY, float pScaleX, float pScaleY, Texture pTexture) {
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

		float sin = (float) (Math.sin(pRotation));
		float cos = (float) (Math.cos(pRotation));

		// Should be passed into method
		float dx = -pROX * pScaleX;
		float dy = -pROY * pScaleY;

		// Vertex 0
		float x0 = x + dx * cos - (dy + h * pScaleX) * sin;
		float y0 = y + dx * sin + (dy + h * pScaleY) * cos;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 1
		/////////////////////////
		float x1 = x + dx * cos - dy * sin;
		float y1 = y + dx * sin + dy * cos;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 2
		/////////////////////////
		float x2 = x + (dx + w * pScaleX) * cos - dy * sin;
		float y2 = y + (dx + w * pScaleY) * sin + dy * cos;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		// Vertex 3
		/////////////////////////
		float x3 = x + (dx + w * pScaleX) * cos - (dy + h * pScaleX) * sin;
		float y3 = y + (dx + w * pScaleY) * sin + (dy + h * pScaleY) * cos;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0

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

		GL20.glVertexAttribPointer(0, positionElementCount, GL11.GL_FLOAT, false, stride, positionByteOffset);
		GL20.glVertexAttribPointer(1, colorElementCount, GL11.GL_FLOAT, false, stride, colorByteOffset);
		GL20.glVertexAttribPointer(2, textureElementCount, GL11.GL_FLOAT, false, stride, textureByteOffset);

		// Is this needed (bind, unbind, rebind ??)
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL30.glBindVertexArray(mVaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mShader.unbind();

		mBuffer.clear();
		mCurNumSprites = 0;

		mCurNumSprites = 0;
		mVertexCount = 0;

	}

	public void cleanUp() {
		if (mVaoId != -1)
			GL15.glDeleteBuffers(mVaoId);

		if (mVboId != -1)
			GL15.glDeleteBuffers(mVboId);
	}
}
