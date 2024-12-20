package net.lintfordlib.core.graphics.sprites.custombatch;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.batching.TextureSlotBatch;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.graphics.textures.TextureManager;

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
	// Methods
	// --------------------------------------

	@Override
	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, Rectangle destRect, float zDepth, Color colorTint) {
		if (spriteSheetDefinition == null)
			return;

		if (spriteInstance == null)
			return;

		if (!mIsDrawing)
			return;

		final var lTexture = spriteSheetDefinition.texture();
		final var lCurrentFrame = spriteInstance.currentSpriteFrame();

		drawGrass(lTexture, lCurrentFrame, destRect, zDepth, colorTint);
	}

	public void drawGrass(Texture tex, Rectangle srcRect, Rectangle destRect, float zDepth, Color colorTint) {
		if (srcRect == null)
			return;

		drawGrass(tex, srcRect.x(), srcRect.y(), srcRect.width(), srcRect.height(), destRect, zDepth, colorTint);
	}

	public void drawGrass(Texture tex, float sx, float sy, float sw, float sh, Rectangle destRect, float zDepth, Color colorTint) {
		if (!isLoaded())
			return;

		if (!mIsDrawing)
			return;

		if (destRect == null)
			return;

		final float pDX = destRect.x();
		final float pDY = destRect.y();
		final float pDW = destRect.width();
		final float pDH = destRect.height();

		if (tex == null) {
			if (TextureManager.USE_DEBUG_MISSING_TEXTURES) {
				tex = mResourceManager.textureManager().textureNotFound();
			} else {
				return;
			}
		}

		int lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(tex);
		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_TEXTURE_INVALID)
			return;

		if (lTextureSlotIndex == TextureSlotBatch.TEXTURE_SLOTS_FULL) {
			flush(); // flush and try again
			lTextureSlotIndex = mTextureSlots.getTextureSlotIndex(tex);
		}

		final var pcx = useHalfPixelCorrection() ? .5f : .0f;
		final var pcy = useHalfPixelCorrection() ? .5f : .0f;

		// Vertex 0
		float x0 = pDX;
		float y0 = pDY;
		float u0 = (sx + pcx) / tex.getTextureWidth();
		float v0 = (sy + pcy) / tex.getTextureHeight();

		// Vertex 1
		float x1 = pDX + pDW;
		float y1 = pDY;
		float u1 = (sx + sw - pcx) / tex.getTextureWidth();
		float v1 = (sy + pcy) / tex.getTextureHeight();

		// Vertex 2
		float x2 = pDX;
		float y2 = pDY + pDH;
		float u2 = (sx + pcx) / tex.getTextureWidth();
		float v2 = (sy + sh - pcy) / tex.getTextureHeight();

		// Vertex 3
		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (sx + sw - pcx) / tex.getTextureWidth();
		float v3 = (sy + sh - pcy) / tex.getTextureHeight();

		float lBottom = mSwayBottomOfSprite ? 1.0f : 0.0f;
		float lTop = !mSwayBottomOfSprite ? 1.0f : 0.0f;

		// TOOD : The winding order here seems incorrect (not being updated with the rest of the engine?)
		// CCW 102203
		addVertToBuffer(x1, y1, zDepth, 1f, lTop, 0f, 0f, colorTint.a, u1, v1, lTextureSlotIndex); // 1
		addVertToBuffer(x0, y0, zDepth, 1f, lTop, 0f, 0f, colorTint.a, u0, v0, lTextureSlotIndex); // 0
		addVertToBuffer(x2, y2, zDepth, 1f, lBottom, 0f, 0f, colorTint.a, u2, v2, lTextureSlotIndex); // 2
		addVertToBuffer(x1, y1, zDepth, 1f, lTop, 0f, 0f, colorTint.a, u1, v1, lTextureSlotIndex); // 1
		addVertToBuffer(x2, y2, zDepth, 1f, lBottom, 0f, 0f, colorTint.a, u2, v2, lTextureSlotIndex); // 2
		addVertToBuffer(x3, y3, zDepth, 1f, lBottom, 0f, 0f, colorTint.a, u3, v3, lTextureSlotIndex); // 3
	}
}
