package net.ld.library.core.graphics.linebatch;

import static org.lwjgl.system.jemalloc.JEmalloc.je_free;
import static org.lwjgl.system.jemalloc.JEmalloc.je_malloc;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.ld.library.core.camera.ICamera;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.VertexDataStructurePC;
import net.ld.library.core.graphics.shaders.ShaderMVP_PT;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;


public class LineBatch {

	// =============================================
	// Constants
	// =============================================

	private static final int MAX_LINES = 400;

	private static final String VERT_FILENAME = "/res/shaders/shader_basic_col.vert";
	private static final String FRAG_FILENAME = "/res/shaders/shader_basic_col.frag";

	// =============================================
	// Variables
	// =============================================

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	public float r, g, b, a;

	private Queue<VertexDataStructurePC> mTempVertQueue;
	private ICamera mCamera;
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private int mCurNumSprites;
	private boolean mIsDrawing;
	private boolean mIsLoaded;

	// =============================================
	// Constructor
	// =============================================

	public LineBatch() {
		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		mTempVertQueue = new LinkedList<>();

		a = r = g = b = 1f;

		

		mModelMatrix = new Matrix4f();
		mIsLoaded = false;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void loadGLContent(ResourceManager pResourceManager) {
		mShader.loadGLContent();

		mVaoId = GL30.glGenVertexArrays();
		mVboId = GL15.glGenBuffers();
		
		final int NUM_VERTS_PER_LINE = 2;
		mBuffer = je_malloc(MAX_LINES * NUM_VERTS_PER_LINE * VertexDataStructurePC.stride).asFloatBuffer();

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		mShader.unloadGLContent();

		GL30.glDeleteVertexArrays(mVaoId);
		GL15.glDeleteBuffers(mVboId);
		
		je_free(mBuffer);

		mIsLoaded = false;

	}

	// =============================================
	// Methods
	// =============================================

	public void begin(ICamera pCamera) {
		mTempVertQueue.clear();
		mCamera = pCamera;
		mCurNumSprites = 0;
		mIsDrawing = true;
	}

	public void drawRect(Rectangle pRect, float pZ) {
		if (!mIsDrawing)
			return;

		draw(pRect.x, pRect.y, pRect.x + pRect.width, pRect.y, pZ); // top
		draw(pRect.x, pRect.y + pRect.height, pRect.x + pRect.width, pRect.y + pRect.height, pZ); // bottom

		draw(pRect.x, pRect.y, pRect.x, pRect.y + pRect.height, pZ); // left
		draw(pRect.x + pRect.width, pRect.y, pRect.x + pRect.width, pRect.y + pRect.height, pZ); // right
	}

	public void drawRect(float pX, float pY, float pW, float pH, float pZ) {
		if (!mIsDrawing)
			return;
		draw(pX, pY, pX + pW, pY, pZ); // top
		draw(pX, pY + pH, pX + pW, pY + pH, pZ); // bottom

		draw(pX, pY, pX, pY + pH, pZ); // left
		draw(pX + pW, pY, pX + pW, pY + pH, pZ); // right
	}

	public void draw(float pP1X, float pP1Y, float pP2X, float pP2Y, float pZ) {

		if (!mIsDrawing)
			return;

		if (mCurNumSprites >= MAX_LINES) {
			flush();
			mCurNumSprites = 0;
		}

		// begin
		VertexDataStructurePC lNewVertex0 = new VertexDataStructurePC();
		lNewVertex0.xyzw(pP1X, pP1Y, pZ, 1f);
		lNewVertex0.rgba(r, g, b, a);

		// end
		VertexDataStructurePC lNewVertex1 = new VertexDataStructurePC();
		lNewVertex1.xyzw(pP2X, pP2Y, pZ, 1f);
		lNewVertex1.rgba(r, g, b, a);

		mTempVertQueue.add(lNewVertex0);
		mTempVertQueue.add(lNewVertex1);

		mCurNumSprites++;
	}

	public void end() {
		mIsDrawing = false;
		flush();
	}

	private void flush() {
		mVertexCount = mTempVertQueue.size();
		if (mVertexCount == 0 || !mIsLoaded)
			return;

		mBuffer.clear();
		for (int i = 0; i < mVertexCount; i++) {
			mBuffer.put(mTempVertQueue.poll().getElements());
		}

		mBuffer.flip();
		mCurNumSprites = 0;

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePC.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePC.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePC.stride, VertexDataStructurePC.colorByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL30.glBindVertexArray(mVaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL11.glDrawArrays(GL11.GL_LINES, 0, mVertexCount);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);

		mShader.unbind();

	}

	public void changeColor(int i, int j, int k, int l) {
		// if (mCurNumSprites > 0) {
		// flush();
		// mCurNumSprites = 0;
		// }

		r = i;
		g = j;
		b = k;
		a = l;

	}

}
