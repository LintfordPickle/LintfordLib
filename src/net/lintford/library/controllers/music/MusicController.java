package net.lintford.library.controllers.music;

import org.lwjgl.glfw.GLFW;

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
	private boolean mBank0Active;

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

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F5)) { // play / pause
			play();

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F2)) { // stop
			stop();

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F6)) { // next
			nextSong();

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F7)) { // prev
			prevSong();

		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F8)) {
			pause();
		}

		return super.handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (!mMusicManager.isMusicEnabled()) {
			if (mIsPlaying) {
				mMusicManager.audioSourceBank0().stop();
				mMusicManager.audioSourceBank1().stop();

				mIsPlaying = false;

			}
			return;
		}

		if (!mIsPlaying) {
			return;

		}

		final float lGameTimeModifer = pCore.gameTime().timeModifier();

		// TODO: This doesn't belong in the library ...
		mMusicManager.audioSourceBank0().setPitch(lGameTimeModifer);
		mMusicManager.audioSourceBank1().setPitch(lGameTimeModifer);

		if (mBank0Active) {
			if (!mMusicManager.audioSourceBank0().isPlaying()) {
				nextSong();
			}
		} else {
			if (!mMusicManager.audioSourceBank1().isPlaying()) {
				prevSong();
			}
		}

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
			}
			mIsPlaying = true;

		} else {
			if (mIsPaused) {
				mMusicManager.audioSourceBank1().continuePlaying();

			} else {
				final var lSongAudioDataBuffer = mMusicManager.getAudioDataByIndex(mCurrentSongIndex);
				mMusicManager.audioSourceBank1().play(lSongAudioDataBuffer.bufferID());
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