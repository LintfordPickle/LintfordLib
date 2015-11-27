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
import net.ld.library.core.graphics.VertexDataStructurePCT;
import net.ld.library.core.graphics.VertexDataStructurePT;
import net.ld.library.core.graphics.shaders.ShaderMVP_PT;
import net.ld.library.core.graphics.sprites.ISprite;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.maths.Vector4f;

// TODO: Need to implement the spritebatch like TileSetRendererVBO, i.e. with separate shaders and 
public class SpriteBatchColor {

	// =============================================
	// Constants
	// =============================================

	private static final int MAX_SPRITES = 1000;

	private static final String VERT_FILENAME = "res/shaders/shader_basic_col.vert";
	private static final String FRAG_FILENAME = "res/shaders/shader_basic_col.frag";

	// =============================================
	// Variables
	// =============================================

	private int mVaoId = -1;
	private int mVboId = -1;
	private int mVertexCount = 0;
	private int mCurrentTexID;

	private Queue<VertexDataStructurePCT> mTempVertQueue;

	private ICamera mCamera;
	private ShaderMVP_PT mShader;
	private Matrix4f mModelMatrix;
	private FloatBuffer mBuffer;
	private int mCurNumSprites;

	private Matrix4f mTempMatrix;
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

	public SpriteBatchColor() {
		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
				GL20.glBindAttribLocation(pShaderID, 2, "inTexCoord");
			}
		};

		mTempVertQueue = new LinkedList<VertexDataStructurePCT>();

		mModelMatrix = new Matrix4f();
		mTempMatrix = new Matrix4f();
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

	/**
	 * Renders a sprite from a source texture at the destination rectangle. This will be rendered into the current attached buffer.
	 * 
	 * @param pPX
	 *            Destination x
	 * @param pPY
	 *            Destination y
	 * @param pZ
	 *            Destination z
	 * @param pWidth
	 *            Destination width
	 * @param pHeight
	 *            Destination height
	 * @param pTexture
	 *            The source texture
	 * @param pSprite
	 *            The sprite to use as the source rectangle
	 */
	public void draw(Vector2f pPosition, float pZ, Vector4f pColor, Texture pTexture, ISprite pSprite) {

		if (pSprite == null) {
			// System.err.println("(SpriteBatch) Cannot find sprite : " + pSpriteName);
			return;
		}

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush(); // Force draw with the current textureS

			// Then update the texture ID and start a new batch
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			System.out.println("spritebatch buffer full, flushing");
			flush();
			mCurNumSprites = 0;
		}

		// FIXME: set the proper origin
		mTempVector.xyzw(pPosition.x, pPosition.y + pSprite.getHeight(), pZ, 1f);

		// 0
		VertexDataStructurePCT lNewVertex0 = new VertexDataStructurePCT();
		lNewVertex0.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex0.rgba(pColor);
		lNewVertex0.uv(pSprite.getX() / pTexture.getTextureWidth(), (pSprite.getY() + pSprite.getHeight()) / pTexture.getTextureHeight());

		// FIXME: set the proper origin
		mTempVector.xyzw(pPosition.x, pPosition.y, pZ, 1f);

		// 1
		VertexDataStructurePCT lNewVertex1 = new VertexDataStructurePCT();
		lNewVertex1.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex1.rgba(pColor);
		lNewVertex1.uv(pSprite.getX() / pTexture.getTextureWidth(), pSprite.getY() / pTexture.getTextureHeight());

		// FIXME: set the proper origin
		mTempVector.xyzw(pPosition.x + pSprite.getWidth(), pPosition.y, pZ, 1f);

		// 2
		VertexDataStructurePCT lNewVertex2 = new VertexDataStructurePCT();
		lNewVertex2.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex2.rgba(pColor);
		lNewVertex2.uv((pSprite.getX() + pSprite.getWidth()) / pTexture.getTextureWidth(), pSprite.getY() / pTexture.getTextureHeight());

		// FIXME: set the proper origin
		mTempVector.xyzw(pPosition.x + pSprite.getWidth(), pPosition.y + pSprite.getHeight(), pZ, 1f);

		// 3
		VertexDataStructurePCT lNewVertex3 = new VertexDataStructurePCT();
		lNewVertex3.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex3.rgba(pColor);
		lNewVertex3.uv((pSprite.getX() + pSprite.getWidth()) / pTexture.getTextureWidth(), (pSprite.getY() + pSprite.getHeight()) / pTexture.getTextureHeight());

		// 0 1 2
		// 2 3 0

		mTempVertQueue.add(lNewVertex0);
		mTempVertQueue.add(lNewVertex1);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex3);
		mTempVertQueue.add(lNewVertex0);

		mCurNumSprites++;

	}

	public void draw(Vector2f pPosition, float pZ, Vector4f pColor, float pRotation, Texture pTexture, ISprite pSprite) {

		if (pSprite == null) {
			// System.err.println("(SpriteBatch) Cannot find sprite : " + pSpriteName);
			return;
		}

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush(); // Force draw with the current textureS

			// Then update the texture ID and start a new batch
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			System.out.println("spritebatch buffer full, flushing");
			flush();
			mCurNumSprites = 0;
		}

		mTempMatrix.setIdentity();
		mTempMatrix.rotate(pRotation, 0, 0, 1);

		mTempVector.xyzw(-.5f * pSprite.getWidth(), .5f * pSprite.getHeight(), pZ, 1f);
		Matrix4f.transform(mTempMatrix, mTempVector, mTempVector);
		mTempVector.x += pPosition.x;
		mTempVector.y += pPosition.y;

		// 0
		VertexDataStructurePCT lNewVertex0 = new VertexDataStructurePCT();
		lNewVertex0.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex0.rgba(pColor);
		lNewVertex0.uv(pSprite.getX() / pTexture.getTextureWidth(), (pSprite.getY() + pSprite.getHeight()) / pTexture.getTextureHeight());

		mTempVector.xyzw(-.5f * pSprite.getWidth(), -.5f * pSprite.getHeight(), pZ, 1f);
		Matrix4f.transform(mTempMatrix, mTempVector, mTempVector);
		mTempVector.x += pPosition.x;
		mTempVector.y += pPosition.y;

		// 1
		VertexDataStructurePCT lNewVertex1 = new VertexDataStructurePCT();
		lNewVertex1.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex1.rgba(pColor);
		lNewVertex1.uv(pSprite.getX() / pTexture.getTextureWidth(), pSprite.getY() / pTexture.getTextureHeight());

		mTempVector.xyzw(.5f * pSprite.getWidth(), -.5f * pSprite.getHeight(), pZ, 1f);
		Matrix4f.transform(mTempMatrix, mTempVector, mTempVector);
		mTempVector.x += pPosition.x;
		mTempVector.y += pPosition.y;

		// 2
		VertexDataStructurePCT lNewVertex2 = new VertexDataStructurePCT();
		lNewVertex2.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex2.rgba(pColor);
		lNewVertex2.uv((pSprite.getX() + pSprite.getWidth()) / pTexture.getTextureWidth(), pSprite.getY() / pTexture.getTextureHeight());

		// FIXME: set the proper origin
		mTempVector.xyzw(.5f * pSprite.getWidth(), .5f * pSprite.getHeight(), pZ, 1f);
		Matrix4f.transform(mTempMatrix, mTempVector, mTempVector);
		mTempVector.x += pPosition.x;
		mTempVector.y += pPosition.y;

		// 3
		VertexDataStructurePCT lNewVertex3 = new VertexDataStructurePCT();
		lNewVertex3.xyzw(mTempVector.x, mTempVector.y, mTempVector.z, mTempVector.w);
		lNewVertex3.rgba(pColor);
		lNewVertex3.uv((pSprite.getX() + pSprite.getWidth()) / pTexture.getTextureWidth(), (pSprite.getY() + pSprite.getHeight()) / pTexture.getTextureHeight());

		// 0 1 2
		// 2 3 0

		mTempVertQueue.add(lNewVertex0);
		mTempVertQueue.add(lNewVertex1);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex3);
		mTempVertQueue.add(lNewVertex0);

		mCurNumSprites++;

	}

	/**
	 * Renders a a source rectangle from a source texture at the destination rectangle. This will be rendered into the current attached buffer.
	 * 
	 * @param pSX
	 *            Source rectangle x
	 * @param pSY
	 *            Source rectangle y
	 * @param pSW
	 *            Source rectangle width
	 * @param pSH
	 *            Source rectangle height
	 * @param pPX
	 *            Destination x
	 * @param pPY
	 *            Destination y
	 * @param pZ
	 *            Destination z
	 * @param pWidth
	 *            Destination width
	 * @param pHeight
	 *            Destination height
	 * @param pTexture
	 *            The source texture
	 */
	public void draw(float pSX, float pSY, float pSW, float pSH, Vector2f pPosition, float pZ, Vector4f pColor, float pWidth, float pHeight, Texture pTexture) {
		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush(); // Force draw with the current textureS

			// Then update the texture ID and start a new batch
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			System.out.println("texturespritebatch buffer full, flushing");
			flush();
			mCurNumSprites = 0;
		}

		// 0
		VertexDataStructurePCT lNewVertex0 = new VertexDataStructurePCT();
		lNewVertex0.xyzw(pPosition.x, pPosition.y + pHeight, pZ, 1f);
		lNewVertex0.rgba(pColor);
		lNewVertex0.uv(pSX / pTexture.getTextureWidth(), (pSY + pSH) / pTexture.getTextureHeight());

		// 1
		VertexDataStructurePCT lNewVertex1 = new VertexDataStructurePCT();
		lNewVertex1.xyzw(pPosition.x, pPosition.y, pZ, 1f);
		lNewVertex1.rgba(pColor);
		lNewVertex1.uv(pSX / pTexture.getTextureWidth(), pSY / pTexture.getTextureHeight());

		// 2
		VertexDataStructurePCT lNewVertex2 = new VertexDataStructurePCT();
		lNewVertex2.xyzw(pPosition.x + pWidth, pPosition.y, pZ, 1f);
		lNewVertex2.rgba(pColor);
		lNewVertex2.uv((pSX + pSW) / pTexture.getTextureWidth(), pSY / pTexture.getTextureHeight());

		// 3
		VertexDataStructurePCT lNewVertex3 = new VertexDataStructurePCT();
		lNewVertex3.xyzw(pPosition.x + pWidth, pPosition.y + pHeight, pZ, 1f);
		lNewVertex3.rgba(pColor);
		lNewVertex3.uv((pSX + pSW) / pTexture.getTextureWidth(), (pSY + pSH) / pTexture.getTextureHeight());

		// 0 1 2
		// 2 3 0

		mTempVertQueue.add(lNewVertex0);
		mTempVertQueue.add(lNewVertex1);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex2);
		mTempVertQueue.add(lNewVertex3);
		mTempVertQueue.add(lNewVertex0);

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

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePCT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePCT.colorElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.colorByteOffset);
		GL20.glVertexAttribPointer(2, VertexDataStructurePCT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePCT.stride, VertexDataStructurePCT.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		// Bind to the VAO that has all the information about the quad vertices
		GL30.glBindVertexArray(mVaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		// /DRAW

		mShader.unbind();

	}

	public void cleanUp() {
		if (mVaoId != -1)
			GL15.glDeleteBuffers(mVaoId);

		if (mVboId != -1)
			GL15.glDeleteBuffers(mVboId);
	}
}
