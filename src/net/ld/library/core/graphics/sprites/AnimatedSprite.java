package net.ld.library.core.graphics.sprites;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.time.GameTime;

public class AnimatedSprite implements ISprite {

	// =============================================
	// Variables
	// =============================================

	private List<ISprite> mSpriteFrames;
	private int mCurrentFrame;
	private float mCurTime;
	private float mFrameLength;
	private boolean mLoopEnabled;
	private boolean mActive;
	private AnimatedSpriteListener mAnimatedSpriteListener;

	// =============================================
	// Properties
	// =============================================

	public List<ISprite> frames() {
		return mSpriteFrames;
	}

	public void frames(List<ISprite> pNewFrames) {
		mSpriteFrames = pNewFrames;
	}

	public float frameLength() {
		return mFrameLength;
	}

	public void frameLength(float pFrameLength) {
		mFrameLength = pFrameLength;
	}

	public boolean enabled() {
		return mActive;
	}

	public void enabled(boolean pEnabled) {
		mActive = pEnabled;

		if (mActive) {
			if (mAnimatedSpriteListener != null) {
				// mAnimatedSpriteListener.onStarted(this);
			}
		}
	}

	public boolean loopEnabled() {
		return mLoopEnabled;
	}

	public void loopEnabled(boolean pLoopEnabled) {
		mLoopEnabled = pLoopEnabled;
	}

	public AnimatedSpriteListener animatedSpriteListender() {
		return mAnimatedSpriteListener;
	}

	public void animatedSpriteListender(AnimatedSpriteListener pNewListener) {
		mAnimatedSpriteListener = pNewListener;
	}

	// =============================================
	// Constructor
	// =============================================

	public AnimatedSprite() {
		mSpriteFrames = new ArrayList<>();
		mFrameLength = 100.0f;
		mLoopEnabled = true;
		mActive = true;
	}

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void update(GameTime pGameTime) {
		if (mFrameLength == 0.0)
			return;
		if (mActive) {
			mCurTime += pGameTime.elapseGameTime();
		}

		// update the current frame
		while (mCurTime > mFrameLength) {
			mCurrentFrame++;
			mCurTime -= mFrameLength;

			if (mCurrentFrame >= mSpriteFrames.size()) {

				if (mLoopEnabled) {
					mCurrentFrame = 0;
					if (mAnimatedSpriteListener != null) {
						mAnimatedSpriteListener.onLooped(this);
					}
				} else {
					mActive = false;
					mCurrentFrame --;
					if (mAnimatedSpriteListener != null) {
						mAnimatedSpriteListener.onStopped(this);
					}
				}

			}

		}

	}

	// =============================================
	// Methods
	// =============================================

	public void addFrame(ISprite pSprite) {
		if (!mSpriteFrames.contains(pSprite)) {
			mSpriteFrames.add(pSprite);
		}
	}

	public void removeFrame(Sprite pSprite) {
		if (mSpriteFrames.contains(pSprite)) {
			mSpriteFrames.remove(pSprite);
		}
	}

	public void loadGridSpriteSheet(int pSrcX, int pSrcY, int pWidth, int pHeight, int pNumFramesWide, int pNumFramesHigh, int pNumFrames) {

		for (int y = 0; y < pNumFramesHigh; y++) {
			for (int x = 0; x < pNumFramesWide; x++) {
				final float lFrameX = pSrcX + (x * pWidth);
				final float lFrameY = pSrcY + (y * pHeight);
				
				mSpriteFrames.add(new Sprite(lFrameX, lFrameY, pWidth, pHeight));
			}
		}

	}

	// =============================================
	// Inherited-Methods
	// =============================================

	@Override
	public float getX() {
		return mSpriteFrames.get(mCurrentFrame).getX();
	}

	@Override
	public float getY() {
		return mSpriteFrames.get(mCurrentFrame).getY();
	}

	public ISprite getSprite() {
		if (mSpriteFrames == null || mSpriteFrames.size() == 0)
			return null;
		return mSpriteFrames.get(mCurrentFrame);
	}

	@Override
	public int getWidth() {
		return mSpriteFrames.get(mCurrentFrame).getWidth();
	}

	@Override
	public int getHeight() {
		return mSpriteFrames.get(mCurrentFrame).getHeight();
	}

	@Override
	public ISprite copy() {
		AnimatedSprite lNewSprite = new AnimatedSprite();
		lNewSprite.frames(mSpriteFrames);
		lNewSprite.frameLength(mFrameLength);

		return lNewSprite;
	}

}
