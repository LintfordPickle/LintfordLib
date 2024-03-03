package net.lintfordlib.core.graphics.batching;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PCT;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.graphics.textures.TextureManager;
import net.lintfordlib.core.maths.Matrix4f;
import net.lintfordlib.core.maths.Vector2f;

public class TextureBatchPCT {

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
	protected static final String FRAG_FILENAME = "/res/shaders/shader_batch_pct.frag";

	private static IntBuffer mIndexBuffer;

	// @formatter:off
	//  1 ---- 2
	//  |      |
	//  |      |
	//  0------3
	// @formatter:on

	private static IntBuffer getIndexBuffer() {
		if (mIndexBuffer == null) {
			mIndexBuffer = MemoryUtil.memAllocInt(MAX_SPRITES * NUM_INDICES_PER_SPRITE);

			mIndexBuffer.clear();
			for (int i = 0; i < MAX_SPRITES; i++) {
				final int offset = i * NUM_VERTICES_PER_SPRITE;
				mIndexBuffer.put(offset + 1);
				mIndexBuffer.put(offset + 0);
				mIndexBuffer.put(offset + 2);

				mIndexBuffer.put(offset + 2);
				mIndexBuffer.put(offset + 0);
				mIndexBuffer.put(offset + 3);
			}
			mIndexBuffer.flip();
		}

		return mIndexBuffer;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final TextureSlotBatch mTextureSlots = new TextureSlotBatch();

	protected ICamera mCamera;
	protected ShaderMVP_PCT mShader;
	protected ShaderMVP_PCT mCustomShader;

	protected FloatBuffer mBuffer;

	private boolean mBlendEnabled;
	private int mBlendFuncSrcFactor;
	private int mBlendFuncDstFactor;

	protected Matrix4f mModelMatrix;
	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVioId = -1;

	protected ResourceManager mResourceManager;
	private boolean mResourcesLoaded;
	private boolean mAreGlContainersInitialized = false;
	protected boolean mIsDrawing;

	protected int mIndexCount;

	private boolean _countDebugStats;

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

	public void modelMatrix(Matrix4f newModelMatrix) {
		if (newModelMatrix == null) {
			mModelMatrix = new Matrix4f();
			mModelMatrix.setIdentity();
		} else {
			mModelMatrix = newModelMatrix;
		}
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	public void setGlBlendEnabled(boolean blendEnabled) {
		mBlendEnabled = blendEnabled;
	}

	public void setGlBlendFactor(int sourceFactor, int destFactor) {
		mBlendFuncSrcFactor = sourceFactor;
		mBlendFuncDstFactor = destFactor;
	}

	public ICamera camera() {
		return mCamera;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TextureBatchPCT() {
		mShader = new ShaderMVP_PCT("TextureBatchShader", VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int shaderID) {
				GL20.glBindAttribLocation(shaderID, 0, "inPosition");
				GL20.glBindAttribLocation(shaderID, 1, "inColor");
				GL20.glBindAttribLocation(shaderID, 2, "inTexCoord");
				GL20.glBindAttribLocation(shaderID, 3, "inTexIndex");
			}

			@Override
			protected void getUniformLocations() {
				super.getUniformLocations();

				final var lIntBuffer = BufferUtils.createIntBuffer(8);
				lIntBuffer.put(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 });
				lIntBuffer.flip();

				int lTextureSamplerLocation = GL20.glGetUniformLocation(shaderID(), "textureSampler");
				GL20.glUniform1iv(lTextureSamplerLocation, lIntBuffer);
			}
		};

		mModelMatrix = new Matrix4f();

		mBlendEnabled = true;
		mBlendFuncSrcFactor = GL11.GL_SRC_ALPHA;
		mBlendFuncDstFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mResourceManager = resourceManager;

		mShader.loadResources(resourceManager);

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDefinition.elementCount);
		getIndexBuffer();

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vbo " + mVboId);
		}

		if (mVioId == -1) {
			mVioId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vio " + mVioId);
		}

		mResourcesLoaded = true;

		if (resourceManager.isMainOpenGlThread())
			initializeGlContainers();

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	/**
	 * OpenGl container objects (Array objects/framebuffers/program pipeline/transform feedback) are not shared between OpenGl contexts and must be created on the main thread.
	 */
	private void initializeGlContainers() {
		if (!mResourcesLoaded) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Cannot create Gl containers until resources have been loaded");
			return;
		}

		if (mAreGlContainersInitialized)
			return;

		if (mVaoId == -1) {
			mVaoId = GL30.glGenVertexArrays();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenVertexArrays: " + mVaoId);
		}

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDefinition.stride, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, VertexDefinition.colorElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.colorByteOffset);

		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);

		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, VertexDefinition.textureIndexElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureIndexByteOffset);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer(), GL15.GL_STATIC_DRAW);

		GL30.glBindVertexArray(0);
		mAreGlContainersInitialized = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteVertexArrays: " + mVaoId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteBuffers VboId: " + mVboId);
			mVboId = -1;
		}

		if (mVioId > -1) {
			GL15.glDeleteBuffers(mVioId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteBuffers VioId: " + mVioId);
			mVioId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		mAreGlContainersInitialized = false;
		mResourcesLoaded = false;

		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		begin(pCamera, mShader);
	}

	public void begin(ICamera pCamera, ShaderMVP_PCT pCustomShader) {
		if (pCamera == null)
			return;

		if (mIsDrawing) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Already drawing!");
			return;
		}

		if (pCustomShader != null)
			mCustomShader = pCustomShader;
		else
			mCustomShader = mShader;

		mCamera = pCamera;

		if (mBuffer != null)
			mBuffer.clear();

		mIndexCount = 0;
		mIsDrawing = true;
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

		if (!mAreGlContainersInitialized)
			initializeGlContainers();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		if (mBlendEnabled) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(mBlendFuncSrcFactor, mBlendFuncDstFactor);
		} else {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		mTextureSlots.bindTextures();

		mCustomShader.projectionMatrix(mCamera.projection());
		mCustomShader.viewMatrix(mCamera.view());
		mCustomShader.modelMatrix(mModelMatrix);

		mCustomShader.bind();

		if (_countDebugStats) {
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);

			final int lNumQuads = mIndexCount / NUM_INDICES_PER_SPRITE;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, lNumQuads * 4);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumQuads * 2);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);

		mCustomShader.unbind();

		mBuffer.clear();
		mIndexCount = 0;

		mTextureSlots.clear();

		_countDebugStats = true;
	}

	// ---

	public void draw(Texture pTexture, Rectangle pSrcRect, Rectangle pDestRect, float pZ, Color pTint) {
		if (pSrcRect == null || pDestRect == null)
			return;

		draw(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.width(), pSrcRect.height(), pDestRect, pZ, pTint);
	}

	public void draw(Texture pTexture, Rectangle pSrcRect, float pDX, float pDY, float pDW, float pDH, float pZ, Color pTint) {
		if (pSrcRect == null)
			return;

		draw(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.width(), pSrcRect.height(), pDX, pDY, pDW, pDH, pZ, pTint);
	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float pZ, Color pTint) {
		if (pDestRect == null)
			return;

		draw(pTexture, pSX, pSY, pSW, pSH, pDestRect.x(), pDestRect.y(), pDestRect.width(), pDestRect.height(), pZ, pTint);
	}

	public void draw(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ, Color pTint) {
		if (!mIsDrawing)
			return;

		if (mCamera.boundingRectangle().intersectsAA(pDX, pDY, pDW, pDH) == false)
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

		float x0 = pDX;
		float y0 = pDY + pDH;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		float x1 = pDX;
		float y1 = pDY;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		float x2 = pDX + pDW;
		float y2 = pDY;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		addVertToBuffer(x0, y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0, lTextureSlotIndex);
		addVertToBuffer(x1, y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1, lTextureSlotIndex);
		addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x3, y3, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	public void draw(RenderTarget pTexture, float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float pZ, Color pTint) {
		if (pDestRect == null)
			return;

		draw(pTexture, pSX, pSY, pSW, pSH, pDestRect.x(), pDestRect.y(), pDestRect.width(), pDestRect.height(), pZ, pTint);
	}

	public void draw(RenderTarget texture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ, Color pTint) {
		if (!mIsDrawing)
			return;

		if (texture == null)
			return;

		if (mCamera.boundingRectangle().intersectsAA(pDX, pDY, pDW, pDH) == false)
			return;

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();

		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(texture.colorTextureID());
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(texture.colorTextureID());
		}

		float x0 = pDX;
		float y0 = pDY + pDH;
		float u0 = pSX / (float)texture.width();
		float v0 = (pSY + pSH) / (float)texture.height();

		float x1 = pDX;
		float y1 = pDY;
		float u1 = pSX / (float)texture.width();
		float v1 = pSY / (float)texture.height();

		float x2 = pDX + pDW;
		float y2 = pDY;
		float u2 = (pSX + pSW) / (float)texture.width();
		float v2 = pSY / (float)texture.height();

		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (pSX + pSW) / (float)texture.width();
		float v3 = (pSY + pSH) / (float)texture.height();

		addVertToBuffer(x0, y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0, lTextureSlotIndex);
		addVertToBuffer(x1, y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1, lTextureSlotIndex);
		addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x3, y3, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	public void drawQuadrilateral(Texture texture, Rectangle srcRect, List<Vector2f> dstPoints, float zDepth, Color colorTint) {
		drawQuadrilateral(texture, srcRect.x(), srcRect.y(), srcRect.width(), srcRect.height(), dstPoints, zDepth, colorTint);
	}

	public void drawQuadrilateral(Texture texture, float sx, float sy, float sw, float sh, List<Vector2f> dstPoints, float pZ, Color pTint) {
		if (dstPoints == null || dstPoints.size() != 4)
			return; // Quadrilateral requires exactly 4 points

		if (!mIsDrawing)
			return;

		if (texture == null && TextureManager.USE_DEBUG_MISSING_TEXTURES)
			texture = mResourceManager.textureManager().textureNotFound();

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();

		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(texture);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(texture);
		}

		float x0 = dstPoints.get(0).x;
		float y0 = dstPoints.get(0).y;
		float u0 = sx / texture.getTextureWidth();
		float v0 = (sy + sh) / texture.getTextureHeight();

		float x1 = dstPoints.get(1).x;
		float y1 = dstPoints.get(1).y;
		float u1 = sx / texture.getTextureWidth();
		float v1 = sy / texture.getTextureHeight();

		float x2 = dstPoints.get(2).x;
		float y2 = dstPoints.get(2).y;
		float u2 = (sx + sw) / texture.getTextureWidth();
		float v2 = sy / texture.getTextureHeight();

		float x3 = dstPoints.get(3).x;
		float y3 = dstPoints.get(3).y;
		float u3 = (sx + sw) / texture.getTextureWidth();
		float v3 = (sy + sh) / texture.getTextureHeight();

		addVertToBuffer(x0, y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0, lTextureSlotIndex);
		addVertToBuffer(x1, y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1, lTextureSlotIndex);
		addVertToBuffer(x2, y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x3, y3, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	public void drawAroundCenter(Texture pTexture, Rectangle pSrcRect, float pDX, float pDY, float pDW, float pDH, float pZ, float pRot, float pROX, float pROY, float pScale, Color pTint) {
		if (pSrcRect == null)
			return;

		drawAroundCenter(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.width(), pSrcRect.height(), pDX, pDY, pDW, pDH, pZ, pRot, pROX, pROY, pScale, pTint);
	}

	public void drawAroundCenter(Texture pTexture, float pSX, float pSY, float pSW, float pSH, float pDX, float pDY, float pDW, float pDH, float pZ, float pRot, float pROX, float pROY, float pScale, Color pTint) {

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

		float sin = (float) Math.sin(pRot);
		float cos = (float) Math.cos(pRot);

		float lHalfW = (pDW * pScale) / 2f;
		float lHalfH = (pDH * pScale) / 2f;

		// define the origin of this sprite
		// note: the rotation origin is not scaled with the sprite (this should be performed before calling this function)
		float originX = -pROX;
		float originY = -pROY;

		// Vertex 0 (bottom left)
		float x0 = -(lHalfW - originX) * cos - (lHalfH + originY) * sin;
		float y0 = -(lHalfW - originX) * sin + (lHalfH + originY) * cos;
		float u0 = pSX / pTexture.getTextureWidth();
		float v0 = (pSY + pSH) / pTexture.getTextureHeight();

		// Vertex 1 (top left)
		float x1 = -(lHalfW - originX) * cos - (-lHalfH + originY) * sin;
		float y1 = -(lHalfW - originX) * sin + (-lHalfH + originY) * cos;
		float u1 = pSX / pTexture.getTextureWidth();
		float v1 = pSY / pTexture.getTextureHeight();

		// Vertex 2 (top right)
		float x2 = (lHalfW + originX) * cos - (-lHalfH + originY) * sin;
		float y2 = (lHalfW + originX) * sin + (-lHalfH + originY) * cos;
		float u2 = (pSX + pSW) / pTexture.getTextureWidth();
		float v2 = pSY / pTexture.getTextureHeight();

		// Vertex 3 (bottom right)
		float x3 = (lHalfW + originX) * cos - (lHalfH + originY) * sin;
		float y3 = (lHalfW + originX) * sin + (lHalfH + originY) * cos;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth();
		float v3 = (pSY + pSH) / pTexture.getTextureHeight();

		addVertToBuffer(pDX + x0, pDY + y0, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u0, v0, lTextureSlotIndex);
		addVertToBuffer(pDX + x1, pDY + y1, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u1, v1, lTextureSlotIndex);
		addVertToBuffer(pDX + x2, pDY + y2, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u2, v2, lTextureSlotIndex);
		addVertToBuffer(pDX + x3, pDY + y3, pZ, 1f, pTint.r, pTint.g, pTint.b, pTint.a, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

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
}