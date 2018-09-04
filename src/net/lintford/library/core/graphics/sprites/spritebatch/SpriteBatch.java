package net.lintford.library.core.graphics.sprites.spritebatch;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

// TODO: ---> Add batching based on SpriteSheetDef (or rather, the Texture).
public class SpriteBatch extends TextureBatch {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteBatch() {
		super();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(SpriteSheetDef pSpriteSheet, SpriteInstance pSprite, float pZ, float pR, float pG, float pB, float pA) {
		if (pSpriteSheet == null)
			return;

		draw(pSpriteSheet, pSprite, pSprite, pZ, pR, pG, pB, pA);

	}

	public void draw(SpriteSheetDef pSpriteSheet, SpriteFrame pSpriteFrame, Rectangle pDstRectangle, float pZ, float pR, float pG, float pB, float pA) {
		draw(pSpriteSheet, pSpriteFrame, pDstRectangle.x, pDstRectangle.y, pDstRectangle.w, pDstRectangle.h, pZ, pR, pG, pB, pA);

	}

	public void draw(SpriteSheetDef pSpriteSheet, SpriteFrame pSpriteFrame, float pDX, float pDY, float pDW, float pDH, float pZ, float pR, float pG, float pB, float pA) {
		if (pSpriteSheet == null)
			return;

		if (!mIsDrawing)
			return;

		if (pSpriteFrame == null) {
			return;
		}

		Texture lTexture = pSpriteSheet.texture();

		if (lTexture == null)
			return;

		draw(lTexture, pSpriteFrame.x, pSpriteFrame.y, pSpriteFrame.w, pSpriteFrame.h, pDX, pDY, pDW, pDH, pZ, pR, pG, pB, pA);

	}

	public void draw(SpriteSheetDef pSpriteSheet, SpriteInstance pSprite, Rectangle pDstRectangle, float pZ, float pR, float pG, float pB, float pA) {
		if (pSpriteSheet == null)
			return;

		if (pSprite == null) {
			return;
		}

		if (!mIsDrawing)
			return;

		Texture lTexture = pSpriteSheet.texture();
		SpriteFrame lCurrentFrame = pSprite.getFrame();

		draw(lTexture, lCurrentFrame, pDstRectangle, pZ, pR, pG, pB, pA);

	}

}