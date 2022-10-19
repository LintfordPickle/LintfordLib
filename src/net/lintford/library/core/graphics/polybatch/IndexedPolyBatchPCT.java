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
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

public class IndexedPolyBatchPCT {

	private class VertexDefinition {

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

	public static final int MAX_TRIS = 2000;
	public static final int NUM_VERTS_PER_TRI = 3;

	public static final int MAX_INDICES = MAX_TRIS * NUM_VERTS_PER_TRI;

	private static final String VERT_FILENAME = "/res/shaders/shader_batch_pct.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_batch_pct.frag";

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
	protected boolean mResourcesLoaded;

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
		mResourcesLoaded = false;
	}

	// --------------------------------------1
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mShader.loadResources(resourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVioId == -1)
			mVioId = GL15.glGenBuffers();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		mBuffer = MemoryUtil.memAllocFloat(MAX_TRIS * NUM_VERTS_PER_TRI * VertexDefinition.elementCount);
		mIndexBuffer = MemoryUtil.memAllocInt(MAX_TRIS * NUM_VERTS_PER_TRI);

		initializeGlContent();

		mResourcesLoaded = true;
	}

	private void initializeGlContent() {
		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_TRIS * NUM_VERTS_PER_TRI * VertexDefinition.stride, GL15.GL_DYNAMIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, MAX_INDICES, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);

		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDefinition.colorElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.colorByteOffset);
		GL20.glVertexAttribPointer(2, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);
		GL20.glVertexAttribPointer(3, VertexDefinition.textureIndexElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureIndexByteOffset);

		GL30.glBindVertexArray(0);
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading VaoId = " + mVaoId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading VboId = " + mVboId);
			mVboId = -1;
		}

		if (mVioId > -1) {
			GL15.glDeleteBuffers(mVioId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading mVioId = " + mVioId);
			mVioId = -1;
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

		if (!mResourcesLoaded)
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

	public void drawRect(Texture texture, Rectangle sourceRect, List<Vector2f> vertexArray, float zDepth, boolean closePolygon) {
		drawRect(texture, sourceRect, vertexArray, zDepth, closePolygon, mColor.r, mColor.g, mColor.b);
	}

	public void drawRect(Texture texture, Rectangle sourceRect, List<Vector2f> vertexArray, float zDepth, boolean closePolygon, float red, float green, float blue) {
		if (vertexArray == null)
			return;

		final var lRectVerts = vertexArray;
		if (lRectVerts.size() < 4)
			return;

		if (texture != null) {
			if (mCurrentTexID == -1) {
				mCurrentTexID = texture.getTextureID();
			} else if (mCurrentTexID != texture.getTextureID()) {
				flush();
				mCurrentTexID = texture.getTextureID();
			}
		}

		addVertToBuffer(lRectVerts.get(0).x, lRectVerts.get(0).y, zDepth, 1f, red, green, blue, mColor.a, sourceRect.left(), sourceRect.top());
		addVertToBuffer(lRectVerts.get(1).x, lRectVerts.get(1).y, zDepth, 1f, red, green, blue, mColor.a, sourceRect.right(), sourceRect.top());
		addVertToBuffer(lRectVerts.get(2).x, lRectVerts.get(2).y, zDepth, 1f, red, green, blue, mColor.a, sourceRect.left(), sourceRect.bottom());
		addVertToBuffer(lRectVerts.get(3).x, lRectVerts.get(3).y, zDepth, 1f, red, green, blue, mColor.a, sourceRect.right(), sourceRect.bottom());

		// Index the triangles
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

		flush();
		mIsDrawing = false;
	}

	protected void flush() {
		if (!mResourcesLoaded || !mIsDrawing)
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

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mIndexBuffer);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);

			final int lNumTris = mIndexCount / NUM_VERTS_PER_TRI;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumTris);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mVertexCount);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_INT, 0);

		mShader.unbind();

		GL30.glBindVertexArray(0);
	}
}