package net.lintford.library.core.graphics.textures.texturebatch;

import net.lintford.library.core.graphics.textures.Texture;

public class TextureBatch9Patch {

	public static void draw9Patch(TextureBatch pTextureBatch, Texture pTexture, float pTileSize, float x, float y, float w, float h, float z, float a) {
		pTextureBatch.draw(pTexture, 448, 64, pTileSize, pTileSize, x, y, pTileSize, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(pTexture, 480, 64, pTileSize, pTileSize, x + pTileSize, y, w - 64, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(pTexture, 512, 64, pTileSize, pTileSize, x + w - 32, y, pTileSize, pTileSize, z, 1, 1, 1, a);

		pTextureBatch.draw(pTexture, 448, 96, pTileSize, pTileSize, x, y + 32, pTileSize, h - 64, z, 1, 1, 1, a);
		pTextureBatch.draw(pTexture, 480, 96, pTileSize, pTileSize, x + pTileSize, y + 32, w - 64, h - 64, z, 1, 1, 1, a);
		pTextureBatch.draw(pTexture, 512, 96, pTileSize, pTileSize, x + w - 32, y + 32, pTileSize, h - 64, z, 1, 1, 1, a);

		pTextureBatch.draw(pTexture, 448, 128, pTileSize, pTileSize, x, y + h - 32, pTileSize, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(pTexture, 480, 128, pTileSize, pTileSize, x + pTileSize, y + h - 32, w - 64, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(pTexture, 512, 128, pTileSize, pTileSize, x + w - 32, y + h - 32, pTileSize, pTileSize, z, 1, 1, 1, a);

	}

}
