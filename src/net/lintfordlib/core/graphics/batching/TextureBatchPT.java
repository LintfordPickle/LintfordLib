package net.lintfordlib.core.graphics.batching;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PT;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.graphics.textures.TextureManager;
import net.lintfordlib.core.maths.Matrix4f;

public class TextureBatchPT {

	private static class VertexDefinition {

		public static final int elementBytes = 4;

		public static final int positionElementCount = 4;
		public static final int textureElementCount = 2;
		public static final int textureIndexElementCount = 1;

		public static final int elementCount = positionElementCount + textureElementCount + textureIndexElementCount;

		public static final int positionBytesCount = positionElementCount * elementBytes;
		public static final int textureBytesCount = textureElementCount * elementBytes;
		public static final int textureIndexBytesCount = textureIndexElementCount * elementBytes;

		public static final int positionByteOffset = 0;
		public static final int textureByteOffset = positionByteOffset + positionBytesCount;
		public static final int textureIndexByteOffset = textureByteOffset + textureBytesCount;

		public static final int stride = positionBytesCount + textureBytesCount + textureIndexBytesCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 10000;
	protected static final int NUM_VERTICES_PER_SPRITE = 4;
	protected static final int NUM_INDICES_PER_SPRITE = 6;

	protected static final int MAX_VERTEX_COUNT = MAX_SPRITES * NUM_VERTICES_PER_SPRITE;
	protected static final int MAX_INDEX_COUNT = MAX_SPRITES * NUM_INDICES_PER_SPRITE;

	protected static final String VERT_FILENAME = "/res/shaders/shader_batch_pt.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_batch_pt.frag";

	private static IntBuffer mIndexBuffer;

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
	protected ShaderMVP_PT mShader;
	protected ShaderMVP_PT mCustomShader;

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

	private int mIndexCount;
	protected boolean mUseHalfPixelCorrection;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean useHalfPixelCorrection() {
		return mUseHalfPixelCorrection;
	}

	public void useHalfPixelCorrection(boolean useHalfPixelCorrection) {
		mUseHalfPixelCorrection = useHalfPixelCorrection;
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

	public void setGlBlendEnabled(boolean pBlendEnabled) {
		mBlendEnabled = pBlendEnabled;
	}

	public void setGlBlendFactor(int pSrcFactor, int pDstFactor) {
		mBlendFuncSrcFactor = pSrcFactor;
		mBlendFuncDstFactor = pDstFactor;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TextureBatchPT() {
		mShader = new ShaderMVP_PT("TextureBatchShaderPT", VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inTexCoord");
				GL20.glBindAttribLocation(pShaderID, 2, "inTexIndex");
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

	public void loadResources(ResourceManager pResourceManager) {
		if (mResourcesLoaded)
			return;

		mResourceManager = pResourceManager;

		mShader.loadResources(pResourceManager);

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDefinition.elementCount);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vbo " + mVboId);
		}

		if (mVioId == -1) {
			mVioId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vio " + mVioId);
		}

		mResourcesLoaded = true;

		if (pResourceManager.isMainOpenGlThread())
			initializeGlContainers();

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

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

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer(), GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);

		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, VertexDefinition.textureIndexElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureIndexByteOffset);

		GL30.glBindVertexArray(0);

		mAreGlContainersInitialized = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();
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

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteVertexArrays: " + mVaoId);
			mVaoId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		mResourcesLoaded = false;
		mAreGlContainersInitialized = false;

		Debug.debugManager().stats().decTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		begin(pCamera, mShader);
	}

	public void begin(ICamera pCamera, ShaderMVP_PT pCustomPTShader) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return;

		if (pCustomPTShader != null)
			mCustomShader = pCustomPTShader;
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

		mTextureSlots.bindTextures();

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

		if (Debug.debugManager().debugManagerEnabled()) {
			final int lNumQuads = mIndexCount / NUM_INDICES_PER_SPRITE;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, lNumQuads * 4);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumQuads * 2);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);

		mCustomShader.unbind();

		mBuffer.clear();
		mTextureSlots.clear();
		mIndexCount = 0;
	}

	// ---

	public void draw(Texture tex, Rectangle srcRect, Rectangle destRect, float zDepth) {
		if (srcRect == null)
			return;

		draw(tex, srcRect.x(), srcRect.y(), srcRect.width(), srcRect.height(), destRect, zDepth);
	}

	public void draw(Texture tex, float sx, float sy, float sw, float sh, Rectangle destRect, float zDepth) {
		if (destRect == null)
			return;

		draw(tex, sx, sy, sw, sh, destRect.x(), destRect.y(), destRect.width(), destRect.height(), zDepth);
	}

	public void draw(Texture tex, float sx, float sy, float sw, float sh, float dx, float dy, float dw, float dh, float zDepth) {
		if (!mIsDrawing)
			return;

		if (tex == null) {
			if (TextureManager.USE_DEBUG_MISSING_TEXTURES)
				tex = mResourceManager.textureManager().textureNotFound();
			else
				return;
		}

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();

		int lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(tex);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(tex);
		}

		final var texWidth = tex.getTextureWidth();
		final var texHeight = tex.getTextureHeight();

		final var pcx = mUseHalfPixelCorrection ? .5f : .0f;
		final var pcy = mUseHalfPixelCorrection ? .5f : .0f;

		float x0 = dx;
		float y0 = dy + dh;
		float u0 = (sx + pcx) / texWidth;
		float v0 = (sy + pcy) / texHeight;

		float x1 = dx;
		float y1 = dy;
		float u1 = (sx + pcx) / texWidth;
		float v1 = (sy + sh - pcy) / texHeight;

		float x2 = dx + dw;
		float y2 = dy;
		float u2 = (sx + sw - pcx) / texWidth;
		float v2 = (sy + sh - pcy) / texHeight;

		float x3 = dx + dw;
		float y3 = dy + dh;
		float u3 = (sx + sw - pcx) / texWidth;
		float v3 = (sy + pcy) / texHeight;

		addVertToBuffer(x0, y0, zDepth, 1f, u0, v0, lTextureSlotIndex);
		addVertToBuffer(x1, y1, zDepth, 1f, u1, v1, lTextureSlotIndex);
		addVertToBuffer(x2, y2, zDepth, 1f, u2, v2, lTextureSlotIndex);
		addVertToBuffer(x3, y3, zDepth, 1f, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	public void drawAroundCenter(Texture tex, Rectangle srcRect, float dx, float dy, float dw, float dh, float zDepth, float rota, float rotx, float roty, float scale) {
		if (srcRect == null)
			return;

		drawAroundCenter(tex, srcRect.x(), srcRect.y(), srcRect.width(), srcRect.height(), dx, dy, dw, dh, zDepth, rota, rotx, roty, scale);
	}

	public void drawAroundCenter(Texture tex, float sx, float sy, float sw, float sh, float dx, float dy, float dw, float dh, float zDepth, float rota, float rotx, float roty, float scale) {
		if (!mResourcesLoaded)
			return;

		if (!mIsDrawing)
			return;

		if (tex == null) {
			if (TextureManager.USE_DEBUG_MISSING_TEXTURES)
				tex = mResourceManager.textureManager().textureNotFound();
			else
				return;
		}

		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(tex);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(tex);
		}

		final var sin = (float) Math.sin(rota);
		final var cos = (float) Math.cos(rota);

		final var lHalfW = (dw * scale) / 2f;
		final var lHalfH = (dh * scale) / 2f;

		final var texWidth = tex.getTextureWidth();
		final var texHeight = tex.getTextureHeight();

		final var pcx = mUseHalfPixelCorrection ? .5f : .0f;
		final var pcy = mUseHalfPixelCorrection ? .5f : .0f;

		// define the origin of this sprite
		// note: the rotation origin is not scaled with the sprite (this should be performed before calling this function)
		final var originX = -rotx;
		final var originY = -roty;

		final var x0 = -(lHalfW - originX) * cos - (lHalfH + originY) * sin;
		final var y0 = -(lHalfW - originX) * sin + (lHalfH + originY) * cos;
		final var u0 = (sx + pcx) / texWidth;
		final var v0 = (sy + pcy) / texHeight;

		final var x1 = -(lHalfW - originX) * cos - (-lHalfH + originY) * sin;
		final var y1 = -(lHalfW - originX) * sin + (-lHalfH + originY) * cos;
		final var u1 = (sx + pcx) / texWidth;
		final var v1 = (sy + sh - pcy) / texHeight;

		final var x2 = (lHalfW + originX) * cos - (-lHalfH + originY) * sin;
		final var y2 = (lHalfW + originX) * sin + (-lHalfH + originY) * cos;
		final var u2 = (sx + sw - pcx) / texWidth;
		final var v2 = (sy + sh - pcy) / texHeight;

		final var x3 = (lHalfW + originX) * cos - (lHalfH + originY) * sin;
		final var y3 = (lHalfW + originX) * sin + (lHalfH + originY) * cos;
		final var u3 = (sx + sw - pcx) / texWidth;
		final var v3 = (sy + pcy) / texHeight;

		addVertToBuffer(dx + x0, dy + y0, zDepth, 1f, u0, v0, lTextureSlotIndex);
		addVertToBuffer(dx + x1, dy + y1, zDepth, 1f, u1, v1, lTextureSlotIndex);
		addVertToBuffer(dx + x2, dy + y2, zDepth, 1f, u2, v2, lTextureSlotIndex);
		addVertToBuffer(dx + x3, dy + y3, zDepth, 1f, u3, v3, lTextureSlotIndex);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	protected void addVertToBuffer(float x, float y, float z, float w, float u, float v, float texIndex) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(u);
		mBuffer.put(v);

		mBuffer.put(texIndex);
	}
}