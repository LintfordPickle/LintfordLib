package net.ld.library.core.graphics.spritebatch;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.ld.library.core.camera.ICamera;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.VertexDataStructurePT;
import net.ld.library.core.graphics.shaders.ShaderMVP_PT;
import net.ld.library.core.graphics.sprites.ISprite;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Vector4f;

// TODO: // https://github.com/ManojLakshan/monogame/blob/master/MonoGame.Framework/Graphics/SpriteBatchItem.cs
// TODO: Need to implement the spritebatch like TileSetRendererVBO, i.e. with separate shaders and 
// TODO: Should be a way to cache batches in case they don't change between frames (which will happen v. often)
public class SpriteBatch {

	// =============================================
	// Constants
	// =============================================

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz!:.? , $&/()[]-*";

	private static final int MAX_SPRITES = 1000;

	private static final String VERT_FILENAME = "bin/res/shaders/shader_basic.vert";
	private static final String FRAG_FILENAME = "bin/res/shaders/shader_basic.frag";

	// =============================================
	// Variables
	// =============================================

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	private int mCurrentTexID;

	private Queue<VertexDataStructurePT> mTempVertQueue;

	private ICamera mCamera;
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private int mCurNumSprites;

	private Vector4f mTempVector;

	// =============================================
	// Properties
	// =============================================

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

	// =============================================
	// Constructor
	// =============================================

	public SpriteBatch() {
		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME);
		mTempVertQueue = new LinkedList<VertexDataStructurePT>();

		mModelMatrix = new Matrix4f();
		mTempVector = new Vector4f();
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void loadContent(ResourceManager pResourceManager) {
		mShader.loadContent();

		mVaoId = GL30.glGenVertexArrays();
		mVboId = GL15.glGenBuffers();

		final int NUM_VERTS_PER_SPRITE = 6;
		mBuffer = BufferUtils.createFloatBuffer(MAX_SPRITES * NUM_VERTS_PER_SPRITE * VertexDataStructurePT.stride);

	}

	// =============================================
	// Methods
	// =============================================

	public void begin(ICamera pCamera) {
		mCurrentTexID = -1;
		mTempVertQueue.clear();
		mCamera = pCamera;
		mCurNumSprites = 0;
	}

	public void draw(String pText, float pX, float pY, float pZ, float pScale, Texture pTexture) {
		draw(pText, pX, pY, pZ, 1f, 1f, 1f, 1f, pScale, pTexture);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, Texture pTexture) {
		if (pTexture == null)
			return;

		final float lCharImageWidth = 32;
		float lPosX = pX;
		float lPosY = pY;

		for (int i = 0; i < pText.length(); i++) {
			int ci = CHARACTERS.indexOf(pText.charAt(i));
			int xx = ci % 16;
			int yy = ci / 16;

			float u = xx * lCharImageWidth;
			float v = yy * lCharImageWidth;

			draw(u, v, lCharImageWidth, lCharImageWidth, lPosX, lPosY, pZ, lCharImageWidth, lCharImageWidth, pScale, pTexture);
			lPosX += lCharImageWidth * pScale;
		}

	}

	public void draw(ISprite pSprite, float pPX, float pPY, float pZ, float pWidth, float pHeight, Texture pTexture) {
		draw(pSprite, pPX, pPY, pZ, pWidth, pHeight, 0f, 1f, pTexture);
	}

	public void draw(ISprite pSprite, float pPX, float pPY, float pZ, float pWidth, float pHeight, float pRotation, float pScale, Texture pTexture) {
		if (pSprite == null) {
			return;
		}

		draw(pSprite.getX(), pSprite.getY(), pSprite.getWidth(), pSprite.getHeight(), pPX, pPY, pZ, pWidth, pHeight, pRotation, 0, 0, pScale, pTexture);
	}

	public void draw(float pSX, float pSY, float pSW, float pSH, float x, float y, float pZ, float w, float h, float pRotation, float pROX, float pROY, float pScale, Texture pTexture) {
		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		float sin = (float) (Math.sin(pRotation));
		float cos = (float) (Math.cos(pRotation));

		// Should be passed into method
		float dx = -pROX * pScale;
		float dy = -pROY * pScale;

		// Vertex 0
		/////////////////////////
		mTempVector.xyzw(x + dx * cos - (dy + h * pScale) * sin, y + dx * sin + (dy + h * pScale) * cos, pZ, 1f);
		VertexDataStructurePT lNewVertex0 = new VertexDataStructurePT();
		lNewVertex0.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex0.uv(pSX / pTexture.getTextureWidth(), (pSY + pSH) / pTexture.getTextureHeight());

		// Vertex 1
		/////////////////////////
		mTempVector.xyzw(x + dx * cos - dy * sin, y + dx * sin + dy * cos, pZ, 1f);
		VertexDataStructurePT lNewVertex1 = new VertexDataStructurePT();
		lNewVertex1.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex1.uv(pSX / pTexture.getTextureWidth(), pSY / pTexture.getTextureHeight());

		// Vertex 2
		/////////////////////////
		mTempVector.xyzw(x + (dx + w * pScale) * cos - dy * sin, y + (dx + w * pScale) * sin + dy * cos, pZ, 1f);
		VertexDataStructurePT lNewVertex2 = new VertexDataStructurePT();
		lNewVertex2.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex2.uv((pSX + pSW) / pTexture.getTextureWidth(), pSY / pTexture.getTextureHeight());

		// Vertex 3
		/////////////////////////
		mTempVector.xyzw(x + (dx + w * pScale) * cos - (dy + h * pScale) * sin, y + (dx + w * pScale) * sin + (dy + h * pScale) * cos, pZ, 1f);
		VertexDataStructurePT lNewVertex3 = new VertexDataStructurePT();
		lNewVertex3.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex3.uv((pSX + pSW) / pTexture.getTextureWidth(), (pSY + pSH) / pTexture.getTextureHeight());

		draw(lNewVertex0, lNewVertex1, lNewVertex2, lNewVertex3);

	}

	public void draw(float pSX, float pSY, float pSW, float pSH, float x, float y, float pZ, float w, float h, float pScale, Texture pTexture) {
		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		// Vertex 0
		/////////////////////////
		mTempVector.xyzw(x, y + h * pScale, pZ, 1f);
		VertexDataStructurePT lNewVertex0 = new VertexDataStructurePT();
		lNewVertex0.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex0.uv(pSX / pTexture.getTextureWidth(), (pSY + pSH) / pTexture.getTextureHeight());

		// Vertex 1
		/////////////////////////
		mTempVector.xyzw(x, y, pZ, 1f);
		VertexDataStructurePT lNewVertex1 = new VertexDataStructurePT();
		lNewVertex1.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex1.uv(pSX / pTexture.getTextureWidth(), pSY / pTexture.getTextureHeight());

		// Vertex 2
		/////////////////////////
		mTempVector.xyzw(x + w * pScale, y, pZ, 1f);
		VertexDataStructurePT lNewVertex2 = new VertexDataStructurePT();
		lNewVertex2.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex2.uv((pSX + pSW) / pTexture.getTextureWidth(), pSY / pTexture.getTextureHeight());

		// Vertex 3
		/////////////////////////
		mTempVector.xyzw(x + w * pScale, y + h * pScale, pZ, 1f);
		VertexDataStructurePT lNewVertex3 = new VertexDataStructurePT();
		lNewVertex3.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex3.uv((pSX + pSW) / pTexture.getTextureWidth(), (pSY + pSH) / pTexture.getTextureHeight());

		draw(lNewVertex0, lNewVertex1, lNewVertex2, lNewVertex3);

	}

	private void draw(VertexDataStructurePT p0, VertexDataStructurePT p1, VertexDataStructurePT p2, VertexDataStructurePT p3) {
		// 0 1 2
		// 2 3 0

		mTempVertQueue.add(p0);
		mTempVertQueue.add(p1);
		mTempVertQueue.add(p2);
		mTempVertQueue.add(p2);
		mTempVertQueue.add(p3);
		mTempVertQueue.add(p0);

		mCurNumSprites++;
	}

	public void end() {
		flush();

	}

	private void flush() {
		// copy vertices to the float buffer
		mVertexCount = mTempVertQueue.size();
		if (mVertexCount == 0)
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

		GL20.glVertexAttribPointer(0, VertexDataStructurePT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL30.glBindVertexArray(mVaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mShader.unbind();

	}

	public void cleanUp() {
		if (mVaoId != -1)
			GL15.glDeleteBuffers(mVaoId);

		if (mVboId != -1)
			GL15.glDeleteBuffers(mVboId);
	}
}
