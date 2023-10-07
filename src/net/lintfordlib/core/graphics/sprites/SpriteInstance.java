package net.lintfordlib.core.graphics.sprites;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;

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
	private transient AnimatedSpriteListener mAnimatedSpriteListener;

	/** The current frame of the animation */
	private int mCurrentFrame;

	/** A timer to track when to change frames */
	private float mTimer;

	/**  */
	@SerializedName(value = "loopingEnabled")
	private boolean mLoopingEnabled;

	/** If true, animations are played, otherwise, animation is stopped. */
	@SerializedName(value = "animationEnabled")
	private boolean mAnimationEnabled;

	/** Custom variables attached to the sprite */
	private float mTimeAliveInMs;
	private float mLifeTime;

	private Color mColor = new Color(ColorConstants.WHITE);

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Color color() {
		return mColor;
	}

	public void setLifeTime(float lifeTime) {
		mLifeTime = lifeTime;
	}

	public boolean getIsAlive() {
		return mLifeTime > 0.f;
	}

	public void resetTimeAliveInMs() {
		mTimeAliveInMs = 0.f;
	}

	public float getTimeAliveInMs() {
		return mTimeAliveInMs;
	}

	public boolean isFree() {
		return mSpriteDefinition == null;
	}

	public boolean enabled() {
		return mAnimationEnabled;
	}

	public void enabled(boolean enabled) {
		mAnimationEnabled = enabled;

		if (mAnimationEnabled) {
			if (mAnimatedSpriteListener != null) {
				mAnimatedSpriteListener.onStarted(this);
			}
		}
	}

	public boolean isLoopingEnabled() {
		return mLoopingEnabled;
	}

	public void isLoopingEnabled(boolean newValue) {
		mLoopingEnabled = newValue;
	}

	public AnimatedSpriteListener animatedSpriteListender() {
		return mAnimatedSpriteListener;
	}

	public void animatedSpriteListender(AnimatedSpriteListener newListener) {
		mAnimatedSpriteListener = newListener;
	}

	/** returns the frame of an animation from the internal timer (updated via sprite.update()) */
	public SpriteFrame currentSpriteFrame() {
		if (mSpriteDefinition == null)
			return null;

		return mSpriteDefinition.getSpriteFrame(mCurrentFrame);
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
		mTimeAliveInMs = 0.f;
		mLifeTime = 0.f;
	}

	public void init(SpriteDefinition spriteDefinition) {
		mSpriteDefinition = spriteDefinition;
		mLoopingEnabled = spriteDefinition.loopEnabled();
		mAnimationEnabled = true;

		updateDimensionsOnCurrentFrame();
	}

	public void update(LintfordCore core) {
		update(core, false);
	}

	public void update(LintfordCore core, boolean reverseAnimation) {
		final float lDeltaTime = (float) core.appTime().elapsedTimeMilli();

		mLifeTime -= lDeltaTime;
		mTimeAliveInMs += lDeltaTime;

		if (mSpriteDefinition.frameCount() < 2)
			return;

		if (mSpriteDefinition.frameDuration() == 0.0)
			return;

		if (mAnimationEnabled) {
			mTimer += lDeltaTime;
		}

		// update the current frame
		final float lFrameDuration = mSpriteDefinition.frameDuration();
		while (mTimer > lFrameDuration) {
			// Handle this time splice
			mTimer -= mSpriteDefinition.frameDuration();

			if (!reverseAnimation) {
				mCurrentFrame++;

				if (mCurrentFrame >= mSpriteDefinition.frameCount()) {

					if (mLoopingEnabled) {
						mCurrentFrame = 0;
						if (mAnimatedSpriteListener != null) {
							mAnimatedSpriteListener.onLooped(this);
						}
					} else {
						mAnimationEnabled = false;
						mCurrentFrame--;
						if (mAnimatedSpriteListener != null) {
							mAnimatedSpriteListener.onStopped(this);
							return;
						}
					}
				}

			} else {
				mCurrentFrame--;

				if (mCurrentFrame < 0) {

					if (mLoopingEnabled) {
						mCurrentFrame = mSpriteDefinition.frameCount() - 1;
						if (mAnimatedSpriteListener != null) {
							mAnimatedSpriteListener.onLooped(this);
						}
					} else {
						mAnimationEnabled = false;
						mCurrentFrame = 0;
						if (mAnimatedSpriteListener != null) {
							mAnimatedSpriteListener.onStopped(this);
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
			scaleX(lCurrentFrame.scaleX());
			scaleY(lCurrentFrame.scaleY());

			width(lCurrentFrame.width());
			height(lCurrentFrame.height());

			pivotX(lCurrentFrame.pivotX() * scaleX());
			pivotY(lCurrentFrame.pivotY() * scaleY());
		}
	}

	// --------------------------------------
	// Inherited Methods
	// --------------------------------------

	/** Sets the animation to the specified frame number. If the given frame number is OoB of the frame set, then the value is clamped. */
	public void setFrame(int frameNumber) {
		mCurrentFrame = frameNumber;
		mTimer -= mSpriteDefinition.frameDuration();

		if (mCurrentFrame < 0)
			mCurrentFrame = 0;
		if (mCurrentFrame >= mSpriteDefinition.frameCount()) {
			mCurrentFrame = mSpriteDefinition.frameCount() - 1;
		}
	}

	public void prevFrame() {
		mCurrentFrame--;
		mTimer -= mSpriteDefinition.frameDuration();

		if (mCurrentFrame < 0) {
			mCurrentFrame = mSpriteDefinition.frameCount() - 1;
		}
	}

	public void nextFrame() {
		mCurrentFrame++;
		mTimer -= mSpriteDefinition.frameDuration();

		if (mCurrentFrame >= mSpriteDefinition.frameCount()) {
			mCurrentFrame = 0;
		}
	}

	public void playFromBeginning() {
		setFrame(0);
		enabled(true);
	}

	public void reset() {
		mLoopingEnabled = false;
		mAnimationEnabled = false;
	}
}