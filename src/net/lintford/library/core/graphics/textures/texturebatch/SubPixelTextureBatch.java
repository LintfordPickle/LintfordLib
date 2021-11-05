package net.lintford.library.core.graphics.textures.texturebatch;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
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
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.shaders.ShaderSubPixel;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePCT;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.core.maths.Vector4f;

public class SubPixelTextureBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 16384;

	protected static final String VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_subpixel_pct.frag";

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

	protected Vector4f mTempVector;
	protected ICamera mCamera;
	protected ShaderSubPixel mShader;
	protected Matrix4f mModelMatrix;
	protected FloatBuffer mBuffer;
	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	protected int mCurrentTexID;
	protected int mCurNumSprites;
	private boolean mResourcesLoaded;
	protected boolean mIsDrawing;
	protected boolean mUseCheckerPattern;
	protected ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void recompileShader() {
		mShader.recompile();
	}

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
		return mResourcesLoaded;
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

	public SubPixelTextureBatch() {
		// TODO: Don't need this constructor params
		mShader = new ShaderSubPixel("ShaderSubPixel", VERT_FILENAME, FRAG_FILENAME);

		mModelMatrix = new Matrix4f();
		mTempVector = new Vector4f();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		if (mResourcesLoaded)
			return;

		mResourceManager = pResourceManager;

		mShader.loadResources(pResourceManager);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v("OpenGL", "SubPixelTextureBatch: VboId = " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTS_PER_SPRITE * stride);

		mResourcesLoaded = true;

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "SubPixelTextureBatch: Unloading VboId = " + mVboId);
			mVboId = -1;
		}

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "SubPixelTextureBatch: Unloading VaoId = " + mVaoId);
			mVaoId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
		}

		mResourcesLoaded = false;
		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_BATCH_OBJECTS);
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
			GL20.glEnableVertexAttribArray(2);

			GL20.glVertexAttribPointer(0, VertexDataStructurePCT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.positionByteOffset);
			GL20.glVertexAttribPointer(1, VertexDataStructurePCT.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.colorByteOffset);
			GL20.glVertexAttribPointer(2, VertexDataStructurePCT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.textureByteOffset);
		}
	}

	public void begin(ICamera pCamera) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return; // already drawing, don't want to flush too early

		mCurrentTexID = -1;
		mCamera = pCamera;

		if (mBuffer != null)
			mBuffer.clear();

		mVertexCount = 0;
		mCurNumSprites = 0;
		mIsDrawing = true;

	}

	public void draw(Texture pTexture, Rectangle pSrcRect, Rectangle pDestRect, float pZ, Color pTint) {
		if (pSrcRect == null || pDestRect == null)
			return;
		draw(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.w(), pSrcRect.h(), pDestRect, pZ, pTint);

	}

	public void draw(Texture pTexture, Rectangle pSrcRect, float pDX, float pDY, float pDW, float pDH, float pZ, Color pTint) {
		if (pSrcRect == null)
			return;

		draw(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.w(), pSrcRect.h(), pDX, pDY, pDW, pDH, pZ, pTint);

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float pZ, Color pTint) {
		if (!mResourcesLoaded)
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
		addVertToBuffer(x1, y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2); // 2
		addVertToBuffer(x1, y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u3, v3); // 3

		mCurNumSprites++;

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ, Color pTint) {
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
		addVertToBuffer(x1, y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2); // 2
		addVertToBuffer(x0, y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0); // 0
		addVertToBuffer(x3, y3, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u3, v3); // 3

		mCurNumSprites++;

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ, float pRot, float pROX, float pROY, float pScale, float pR, float pG, float pB, float pA) {
		if (!mResourcesLoaded)
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

		float sin = (float) Math.sin(pRot);
		float cos = (float) Math.cos(pRot);

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
		addVertToBuffer(pDX + x1, pDY + y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(pDX + x0, pDY + y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(pDX + x2, pDY + y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(pDX + x2, pDY + y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(pDX + x0, pDY + y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(pDX + x3, pDY + y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3

		mCurNumSprites++;

	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Circle dstCircle, float pZ, Color pTint) {
		if (!mResourcesLoaded)
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

			addVertToBuffer(x1, y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1); // 1
			addVertToBuffer(x0, y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0); // 0
			addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2); // 2
		}

		mCurNumSprites++;

	}

	protected void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v) {
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

		flush();
		mIsDrawing = false;

	}

	protected void flush() {
		if (!mResourcesLoaded || !mIsDrawing)
			return;

		if (mVertexCount == 0)
			return;

		mBuffer.flip();

		initializeGlContent();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		int_redraw();

		mBuffer.clear();

		mCurNumSprites = 0;
		mVertexCount = 0;
	}

	public void redraw() {
		if (mVertexCount == 0)
			return;

		initializeGlContent();

		GL30.glBindVertexArray(mVaoId);

		int_redraw();
	}

	private void int_redraw() {
		if (mCurrentTexID != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mVertexCount);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, mVertexCount / 3);
		}

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL30.glBindVertexArray(0);

		mShader.unbind();

	}
}