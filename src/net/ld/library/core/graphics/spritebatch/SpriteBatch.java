package net.ld.library.core.graphics.spritebatch;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.ld.library.core.camera.ICamera;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.shaders.ShaderMVP_PT;
import net.ld.library.core.graphics.sprites.ISprite;
import net.ld.library.core.graphics.sprites.SpriteSheet;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Vector4f;

// TODO(John): The SpriteBatch doesn't actually allow to cache buffers between frames if there is no change.
public class SpriteBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final int MAX_SPRITES = 2048;

	protected static final String VERT_FILENAME = "res/shaders/shader_basic.vert";
	protected static final String FRAG_FILENAME = "res/shaders/shader_basic.frag";

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

	protected int vaoId = -1;
	protected int vboId = -1;
	protected int vertexCount = 0;
	protected int currentTexID;

	protected ICamera camera;
	protected ShaderMVP_PT shader;
	protected Matrix4f modelMatrix;
	protected FloatBuffer buffer;
	protected int currentNumSprites;

	protected Vector4f tempVector;

	protected boolean isLoaded;
	protected boolean isDrawing;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isDrawing() {
		return isDrawing;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void modelMatrix(Matrix4f pNewMatrix) {
		if (pNewMatrix == null) {
			modelMatrix = new Matrix4f();
			modelMatrix.setIdentity();
		} else {
			modelMatrix = pNewMatrix;
		}
	}

	public Matrix4f modelMatrix() {
		return modelMatrix;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteBatch() {
		shader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
				GL20.glBindAttribLocation(pShaderID, 2, "inTexCoord");
			}
		};

		modelMatrix = new Matrix4f();
		tempVector = new Vector4f();

		buffer = BufferUtils.createFloatBuffer(MAX_SPRITES * NUM_VERTS_PER_SPRITE * stride);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		shader.loadGLContent(pResourceManager);

		vaoId = GL30.glGenVertexArrays();
		vboId = GL15.glGenBuffers();

		isLoaded = true;
	}

	public void unloadGLContent() {
		shader.unloadGLContent();

		GL30.glDeleteVertexArrays(vaoId);
		GL15.glDeleteBuffers(vboId);

		isLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void begin(ICamera pCamera) {
		if (isDrawing)
			return; // already drawing, don't want to flush too early

		currentTexID = -1;
		camera = pCamera;

		buffer.clear();
		vertexCount = 0;
		currentNumSprites = 0;
		isDrawing = true;

	}

	public void draw(SpriteSheet pSpriteSheet, String pSpriteName, float pDstX, float pDstY, float pZ, float pDstW,
			float pDstH, float pScale, float pR, float pG, float pB, float pA) {
		if (!isDrawing)
			return;

		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		final ISprite SPRITE = pSpriteSheet.getSprite(pSpriteName);

		draw(pSpriteSheet, SPRITE, pDstX, pDstY, pZ, pDstW, pDstH, pScale, pR, pG, pB, pA);

	}

	public void draw(SpriteSheet pSpriteSheet, ISprite pSprite, float pDstX, float pDstY, float pZ, float pDstW,
			float pDstH, float pScale, float pR, float pG, float pB, float pA) {
		draw(pSpriteSheet, pSprite, pDstX, pDstY, pZ, pDstW, pDstH, false, pScale, pR, pG, pB, pA);

	}

	public void draw(SpriteSheet pSpriteSheet, ISprite pSprite, float pDstX, float pDstY, float pZ, float pDstW,
			float pDstH, boolean pFlipH, float pScale, float pR, float pG, float pB, float pA) {
		if (!isDrawing)
			return;

		if (pSprite == null)
			return;

		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		final Texture TEXTURE = pSpriteSheet.texture();

		if (currentTexID == -1) { // first texture
			currentTexID = TEXTURE.getTextureID();
		} else if (currentTexID != TEXTURE.getTextureID()) {
			flush();
			currentTexID = TEXTURE.getTextureID();
		}

		if (currentNumSprites >= MAX_SPRITES) {
			flush();
		}

		final float lx = !pFlipH ? pSprite.getX() : pSprite.getX() + pSprite.getWidth();
		final float gx = pFlipH ? pSprite.getX() : pSprite.getX() + pSprite.getWidth();

		// Vertex 0
		final float x0 = pDstX;
		final float y0 = pDstY + pDstH * pScale;
		final float u0 = lx / TEXTURE.getTextureWidth();
		final float v0 = (pSprite.getY() + pSprite.getHeight()) / TEXTURE.getTextureHeight();

		// Vertex 1
		final float x1 = pDstX;
		final float y1 = pDstY;
		final float u1 = lx / TEXTURE.getTextureWidth();
		final float v1 = pSprite.getY() / TEXTURE.getTextureHeight();

		// Vertex 2
		final float x2 = pDstX + pDstW * pScale;
		final float y2 = pDstY;
		final float u2 = gx / TEXTURE.getTextureWidth();
		final float v2 = pSprite.getY() / TEXTURE.getTextureHeight();

		// Vertex 3
		final float x3 = pDstX + pDstW * pScale;
		final float y3 = pDstY + pDstH * pScale;
		final float u3 = gx / TEXTURE.getTextureWidth();
		final float v3 = (pSprite.getY() + pSprite.getHeight()) / TEXTURE.getTextureHeight();

		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0

		currentNumSprites++;

	}

	public void draw(SpriteSheet pSpriteSheet, String pSpriteName, float pDstX, float pDstY, float pZ, float pDstW,
			float pDstH, float pR, float pG, float pB, float pA, float pRotation, float pROX, float pROY, float pScaleX,
			float pScaleY) {
		if (!isDrawing)
			return;

		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		final ISprite SPRITE = pSpriteSheet.getSprite(pSpriteName);

		draw(pSpriteSheet, SPRITE, pDstX, pDstY, pZ, pDstW, pDstH, pR, pG, pB, pA, pRotation, pROX, pROY, pScaleX,
				pScaleY);
	}

	public void draw(SpriteSheet pSpriteSheet, ISprite pSprite, float pDstX, float pDstZ, float pZ, float pDstW,
			float pDstH, float pR, float pG, float pB, float pA, float pRotation, float pROX, float pROY, float pScaleX,
			float pScaleY) {
		if (!isDrawing)
			return;

		if (pSpriteSheet == null || !pSpriteSheet.isLoaded())
			return;

		final Texture TEXTURE = pSpriteSheet.texture();

		if (currentTexID == -1) { // first texture
			currentTexID = TEXTURE.getTextureID();
		} else if (currentTexID != TEXTURE.getTextureID()) {
			flush();
			currentTexID = TEXTURE.getTextureID();
		}

		if (currentNumSprites >= MAX_SPRITES) {
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
		/////////////////////////
		float x0 = pDstX + dx * cos - (dy + pDstH * pScaleX) * sin;
		float y0 = pDstZ + dx * sin + (dy + pDstH * pScaleY) * cos;
		float u0 = pSprite.getX() / TEXTURE.getTextureWidth();
		float v0 = (pSprite.getY() + pSprite.getHeight()) / TEXTURE.getTextureHeight();

		// Vertex 1
		/////////////////////////
		float x1 = pDstX + dx * cos - dy * sin;
		float y1 = pDstZ + dx * sin + dy * cos;
		float u1 = pSprite.getX() / TEXTURE.getTextureWidth();
		float v1 = pSprite.getY() / TEXTURE.getTextureHeight();

		// Vertex 2
		/////////////////////////
		float x2 = pDstX + (dx + pDstW * pScaleX) * cos - dy * sin;
		float y2 = pDstZ + (dx + pDstW * pScaleY) * sin + dy * cos;
		float u2 = (pSprite.getX() + pSprite.getWidth()) / TEXTURE.getTextureWidth();
		float v2 = pSprite.getY() / TEXTURE.getTextureHeight();

		// Vertex 3
		/////////////////////////
		float x3 = pDstX + (dx + pDstW * pScaleX) * cos - (dy + pDstH * pScaleX) * sin;
		float y3 = pDstZ + (dx + pDstW * pScaleY) * sin + (dy + pDstH * pScaleY) * cos;
		float u3 = (pSprite.getX() + pSprite.getWidth()) / TEXTURE.getTextureWidth();
		float v3 = (pSprite.getY() + pSprite.getHeight()) / TEXTURE.getTextureHeight();

		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0
		addVertToBuffer(x1, y1, pZ, 1f, pR, pG, pB, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x2, y2, pZ, 1f, pR, pG, pB, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, pR, pG, pB, pA, u3, v3); // 3
		addVertToBuffer(x0, y0, pZ, 1f, pR, pG, pB, pA, u0, v0); // 0

		currentNumSprites++;

	}

	private void addVertToBuffer(float x, float y, float z, float w, float r, float g, float b, float a, float u,
			float v) {
		// If the buffer is already full, we need to draw what is currently in
		// the buffer and start a new one.
		if (currentNumSprites >= MAX_SPRITES * NUM_VERTS_PER_SPRITE - 1) {
			flush();

		}

		buffer.put(x);
		buffer.put(y);
		buffer.put(z);
		buffer.put(w);

		buffer.put(r);
		buffer.put(g);
		buffer.put(b);
		buffer.put(a);

		buffer.put(u);
		buffer.put(v);

		vertexCount++;

	}

	public void end() {
		if (!isDrawing)
			return;

		isDrawing = false;
		flush();

	}

	private void flush() {
		if (!isLoaded)
			return;

		if (vertexCount == 0)
			return;

		buffer.flip();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexID);

		GL30.glBindVertexArray(vaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, positionElementCount, GL11.GL_FLOAT, false, stride, positionByteOffset);
		GL20.glVertexAttribPointer(1, colorElementCount, GL11.GL_FLOAT, false, stride, colorByteOffset);
		GL20.glVertexAttribPointer(2, textureElementCount, GL11.GL_FLOAT, false, stride, textureByteOffset);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		shader.projectionMatrix(camera.projection());
		shader.viewMatrix(camera.view());
		shader.modelMatrix(modelMatrix);

		shader.bind();

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);

		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		shader.unbind();

		buffer.clear();
		currentNumSprites = 0;

		currentNumSprites = 0;
		vertexCount = 0;

	}

	public void cleanUp() {
		if (vaoId != -1)
			GL15.glDeleteBuffers(vaoId);

		if (vboId != -1)
			GL15.glDeleteBuffers(vboId);
	}
}