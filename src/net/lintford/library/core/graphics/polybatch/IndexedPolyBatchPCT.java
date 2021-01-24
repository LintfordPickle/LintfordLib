package net.lintford.library.core.graphics.polybatch;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PCT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePCT;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

public class IndexedPolyBatchPCT {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_TRIS = 4096;
	public static final int NUM_VERTS_PER_TRI = 3;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mVaoId = -1;
	protected int mVioId = -1;
	protected int mVboId = -1;
	protected int mIndexCount = 0;
	protected int mVertexCount = 0;
	protected final Color mColor = new Color(1.f, 1.f, 1.f, 1.f);
	protected int mCurrentTexID;

	protected ICamera mCamera;
	protected ShaderMVP_PCT mShader;
	protected Matrix4f mModelMatrix;
	protected FloatBuffer mBuffer;
	protected IntBuffer mIndexBuffer;
	protected boolean mIsDrawing;
	protected boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// ------------------------------------

	public Color color() {
		return mColor;
	}

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public IndexedPolyBatchPCT() {
		mShader = new ShaderMVP_PCT(ShaderMVP_PCT.SHADER_NAME, VERT_FILENAME, FRAG_FILENAME);

		mModelMatrix = new Matrix4f();
		mIsLoaded = false;

	}

	// --------------------------------------1
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		mShader.loadGLContent(pResourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVioId == -1)
			mVioId = GL15.glGenBuffers();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		// TODO: Make sure this is the new / correct way to allocation memory for a buffer
		// TODO: We are not benefiting from the indexed nature (with regards to reduced vert count)
		mBuffer = MemoryUtil.memAllocFloat(MAX_TRIS * NUM_VERTS_PER_TRI * VertexDataStructurePCT.stride);
		mIndexBuffer = MemoryUtil.memAllocInt(MAX_TRIS * NUM_VERTS_PER_TRI);

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

		mVaoId = -1;
		mVboId = -1;

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);

		}

		if (mIndexBuffer != null) {
			mIndexBuffer.clear();
			MemoryUtil.memFree(mIndexBuffer);

		}

		mIsLoaded = false;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		if (pCamera == null)
			return;

		if (!mIsLoaded)
			return;

		if (mIsDrawing)
			return;

		mCamera = pCamera;

		mBuffer.clear();
		mIndexBuffer.clear();

		mIndexCount = 0;
		mVertexCount = 0;

		mIsDrawing = true;

	}

	public void drawRect(Texture pTexture, Rectangle pSrcRect, List<Vector2f> pVertexArray, float pZ, boolean pClose) {
		drawRect(pTexture, pSrcRect, pVertexArray, pZ, pClose, mColor.r, mColor.g, mColor.b);

	}

	public void drawRect(Texture pTexture, Rectangle pSrcRect, List<Vector2f> pVertexArray, float pZ, boolean pClose, float pR, float pG, float pB) {
		if (pVertexArray == null)
			return;

		final var lRectVerts = pVertexArray;
		if (lRectVerts.size() < 4)
			return;

		if (pTexture != null) {
			if (mCurrentTexID == -1) {
				mCurrentTexID = pTexture.getTextureID();

			} else if (mCurrentTexID != pTexture.getTextureID()) {
				flush();
				mCurrentTexID = pTexture.getTextureID();

			}

		}

		addVertToBuffer(lRectVerts.get(0).x, lRectVerts.get(0).y, pZ, 1f, pR, pG, pB, mColor.a, pSrcRect.left(), pSrcRect.top());
		addVertToBuffer(lRectVerts.get(1).x, lRectVerts.get(1).y, pZ, 1f, pR, pG, pB, mColor.a, pSrcRect.right(), pSrcRect.top());
		addVertToBuffer(lRectVerts.get(2).x, lRectVerts.get(2).y, pZ, 1f, pR, pG, pB, mColor.a, pSrcRect.left(), pSrcRect.bottom());
		addVertToBuffer(lRectVerts.get(3).x, lRectVerts.get(3).y, pZ, 1f, pR, pG, pB, mColor.a, pSrcRect.right(), pSrcRect.bottom());

		// Index the triangle
		mIndexBuffer.put(mVertexCount - 4);
		mIndexBuffer.put(mVertexCount - 3);
		mIndexBuffer.put(mVertexCount - 2);

		mIndexBuffer.put(mVertexCount - 3);
		mIndexBuffer.put(mVertexCount - 1);
		mIndexBuffer.put(mVertexCount - 2);

		mIndexCount += 6;

	}

	protected void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v) {
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

	protected void flush() {
		if (!mIsLoaded)
			return;

		if (mIndexCount == 0)
			return;

		if (mCurrentTexID != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		} else {
			return;

		}

		mBuffer.flip();
		mIndexBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		// Bind vertices

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePCT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePCT.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.colorByteOffset);
		GL20.glVertexAttribPointer(2, VertexDataStructurePCT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.textureByteOffset);

		// Bind indices

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mIndexCount * 3);

		}

		// glDrawElements for index buffer
		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		GL30.glBindVertexArray(0);

		mShader.unbind();

	}

	public void redraw() {
		if (mVertexCount == 0)
			return;

		GL30.glBindVertexArray(mVaoId);

		// TODO: 

	}

}
