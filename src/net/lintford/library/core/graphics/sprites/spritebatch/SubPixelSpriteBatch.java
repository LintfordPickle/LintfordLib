package net.lintford.library.core.graphics.sprites.spritebatch;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.texturebatch.SubPixelTextureBatch;

// TODO: Add batching based on texture calls
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

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, float zDepth, Color colorTint) {
		if (spriteSheetDefinition == null)
			return;

		draw(spriteSheetDefinition, spriteInstance, spriteInstance, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, Rectangle destRectangle, float zDepth, Color colorTint) {
		if (destRectangle == null)
			return;

		draw(spriteSheetDefinition, spriteFrame, destRectangle.x(), destRectangle.y(), destRectangle.width(), destRectangle.height(), zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, float destX, float destY, float destWidth, float destHeight, float zDepth, Color colorTint) {
		if (spriteSheetDefinition == null)
			return;

		if (!mIsDrawing)
			return;

		if (spriteFrame == null)
			return;

		final var lTexture = spriteSheetDefinition.texture();

		if (lTexture == null)
			return;

		draw(lTexture, spriteFrame.x(), spriteFrame.y(), spriteFrame.width(), spriteFrame.height(), destX, destY, destWidth, destHeight, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, Rectangle destRectangle, float zDepth, Color colorTint) {
		if (spriteSheetDefinition == null)
			return;

		if (spriteInstance == null)
			return;

		if (!mIsDrawing)
			return;

		final var lTexture = spriteSheetDefinition.texture();
		final var lCurrentSpriteFrame = spriteInstance.currentSpriteFrame();

		draw(lTexture, lCurrentSpriteFrame, destRectangle, zDepth, colorTint);
	}
}