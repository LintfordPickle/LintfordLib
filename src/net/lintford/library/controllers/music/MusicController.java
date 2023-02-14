package net.lintford.library.controllers.music;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.audio.music.MusicManager;
import net.lintford.library.core.input.mouse.IInputProcessor;

public class MusicController extends BaseController implements IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Music Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MusicManager mMusicManager;
	private boolean mIsPlaying;
	private boolean mIsPaused;
	private int mCurrentSongIndex = 0;
	private boolean mBank0Active;
	private float mInputTimer;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MusicController(final ControllerManager cControllerManager, final MusicManager musicManager, int entityGroupUid) {
		super(cControllerManager, CONTROLLER_NAME, entityGroupUid);

		mMusicManager = musicManager;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unloadController() {
		mMusicManager = null;
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F5, this))
			play();

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F2, this))
			stop();

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F6, this))
			nextSong();

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F7, this))
			prevSong();

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F8, this))
			pause();

		return super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (!mMusicManager.isMusicEnabled()) {
			if (mIsPlaying) {
				mMusicManager.audioSourceBank0().stop();
				mMusicManager.audioSourceBank1().stop();

				mIsPlaying = false;
			}
			return;
		}

		if (!mIsPlaying)
			return;

		final float lGameTimeModifer = core.gameTime().timeModifier();
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
		if (mIsPlaying)
			return;

		int_play();
	}

	private void int_play() {
		if (mMusicManager.getNumberSondsLoaded() == 0)
			return;

		if (!mIsPaused)
			mBank0Active = !mBank0Active;

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

		if (!mIsPlaying)
			return;

		mIsPlaying = false;

		if (mBank0Active)
			mMusicManager.audioSourceBank0().stop();
		else
			mMusicManager.audioSourceBank1().stop();
	}

	public void pause() {
		if (!mIsPlaying)
			return;

		mIsPlaying = false;
		mIsPaused = true;

		if (mBank0Active)
			mMusicManager.audioSourceBank0().pause();
		else
			mMusicManager.audioSourceBank1().pause();
	}

	public void nextSong() {
		final int lNumberSongs = mMusicManager.getNumberSondsLoaded();

		if (mCurrentSongIndex >= lNumberSongs - 1)
			mCurrentSongIndex = 0;
		else
			mCurrentSongIndex++;

		stop();
		int_play();
	}

	public void prevSong() {
		final int lNumberSongs = mMusicManager.getNumberSondsLoaded();

		if (mCurrentSongIndex <= 0)
			mCurrentSongIndex = lNumberSongs - 1;
		else
			mCurrentSongIndex--;

		stop();
		int_play();
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mInputTimer = IInputProcessor.INPUT_COOLDOWN_TIME;

	}
}