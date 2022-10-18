package net.lintford.library.core.graphics.batching;

import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;

public class TextureBatch9Patch {

	public static void draw9Patch(SpriteBatch spritesheet, SpriteSheetDefinition coreSpritesheet, float tileSize, float x, float y, float w, float h, float z, Color color) {
		tileSize += 1;
		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, (int) x, y, tileSize, tileSize, z, color);
		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, (int) (x + tileSize), y, w - 64, tileSize, z, color);
		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, (int) (x + w - 32), y, tileSize, tileSize, z, color);

		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT, (int) x, y + 32, tileSize, h - 64, z, color);
		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, (int) (x + tileSize), y + 32, w - 64, h - 64, z, color);
		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT, (int) (x + w - 32), y + 32, tileSize, h - 64, z, color);

		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, (int) x, y + h - 32, tileSize, tileSize, z, color);
		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, (int) (x + tileSize), y + h - 32, w - 64, tileSize, z, color);
		spritesheet.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, (int) (x + w - 32), y + h - 32, tileSize, tileSize, z, color);
	}
}
