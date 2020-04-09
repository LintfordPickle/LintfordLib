package net.lintford.library.core.graphics.sprites;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;

public class SpriteInstance extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5763179155254740467L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient SpriteDefinition mSpriteDefinition;

	/** A listener to allow subscribers to be notified of changes to the state of an {@link AnimatedSprite} */
	private transient AnimatedSpriteListener animatedSpriteListener;

	/** The current frame of the animation */
	private int currentFrame;

	/** A timer to track when to change frames */
	private float timer;

	/** If true, rotation and position values will be eased in/out within the time given. 0 for no easing. */
	// private float interpolateTime;

	/**  */
	private boolean loopingEnabled;

	/** If true, animations are played, otherwise, animation is stopped. */
	private boolean animationEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isFree() {
		return mSpriteDefinition == null;
	}

	public boolean enabled() {
		return animationEnabled;
	}

	public void enabled(boolean pEnabled) {
		animationEnabled = pEnabled;

		if (animationEnabled) {
			if (animatedSpriteListener != null) {
				animatedSpriteListener.onStarted(this);

			}

		}

	}

	public boolean isLoopingEnabled() {
		return loopingEnabled;
	}

	public void isLoopingEnabled(boolean pNewValue) {
		loopingEnabled = pNewValue;
	}

	public AnimatedSpriteListener animatedSpriteListender() {
		return animatedSpriteListener;
	}

	public void animatedSpriteListender(AnimatedSpriteListener pNewListener) {
		animatedSpriteListener = pNewListener;
	}

	/** returns the frame of an animation from the internal timer (updated via sprite.update()) */
	public SpriteFrame currentSpriteFrame() {
		if (mSpriteDefinition == null)
			return null;

		return mSpriteDefinition.getSpriteFrame(currentFrame);

	}

	public SpriteDefinition spriteDefinition() {
		return mSpriteDefinition;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteInstance() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void kill() {
		mSpriteDefinition = null;

	}

	public void init(SpriteDefinition pSpriteDef) {
		mSpriteDefinition = pSpriteDef;
		loopingEnabled = pSpriteDef.loopEnabled();
		animationEnabled = true;

		mAreVerticesDirty = true;

		updateDimensionsOnCurrentFrame();

	}

	public void update(LintfordCore pCore) {
		update(pCore, false);

	}

	public void update(LintfordCore pCore, boolean pReverse) {
		final float lDeltaTime = (float) pCore.time().elapseGameTimeMilli();

		if (mSpriteDefinition.frameDuration() == 0.0)
			return;

		if (animationEnabled) {
			timer += lDeltaTime;
		}

		// update the current frame
		while (timer > mSpriteDefinition.frameDuration()) {
			// Handle this time splice
			timer -= mSpriteDefinition.frameDuration();

			if (!pReverse) {
				currentFrame++;

				if (currentFrame >= mSpriteDefinition.frameCount()) {

					if (loopingEnabled) {
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

			} else {
				currentFrame--;

				if (currentFrame < 0) {

					if (loopingEnabled) {
						currentFrame = mSpriteDefinition.frameCount() - 1;
						if (animatedSpriteListener != null) {
							animatedSpriteListener.onLooped(this);
						}
					} else {
						animationEnabled = false;
						currentFrame = 0;
						if (animatedSpriteListener != null) {
							animatedSpriteListener.onStopped(this);
						}
					}

				}

			}

		}

		updateDimensionsOnCurrentFrame();

	}

	private void updateDimensionsOnCurrentFrame() {
		var lCurrentFrame = currentSpriteFrame();
		if (lCurrentFrame != null) {
			scaleX(lCurrentFrame.scaleX()); //
			scaleY(lCurrentFrame.scaleY()); //

			width(lCurrentFrame.width() * scaleX());
			height(lCurrentFrame.height() * scaleY());

			pivotX(lCurrentFrame.pivotX() * scaleX());
			pivotY(lCurrentFrame.pivotY() * scaleY());

		}
	}

	// --------------------------------------
	// Inherited Methods
	// --------------------------------------

	/** Sets the animation to the specified frame number. If the given frame number is OoB of the frame set, then the value is clamped. */
	public void setFrame(int pFrameNumber) {
		currentFrame = pFrameNumber;
		timer -= mSpriteDefinition.frameDuration();

		if (currentFrame < 0)
			currentFrame = 0;

		if (currentFrame >= mSpriteDefinition.frameCount()) {
			currentFrame = mSpriteDefinition.frameCount() - 1;

		}

	}

	public void prevFrame() {
		currentFrame--;
		timer -= mSpriteDefinition.frameDuration();

		if (currentFrame < 0) {
			currentFrame = mSpriteDefinition.frameCount() - 1;

		}

	}

	public void nextFrame() {
		currentFrame++;
		timer -= mSpriteDefinition.frameDuration();

		if (currentFrame >= mSpriteDefinition.frameCount()) {
			currentFrame = 0;

		}

	}

	public void playFromBeginning() {
		setFrame(0);
		enabled(true);

	}

	public void reset() {
		loopingEnabled = false;
		animationEnabled = false;

	}

}