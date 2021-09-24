package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

/** {@link SpriteDefinition}s are a collection of *one* or more {@link SpriteFrame}s. */
public class SpriteDefinition implements Serializable {

	private static final long serialVersionUID = -2995518836520839609L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/* The name of the SpriteDefinition (used for animation listeners) */
	public String name;

	/** The duration of each frame, in milliseconds */
	private float frameDuration;

	/** If true, the animation loops back to the beginning when finished. */
	private boolean loopAnimation;

	/** A list of indices of the sprites which make up this animation. */
	private int[] animationSpriteIndices;

	/** A collection of sprites which make up this animation. */
	private transient List<SpriteFrame> spriteFrames;

	/** true if this AnimatedSprite has been loaded, false otherwise. */
	private boolean isLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if this AnimatedSprite has beene loaded, false otherwise. */
	public boolean isLoaded() {
		return this.isLoaded;
	}

	public List<SpriteFrame> frames() {
		return spriteFrames;
	}

	/** Returns the number of frames in this animation. */
	public int frameCount() {
		return spriteFrames.size();
	}

	public float frameDuration() {
		return frameDuration;
	}

	public void frameDuration(float pFrameLength) {
		frameDuration = pFrameLength;
	}

	public boolean loopEnabled() {
		return loopAnimation;
	}

	public void loopEnabled(boolean pLoopEnabled) {
		loopAnimation = pLoopEnabled;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteDefinition() {
		spriteFrames = new ArrayList<>();
		frameDuration = 100.0f;
		loopAnimation = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadContent(final SpriteSheetDefinition pSpriteSheet) {
		if (animationSpriteIndices == null)
			return;

		final int lNumSprites = animationSpriteIndices.length;
		for (int i = 0; i < lNumSprites; i++) {
			final SpriteFrame lSpriteFrame = pSpriteSheet.getSpriteFrame(animationSpriteIndices[i]);

			if (lSpriteFrame == null) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("SpriteFrame missing in spritesheet: '%s'", animationSpriteIndices[i]));
				continue;
			}

			spriteFrames.add(lSpriteFrame);
		}

		isLoaded = true;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Adds the given {@link SpriteFrame} instance to this animation collection, at the end of the current animation. */
	public void addFrame(final SpriteFrame pSprite) {
		if (!spriteFrames.contains(pSprite)) {
			spriteFrames.add(pSprite);
		}
	}

	public void removeFrame(SpriteFrame pSprite) {
		if (spriteFrames.contains(pSprite)) {
			spriteFrames.remove(pSprite);
		}
	}

	/** returns the frame of an animation from the internal timer. */
	public SpriteFrame getSpriteFrame(int pFrameIndex) {
		if (spriteFrames == null || spriteFrames.size() == 0)
			return null;

		final int lFrameCount = this.spriteFrames.size();

		if (pFrameIndex < 0 || pFrameIndex >= lFrameCount)
			return null;

		return spriteFrames.get(pFrameIndex);

	}

	/** returns the frame of an animation from an external timer */
	public SpriteFrame getAnimationFromTime(float pTime) {
		int frameNumber = (int) ((pTime / frameDuration));

		return spriteFrames.get(frameNumber % spriteFrames.size());

	}

}
