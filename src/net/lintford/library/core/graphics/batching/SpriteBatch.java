package net.lintford.library.core.graphics.batching;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class SpriteBatch extends TextureBatchPCT {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteBatch() {
		super();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		draw(spriteSheetDefinition, spriteInstance, spriteInstance, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, int spriteFrameIndex, Rectangle destRectangle, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		draw(spriteSheetDefinition, spriteSheetDefinition.getSpriteFrame(spriteFrameIndex), destRectangle, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, Rectangle pDstRectangle, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (pDstRectangle == null)
			return;

		draw(spriteSheetDefinition, spriteFrame, pDstRectangle.x(), pDstRectangle.y(), pDstRectangle.width(), pDstRectangle.height(), zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, int spriteFrameIndex, float destX, float destY, float destWidth, float destHeight, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		draw(spriteSheetDefinition, spriteSheetDefinition.getSpriteFrame(spriteFrameIndex), destX, destY, destWidth, destHeight, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, float destX, float destY, float destWidth, float destHeight, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		if (spriteFrame == null)
			return;

		final var lTexture = spriteSheetDefinition.texture();

		if (lTexture == null)
			return;

		draw(lTexture, spriteFrame.x(), spriteFrame.y(), spriteFrame.width(), spriteFrame.height(), destX, destY, destWidth, destHeight, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, Rectangle destRectangle, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		if (spriteInstance == null)
			return;

		final var lTexture = spriteSheetDefinition.texture();
		final var lCurrentSpriteFrame = spriteInstance.currentSpriteFrame();

		draw(lTexture, lCurrentSpriteFrame, destRectangle, zDepth, colorTint);
	}

	public void drawAroundCenter(SpriteSheetDefinition spriteSheetDefinition, int spriteFrameIndex, float destX, float destY, float destWidth, float destHeight, float rot, float pivotX, float pivotY, float zDepth, Color colorTint) {
		drawAroundCenter(spriteSheetDefinition, spriteSheetDefinition.getSpriteFrame(spriteFrameIndex), destX, destY, destWidth, destHeight, rot, pivotX, pivotY, zDepth, colorTint);
	}

	public void drawAroundCenter(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, float destX, float destY, float destWidth, float destHeight, float rot, float pivotX, float pivotY, float zDepth, Color colorTint) {
		drawAroundCenter(spriteSheetDefinition.texture(), spriteFrame.x(), spriteFrame.y(), spriteFrame.width(), spriteFrame.height(), destX, destY, destWidth, destHeight, -0.01f, rot, pivotX, pivotY, 1.f, colorTint);
	}
}