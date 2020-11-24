package net.lintford.library.core.graphics.sprites.spritebatch;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.texturebatch.SubPixelTextureBatch;

// TODO: ---> Add batching based on SpriteSheetDef (or rather, the Texture).
public class SubPixelSpriteBatch extends SubPixelTextureBatch {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SubPixelSpriteBatch() {
		super();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(SpriteSheetDefinition pSpriteSheetDefinition, SpriteInstance pSprite, float pZ, Color pTint) {
		if (pSpriteSheetDefinition == null)
			return;

		draw(pSpriteSheetDefinition, pSprite, pSprite, pZ, pTint);

	}

	public void draw(SpriteSheetDefinition pSpriteSheetDefinition, SpriteFrame pSpriteFrame, Rectangle pDstRectangle, float pZ, Color pTint) {
		if (pDstRectangle == null) {
			return;

		}

		draw(pSpriteSheetDefinition, pSpriteFrame, pDstRectangle.x(), pDstRectangle.y(), pDstRectangle.w(), pDstRectangle.h(), pZ, pTint);

	}

	public void draw(SpriteSheetDefinition pSpriteSheetDefinition, SpriteFrame pSpriteFrame, float pDX, float pDY, float pDW, float pDH, float pZ, Color pTint) {
		if (pSpriteSheetDefinition == null)
			return;

		if (!mIsDrawing)
			return;

		if (pSpriteFrame == null) {
			return;
		}

		final var lTexture = pSpriteSheetDefinition.texture();

		if (lTexture == null)
			return;

		draw(lTexture, pSpriteFrame.x(), pSpriteFrame.y(), pSpriteFrame.w(), pSpriteFrame.h(), pDX, pDY, pDW, pDH, pZ, pTint);

	}

	public void draw(SpriteSheetDefinition pSpriteSheetDefinition, SpriteInstance pSprite, Rectangle pDstRectangle, float pZ, Color pTint) {
		if (pSpriteSheetDefinition == null)
			return;

		if (pSprite == null) {
			return;
		}

		if (!mIsDrawing)
			return;

		final var lTexture = pSpriteSheetDefinition.texture();
		final var lCurrentSpriteFrame = pSprite.currentSpriteFrame();

		draw(lTexture, lCurrentSpriteFrame, pDstRectangle, pZ, pTint);

	}

}