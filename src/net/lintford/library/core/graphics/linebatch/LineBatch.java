package net.lintford.library.core.graphics.linebatch;

import java.nio.FloatBuffer;

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
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePC;
import net.lintford.library.core.maths.Matrix4f;

public class LineBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_LINES = 4096;
	public static final int NUM_VERTS_PER_LINE = 2;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_pc.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_pc.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	private float mR, mG, mB, mA;

	private ICamera mCamera;
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mResourcesLoaded;
	private int mGLLineType;
	private float mGLLineWidth;
	private boolean mAntiAliasing;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void red(float red) {
		mR = red;
	}

	public void green(float green) {
		mG = green;
	}

	public void blue(float blue) {
		mB = blue;
	}

	public void alpha(float alpha) {
		mA = alpha;
	}

	public void lineAntialiasing(boolean enableSmoothing) {
		mAntiAliasing = enableSmoothing;
	}

	public boolean lineAntialiasing() {
		return mAntiAliasing;
	}

	public void lineWidth(float newWidth) {
		if (newWidth < 1.f)
			newWidth = 1.f;
		if (newWidth > 10.f)
			newWidth = 10.f;
		mGLLineWidth = newWidth;
	}

	public float lineWidth() {
		return mGLLineWidth;
	}

	/** Sets the line type to use by OpenGL. Choices are either GL11.GL_LINE_STRIP or GL11.GL_LINES */
	public void lineType(int lineType) {
		mGLLineType = lineType;

		if (mGLLineType != GL11.GL_LINE_STRIP && mGLLineType != GL11.GL_LINES && mGLLineType != GL11.GL_POINTS) {
			mGLLineType = GL11.GL_LINES;
		}
	}

	public int lineType() {
		return mGLLineType;
	}

	public boolean isDrawing() {
		return mIsDrawing;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LineBatch() {
		mShader = new ShaderMVP_PT("ShaderMVP_PT", VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		mA = mR = mG = mB = 1f;

		mGLLineWidth = 2.f;
		mModelMatrix = new Matrix4f();
		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mShader.loadResources(resourceManager);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v("OpenGL", "LineBatch: VboId = " + mVboId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_LINES * NUM_VERTS_PER_LINE * VertexDataStructurePC.stride);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mShader.unloadResources();

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "LineBatch: Unloading VboId = " + mVboId);
			mVboId = -1;
		}

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "LineBatch: Unloading VaoId = " + mVaoId);
			mVaoId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		mResourcesLoaded = false;
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

			GL20.glVertexAttribPointer(0, VertexDataStructurePC.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.positionByteOffset);
			GL20.glVertexAttribPointer(1, VertexDataStructurePC.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.colorByteOffset);
		}
	}

	public void begin(ICamera camera) {
		if (camera == null)
			return;

		if (mIsDrawing)
			return;

		mCamera = camera;

		mBuffer.clear();
		mVertexCount = 0;
		mIsDrawing = true;
	}

	public void drawRect(Rectangle rectangle, float zDepth) {
		if (!mIsDrawing)
			return;

		drawRect(rectangle, 1f, zDepth);
	}

	public void drawRect(Rectangle rectangle, float scale, float zDepth) {
		if (!mIsDrawing)
			return;

		drawRect(rectangle, 0f, 0f, scale, zDepth);
	}

	public void drawRect(Rectangle rectangle, float originX, float originY, float scale, float zDepth) {
		if (!mIsDrawing)
			return;

		drawRect(rectangle, originX, originY, scale, zDepth, 1f, 1f, 1f);
	}

	public void drawRect(Rectangle rectangle, float originX, float originY, float scale, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		final float lModWidth = rectangle.width() * scale;
		final float lModHeight = rectangle.height() * scale;

		final float lModX = rectangle.left() - originX * scale;
		final float lModY = rectangle.top() - originY * scale;

		draw(lModX, lModY, lModX + lModWidth, lModY, zDepth, red, green, blue); // top
		draw(lModX, lModY + lModHeight, lModX + lModWidth, lModY + lModHeight, zDepth, red, green, blue); // bottom
		draw(lModX, lModY, lModX, lModY + lModHeight, zDepth, red, green, blue); // left
		draw(lModX + lModWidth, lModY, lModX + lModWidth, lModY + lModHeight, zDepth, red, green, blue); // right
	}

	public void drawRect(float positionX, float positionY, float width, float height, float zDepth) {
		if (!mIsDrawing)
			return;

		draw(positionX, positionY, positionX + width, positionY, zDepth, mR, mG, mB); // top
		draw(positionX, positionY + height, positionX + width, positionY + height, zDepth, mR, mG, mB); // bottom

		draw(positionX, positionY, positionX, positionY + height, zDepth, mR, mG, mB); // left
		draw(positionX + width, positionY, positionX + width, positionY + height, zDepth, mR, mG, mB); // right
	}

	public void drawRect(float positionX, float positionY, float width, float height, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		draw(positionX, positionY, positionX + width, positionY, zDepth, red, green, blue); // top
		draw(positionX, positionY + height, positionX + width, positionY + height, zDepth, red, green, blue); // bottom

		draw(positionX, positionY, positionX, positionY + height, zDepth, red, green, blue); // left
		draw(positionX + width, positionY, positionX + width, positionY + height, zDepth, red, green, blue); // right
	}

	public void drawArrowDown(float positionX, float positionY, float width, float height, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		draw(positionX, positionY, positionX + width, positionY, zDepth, red, green, blue);
		draw(positionX, positionY, positionX + width * 0.5f, positionY + height, zDepth, red, green, blue);
		draw(positionX + width, positionY, positionX + width * 0.5f, positionY + height, zDepth, red, green, blue);
	}

	public void drawArrowUp(float positionX, float positionY, float width, float height, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		draw(positionX, positionY + height, positionX + width, positionY + height, zDepth, red, green, blue);
		draw(positionX, positionY + height, positionX + width * 0.5f, positionY, zDepth, red, green, blue);
		draw(positionX + width, positionY + height, positionX + width * 0.5f, positionY, zDepth, red, green, blue);
	}

	public void drawArrowLeft(float positionX, float positionY, float width, float height, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		draw(positionX + width, positionY, positionX + width, positionY + height, zDepth, red, green, blue);
		draw(positionX, positionY + height * 0.5f, positionX + width, positionY, zDepth, red, green, blue);
		draw(positionX, positionY + height * 0.5f, positionX + width, positionY + height, zDepth, red, green, blue);
	}

	public void drawArrowRight(float positionX, float positionY, float width, float height, float zDepth, float red, float green, float blue) {
		if (!mIsDrawing)
			return;

		draw(positionX, positionY, positionX, positionY + height, zDepth, red, green, blue);
		draw(positionX + width, positionY + height * 0.5f, positionX, positionY, zDepth, red, green, blue);
		draw(positionX + width, positionY + height * 0.5f, positionX, positionY + height, zDepth, red, green, blue);
	}

	public void draw(float point0X, float point0Y, float point1X, float point1Y, float zDepth, float red, float green, float blue) {

		if (!mIsDrawing)
			return;

		if (mVertexCount * 2 >= MAX_LINES)
			flush();

		draw(point0X, point0Y, point1X, point1Y, zDepth, red, green, blue, mA);
	}

	public void draw(float point0X, float point0Y, float point1X, float point1Y, float zDepth, float red, float green, float blue, float alpha) {

		if (!mIsDrawing)
			return;

		if (mVertexCount * 2 >= MAX_LINES)
			flush();

		draw(point0X, point0Y, zDepth, red, green, blue, alpha);
		draw(point1X, point1Y, zDepth, red, green, blue, alpha);
	}

	public void draw(float point0X, float point0Y, float zDepth, float red, float green, float blue, float alpha) {
		if (!mIsDrawing)
			return;

		if (mVertexCount * 2 >= MAX_LINES)
			flush();

		addVertToBuffer(point0X, point0Y, zDepth, 1f, red, green, blue, alpha);
	}

	private void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a) {
		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(w);

		mBuffer.put(r);
		mBuffer.put(g);
		mBuffer.put(b);
		mBuffer.put(a);

		mVertexCount++;
	}

	public void forceFlush() {
		flush();
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

		initializeGlContent();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		{
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, mVertexCount);
		}

		if (mAntiAliasing) {
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
		} else {
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		}

		GL11.glLineWidth(mGLLineWidth);
		GL11.glDrawArrays(mGLLineType, 0, mVertexCount);
		GL30.glBindVertexArray(0);

		mShader.unbind();

		mBuffer.clear();

		mVertexCount = 0;
	}

	public void changeColorNormalized(float red, float green, float blue, float alpha) {
		mR = red;
		mG = green;
		mB = blue;
		mA = alpha;
	}
}