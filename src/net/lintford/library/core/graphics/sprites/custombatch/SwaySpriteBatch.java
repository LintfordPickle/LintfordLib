package net.lintford.library.core.graphics.sprites.custombatch;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.batching.TextureSlotBatch;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;

public class SwaySpriteBatch extends SpriteBatch {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mSwayBottomOfSprite;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void swayBottomOfSprite(boolean newValue) {
		mSwayBottomOfSprite = newValue;
	}

	public boolean swayBottomOfSprite() {
		return mSwayBottomOfSprite;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SwaySpriteBatch() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, Rectangle destRectangle, float zDepth, Color colorTint) {
		if (spriteSheetDefinition == null)
			return;

		if (spriteInstance == null)
			return;

		if (!mIsDrawing)
			return;

		final var lTexture = spriteSheetDefinition.texture();
		final var lCurrentFrame = spriteInstance.currentSpriteFrame();

		drawGrass(lTexture, lCurrentFrame, destRectangle, zDepth, colorTint);
	}

	public void drawGrass(Texture texture, Rectangle sourceRectangle, Rectangle destRectangle, float zDepth, Color colorTint) {
		if (sourceRectangle == null)
			return;

		drawGrass(texture, sourceRectangle.x(), sourceRectangle.y(), sourceRectangle.width(), sourceRectangle.height(), destRectangle, zDepth, colorTint);
	}

	public void drawGrass(Texture texture, float sourceX, float sourceY, float sourceWidth, float sourceHeight, Rectangle destRectangle, float zDepth, Color colorTint) {
		if (!isLoaded())
			return;

		if (!mIsDrawing)
			return;

		if (destRectangle == null)
			return;

		final var lVertList = destRectangle.getVertices();

		final float pDX = lVertList.get(0).x;
		final float pDY = lVertList.get(0).y;
		final float pDW = lVertList.get(1).x - lVertList.get(0).x;
		final float pDH = lVertList.get(2).y - lVertList.get(0).y;

		if (texture == null) {
			if (TextureManager.USE_DEBUG_MISSING_TEXTURES) {
				texture = mResourceManager.textureManager().textureNotFound();
			} else {
				return;
			}
		}

		float lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(texture);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(texture);
		}

		final float lHalfWPixel = (1f / texture.getTextureWidth()) * 0.5f;
		final float lHalfHPixel = (1f / texture.getTextureHeight()) * 0.5f;

		// Vertex 0
		float x0 = pDX;
		float y0 = pDY;
		float u0 = sourceX / texture.getTextureWidth() + lHalfWPixel;
		float v0 = sourceY / (float) texture.getTextureHeight() + lHalfHPixel;

		// Vertex 1
		float x1 = pDX + pDW;
		float y1 = pDY;
		float u1 = (sourceX + sourceWidth) / texture.getTextureWidth() - lHalfWPixel;
		float v1 = sourceY / texture.getTextureHeight() + lHalfHPixel;

		// Vertex 2
		float x2 = pDX;
		float y2 = pDY + pDH;
		float u2 = sourceX / texture.getTextureWidth() + lHalfWPixel;
		float v2 = (sourceY + sourceHeight) / texture.getTextureHeight() - lHalfHPixel;

		// Vertex 3
		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (sourceX + sourceWidth) / texture.getTextureWidth() - lHalfWPixel;
		float v3 = (sourceY + sourceHeight) / texture.getTextureHeight() - lHalfHPixel;

		float lBottom = mSwayBottomOfSprite ? 1.0f : 0.0f;
		float lTop = !mSwayBottomOfSprite ? 1.0f : 0.0f;

		// CCW 102203
		addVertToBuffer(x1, y1, zDepth, 1f, lTop, 0f, 0f, colorTint.a, u1, v1, lTextureSlotIndex); // 1
		addVertToBuffer(x0, y0, zDepth, 1f, lTop, 0f, 0f, colorTint.a, u0, v0, lTextureSlotIndex); // 0
		addVertToBuffer(x2, y2, zDepth, 1f, lBottom, 0f, 0f, colorTint.a, u2, v2, lTextureSlotIndex); // 2
		addVertToBuffer(x1, y1, zDepth, 1f, lTop, 0f, 0f, colorTint.a, u1, v1, lTextureSlotIndex); // 1
		addVertToBuffer(x2, y2, zDepth, 1f, lBottom, 0f, 0f, colorTint.a, u2, v2, lTextureSlotIndex); // 2
		addVertToBuffer(x3, y3, zDepth, 1f, lBottom, 0f, 0f, colorTint.a, u3, v3, lTextureSlotIndex); // 3
	}
}
