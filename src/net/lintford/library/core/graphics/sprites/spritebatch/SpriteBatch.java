package net.lintford.library.core.graphics.sprites.spritebatch;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.sprites.ISprite;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheet;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector4f;

// TODO(John): The SpriteBatch doesn't actually allow to cache buffers between frames if there is no change.
public class SpriteBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 2048;

	protected static final String VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	protected static final int NUM_VERTS_PER_SPRITE = 6;

	// The number of bytes an element has (all elements are floats here)
	protected static final int elementBytes = 4;

	// Elements per parameter
	protected static final int positionElementCount = 4;
	protected static final int colorElementCount = 4;
	protected static final int textureElementCount = 2;

	// Bytes per parameter
	protected static final int positionBytesCount = positionElementCount * elementBytes;
	protected static final int colorBytesCount = colorElementCount * elementBytes;
	protected static final int textureBytesCount = textureElementCount * elementBytes;

	// Byte offsets per parameter
	protected static final int positionByteOffset = 0;
	protected static final int colorByteOffset = positionByteOffset + positionBytesCount;
	protected static final int textureByteOffset = colorByteOffset + colorBytesCount;

	// The amount of elements that a vertex has
	protected static final int elementCount = positionElementCount + colorElementCount + textureElementCount;

	// The size of a vertex in bytes (sizeOf())
	protected static final int stride = positionBytesCount + colorBytesCount + textureBytesCount;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mVaoID = -1;
	protected int mVboID = -1;
	protected int mVertexCount = 0;
	protected int mCurrentTexID;

	protected ICamera mCamera;
	protected ShaderMVP_PT mShader;
	protected Matrix4f mModelMatrix;
	protected FloatBuffer mBuffer;
	protected int mCurrentNumSprites;

	protected Vector4f mTempVector;

	protected boolean mIsLoaded;
	protected boolean mIsDrawing;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isDrawing() {
		return mIsDrawing;
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteBatch() {
		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
				GL20.glBindAttribLocation(pShaderID, 2, "inTexCoord");
			}
		};

		mModelMatrix = new Matrix4f();
		mTempVector = new Vector4f();

		mBuffer = BufferUtils.createFloatBuffer(MAX_SPRITES * NUM_VERTS_PER_SPRITE * stride);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if(mIsLoaded) return;
		
		mShader.loadGLContent(pResourceManager);

		if(mVaoID == -1)
			mVaoID = GL30.glGenVertexArrays();
		
		if(mVboID == -1)
			mVboID = GL15.glGenBuffers();
		
		mIsLoaded = true;
	}

	public void unloadGLContent() {
		if(!mIsLoaded) return;
		
		mShader.unloadGLContent();
		
		if(mVaoID > -1)
			GL30.glDeleteVertexArrays(mVaoID);
		
		if(mVboID > -1)
			GL15.glDeleteBuffers(mVboID);
		
		mIsLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		if (mIsDrawing)
			return; // already drawing, don't want to flush too early

		mCurrentTexID = -1;
		mCamera = pCamera;

		mBuffer.clear();
		mVertexCount = 0;
		mCurrentNumSprites = 0;
		mIsDrawing = true;

	}

	public void draw(SpriteSheet pSpriteSheet, String pSpriteName, float pDstX, float pDstY, float pZ, float pDstW, float pDstH, float pScale, float pR, float pG, float pB, float pA) {
		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		final ISprite SPRITE = pSpriteSheet.getSprite(pSpriteName);

		draw(pSpriteSheet, SPRITE, pDstX, pDstY, pZ, pDstW, pDstH, pScale, pR, pG, pB, pA);

	}

	public void draw(SpriteSheet pSpriteSheet, ISprite pSprite, float pDstX, float pDstY, float pZ, float pDstW, float pDstH, float pScale, float pR, float pG, float pB, float pA) {
		draw(pSpriteSheet, pSprite, pDstX, pDstY, pZ, pDstW, pDstH, false, pScale, pR, pG, pB, pA);

	}

	public void draw(SpriteSheet pSpriteSheet, ISprite pSprite, float pDstX, float pDstY, float pZ, float pDstW, float pDstH, boolean pFlipH, float pScale, float pR, float pG, float pB, float pA) {
		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		draw(pSpriteSheet.texture(), pSprite, pDstX, pDstY, pZ, pDstW, pDstH, pFlipH, pScale, pR, pG, pB, pA);

	}

	public void draw(Texture pTexture, ISprite pSprite, float x, float y, float pZ, float w, float h, boolean pFlipH, float pScale, float pR, float pG, float pB, float pA) {
		if (!mIsDrawing)
			return;

		if (pSprite == null)
			return;

		if (pTexture == null)
			return;

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurrentNumSprites >= MAX_SPRITES) {
			flush();
		}

		final float lx = !pFlipH ? pSprite.getX() : pSprite.getX() + pSprite.getW();
		final float gx = pFlipH ? pSprite.getX() : pSprite.getX() + pSprite.getW();

		// Vertex 0
		float x0 = x;
		float y0 = y + h * pScale;
		float u0 = pSprite.getX() / pTexture.getTextureWidth();
		float v0 = (pSprite.getY() + pSprite.getH()) / (float) pTexture.getTextureHeight();

		// Vertex 1
		float x1 = x;
		float y1 = y;
		float u1 = pSprite.getX() / pTexture.getTextureWidth();
		float v1 = pSprite.getY() / pTexture.getTextureHeight();

		// Vertex 2
		float x2 = x + w * pScale;
		float y2 = y;
		float u2 = (pSprite.getX() + pSprite.getW()) / pTexture.getTextureWidth();
		float v2 = pSprite.getY() / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = x + w * pScale;
		float y3 = y + h * pScale;
		float u3 = (pSprite.getX() + pSprite.getW()) / pTexture.getTextureWidth();
		float v3 = (pSprite.getY() + pSprite.getH()) / pTexture.getTextureHeight();

		// CCW 102203
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3

		mCurrentNumSprites++;

	}

	public void draw(SpriteSheet pSpriteSheet, String pSpriteName, float pDstX, float pDstY, float pZ, float pDstW, float pDstH, float pR, float pG, float pB, float pA, float pRotation, float pROX, float pROY, float pScaleX, float pScaleY) {
		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		final ISprite SPRITE = pSpriteSheet.getSprite(pSpriteName);

		draw(pSpriteSheet, SPRITE, pDstX, pDstY, pZ, pDstW, pDstH, pR, pG, pB, pA, pRotation, pROX, pROY, pScaleX, pScaleY);
	}

	public void draw(SpriteSheet pSpriteSheet, ISprite pSprite, float pDstX, float pDstY, float pZ, float pDstW, float pDstH, float pR, float pG, float pB, float pA, float pRotation, float pROX, float pROY, float pScaleX, float pScaleY) {
		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		draw(pSpriteSheet.texture(), pSprite, pDstX, pDstY, pZ, pDstW, pDstH, pR, pG, pB, pA, pRotation, pROX, pROY, pScaleX, pScaleY);

	}

	public void draw(Texture pTexture, ISprite pSprite, float pDstX, float pDstZ, float pZ, float pDstW, float pDstH, float pR, float pG, float pB, float pA, float pRotation, float pROX, float pROY, float pScaleX, float pScaleY) {
		if (!mIsDrawing)
			return;
		
		if(pSprite == null) {
			return;
		}

		if (pTexture == null)
			return;

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurrentNumSprites >= MAX_SPRITES) {
			flush();
		}

		float sin = (float) (Math.sin(pRotation));
		float cos = (float) (Math.cos(pRotation));

		// Translate the sprite to the origin
		float dx = -pROX * pScaleX;
		float dy = -pROY * pScaleY;

		// Apply the difference back to the global positions
		pDstX += pROX * pScaleX;
		pDstZ += pROY * pScaleY;

		// Vertex 0
		float x0 = pDstX + dx * cos - (dy + pDstH * pScaleX) * sin;
		float y0 = pDstZ + dx * sin + (dy + pDstH * pScaleY) * cos;
		float u0 = pSprite.getX() / pTexture.getTextureWidth();
		float v0 = (pSprite.getY() + pSprite.getH()) / pTexture.getTextureHeight();

		// Vertex 1
		float x1 = pDstX + dx * cos - dy * sin;
		float y1 = pDstZ + dx * sin + dy * cos;
		float u1 = pSprite.getX() / pTexture.getTextureWidth();
		float v1 = pSprite.getY() / pTexture.getTextureHeight();

		// Vertex 2
		float x2 = pDstX + (dx + pDstW * pScaleX) * cos - dy * sin;
		float y2 = pDstZ + (dx + pDstW * pScaleY) * sin + dy * cos;
		float u2 = (pSprite.getX() + pSprite.getW()) / pTexture.getTextureWidth();
		float v2 = pSprite.getY() / pTexture.getTextureHeight();

		// Vertex 3
		float x3 = pDstX + (dx + pDstW * pScaleX) * cos - (dy + pDstH * pScaleX) * sin;
		float y3 = pDstZ + (dx + pDstW * pScaleY) * sin + (dy + pDstH * pScaleY) * cos;
		float u3 = (pSprite.getX() + pSprite.getW()) / pTexture.getTextureWidth();
		float v3 = (pSprite.getY() + pSprite.getH()) / pTexture.getTextureHeight();

		// CCW 102203
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3

		mCurrentNumSprites++;
	}

	private void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u, float v) {
		// If the buffer is already full, we need to draw what is currently in the buffer and start a new one.
		if (mCurrentNumSprites >= MAX_SPRITES * NUM_VERTS_PER_SPRITE - 1) {
			flush();

		}

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
		if (!mIsLoaded)
			return;

		if (mVertexCount == 0)
			return;

		mBuffer.flip();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);

		GL30.glBindVertexArray(mVaoID);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, positionElementCount, GL11.GL_FLOAT, false, stride, positionByteOffset);
		GL20.glVertexAttribPointer(1, colorElementCount, GL11.GL_FLOAT, false, stride, colorByteOffset);
		GL20.glVertexAttribPointer(2, textureElementCount, GL11.GL_FLOAT, false, stride, textureByteOffset);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		mShader.projectionMatrix(mCamera.projection());
		mShader.viewMatrix(mCamera.view());
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mVertexCount);

		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mShader.unbind();

		mBuffer.clear();

		mCurrentNumSprites = 0;
		mVertexCount = 0;

	}

}