package net.ld.library.core.graphics.spritebatch;

import net.ld.library.core.graphics.sprites.SpriteSheet;

public class SpriteBatch9Patch extends SpriteBatch {

	// =============================================
	// Constructor
	// =============================================

	public SpriteBatch9Patch() {
		super();
	}

	// =============================================
	// Methods
	// =============================================

	public void draw9Patch(float pPX, float pPY, float pZ, float pWidth, float pHeight, float pScale, SpriteSheet pSheet, String pSpriteName) {

		// draw the 9 parts of the 9patch
		final float scale = pScale;
		final float cornerSize = 20 * scale;
		draw(pSheet.getSprite(pSpriteName + "0"), pPX, pPY, pZ, cornerSize, cornerSize, pSheet.texture());
		draw(pSheet.getSprite(pSpriteName + "1"), pPX + cornerSize, pPY, pZ, pWidth - cornerSize * 2, cornerSize, pSheet.texture());
		draw(pSheet.getSprite(pSpriteName + "2"), pPX + pWidth - cornerSize, pPY, pZ, cornerSize, cornerSize, pSheet.texture());

		draw(pSheet.getSprite(pSpriteName + "3"), pPX, pPY + cornerSize, pZ, cornerSize, pHeight - cornerSize * 2, pSheet.texture());
		draw(pSheet.getSprite(pSpriteName + "4"), pPX + cornerSize, pPY + cornerSize, pZ, pWidth - cornerSize * 2, pHeight - cornerSize * 2, pSheet.texture());
		draw(pSheet.getSprite(pSpriteName + "5"), pPX + pWidth - cornerSize, pPY + cornerSize, pZ, cornerSize, pHeight - cornerSize * 2, pSheet.texture());

		draw(pSheet.getSprite(pSpriteName + "6"), pPX, pPY + pHeight - cornerSize, pZ, cornerSize, cornerSize, pSheet.texture());
		draw(pSheet.getSprite(pSpriteName + "7"), pPX + cornerSize, pPY + pHeight - cornerSize, pZ, pWidth - cornerSize * 2, cornerSize, pSheet.texture());
		draw(pSheet.getSprite(pSpriteName + "8"), pPX + pWidth - cornerSize, pPY + pHeight - cornerSize, pZ, cornerSize, cornerSize, pSheet.texture());

	}
}
