package net.lintfordlib.core.graphics.sprites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

/** {@link SpriteDefinition}s are a collection of *one* or more {@link SpriteFrame}s. */
public class SpriteDefinition implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2995518836520839609L;
	private static final int INVALID_FRAME_REFERENCE = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/* The name of the SpriteDefinition (used for animation listeners) */
	@SerializedName(value = "name")
	private String mName;

	/** The duration of each frame, in milliseconds */
	@SerializedName(value = "frameDuration")
	private float mFrameDuration;

	/** If true, the animation loops back to the beginning when finished. */
	@SerializedName(value = "loopAnimation")
	private boolean mLoopAnimation;

	/** Specifies a minimum amount of time (in Ms) the animation should have to play out in order to be meaningful */
	@SerializedName(value = "minimumViableRuntime")
	private int mMinimumViableRuntime;

	/** A list of indices of the sprites which make up this animation. */
	@SerializedName(value = "animationSpriteIndices")
	private int[] mAnimationSpriteIndices;

	/** A collection of sprites which make up this animation. */
	@SerializedName(value = "spriteFrames")
	private transient List<SpriteFrame> mSpriteFrames;

	/** true if this AnimatedSprite has been loaded, false otherwise. */
	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mName;
	}

	public void name(String newName) {
		mName = newName;
	}

	/** Returns true if this AnimatedSprite has beene loaded, false otherwise. */
	public boolean isLoaded() {
		return this.mIsLoaded;
	}

	public List<SpriteFrame> frames() {
		return mSpriteFrames;
	}

	/** Returns the number of frames in this animation. */
	public int frameCount() {
		return mSpriteFrames.size();
	}

	public float frameDuration() {
		return mFrameDuration;
	}

	public void frameDuration(float frameLength) {
		mFrameDuration = frameLength;
	}

	public boolean loopEnabled() {
		return mLoopAnimation;
	}

	public void loopEnabled(boolean loopEnabled) {
		mLoopAnimation = loopEnabled;
	}

	public int minimumViableRuntime() {
		return mMinimumViableRuntime;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteDefinition() {
		mSpriteFrames = new ArrayList<>();
		mFrameDuration = 100.0f;
		mLoopAnimation = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadContent(final SpriteSheetDefinition spriteSheetDefinition) {
		if (mAnimationSpriteIndices == null)
			return;

		final int lNumSprites = mAnimationSpriteIndices.length;
		for (int i = 0; i < lNumSprites; i++) {
			if (i == INVALID_FRAME_REFERENCE) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Error resolving animation frame in " + mName);
				continue;
			}

			final var lSpriteFrame = spriteSheetDefinition.getSpriteFrame(mAnimationSpriteIndices[i]);

			if (lSpriteFrame == null) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("[%s] SpriteFrame in mAnimationSpriteIndices is missing in spritesheet: '%d'", mName, mAnimationSpriteIndices[i]));
				continue;
			}

			mSpriteFrames.add(lSpriteFrame);
		}

		mIsLoaded = true;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Adds the given {@link SpriteFrame} instance to this animation collection, at the end of the current animation. */
	public void addFrame(final SpriteFrame spriteFrame) {
		if (!mSpriteFrames.contains(spriteFrame)) {
			mSpriteFrames.add(spriteFrame);
		}
	}

	public void removeFrame(SpriteFrame spriteFrame) {
		if (mSpriteFrames.contains(spriteFrame)) {
			mSpriteFrames.remove(spriteFrame);
		}
	}

	/** returns the frame of an animation from the internal timer. */
	public SpriteFrame getSpriteFrame(int frameIndex) {
		if (mSpriteFrames == null || mSpriteFrames.isEmpty())
			return null;

		final int lFrameCount = this.mSpriteFrames.size();

		if (frameIndex < 0 || frameIndex >= lFrameCount)
			return null;

		return mSpriteFrames.get(frameIndex);
	}

	/** returns the frame of an animation from an external timer */
	public SpriteFrame getAnimationFromTime(float time) {
		final int frameNumber = (int) ((time / mFrameDuration));

		return mSpriteFrames.get(frameNumber % mSpriteFrames.size());
	}
}
