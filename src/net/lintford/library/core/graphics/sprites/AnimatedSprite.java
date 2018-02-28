package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphAnchorDef;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheet;

// TODO: Need to make an AnimatedSpriteState which should be sent when updating a specfic instance of AnimatedSprite.
public class AnimatedSprite implements ISprite, Serializable {

	private static final long serialVersionUID = -2995518836520839609L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** The duration of each frame, in milliseconds */
	private float frameDuration;

	/** If true, the animation loops back to the beginning when finished. */
	private boolean loopAnimation;

	/** If true, animations are played, otherwise, animation is stopped. */
	private boolean animationEnabled;

	/** A list of names of Sprites which make up this animation. */
	private String[] animationSprites;

	/** A collection of sprites which make up this animation. */
	private transient List<ISprite> spriteFrames;

	/** The current frame of the animation */
	private transient int currentFrame;

	/** A timer to track when to change frames */
	private transient float timer;

	/** A listener to allow subscribers to be notified of changes to the state of an {@link AnimatedSprite} */
	private transient AnimatedSpriteListener animatedSpriteListener;

	/** true if this AnimatedSprite has been loaded, false otherwise. */
	private boolean isLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if this AnimatedSprite has beene loaded, false otherwise. */
	public boolean isLoaded() {
		return this.isLoaded;
	}

	public List<ISprite> frames() {
		return spriteFrames;
	}

	/** Returns the number of frames in this animation. */
	public int frameCount() {
		return spriteFrames.size();
	}

	public float frameLength() {
		return frameDuration;
	}

	public void frameLength(float pFrameLength) {
		frameDuration = pFrameLength;
	}

	public boolean enabled() {
		return animationEnabled;
	}

	public void enabled(boolean pEnabled) {
		animationEnabled = pEnabled;

		if (animationEnabled) {
			if (animatedSpriteListener != null) {
				// mAnimatedSpriteListener.onStarted(this);
			}
		}
	}

	public boolean loopEnabled() {
		return loopAnimation;
	}

	public void loopEnabled(boolean pLoopEnabled) {
		loopAnimation = pLoopEnabled;
	}

	public AnimatedSpriteListener animatedSpriteListender() {
		return animatedSpriteListener;
	}

	public void animatedSpriteListender(AnimatedSpriteListener pNewListener) {
		animatedSpriteListener = pNewListener;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AnimatedSprite() {
		spriteFrames = new ArrayList<>();
		frameDuration = 100.0f;
		loopAnimation = true;
		animationEnabled = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadContent(final SpriteSheet pSpriteSheet) {
		final int SPRITE_COUNT = animationSprites.length;
		for (int i = 0; i < SPRITE_COUNT; i++) {
			final ISprite SPRITE = pSpriteSheet.getSprite(animationSprites[i]);

			if (SPRITE == null) {
				System.err.println("AnimatedSprite missing Sprite in spritesheet: " + animationSprites[i]);
				continue;
			}

			spriteFrames.add(SPRITE);

		}

		isLoaded = true;

	}

	public void update(LintfordCore pCore, float pTimeModifier) {
		if (frameDuration == 0.0)
			return;

		final float lDeltaTime = (float) pCore.time().elapseGameTimeMilli();

		if (animationEnabled) {
			timer += lDeltaTime * pTimeModifier;
		}

		// update the current frame
		while (timer > frameDuration) {
			currentFrame++;
			timer -= frameDuration;

			if (currentFrame >= spriteFrames.size()) {

				if (loopAnimation) {
					currentFrame = 0;
					if (animatedSpriteListener != null) {
						animatedSpriteListener.onLooped(this);
					}
				} else {
					animationEnabled = false;
					currentFrame--;
					if (animatedSpriteListener != null) {
						animatedSpriteListener.onStopped(this);
					}
				}

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Adds the given {@link Sprite} instance to this animation collection, at the end of the current animation. */
	public void addFrame(final Sprite pSprite) {
		if (!spriteFrames.contains(pSprite)) {
			spriteFrames.add(pSprite);
		}
	}

	public void removeFrame(Sprite pSprite) {
		if (spriteFrames.contains(pSprite)) {
			spriteFrames.remove(pSprite);
		}
	}

	/** returns the frame of an animation from the internal timer (updated via sprite.update()) */
	public ISprite getSprite() {
		if (spriteFrames == null || spriteFrames.size() == 0)
			return null;
		return spriteFrames.get(currentFrame);
	}

	/** returns the frame of an animation from an external timer */
	public ISprite getAnimationFromTime(float pTime) {
		int frameNumber = (int) ((pTime / frameDuration));

		return spriteFrames.get(frameNumber % spriteFrames.size());

	}

	/** Sets the animation to the specified frame number. If the given frame number is OoB of the frame set, then the value is clamped. */
	public void setFrame(int pFrameNumber) {
		currentFrame = pFrameNumber;
		timer -= frameDuration;

		if (currentFrame < 0)
			currentFrame = 0;

		if (currentFrame >= spriteFrames.size()) {
			currentFrame = spriteFrames.size() - 1;

		}

	}

	public void nextFrame() {
		currentFrame++;
		timer -= frameDuration;

		if (currentFrame >= spriteFrames.size()) {
			currentFrame = 0;

		}

	}

	public void playFromBeginning() {
		setFrame(0);
		enabled(true);
		
	}
	
	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public float getSrcX() {
		return spriteFrames.get(currentFrame).getSrcX();
	}

	@Override
	public float getSrcY() {
		return spriteFrames.get(currentFrame).getSrcY();
	}

	@Override
	public float getSrcWidth() {
		return spriteFrames.get(currentFrame).getSrcWidth();
	}

	@Override
	public float getSrcHeight() {
		return spriteFrames.get(currentFrame).getSrcHeight();
	}

	@Override
	public float getRotation() {
		return spriteFrames.get(currentFrame).getRotation();
	}

	@Override
	public SpriteGraphAnchorDef getAnchorPoint(String pName) {
		ISprite lSprite = spriteFrames.get(currentFrame);
		if (lSprite == null)
			return SpriteGraphAnchorDef.ZERO_ANCHOR;

		return lSprite.getAnchorPoint(pName);

	}

	@Override
	public float getPivotPointX() {
		return spriteFrames.get(currentFrame).getPivotPointX();
	}

	@Override
	public float getPivotPointY() {
		return spriteFrames.get(currentFrame).getPivotPointY();
	}

}
