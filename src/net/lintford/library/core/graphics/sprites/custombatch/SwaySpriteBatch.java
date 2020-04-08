package net.lintford.library.core.graphics.sprites.custombatch;

import java.util.List;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.maths.Vector2f;

public class SwaySpriteBatch extends SpriteBatch {

	public boolean bottom;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SwaySpriteBatch() {
		// TODO Auto-generated constructor stub
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(SpriteSheetDefinition pSpriteSheet, SpriteInstance pSprite, Rectangle pDstRectangle, float pZ, float pR, float pG, float pB, float pA) {
		if (pSpriteSheet == null)
			return;

		if (pSprite == null) {
			return;
		}

		if (!mIsDrawing)
			return;

		Texture lTexture = pSpriteSheet.texture();
		SpriteFrame lCurrentFrame = pSprite.currentSpriteFrame();

		drawGrass(lTexture, lCurrentFrame, pDstRectangle, pZ, pA);

	}

	public void drawGrass(Texture pTexture, Rectangle pSrcRect, Rectangle pDestRect, float pZ, float pA) {
		if (pSrcRect == null)
			return;

		drawGrass(pTexture, pSrcRect.x(), pSrcRect.y(), pSrcRect.w(), pSrcRect.h(), pDestRect, pZ, pA);

	}

	public void drawGrass(Texture pTexture, float pSX, float pSY, float pSW, float pSH, Rectangle pDestRect, float pZ, float pA) {
		if (!isLoaded())
			return;

		if (!mIsDrawing)
			return;

		if (pDestRect == null)
			return;

		List<Vector2f> lVertList = pDestRect.getVertices();

		final float pDX = lVertList.get(0).x;
		final float pDY = lVertList.get(0).y;
		final float pDW = lVertList.get(1).x - lVertList.get(0).x;
		final float pDH = lVertList.get(2).y - lVertList.get(0).y;

		if (pTexture == null) {
			// Resolve to use a default texture, or the 'MISSING_TEXTURE'
			if (TextureManager.USE_DEBUG_MISSING_TEXTURES) {
				pTexture = mResourceManager.textureManager().textureNotFound();

			} else {
				return;

			}
		}

		if (mUseCheckerPattern) {
			pTexture = mResourceManager.textureManager().checkerIndexedTexture();

		}

		if (mCurrentTexID == -1) { // first texture
			mCurrentTexID = pTexture.getTextureID();
		} else if (mCurrentTexID != pTexture.getTextureID()) {
			flush();
			mCurrentTexID = pTexture.getTextureID();
		}

		if (mCurNumSprites >= MAX_SPRITES) {
			flush();
		}

		final float lHalfWPixel = (1f / pTexture.getTextureWidth()) * 0.5f;
		final float lHalfHPixel = (1f / pTexture.getTextureHeight()) * 0.5f;

		// Vertex 0
		float x0 = pDX;
		float y0 = pDY;
		float u0 = pSX / pTexture.getTextureWidth() + lHalfWPixel;
		float v0 = pSY / (float) pTexture.getTextureHeight() + lHalfHPixel;

		// Vertex 1
		float x1 = pDX + pDW;
		float y1 = pDY;
		float u1 = (pSX + pSW) / pTexture.getTextureWidth() - lHalfWPixel;
		float v1 = pSY / pTexture.getTextureHeight() + lHalfHPixel;

		// Vertex 2
		float x2 = pDX;
		float y2 = pDY + pDH;
		float u2 = pSX / pTexture.getTextureWidth() + lHalfWPixel;
		float v2 = (pSY + pSH) / pTexture.getTextureHeight() - lHalfHPixel;

		// Vertex 3
		float x3 = pDX + pDW;
		float y3 = pDY + pDH;
		float u3 = (pSX + pSW) / pTexture.getTextureWidth() - lHalfWPixel;
		float v3 = (pSY + pSH) / pTexture.getTextureHeight() - lHalfHPixel;

		float lBottom = bottom ? 1.0f : 0.0f;
		float lTop = !bottom ? 1.0f : 0.0f;

		// CCW 102203
		addVertToBuffer(x1, y1, pZ, 1f, lTop, 0f, 0f, pA, u1, v1); // 1
		addVertToBuffer(x0, y0, pZ, 1f, lTop, 0f, 0f, pA, u0, v0); // 0
		addVertToBuffer(x2, y2, pZ, 1f, lBottom, 0f, 0f, pA, u2, v2); // 2
		addVertToBuffer(x1, y1, pZ, 1f, lTop, 0f, 0f, pA, u1, v1); // 1
		addVertToBuffer(x2, y2, pZ, 1f, lBottom, 0f, 0f, pA, u2, v2); // 2
		addVertToBuffer(x3, y3, pZ, 1f, lBottom, 0f, 0f, pA, u3, v3); // 3

		mCurNumSprites++;
	}

}
