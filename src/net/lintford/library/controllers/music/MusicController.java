package net.lintford.library.controllers.music;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.audio.music.MusicManager;
import net.lintford.library.core.debug.Debug;
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
	private boolean mBank0Active;
	private float mInputTimer;

	private int mCurrentSongIndex = 0;

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

		if (mInputTimer >= 0)
			mInputTimer -= core.gameTime().elapsedTimeMilli();

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

		updatePlayingState(core);

	}

	private void updatePlayingState(LintfordCore core) {
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

	public void play(String songName) {
		if (mIsPlaying)
			return;

		final var lSearchedSongIndex = getSongIndexByName(songName);
		if (lSearchedSongIndex == MusicManager.NO_MUSIC_INDEX)
			return;

		int_play(lSearchedSongIndex);
	}

	public int getSongIndexByName(String songName) {
		return mMusicManager.getMusicIndexByName(songName);
	}

	public void play(int songIndex) {
		if (mIsPlaying)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), ".play(" + songIndex + ")");

		int_play(songIndex);
	}

	public void play() {
		if (mIsPlaying)
			return;

		Debug.debugManager().logger().i(getClass().getSimpleName(), ".play(" + mCurrentSongIndex + ")");

		int_play(mCurrentSongIndex);
	}

	private void int_play(int songIndex) {
		if (mMusicManager.getNumberSondsLoaded() == 0)
			return;

		if (!mIsPaused)
			mBank0Active = !mBank0Active;

		if (mIsPaused) {
			if (mBank0Active) {
				if (mCurrentSongIndex == songIndex) {
					mMusicManager.audioSourceBank0().continuePlaying();
					mIsPlaying = true;
					mIsPaused = false;
					return;
				}
			} else {
				if (mCurrentSongIndex == songIndex) {
					mMusicManager.audioSourceBank0().continuePlaying();
					mIsPlaying = true;
					mIsPaused = false;
					return;
				}
			}
		}

		if (mBank0Active) {
			final var lSongAudioDataBuffer = mMusicManager.getAudioDataByIndex(songIndex);
			mMusicManager.audioSourceBank0().play(lSongAudioDataBuffer.bufferID());

			mIsPlaying = true;

		} else {
			final var lSongAudioDataBuffer = mMusicManager.getAudioDataByIndex(songIndex);
			mMusicManager.audioSourceBank1().play(lSongAudioDataBuffer.bufferID());

			mIsPlaying = true;
		}

		mCurrentSongIndex = songIndex;
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
		var lCurrentSongIndex = mCurrentSongIndex;

		if (lCurrentSongIndex >= lNumberSongs - 1)
			lCurrentSongIndex = 0;
		else
			lCurrentSongIndex++;

		Debug.debugManager().logger().i(getClass().getSimpleName(), ".nextSong -> .play(" + lCurrentSongIndex + ")");

		stop();
		int_play(lCurrentSongIndex);
	}

	public void prevSong() {
		final int lNumberSongs = mMusicManager.getNumberSondsLoaded();
		var lCurrentSongIndex = mCurrentSongIndex;

		if (lCurrentSongIndex <= 0)
			lCurrentSongIndex = lNumberSongs - 1;
		else
			lCurrentSongIndex--;

		Debug.debugManager().logger().i(getClass().getSimpleName(), ".prevSong -> .play(" + lCurrentSongIndex + ")");

		stop();
		int_play(lCurrentSongIndex);
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mInputTimer = IInputProcessor.INPUT_COOLDOWN_TIME;
	}

	// ---

	@Override
	public boolean allowGamepadInput() {
		return true;
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}
}