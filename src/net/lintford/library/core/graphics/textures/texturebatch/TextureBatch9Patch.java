package net.lintford.library.core.graphics.textures.texturebatch;

import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.textures.Texture;

public class TextureBatch9Patch {

	public static void draw9Patch(TextureBatchPCT pTextureBatch, Texture pTexture, float pTileSize, float x, float y, float w, float h, float z, Color pTint) {
		pTextureBatch.draw(pTexture, 256, 0, pTileSize, pTileSize, x, y, pTileSize, pTileSize, z, pTint);
		pTextureBatch.draw(pTexture, 288, 0, pTileSize, pTileSize, x + pTileSize, y, w - 64, pTileSize, z, pTint);
		pTextureBatch.draw(pTexture, 320, 0, pTileSize, pTileSize, x + w - 32, y, pTileSize, pTileSize, z, pTint);

		pTextureBatch.draw(pTexture, 256, 32, pTileSize, pTileSize, x, y + 32, pTileSize, h - 64, z, pTint);
		pTextureBatch.draw(pTexture, 288, 32, pTileSize, pTileSize, x + pTileSize, y + 32, w - 64, h - 64, z, pTint);
		pTextureBatch.draw(pTexture, 320, 32, pTileSize, pTileSize, x + w - 32, y + 32, pTileSize, h - 64, z, pTint);

		pTextureBatch.draw(pTexture, 256, 64, pTileSize, pTileSize, x, y + h - 32, pTileSize, pTileSize, z, pTint);
		pTextureBatch.draw(pTexture, 288, 64, pTileSize, pTileSize, x + pTileSize, y + h - 32, w - 64, pTileSize, z, pTint);
		pTextureBatch.draw(pTexture, 320, 64, pTileSize, pTileSize, x + w - 32, y + h - 32, pTileSize, pTileSize, z, pTint);

	}

}
