package net.lintford.library.core.graphics.batching;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
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
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PCT;
import net.lintford.library.core.maths.Matrix4f;

public class TextureBatchPC {

	public class VertexDataStructure {

		public static final int elementBytes = 4;

		public static final int positionElementCount = 4;
		public static final int colorElementCount = 4;

		public static final int positionBytesCount = positionElementCount * elementBytes;
		public static final int colorBytesCount = colorElementCount * elementBytes;

		public static final int positionByteOffset = 0;
		public static final int colorByteOffset = positionByteOffset + positionBytesCount;

		public static final int stride = positionBytesCount + colorBytesCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 10000;
	protected static final int NUM_VERTICES_PER_SPRITE = 4;
	protected static final int NUM_INDICES_PER_SPRITE = 6;

	protected static final int MAX_VERTEX_COUNT = MAX_SPRITES * NUM_VERTICES_PER_SPRITE;
	protected static final int MAX_INDEX_COUNT = MAX_SPRITES * NUM_INDICES_PER_SPRITE;

	protected static final String VERT_FILENAME = "/res/shaders/shader_batch_pc.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_batch_pc.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ICamera mCamera;
	protected ShaderMVP_PCT mShader;
	protected ShaderMVP_PCT mCustomShader;

	protected FloatBuffer mBuffer;
	protected IntBuffer mIndexBuffer;

	private boolean mBlendEnabled;
	private int mBlendFuncSrcFactor;
	private int mBlendFuncDstFactor;

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

	public TextureBatchPC() {
		mShader = new ShaderMVP_PCT("TextureBatchShader", VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
				GL20.glBindAttribLocation(pShaderID, 2, "inTexCoord");
				GL20.glBindAttribLocation(pShaderID, 3, "inTexIndex");
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

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		if (mVioId == -1)
			mVioId = GL15.glGenBuffers();

		mBuffer = MemoryUtil.memAllocFloat(MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDataStructure.stride);
		mIndexBuffer = MemoryUtil.memAllocInt(MAX_SPRITES * NUM_INDICES_PER_SPRITE);

		initializeGlContent();

		mResourcesLoaded = true;

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_BATCH_OBJECTS);
	}

	private void initializeGlContent() {
		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_SPRITES * NUM_VERTICES_PER_SPRITE * VertexDataStructure.stride, GL15.GL_DYNAMIC_DRAW);

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
		GL20.glVertexAttribPointer(0, VertexDataStructure.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructure.stride, VertexDataStructure.positionByteOffset);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, VertexDataStructure.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructure.stride, VertexDataStructure.colorByteOffset);

		GL30.glBindVertexArray(0);
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

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

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		begin(pCamera, mShader);
	}

	public void begin(ICamera pCamera, ShaderMVP_PCT pCustomShader) {
		if (pCamera == null)
			return;

		if (mIsDrawing)
			return;

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

		mCustomShader.projectionMatrix(mCamera.projection());
		mCustomShader.viewMatrix(mCamera.view());
		mCustomShader.modelMatrix(mModelMatrix);

		mCustomShader.bind();

		if (_countDebugStats) {
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
		_countDebugStats = true;
	}

	// ---

	public void draw(Rectangle pSrcRect, Rectangle pDestRect, float pZ, Color color) {
		if (pSrcRect == null || pDestRect == null)
			return;

		draw(pSrcRect.x(), pSrcRect.y(), pSrcRect.width(), pSrcRect.height(), pDestRect, pZ, color);
	}

	public void draw(Rectangle pSrcRect, float pDX, float pDY, float pDW, float pDH, float pZ, Color color) {
		if (pSrcRect == null)
			return;

		draw(pDX, pDY, pDW, pDH, pZ, color);
	}

	public void draw(float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float pZ, Color color) {
		if (pDestRect == null)
			return;

		draw(pDestRect.x(), pDestRect.y(), pDestRect.width(), pDestRect.height(), pZ, color);
	}

	public void draw(float pDX, float pDY, float pDW, float pDH, float pZ, Color color) {
		if (!mIsDrawing)
			return;

		if (mIndexCount >= MAX_SPRITES * NUM_INDICES_PER_SPRITE - NUM_INDICES_PER_SPRITE)
			flush();

		float x0 = pDX;
		float y0 = pDY + pDH;

		float x1 = pDX;
		float y1 = pDY;

		float x2 = pDX + pDW;
		float y2 = pDY;

		float x3 = pDX + pDW;
		float y3 = pDY + pDH;

		addVertToBuffer(x0, y0, pZ, 1f, color.r, color.g, color.b, color.a);
		addVertToBuffer(x1, y1, pZ, 1f, color.r, color.g, color.b, color.a);
		addVertToBuffer(x2, y2, pZ, 1f, color.r, color.g, color.b, color.a);
		addVertToBuffer(x3, y3, pZ, 1f, color.r, color.g, color.b, color.a);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	public void drawAroundCenter(Rectangle destRectangle, float pZ, float pRot, float pROX, float pROY, float pScale, Color color) {
		if (destRectangle == null)
			return;

		drawAroundCenter(destRectangle.x(), destRectangle.y(), destRectangle.width(), destRectangle.height(), pZ, pRot, pROX, pROY, pScale, color);
	}

	public void drawAroundCenter(float pDX, float pDY, float pDW, float pDH, float pZ, float pRot, float pROX, float pROY, float pScale, Color color) {
		if (!mResourcesLoaded)
			return;

		if (!mIsDrawing)
			return;

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

		// Vertex 1 (top left)
		float x1 = -(lHalfW - originX) * cos - (-lHalfH + originY) * sin;
		float y1 = -(lHalfW - originX) * sin + (-lHalfH + originY) * cos;

		// Vertex 2 (top right)
		float x2 = (lHalfW + originX) * cos - (-lHalfH + originY) * sin;
		float y2 = (lHalfW + originX) * sin + (-lHalfH + originY) * cos;

		// Vertex 3 (bottom right)
		float x3 = (lHalfW + originX) * cos - (lHalfH + originY) * sin;
		float y3 = (lHalfW + originX) * sin + (lHalfH + originY) * cos;

		addVertToBuffer(pDX + x0, pDY + y0, pZ, 1f, color.r, color.g, color.b, color.a);
		addVertToBuffer(pDX + x1, pDY + y1, pZ, 1f, color.r, color.g, color.b, color.a);
		addVertToBuffer(pDX + x2, pDY + y2, pZ, 1f, color.r, color.g, color.b, color.a);
		addVertToBuffer(pDX + x3, pDY + y3, pZ, 1f, color.r, color.g, color.b, color.a);

		mIndexCount += NUM_INDICES_PER_SPRITE;
	}

	// ---

	protected void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(r);
		mBuffer.put(g);
		mBuffer.put(b);
		mBuffer.put(a);
	}
}