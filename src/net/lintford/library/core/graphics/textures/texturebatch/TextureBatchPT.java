package net.lintford.library.core.graphics.textures.texturebatch;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.geometry.Circle;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePT;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.core.maths.Vector4f;

// TODO: Non of the Batch rendering classes are using indices I notice...
// TODO: The SpriteBatch doesn't actually allow to cache buffers between frames if there is no change (no vertex + transformations).
// TODO: Add Batch types (call, texture, Z-Order).
public class TextureBatchPT {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 2048;

	protected static final String VERT_FILENAME = "/res/shaders/shader_basic_pt.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_basic_pt.frag";

	protected static final int NUM_VERTS_PER_SPRITE = 6;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Vector4f mTempVector;
	protected ICamera mCamera;
	protected ShaderMVP_PT mShader;
	protected ShaderMVP_PT mCustomShader;
	protected Matrix4f mModelMatrix;
	protected FloatBuffer mBuffer;
	private boolean mBlendEnabled;
	private int mBlendFuncSrcFactor;
	private int mBlendFuncDstFactor;
	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	protected int mCurrentTexID;
	protected int mCurNumSprites;
	private boolean mIsLoaded;
	protected boolean mIsDrawing;
	protected boolean mUseCheckerPattern;
	protected ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean useCheckerPattern() {
		return mUseCheckerPattern;
	}

	public void useCheckerPattern(boolean pNewValue) {
		mUseCheckerPattern = pNewValue;
	}

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

	public TextureBatchPT() {
		mShader = new ShaderMVP_PT("TextureBatchShaderPT", VERT_FILENAME, FRAG_FILENAME);

		mModelMatrix = new Matrix4f();
		mTempVector = new Vector4f();

		mBlendEnabled = true;
		mBlendFuncSrcFactor = GL11.GL_SRC_ALPHA;
		mBlendFuncDstFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;

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

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTS_PER_SPRITE * VertexDataStructurePT.stride);

		mIsLoaded = true;

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		mShader.unloadGLContent();

		if (mVboId > -1)
			GL15.glDeleteBuffers(mVboId);

		if (mVaoId > -1)
			GL30.glDeleteVertexArrays(mVaoId);

		mVboId = -1;
		mVaoId = -1;

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);

		}

		mIsLoaded = false;
		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_BATCH_OBJECTS);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera, ShaderMVP_PT pCustomPTShader) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return; // already drawing, don't want to flush too early

		if (pCustomPTShader != null)
			mCustomShader = pCustomPTShader;
		else
			mCustomShader = mShader;

		mCurrentTexID = -1;
		mCamera = pCamera;

		if (mBuffer != null)
			mBuffer.clear();

		mVertexCount = 0;
		mCurNumSprites = 0;
		mIsDrawing = true;

	}

	public void begin(ICamera pCamera) {
		begin(pCamera, mShader);

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float pZ) {
		if (!mIsLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES) {
			pTexture = mResourceManager.textureManager().textureNotFound();

		}

		if (mUseCheckerPattern) {
			pTexture = mResourceManager.textureManager().checkerIndexedTexture();

		}

		if (pTexture != null) {
			if (mCurrentTexID == -1) {
				mCurrentTexID = pTexture.getTextureID();

			} else if (mCurrentTexID != pTexture.getTextureID()) {
				flush();
				mCurrentTexID = pTexture.getTextureID();

			}

		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		final List<Vector2f> lVertList = pDestRect.getVertices();

		// Vertex 0
		float x0 = lVertList.get(0).x;
		float y0 = lVertList.get(0).y;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = pSY / pTexture.getTextureHeight();

		// Vertex 1
		float x1 = lVertList.get(1).x;
		float y1 = lVertList.get(1).y;
		float u1 = (pSX + pSW) / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 2
		float x2 = lVertList.get(2).x;
		float y2 = lVertList.get(2).y;
		float u2 = pSX / pTexture.getTextureWidth();
		float v2 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = lVertList.get(3).x;
		float y3 = lVertList.get(3).y;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		// CCW 102123
		addVertToBuffer(x1, y1, pZ, 1f, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, u2, v2); // 2
		addVertToBuffer(x1, y1, pZ, 1f, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, u3, v3); // 3

		mCurNumSprites++;

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ) {
		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES) {
			pTexture = mResourceManager.textureManager().textureNotFound();

		}

		if (mUseCheckerPattern) {
			pTexture = mResourceManager.textureManager().checkerIndexedTexture();

		}

		if (pTexture != null) {
			if (mCurrentTexID == -1) {
				mCurrentTexID = pTexture.getTextureID();

			} else if (mCurrentTexID != pTexture.getTextureID()) {
				flush();
				mCurrentTexID = pTexture.getTextureID();

			}

		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();

		}

		// Vertex 0
		float x1 = pDX;
		float y1 = pDY;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 1
		float x2 = pDX + pDW;
		float y2 = pDY;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		// Vertex 2
		float x0 = pDX;
		float y0 = pDY + pDH;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		// 102203
		addVertToBuffer(x1, y1, pZ, 1f, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, u2, v2); // 2
		addVertToBuffer(x0, y0, pZ, 1f, u0, v0); // 0
		addVertToBuffer(x3, y3, pZ, 1f, u3, v3); // 3

		mCurNumSprites++;

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ, float pRot, float pROX, float pROY, float pScale) {
		if (!mIsLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES) {
			pTexture = mResourceManager.textureManager().textureNotFound();

		}

		if (pTexture != null) {
			if (mCurrentTexID == -1) {
				mCurrentTexID = pTexture.getTextureID();

			} else if (mCurrentTexID != pTexture.getTextureID()) {
				flush();
				mCurrentTexID = pTexture.getTextureID();

			}

		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		float sin = (float) (Math.sin(pRot));
		float cos = (float) (Math.cos(pRot));

		float lHalfW = (pDW * pScale) / 2f;
		float lHalfH = (pDH * pScale) / 2f;

		// define the origin of this sprite
		// note: the rotation origin is not scaled with the sprite (this should be performed before calling this function)
		float originX = -pROX;
		float originY = -pROY;

		// Vertex 0 (bottom left)
		float x0 = (originX - lHalfW) * cos - (originY + lHalfH) * sin;
		float y0 = (originX - lHalfW) * sin + (originY + lHalfH) * cos;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 1 (top left)
		float x1 = (originX - lHalfW) * cos - (originY - lHalfH) * sin;
		float y1 = (originX - lHalfW) * sin + (originY - lHalfH) * cos;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 2 (top right)
		float x2 = (originX + lHalfW) * cos - (originY - lHalfH) * sin;
		float y2 = (originX + lHalfW) * sin + (originY - lHalfH) * cos;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		// Vertex 3 (bottom right)
		float x3 = (originX + lHalfW) * cos - (originY + lHalfH) * sin;
		float y3 = (originX + lHalfW) * sin + (originY + lHalfH) * cos;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		// Apply the difference back to the global positions
		pDX += pROX;
		pDY += pROY;

		// CCW 102203
		addVertToBuffer(pDX + x1, pDY + y1, pZ, 1f, u1, v1); // 1
		addVertToBuffer(pDX + x0, pDY + y0, pZ, 1f, u0, v0); // 0
		addVertToBuffer(pDX + x2, pDY + y2, pZ, 1f, u2, v2); // 2
		addVertToBuffer(pDX + x2, pDY + y2, pZ, 1f, u2, v2); // 2
		addVertToBuffer(pDX + x0, pDY + y0, pZ, 1f, u0, v0); // 0
		addVertToBuffer(pDX + x3, pDY + y3, pZ, 1f, u3, v3); // 3

		mCurNumSprites++;

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Circle dstCircle, float pZ) {
		if (!mIsLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES) {
			pTexture = mResourceManager.textureManager().textureNotFound();

		}

		if (mUseCheckerPattern) {
			pTexture = mResourceManager.textureManager().checkerIndexedTexture();

		}

		if (pTexture != null) {
			if (mCurrentTexID == -1) {
				mCurrentTexID = pTexture.getTextureID();

			} else if (mCurrentTexID != pTexture.getTextureID()) {
				flush();
				mCurrentTexID = pTexture.getTextureID();

			}

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
			float x1 = dstCircle.centerX() + (float) Math.cos(angle + dstCircle.rotation) * dstCircle.radius;
			float y1 = dstCircle.centerY() + (float) Math.sin(angle + dstCircle.rotation) * dstCircle.radius;
			float u1 = 0.5f + ((float) Math.cos(angle) * 0.5f);
			float v1 = 0.5f + ((float) Math.sin(angle) * 0.5f);

			angle += intervalSize;

			// Vertex 2
			float x2 = dstCircle.centerX() + (float) Math.cos(angle + dstCircle.rotation) * dstCircle.radius;
			float y2 = dstCircle.centerY() + (float) Math.sin(angle + dstCircle.rotation) * dstCircle.radius;
			float u2 = 0.5f + ((float) Math.cos(angle) * 0.5f);
			float v2 = 0.5f + ((float) Math.sin(angle) * 0.5f);

			addVertToBuffer(x1, y1, pZ, 1f, u1, v1); // 1
			addVertToBuffer(x0, y0, pZ, 1f, u0, v0); // 0
			addVertToBuffer(x2, y2, pZ, 1f, u2, v2); // 2
		}

		mCurNumSprites++;

	}

	protected void addVertToBuffer(float x, float y, float z, float w, float u, float v) {
		// If the buffer is already full, we need to draw what is currently in the buffer and start a new one.
		if (mCurNumSprites >= MAX_SPRITES * NUM_VERTS_PER_SPRITE - 1) {
			flush();

		}

		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(u);
		mBuffer.put(v);

		mVertexCount++;

	}

	public void end() {
		if (!mIsDrawing)
			return;

		flush();
		mIsDrawing = false;

	}

	protected void flush() {
		if (!mIsLoaded || !mIsDrawing)
			return;

		if (mVertexCount == 0)
			return;

		mBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.textureByteOffset);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		int_redraw();

		mBuffer.clear();
		mCurNumSprites = 0;
		mVertexCount = 0;

	}

	public void redraw() {
		if (mVertexCount == 0)
			return;

		GL30.glBindVertexArray(mVaoId);

		int_redraw();

	}

	public void setGlBlendEnabled(boolean pBlendEnabled) {
		mBlendEnabled = pBlendEnabled;

	}

	public void setGlBlendFactor(int pSrcFactor, int pDstFactor) {
		mBlendFuncSrcFactor = pSrcFactor;
		mBlendFuncDstFactor = pDstFactor;
	}

	private void int_redraw() {
		if (mCurrentTexID != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		}

		if (mBlendEnabled) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(mBlendFuncSrcFactor, mBlendFuncDstFactor);

		} else {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		}

		mCustomShader.projectionMatrix(mCamera.projection());
		mCustomShader.viewMatrix(mCamera.view());
		mCustomShader.modelMatrix(mModelMatrix);

		mCustomShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mVertexCount);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, mVertexCount / 3);
		}

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mCustomShader.unbind();
	}

}