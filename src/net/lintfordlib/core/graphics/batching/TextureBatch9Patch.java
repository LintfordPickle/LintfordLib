package net.lintfordlib.core.graphics.batching;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;

//@formatter:off
public class TextureBatch9Patch {

	public static void drawBackground(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, float tileSize, int x, int y, int w, int h, Color color, boolean withTitlebar, float componentDepth) {
		if (withTitlebar) {
			if (h < 64) {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT,     x,                  y,                tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID,      x + tileSize,       y,                w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT,    x + w - tileSize,   y,                tileSize,         tileSize,         componentDepth, color);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT,  x,                  y + h - tileSize, tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID,   x + tileSize,       y + h - tileSize, w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize, tileSize,         tileSize,         componentDepth, color);
				
			} else {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT,     x,                  y,                tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID,      x + tileSize,       y,                w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT,    x + w - tileSize,   y,                tileSize,         tileSize,         componentDepth, color);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT,     x,                  y + tileSize,     tileSize,         h - tileSize * 2, componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER,   x + tileSize,       y + tileSize,     w - tileSize * 2, h - 64,           componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT,    x + w - tileSize,   y + tileSize,     tileSize,         h - tileSize * 2, componentDepth, color);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT,  x,                  y + h - tileSize, tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID,   x + tileSize,       y + h - tileSize, w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize, tileSize,         tileSize,         componentDepth, color);
				
			}
		} else {
			if (h < 64) {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT,     x,                  y,                   tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID,      x + tileSize,       y,                   w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT,    x + w - tileSize,   y,                   tileSize,         tileSize,         componentDepth, color);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT,  x,                  y + h - tileSize,    tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID,   x + tileSize,       y + h - tileSize,    w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize,    tileSize,         tileSize,         componentDepth, color);
				
			} else {
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT,     x,                  y,                   tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID,      x + tileSize,       y,                   w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT,    x + w - tileSize,   y,                   tileSize,         tileSize,         componentDepth, color);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT,     x,                  y + tileSize,        tileSize,         h - tileSize * 2, componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER,   x + tileSize,       y + tileSize,        w - tileSize * 2, h - 64,           componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT,    x + w - tileSize,   y + tileSize,        tileSize,         h - tileSize * 2, componentDepth, color);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT,  x,                  y + h - tileSize,    tileSize,         tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID,   x + tileSize,       y + h - tileSize,    w - tileSize * 2, tileSize,         componentDepth, color);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, x + w - tileSize,   y + h - tileSize,    tileSize,         tileSize,         componentDepth, color);
			}
		}
	}
}
