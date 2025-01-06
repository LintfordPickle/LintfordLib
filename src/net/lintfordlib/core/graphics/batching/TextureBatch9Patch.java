package net.lintfordlib.core.graphics.batching;

import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;

//@formatter:off
public class TextureBatch9Patch {
	
	// --------------------------------------
	// Constructor (static)
	// --------------------------------------
	
	private TextureBatch9Patch() {
	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------
	
	public static void drawBackground(SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, float tileSize, float x, float y, float w, float h, boolean withTitlebar, float componentDepth) {
		if (withTitlebar) {
			if (h < 64) {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT,     x,                  y,                tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID,      x + tileSize,       y,                w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT,    x + w - tileSize,   y,                tileSize,         tileSize,         componentDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT,  x,                  y + h - tileSize, tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID,   x + tileSize,       y + h - tileSize, w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize, tileSize,         tileSize,         componentDepth);
				
			} else {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT,     x,                  y,                tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID,      x + tileSize,       y,                w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT,    x + w - tileSize,   y,                tileSize,         tileSize,         componentDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT,     x,                  y + tileSize,     tileSize,         h - tileSize * 2, componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER,   x + tileSize,       y + tileSize,     w - tileSize * 2, h - 64,           componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT,    x + w - tileSize,   y + tileSize,     tileSize,         h - tileSize * 2, componentDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT,  x,                  y + h - tileSize, tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID,   x + tileSize,       y + h - tileSize, w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize, tileSize,         tileSize,         componentDepth);
				
			}
		} else {
			if (h < 64) {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT,     x,                  y,                   tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID,      x + tileSize,       y,                   w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT,    x + w - tileSize,   y,                   tileSize,         tileSize,         componentDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT,  x,                  y + h - tileSize,    tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID,   x + tileSize,       y + h - tileSize,    w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize,    tileSize,         tileSize,         componentDepth);
				
			} else {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT,     x,                  y,                   tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID,      x + tileSize,       y,                   w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT,    x + w - tileSize,   y,                   tileSize,         tileSize,         componentDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT,     x,                  y + tileSize,        tileSize,         h - tileSize * 2, componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER,   x + tileSize,       y + tileSize,        w - tileSize * 2, h - 64,           componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT,    x + w - tileSize,   y + tileSize,        tileSize,         h - tileSize * 2, componentDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT,  x,                  y + h - tileSize,    tileSize,         tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID,   x + tileSize,       y + h - tileSize,    w - tileSize * 2, tileSize,         componentDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize,    tileSize,         tileSize,         componentDepth);
			}
		}
	}
}
