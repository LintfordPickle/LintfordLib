package net.lintfordlib.core.graphics.batching;

import java.util.List;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.sprites.SpriteFrame;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.maths.Vector2f;

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
		draw(spriteSheetDefinition, spriteInstance, destRectangle, 1.f, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, Rectangle destRectangle, float scale, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		if (spriteInstance == null)
			return;

		final var lTexture = spriteSheetDefinition.texture();
		final var lCurrentSpriteFrame = spriteInstance.currentSpriteFrame();

		// Need to resolve the destination rectangle manually to account for the scaling
		draw(lTexture, lCurrentSpriteFrame.x(), lCurrentSpriteFrame.y(), lCurrentSpriteFrame.width(), lCurrentSpriteFrame.height(), destRectangle.x() * scale, destRectangle.y() * scale, destRectangle.width() * scale, destRectangle.height() * scale, zDepth, colorTint);
	}

	public void draw(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, float destX, float destY, float destW, float destH, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		if (spriteInstance == null)
			return;

		final var lTexture = spriteSheetDefinition.texture();
		final var lCurrentSpriteFrame = spriteInstance.currentSpriteFrame();

		// Need to resolve the destination rectangle manually to account for the scaling
		draw(lTexture, lCurrentSpriteFrame.x(), lCurrentSpriteFrame.y(), lCurrentSpriteFrame.width(), lCurrentSpriteFrame.height(), destX, destY, destW, destH, zDepth, colorTint);
	}

	// ---

	public void drawQuadrilateral(SpriteSheetDefinition spriteSheetDefinition, SpriteInstance spriteInstance, List<Vector2f> dstPoints, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		if (spriteInstance == null)
			return;

		final var lTexture = spriteSheetDefinition.texture();
		final var lCurrentSpriteFrame = spriteInstance.currentSpriteFrame();

		drawQuadrilateral(lTexture, lCurrentSpriteFrame, dstPoints, zDepth, colorTint);
	}

	public void drawQuadrilateral(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, List<Vector2f> dstPoints, float zDepth, Color colorTint) {
		if (!mIsDrawing)
			return;

		if (spriteSheetDefinition == null)
			return;

		if (spriteFrame == null)
			return;

		final var lTexture = spriteSheetDefinition.texture();

		drawQuadrilateral(lTexture, spriteFrame, dstPoints, zDepth, colorTint);
	}

	// ---

	public void drawAroundCenter(SpriteSheetDefinition spriteSheetDefinition, int spriteFrameIndex, float destX, float destY, float destWidth, float destHeight, float rot, float pivotX, float pivotY, float zDepth, Color colorTint) {
		if (spriteSheetDefinition == null)
			return;

		drawAroundCenter(spriteSheetDefinition, spriteSheetDefinition.getSpriteFrame(spriteFrameIndex), destX, destY, destWidth, destHeight, rot, pivotX, pivotY, zDepth, colorTint);
	}

	public void drawAroundCenter(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, Rectangle destRect, float rot, float pivotX, float pivotY, float zDepth, Color colorTint) {
		if (spriteSheetDefinition == null)
			return;

		if (spriteFrame == null)
			return;

		drawAroundCenter(spriteSheetDefinition, spriteFrame, destRect.x(), destRect.y(), destRect.width(), destRect.height(), rot, pivotX, pivotY, zDepth, colorTint);
	}

	public void drawAroundCenter(SpriteSheetDefinition spriteSheetDefinition, SpriteFrame spriteFrame, float destX, float destY, float destWidth, float destHeight, float rot, float pivotX, float pivotY, float zDepth, Color colorTint) {
		if (spriteFrame == null)
			return;

		final float srcX = spriteFrame.flipHorizontal() ? spriteFrame.x() + spriteFrame.width() : spriteFrame.x();
		final float srcY = spriteFrame.flipVertical() ? spriteFrame.y() + spriteFrame.height() : spriteFrame.y();
		final float srcW = spriteFrame.flipHorizontal() ? -spriteFrame.width() : spriteFrame.width();
		final float srcH = spriteFrame.flipVertical() ? -spriteFrame.height() : spriteFrame.height();

		drawAroundCenter(spriteSheetDefinition.texture(), srcX, srcY, srcW, srcH, destX, destY, destWidth, destHeight, zDepth, rot, pivotX, pivotY, 1.f, colorTint);
	}
}