package net.lintford.library.controllers.music;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.audio.music.MusicManager;

public class MusicController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "MusicController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MusicManager mMusicManager;

	private boolean mIsPlaying;
	private boolean mIsPaused;

	private int mCurrentSongIndex = 0;
	private float mBank0Gain;
	private float mBank1Gain;
	private boolean mBank0Active;

	private float mSwitchSongInTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if this {@link MusicController} has been initialized properly. */
	@Override
	public boolean isinitialized() {
		return mMusicManager != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MusicController(final ControllerManager pControllerManager, final MusicManager pMusicManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mMusicManager = pMusicManager;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		mMusicManager = null;

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		return super.handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (!mIsPlaying) {
			return;

		}

		final float lDelta = (float) pCore.time().elapseGameTimeMilli();
		final float lFadeSpeed = 1.0f;

		mSwitchSongInTimer -= lDelta;

		if (mSwitchSongInTimer <= 0.0f) {
			nextSong();

		}

		if (mBank0Active) {
			if (mBank0Gain < 1.0f)
				mBank0Gain += lDelta * lFadeSpeed;
			if (mBank1Gain > 0.0f)
				mBank1Gain -= lDelta * lFadeSpeed;

		} else {
			if (mBank1Gain < 1.0f)
				mBank1Gain += lDelta * lFadeSpeed;
			if (mBank0Gain > 0.0f)
				mBank0Gain -= lDelta * lFadeSpeed;
		}

		mMusicManager.audioSourceBank0().setGain(mBank0Gain);
		mMusicManager.audioSourceBank1().setGain(mBank1Gain);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void play() {
		if (mIsPlaying) {
			return;

		}

		int_play();

	}

	private void int_play() {
		if (mMusicManager.getNumberSondsLoaded() == 0) {
			return;

		}

		if (!mIsPaused) {
			mBank0Active = !mBank0Active;

		}

		if (mBank0Active) {
			if (mIsPaused) {
				mMusicManager.audioSourceBank0().continuePlaying();

			} else {
				final var lSongAudioDataBuffer = mMusicManager.getAudioDataByIndex(mCurrentSongIndex);
				mMusicManager.audioSourceBank0().play(lSongAudioDataBuffer.bufferID());

				mSwitchSongInTimer = lSongAudioDataBuffer.durationInSeconds() * 1000f + 500f;

			}
			mIsPlaying = true;

		} else {
			if (mIsPaused) {
				mMusicManager.audioSourceBank1().continuePlaying();

			} else {
				final var lSongAudioDataBuffer = mMusicManager.getAudioDataByIndex(mCurrentSongIndex);
				mMusicManager.audioSourceBank1().play(lSongAudioDataBuffer.bufferID());

				mSwitchSongInTimer = lSongAudioDataBuffer.durationInSeconds() * 1000f + 500f;

			}

			mIsPlaying = true;

		}

	}

	public void stop() {
		mIsPaused = false;

		if (!mIsPlaying) {
			return;

		}

		mIsPlaying = false;

		if (mBank0Active) {
			mMusicManager.audioSourceBank0().stop();

		} else {
			mMusicManager.audioSourceBank1().stop();

		}
	}

	public void pause() {
		if (!mIsPlaying) {
			return;

		}

		mIsPlaying = false;
		mIsPaused = true;

		if (mBank0Active) {
			mMusicManager.audioSourceBank0().pause();

		} else {
			mMusicManager.audioSourceBank1().pause();

		}

	}

	public void nextSong() {
		final int lNumberSongs = mMusicManager.getNumberSondsLoaded();

		if (mCurrentSongIndex >= lNumberSongs - 1) {
			mCurrentSongIndex = 0;
		} else {
			mCurrentSongIndex++;
		}

		stop();
		int_play();

	}

	public void prevSong() {
		final int lNumberSongs = mMusicManager.getNumberSondsLoaded();

		if (mCurrentSongIndex <= 0) {
			mCurrentSongIndex = lNumberSongs - 1;
		} else {
			mCurrentSongIndex--;
		}

		stop();
		int_play();

	}

}
