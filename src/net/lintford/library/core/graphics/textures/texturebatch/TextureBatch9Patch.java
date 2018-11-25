package net.lintford.library.core.graphics.textures.texturebatch;

import net.lintford.library.core.graphics.textures.TextureManager;

public class TextureBatch9Patch {

	public static void draw9Patch(TextureBatch pTextureBatch, float pTileSize, float x, float y, float w, float h, float z, float a) {
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 64, pTileSize, pTileSize, x, y, pTileSize, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 64, pTileSize, pTileSize, x + pTileSize, y, w - 64, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 64, pTileSize, pTileSize, x + w - 32, y, pTileSize, pTileSize, z, 1, 1, 1, a);

		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 96, pTileSize, pTileSize, x, y + 32, pTileSize, h - 64, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 96, pTileSize, pTileSize, x + pTileSize, y + 32, w - 64, h - 64, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 96, pTileSize, pTileSize, x + w - 32, y + 32, pTileSize, h - 64, z, 1, 1, 1, a);

		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 128, pTileSize, pTileSize, x, y + h - 32, pTileSize, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 128, pTileSize, pTileSize, x + pTileSize, y + h - 32, w - 64, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 128, pTileSize, pTileSize, x + w - 32, y + h - 32, pTileSize, pTileSize, z, 1, 1, 1, a);
		
	}

	/*
	 
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 64, pTileSize, pTileSize, x, y, pTileSize, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 64, pTileSize, pTileSize, x + pTileSize, y, w - 64, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 64, pTileSize, pTileSize, x + w - 32, y, pTileSize, pTileSize, z, 1, 1, 1, a);
		
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 96, pTileSize, pTileSize, x, y + 32, pTileSize, h - 64, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 96, pTileSize, pTileSize, x + pTileSize, y + 32, w - 64, h - 64, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 96, pTileSize, pTileSize, x + w - 32, y + 32, pTileSize, h - 64, z, 1, 1, 1, a);
		
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 128, pTileSize, pTileSize, x, y + h - 32, pTileSize, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 128, pTileSize, pTileSize, x + pTileSize, y + h - 32, w - 64, pTileSize, z, 1, 1, 1, a);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 128, pTileSize, pTileSize, x + w - 32, y + h - 32, pTileSize, pTileSize, z, 1, 1, 1, a);
	  
	 */
	
}
