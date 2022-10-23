package net.lintford.library.core.graphics.batching;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
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
import net.lintford.library.core.maths.Matrix4f;

public class SubPixelTextureBatch {

	private static class VertexDefinition {

		public static final int elementBytes = 4;

		public static final int positionElementCount = 4;
		public static final int colorElementCount = 4;
		public static final int textureElementCount = 2;
		public static final int textureIndexElementCount = 1;

		public static final int elementCount = positionElementCount + colorElementCount + textureElementCount + textureIndexElementCount;

		public static final int positionBytesCount = positionElementCount * elementBytes;
		public static final int colorBytesCount = colorElementCount * elementBytes;
		public static final int textureBytesCount = textureElementCount * elementBytes;
		public static final int textureIndexBytesCount = textureIndexElementCount * elementBytes;

		public static final int positionByteOffset = 0;
		public static final int colorByteOffset = positionByteOffset + positionBytesCount;
		public static final int textureByteOffset = colorByteOffset + colorBytesCount;
		public static final int textureIndexByteOffset = textureByteOffset + textureBytesCount;

		public static final int stride = positionBytesCount + colorBytesCount + textureBytesCount + textureIndexBytesCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 10000;
	protected static final int NUM_VERTICES_PER_SPRITE = 4;
	protected static final int NUM_INDICES_PER_SPRITE = 6;

	protected static final int MAX_VERTEX_COUNT = MAX_SPRITES * NUM_VERTICES_PER_SPRITE;
	protected static final int MAX_INDEX_COUNT = MAX_SPRITES * NUM_INDICES_PER_SPRITE;

	protected static final String VERT_FILENAME = "/res/shaders/shader_batch_pct.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_subpixel_pct.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final TextureSlotBatch mTextureSlots = new TextureSlotBatch();

	protected ICamera mCamera;
	protected ShaderSubPixel mShader;

	protected FloatBuffer mBuffer;
	protected IntBuffer mIndexBuffer;

	protected Matrix4f mModelMatrix;
	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVioId = -1;

	protected ResourceManager mResourceManager;
	private boolean mResourcesLoaded;
	protected boolean mIsDrawing;

	protected int mIndexCount;

	private boolean _countDebugStats = true;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void _countDebugStats(boolean enableCountStats) {
		_countDebugStats = enableCountStats;
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
		mShader = new ShaderSubPixel("ShaderSubPixel", VERT_FILENAME, FRAG_FILENAME);

		mModelMatrix = new Matrix4f();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		if (mResourcesLoaded)
			return;

		mResourceManager = pResourceManager;

		mShader.loadResources(pResourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		if (mVioId == -1)
			mVioId = GL15.glGenBuffers();

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDefinition.elementCount);
		mIndexBuffer = MemoryUtil.memAllocInt(MAX_SPRITES * NUM_INDICES_PER_SPRITE);

		initializeGlContent();

		mResourcesLoaded = true;

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	private void initializeGlContent() {
		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDefinition.stride, GL15.GL_DYNAMIC_DRAW);

		{ // prepare the static indices
			mIndexBuffer.clear();
			for (int i = 0; i < MAX_SPRITES; i += NUM_VERTICES_PER_SPRITE) {
				mIndexBuffer.put(i + 1);
				mIndexBuffer.put(i + 0);
				mIndexBuffer.put(i + 2);

				mIndexBuffer.put(i + 2);
				mIndexBuffer.put(i + 0);
				mIndexBuffer.put(i + 3);

			}
			mIndexBuffer.flip();
		}

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer, GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, VertexDefinition.colorElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.colorByteOffset);

		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);

		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, VertexDefinition.textureIndexElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureIndexByteOffset);

		GL30.glBindVertexArray(0);
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

		mShader.unloadResources();
		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			mVboId = -1;
		}

		if (mVioId > -1) {
			GL15.glDeleteBuffers(mVioId);
			mVioId = -1;
		}

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			mVaoId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		if (mIndexBuffer != null) {
			mIndexBuffer.clear();
			MemoryUtil.memFree(mIndexBuffer);
			mIndexBuffer = null;
		}

		mResourcesLoaded = false;
		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	// ---

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return;

		mCamera = pCamera;

		if (mBuffer != null)
			mBuffer.clear();

		mIndexCount = 0;
		mIsDrawing = true;
	}

	public void draw(Texture pTexture, Rectangle pSrcRect, Rectangle pDestRect, float zDepth, Color color) {
		if (pSrcRect == null || pDestRect == null)
			return;

		draw(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.width(), pSrcRect.height(), pDestRect, zDepth, color);
	}

	public void draw(Texture pTexture, Rectangle pSrcRect, float pDX, float pDY, float pDW, float pDH, float zDepth, Color color) {
		if (pSrcRect == null)
			return;

		draw(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.width(), pSrcRect.height(), pDX, pDY, pDW, pDH, zDepth, color);
	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float zDepth, Color color) {
		if (!mResourcesLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES)
			pTexture = mResourceManager.textureManager().textureNotFound();

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();

		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		}

		final var lVertList = pDestRect.getVertices();

		float x0 = lVertList.get(0).x;
		float y0 = lVertList.get(0).y;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = pSY / pTexture.getTextureHeight();

		float x1 = lVertList.get(1).x;
		float y1 = lVertList.get(1).y;
		float u1 = (pSX + pSW) / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		float x2 = lVertList.get(2).x;
		float y2 = lVertList.get(2).y;
		float u2 = pSX / pTexture.getTextureWidth();
		float v2 = (pSY + pSH) / pTexture.getTextureHeight();

		float x3 = lVertList.get(3).x;
		float y3 = lVertList.get(3).y;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		addVertToBuffer(x1, y1, zDepth, 1f, color.r, color.g, color.b, color.a, u1, v1, lTextureSlotIndex);
		addVertToBuffer(x0, y0, zDepth, 1f, color.r, color.g, color.b, color.a, u0, v0, lTextureSlotIndex);
		addVertToBuffer(x2, y2, zDepth, 1f, color.r, color.g, color.b, color.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x1, y1, zDepth, 1f, color.r, color.g, color.b, color.a, u1, v1, lTextureSlotIndex);
		addVertToBuffer(x2, y2, zDepth, 1f, color.r, color.g, color.b, color.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x3, y3, zDepth, 1f, color.r, color.g, color.b, color.a, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float zDepth, Color color) {
		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES)
			pTexture = mResourceManager.textureManager().textureNotFound();

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();

		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		}
		
		float x1 = pDX;
		float y1 = pDY;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		float x2 = pDX + pDW;
		float y2 = pDY;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		float x0 = pDX;
		float y0 = pDY + pDH;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		addVertToBuffer(x1, y1, zDepth, 1f, color.r, color.g, color.b, color.a, u1, v1, lTextureSlotIndex);
		addVertToBuffer(x0, y0, zDepth, 1f, color.r, color.g, color.b, color.a, u0, v0, lTextureSlotIndex);
		addVertToBuffer(x2, y2, zDepth, 1f, color.r, color.g, color.b, color.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x2, y2, zDepth, 1f, color.r, color.g, color.b, color.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x0, y0, zDepth, 1f, color.r, color.g, color.b, color.a, u0, v0, lTextureSlotIndex);
		addVertToBuffer(x3, y3, zDepth, 1f, color.r, color.g, color.b, color.a, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float zDepth, float pRot, float pROX, float pROY, float pScale, float pR, float pG, float pB, float pA) {
		if (!mResourcesLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES)
			pTexture = mResourceManager.textureManager().textureNotFound();

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();
		
		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		}

		float sin = (float) Math.sin(pRot);
		float cos = (float) Math.cos(pRot);

		float lHalfW = (pDW * pScale) / 2f;
		float lHalfH = (pDH * pScale) / 2f;

		// define the origin of this sprite
		// note: the rotation origin is not scaled with the sprite (this should be performed before calling this function)
		float originX = -pROX;
		float originY = -pROY;

		float x0 = (originX - lHalfW) * cos - (originY + lHalfH) * sin;
		float y0 = (originX - lHalfW) * sin + (originY + lHalfH) * cos;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		float x1 = (originX - lHalfW) * cos - (originY - lHalfH) * sin;
		float y1 = (originX - lHalfW) * sin + (originY - lHalfH) * cos;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		float x2 = (originX + lHalfW) * cos - (originY - lHalfH) * sin;
		float y2 = (originX + lHalfW) * sin + (originY - lHalfH) * cos;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		float x3 = (originX + lHalfW) * cos - (originY + lHalfH) * sin;
		float y3 = (originX + lHalfW) * sin + (originY + lHalfH) * cos;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		pDX += pROX;
		pDY += pROY;

		addVertToBuffer(pDX + x1, pDY + y1, zDepth, 1f, pR, pG, pB, pA, u1, v1, lTextureSlotIndex);
		addVertToBuffer(pDX + x0, pDY + y0, zDepth, 1f, pR, pG, pB, pA, u0, v0, lTextureSlotIndex);
		addVertToBuffer(pDX + x2, pDY + y2, zDepth, 1f, pR, pG, pB, pA, u2, v2, lTextureSlotIndex);
		addVertToBuffer(pDX + x2, pDY + y2, zDepth, 1f, pR, pG, pB, pA, u2, v2, lTextureSlotIndex);
		addVertToBuffer(pDX + x0, pDY + y0, zDepth, 1f, pR, pG, pB, pA, u0, v0, lTextureSlotIndex);
		addVertToBuffer(pDX + x3, pDY + y3, zDepth, 1f, pR, pG, pB, pA, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Circle dstCircle, float zDepth, Color color) {
		if (!mResourcesLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (pTexture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES)
			pTexture = mResourceManager.textureManager().textureNotFound();

		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(pTexture);
		}

		final int lNumPointsInCircle = 12;

		float angle = 0;
		float intervalSize = (float) (Math.PI * 2 / lNumPointsInCircle);
		for (int i = 0; i < lNumPointsInCircle; i++) {
			float x0 = dstCircle.centerX();
			float y0 = dstCircle.centerY();
			float u0 = 0.5f;
			float v0 = 0.5f;

			float x1 = dstCircle.centerX() + (float) Math.cos(angle + dstCircle.rotation()) * dstCircle.radius();
			float y1 = dstCircle.centerY() + (float) Math.sin(angle + dstCircle.rotation()) * dstCircle.radius();
			float u1 = 0.5f + ((float) Math.cos(angle) * 0.5f);
			float v1 = 0.5f + ((float) Math.sin(angle) * 0.5f);

			angle += intervalSize;

			float x2 = dstCircle.centerX() + (float) Math.cos(angle + dstCircle.rotation()) * dstCircle.radius();
			float y2 = dstCircle.centerY() + (float) Math.sin(angle + dstCircle.rotation()) * dstCircle.radius();
			float u2 = 0.5f + ((float) Math.cos(angle) * 0.5f);
			float v2 = 0.5f + ((float) Math.sin(angle) * 0.5f);

			addVertToBuffer(x1, y1, zDepth, 1f, color.r, color.g, color.b, color.a, u1, v1, lTextureSlotIndex);
			addVertToBuffer(x0, y0, zDepth, 1f, color.r, color.g, color.b, color.a, u0, v0, lTextureSlotIndex);
			addVertToBuffer(x2, y2, zDepth, 1f, color.r, color.g, color.b, color.a, u2, v2, lTextureSlotIndex);
		}

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	protected void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v, float texIndex) {
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

		mBuffer.put(texIndex);
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

		if (mIndexCount == 0)
			return;

		mBuffer.flip();

		GL30.glBindVertexArray(mVaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		mTextureSlots.bindTextures();

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		if (_countDebugStats) {
			final int lNumQuads = mIndexCount / NUM_INDICES_PER_SPRITE;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, lNumQuads * 4);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumQuads * 2);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);

		mBuffer.clear();
		mTextureSlots.clear();

		_countDebugStats = true;
	}

}