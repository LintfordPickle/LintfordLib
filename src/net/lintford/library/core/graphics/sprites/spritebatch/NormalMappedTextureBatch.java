package net.lintford.library.core.graphics.sprites.spritebatch;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePT;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;

// TODO: Need to implement the spritebatch like TileSetRendererVBO, i.e. with separate shaders and 
// also need to seperate out the texture stuff (should probably just pass the GL texture IDs).
public class NormalMappedTextureBatch {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Queue<VertexDataStructurePT> mTempVertQueue;
	private Texture mDiffuseTex;
	private Texture mNormalTex;
	private Vector2f mSunPosition;
	private Matrix4f mModelMatrix;
	private ICamera mCamera;
	private NormalBatchShader mShader;

	private int mVboId = -1;
	private int mVertexCount = 0;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Vector2f sunPosition() {
		return mSunPosition;
	}

	public void sunPosition(Vector2f pNewSunPosition) {
		mSunPosition = pNewSunPosition;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public NormalMappedTextureBatch() {

		mTempVertQueue = new LinkedList<VertexDataStructurePT>();
		mSunPosition = new Vector2f();

		mModelMatrix = new Matrix4f();

		mShader = new NormalBatchShader();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if(mVboId == -1)
			mVboId = GL15.glGenBuffers();
		
		mShader.loadGLContent(pResourceManager);
	}

	public void unloadGLContent() {
		if(mVboId > -1)
			GL15.glDeleteBuffers(mVboId);
		
		mShader.unloadGLContent();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void clear() {
		mDiffuseTex = null;
		mNormalTex = null;
		mTempVertQueue.clear();
	}

	public void begin(ICamera pCamera) {
		if (pCamera == null) {
			System.err.println("NormalMappedTextureSprite needs a valid ICamera object!");
			return; // do nothing
		}
		mCamera = pCamera;
		clear();
	}

	public void handleInput(InputState pInputState) {

	}

	public void draw(float pSX, float pSY, float pSW, float pSH, float pPX, float pPY, float pZ, float pWidth, float pHeight, Texture pDiffuse, Texture pNormal) {
		if (pDiffuse == null)
			return;

		if (mDiffuseTex == null) { // first texture
			mDiffuseTex = pDiffuse;
			mNormalTex = pNormal;
		} else if (mDiffuseTex != pDiffuse) {
			flush(); // Force draw with the current textureS

			// Then update the texture ID and start a new batch
			mDiffuseTex = pDiffuse;
			mNormalTex = pNormal;

		}

		// 0
		VertexDataStructurePT lNewVertex0 = new VertexDataStructurePT();
		lNewVertex0.xyzw(pPX, pPY + pHeight, pZ, 1f);
		lNewVertex0.uv(pSX / pDiffuse.getTextureWidth(), (pSY + pSH) / pDiffuse.getTextureHeight());

		// 1
		VertexDataStructurePT lNewVertex1 = new VertexDataStructurePT();
		lNewVertex1.xyzw(pPX, pPY, pZ, 1f);
		lNewVertex1.uv(pSX / pDiffuse.getTextureWidth(), pSY / pDiffuse.getTextureHeight());

		// 2
		VertexDataStructurePT lNewVertex2 = new VertexDataStructurePT();
		lNewVertex2.xyzw(pPX + pWidth, pPY, pZ, 1f);
		lNewVertex2.uv((pSX + pSW) / pDiffuse.getTextureWidth(), pSY / pDiffuse.getTextureHeight());

		// 3
		VertexDataStructurePT lNewVertex3 = new VertexDataStructurePT();
		lNewVertex3.xyzw(pPX + pWidth, pPY + pHeight, pZ, 1f);
		lNewVertex3.uv((pSX + pSW) / pDiffuse.getTextureWidth(), (pSY + pSH) / pDiffuse.getTextureHeight());

		// 0 1 2
		// 2 3 0

		mTempVertQueue.add(lNewVertex0);
		mTempVertQueue.add(lNewVertex1);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex3);
		mTempVertQueue.add(lNewVertex0);

	}

	public void end() {
		flush();

	}

	private void flush() {
		mVertexCount = mTempVertQueue.size();
		if (mVertexCount == 0)
			return;

		FloatBuffer lBuffer = BufferUtils.createFloatBuffer(mVertexCount * VertexDataStructurePT.stride);

		// TOOD: Better way of getting vertices from a queue to?
		for (int i = 0; i < mVertexCount; i++) {
			lBuffer.put(mTempVertQueue.poll().getElements());
		}

		lBuffer.flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.textureByteOffset);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mDiffuseTex.getTextureID());

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mNormalTex.getTextureID());

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mShader.unbind();

	}

	}
