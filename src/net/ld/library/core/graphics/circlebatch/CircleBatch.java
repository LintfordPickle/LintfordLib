package net.ld.library.core.graphics.circlebatch;

import static org.lwjgl.system.jemalloc.JEmalloc.je_free;
import static org.lwjgl.system.jemalloc.JEmalloc.je_malloc;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.ld.library.core.camera.ICamera;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.VertexDataStructurePC;
import net.ld.library.core.graphics.shaders.ShaderMVP_PT;
import net.ld.library.core.maths.Matrix4f;

public class CircleBatch {

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

	private ICamera mCamera;
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private boolean mIsDrawing;
	private boolean mIsLoaded;

	// =============================================
	// Constructor
	// =============================================

	public CircleBatch() {
		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
			}
		};

		a = r = g = b = 1f;

		mModelMatrix = new Matrix4f();
		mIsLoaded = false;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void loadGLContent(ResourceManager pResourceManager) {
		mShader.loadGLContent(pResourceManager);

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
		mCamera = pCamera;
		mIsDrawing = true;
		mBuffer.clear();
		mVertexCount = 0;
	}

	public void draw(float pX, float pY, float pRadius, float pZ) {
		draw(pX, pY, pRadius, 0f, pZ);

	}

	public void draw(float pX, float pY, float pRadius, float pRotation, float pZ) {

		if (!mIsDrawing)
			return;

		int lNumPoints = 32;
		float lAng = (float) Math.toRadians(pRotation);
		float lDegPerSeg = (float) (Math.PI * 2) / lNumPoints;

		for (int i = 0; i < lNumPoints; i++) {
			float lXX = (float) Math.cos(lAng) * pRadius;
			float lYY = (float) Math.sin(lAng) * pRadius;

			lAng += lDegPerSeg;

			addVertToBuffer(pX + lXX, pY + lYY, pZ, 1f, r, g, b, a);

		}

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

	public void end() {
		mIsDrawing = false;
		flush();
	}

	private void flush() {
		if (mVertexCount == 0 || !mIsLoaded)
			return;

		mBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePC.positionElementCount, GL11.GL_FLOAT, false,
				VertexDataStructurePC.stride, VertexDataStructurePC.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePC.colorElementCount, GL11.GL_FLOAT, false,
				VertexDataStructurePC.stride, VertexDataStructurePC.colorByteOffset);

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
