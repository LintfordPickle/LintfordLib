package net.lintford.library.core.graphics.textures.texturebatch;

import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;

public class TextureBatch9Patch {

	public static void draw9Patch(SpriteBatch pSpritesheet, SpriteSheetDefinition pCoreSpritesheet, float pTileSize, float x, float y, float w, float h, float z, Color pTint) {
		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x, y, pTileSize, pTileSize, z, pTint);
		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, x + pTileSize, y, w - 64, pTileSize, z, pTint);
		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, x + w - 32, y, pTileSize, pTileSize, z, pTint);

		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT, x, y + 32, pTileSize, h - 64, z, pTint);
		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, x + pTileSize, y + 32, w - 64, h - 64, z, pTint);
		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT, x + w - 32, y + 32, pTileSize, h - 64, z, pTint);

		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, x, y + h - 32, pTileSize, pTileSize, z, pTint);
		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, x + pTileSize, y + h - 32, w - 64, pTileSize, z, pTint);
		pSpritesheet.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - 32, y + h - 32, pTileSize, pTileSize, z, pTint);
	}
}
