package net.lintford.library.core.box2d.renderers;

import java.nio.FloatBuffer;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.common.VertexDataStructurePCT;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PCT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.maths.Matrix4f;

public class JBox2dPolyBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_VERTS = 10000;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mCurrentTexID;
	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	public float r, g, b, a;
	private ICamera mCamera;
	private ShaderMVP_PCT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mResourcesLoaded;
	protected boolean mUseCheckerPattern;
	protected ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// ------------------------------------

	public boolean useCheckerPattern() {
		return mUseCheckerPattern;
	}

	public void useCheckerPattern(boolean newValue) {
		mUseCheckerPattern = newValue;
	}

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dPolyBatch() {
		mShader = new ShaderMVP_PCT(ShaderMVP_PCT.SHADER_NAME, VERT_FILENAME, FRAG_FILENAME);

		a = r = g = b = 1f;

		mModelMatrix = new Matrix4f();
		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mResourceManager = resourceManager;

		mShader.loadResources(resourceManager);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vbo " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_VERTS * VertexDataStructurePCT.elementCount);

		mResourcesLoaded = true;

		if (resourceManager.isMainOpenGlThread())
			initializeGlContainers();
	}

	private boolean mAreGlContainersInitialized = false;

	private void initializeGlContainers() {
		if (!mResourcesLoaded)
			return;

		if (mAreGlContainersInitialized)
			return;

		if (mVaoId == -1) {
			mVaoId = GL30.glGenVertexArrays();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenVertexArrays: " + mVaoId);
		}

		GL30.glBindVertexArray(mVaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_VERTS * VertexDataStructurePCT.stride, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL20.glVertexAttribPointer(0, VertexDataStructurePCT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePCT.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.colorByteOffset);
		GL20.glVertexAttribPointer(2, VertexDataStructurePCT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.textureByteOffset);

		GL30.glBindVertexArray(0);

		mAreGlContainersInitialized = false;
	}

	public void unloadResources() {
		if (mResourcesLoaded)
			return;

		mShader.unloadResources();

		if (mVaoId != -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteVertexArrays: " + mVaoId);
			mVaoId = -1;
		}

		if (mVboId != -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glDeleteBuffers VboId: " + mVboId);
			mVboId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
		}

		mResourcesLoaded = false;
		mAreGlContainersInitialized = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera camera) {
		if (!mResourcesLoaded)
			return;

		if (camera == null)
			return;

		if (mIsDrawing)
			return;

		mCamera = camera;

		mBuffer.clear();
		mVertexCount = 0;
		mIsDrawing = true;
	}

	public void drawPolygon(Texture texture, Body body, Vec2[] vertexArray, Rectangle sourceRect, float zDepth, float red, float green, float blue, float alpha) {
		drawPolygon(texture, body, vertexArray, sourceRect.x(), sourceRect.y(), sourceRect.width(), sourceRect.height(), zDepth, red, green, blue, alpha);
	}

	public void drawPolygon(Texture texture, Body body, Vec2[] vertexArray, float sourceX, float sourceY, float sourceWidth, float sourceHeight, float zDepth, float red, float green, float blue, float alpha) {
		if (!mResourcesLoaded || !mIsDrawing)
			return;

		if (vertexArray == null || vertexArray.length < 4)
			return;

		if (texture == null) {
			if (TextureManager.USE_DEBUG_MISSING_TEXTURES) {
				texture = mResourceManager.textureManager().textureNotFound();
			} else {
				return;
			}
		}

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = texture.getTextureID();
		} else if (mCurrentTexID != texture.getTextureID()) {
			flush();
			mCurrentTexID = texture.getTextureID();
		}

		if (mUseCheckerPattern)
			texture = mResourceManager.textureManager().checkerIndexedTexture();

		// This doesn't make sense - we are trying to draw a polygon, 
		// yet restricting it to a quadrilatral (and how are the UVs being mapped!)?
		final var vert0 = body.getWorldPoint(vertexArray[0]);
		float x0 = vert0.x * ConstantsPhysics.UnitsToPixels();
		float y0 = vert0.y * ConstantsPhysics.UnitsToPixels();
		float u0 = (sourceX + sourceWidth) / texture.getTextureWidth();
		float v0 = sourceY / texture.getTextureHeight();

		final var vert1 = body.getWorldPoint(vertexArray[1]);
		float x1 = vert1.x * ConstantsPhysics.UnitsToPixels();
		float y1 = vert1.y * ConstantsPhysics.UnitsToPixels();
		float u1 = (sourceX) / texture.getTextureWidth();
		float v1 = (sourceY) / texture.getTextureHeight();

		final var vert2 = body.getWorldPoint(vertexArray[3]);
		float x2 = vert2.x * ConstantsPhysics.UnitsToPixels();
		float y2 = vert2.y * ConstantsPhysics.UnitsToPixels();
		float u2 = sourceX / texture.getTextureWidth();
		float v2 = (sourceY + sourceHeight) / texture.getTextureHeight();

		final var vert3 = body.getWorldPoint(vertexArray[2]);
		float x3 = vert3.x * ConstantsPhysics.UnitsToPixels();
		float y3 = vert3.y * ConstantsPhysics.UnitsToPixels();
		float u3 = (sourceX + sourceWidth) / texture.getTextureWidth();
		float v3 = (sourceY + sourceHeight) / texture.getTextureHeight();

		addVertToBuffer(x0, y0, zDepth, 1f, red, green, blue, alpha, u0, v0); // 0
		addVertToBuffer(x2, y2, zDepth, 1f, red, green, blue, alpha, u2, v2); // 2
		addVertToBuffer(x3, y3, zDepth, 1f, red, green, blue, alpha, u3, v3); // 3
		addVertToBuffer(x0, y0, zDepth, 1f, red, green, blue, alpha, u0, v0); // 0
		addVertToBuffer(x1, y1, zDepth, 1f, red, green, blue, alpha, u1, v1); // 1
		addVertToBuffer(x2, y2, zDepth, 1f, red, green, blue, alpha, u2, v2); // 2
	}

	private void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v) {
		if (mVertexCount >= MAX_VERTS)
			flush();

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
		if (!mResourcesLoaded)
			return;

		if (mVertexCount == 0)
			return;

		mBuffer.flip();

		if (!mAreGlContainersInitialized)
			initializeGlContainers();

		GL30.glBindVertexArray(mVaoId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		if (mCurrentTexID != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);
		}

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

		mBuffer.clear();
		mVertexCount = 0;
	}
}
