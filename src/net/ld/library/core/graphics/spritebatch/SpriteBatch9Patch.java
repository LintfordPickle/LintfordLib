package net.ld.library.core.graphics.spritebatch;

import net.ld.library.core.graphics.textures.Texture;

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

	// TODO: Need to fix the sx, sy, sw and sh positions (after removing sprites)
	public void draw9Patch(float pPX, float pPY, float pZ, float pWidth, float pHeight, float pScale, Texture pTexture) {

		// draw the 9 parts of the 9patch
		final float scale = pScale;
		final float cornerSize = 20 * scale;
		draw(0, 0, 16, 16, pPX, pPY, pZ, cornerSize, cornerSize, 1f, pTexture);
		draw(16, 0, 16, 16, pPX + cornerSize, pPY, pZ, pWidth - cornerSize * 2, cornerSize, 1f, pTexture);
		draw(32, 0, 16, 16, pPX + pWidth - cornerSize, pPY, pZ, cornerSize, cornerSize, 1f, pTexture);

		draw(0, 16, 16, 16, pPX, pPY + cornerSize, pZ, cornerSize, pHeight - cornerSize * 2, 1f, pTexture);
		draw(16, 16, 16, 16, pPX + cornerSize, pPY + cornerSize, pZ, pWidth - cornerSize * 2, pHeight - cornerSize * 2, 1f, pTexture);
		draw(32, 16, 16, 16, pPX + pWidth - cornerSize, pPY + cornerSize, pZ, cornerSize, pHeight - cornerSize * 2, 1f, pTexture);

		draw(0, 32, 16, 16, pPX, pPY + pHeight - cornerSize, pZ, cornerSize, cornerSize, 1f, pTexture);
		draw(16, 32, 16, 16, pPX + cornerSize, pPY + pHeight - cornerSize, pZ, pWidth - cornerSize * 2, cornerSize, 1f, pTexture);
		draw(32, 32, 16, 16, pPX + pWidth - cornerSize, pPY + pHeight - cornerSize, pZ, cornerSize, cornerSize, 1f, pTexture);

	}
}
