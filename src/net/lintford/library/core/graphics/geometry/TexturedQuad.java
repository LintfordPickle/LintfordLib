package net.lintford.library.core.graphics.geometry;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePT;
import net.lintford.library.core.maths.Matrix4f;

public class TexturedQuad {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Matrix4f mModelMatrix;
	protected int mVaoId = -1;
	protected int mVboId = -1;
	protected boolean mIsLoaded;
	protected float mZDepth;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the Z Depth this {@link TexturedQuad}s modelmatrix will be translated to upon request. */
	public float zDepth() {
		return mZDepth;
	}

	/** Sets a new ZDepth value for this {@link TexturedQuad}. */
	public void zDepth(float pNewValue) {
		mZDepth = pNewValue;
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	public void modelMatrix(Matrix4f pNewMatrix) {
		mModelMatrix = pNewMatrix;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TexturedQuad() {
		mModelMatrix = new Matrix4f();
		createModelMatrix();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		setupVerts();

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		cleanup();

		mIsLoaded = false;

	}

	public void setupVerts() {
		VertexDataStructurePT lNewVertex0 = new VertexDataStructurePT();
		lNewVertex0.xyzw(-.5f, .5f, 0f, 1f);
		lNewVertex0.uv(0, 0);

		VertexDataStructurePT lNewVertex1 = new VertexDataStructurePT();
		lNewVertex1.xyzw(-.5f, -.5f, 0f, 1f);
		lNewVertex1.uv(0, 1);

		VertexDataStructurePT lNewVertex2 = new VertexDataStructurePT();
		lNewVertex2.xyzw(.5f, -.5f, 0f, 1f);
		lNewVertex2.uv(1, 1);

		VertexDataStructurePT lNewVertex3 = new VertexDataStructurePT();
		lNewVertex3.xyzw(.5f, .5f, 0f, 1f);
		lNewVertex3.uv(1, 0);

		// copy vertices to the float buffer
		FloatBuffer lBuffer = BufferUtils.createFloatBuffer(6 * VertexDataStructurePT.stride);

		lBuffer.put(lNewVertex0.getElements());
		lBuffer.put(lNewVertex1.getElements());
		lBuffer.put(lNewVertex2.getElements());

		lBuffer.put(lNewVertex2.getElements());
		lBuffer.put(lNewVertex3.getElements());
		lBuffer.put(lNewVertex0.getElements());

		lBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.textureByteOffset);

		GL30.glBindVertexArray(0);

	}

	public void cleanup() {
		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			mVaoId = -1;

		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			mVboId = -1;

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore pCore) {

		// Bind to the VAO that has all the information about the quad vertices
		GL30.glBindVertexArray(mVaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

		// Put everything back to default
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void createModelMatrix() {
		createModelMatrix(0, 0);
	}

	public void createModelMatrix(float pPositionX, float pPositionY) {
		createModelMatrix(pPositionX, pPositionY, mZDepth);
	}

	public void createModelMatrix(float pPositionX, float pPositionY, float pPositionZ) {
		createModelMatrix(pPositionX, pPositionY, 200, 200, pPositionZ);
	}

	public void createModelMatrix(float pPositionX, float pPositionY, float pWidth, float pHeight, float pPositionZ) {
		mModelMatrix.setIdentity();
		mModelMatrix.scale(pWidth, pHeight, 1f);
		mModelMatrix.translate(pPositionX, pPositionY, mZDepth);
	}
}
