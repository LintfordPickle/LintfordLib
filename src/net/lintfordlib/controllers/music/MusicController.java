package net.lintfordlib.controllers.music;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.audio.data.AudioDataBase;
import net.lintfordlib.core.audio.music.MusicManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class MusicController extends BaseController implements IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Music Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MusicManager mMusicManager;
	private boolean mAutoResumeAfterEnabled;
	private boolean mIsPlaying;
	private boolean mIsPaused;
	private boolean mBank0Active;
	private float mInputTimer;

	private int mCurrentSongIndex;
	private int mCurrentGroupIndex;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getSongIndexByName(String songName) {
		return mMusicManager.getMusicIndexByName(songName);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MusicController(final ControllerManager cControllerManager, final MusicManager musicManager, int entityGroupUid) {
		super(cControllerManager, CONTROLLER_NAME, entityGroupUid);

		mMusicManager = musicManager;

		mCurrentSongIndex = 0;
		mCurrentGroupIndex = 0;
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
			play(0);

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F4, this))
			resume();

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
				mMusicManager.audioSourceBank0().pause();
				mMusicManager.audioSourceBank1().pause();

				mAutoResumeAfterEnabled = true;
				mIsPlaying = false;
			}
			return;
		}

		if (!mIsPlaying & !mIsPaused) {
			if (mAutoResumeAfterEnabled) {
				resume();
			} else
				return;
		}

		if (mIsPlaying)
			updatePlayingState(core);

	}

	private void updatePlayingState(LintfordCore core) {
//		final float lGameTimeModifer = core.gameTime().timeModifier();
//		mMusicManager.audioSourceBank0().setPitch(lGameTimeModifer);
//		mMusicManager.audioSourceBank1().setPitch(lGameTimeModifer);

		if (mBank0Active) {
			if (!mMusicManager.audioSourceBank0().isPlaying()) {
				nextSong();
			}

		} else {
			if (!mMusicManager.audioSourceBank1().isPlaying()) {
				nextSong();
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

		int_play(lSearchedSongIndex, MusicManager.NO_GROUP_INDEX);
	}

	public void play(int songIndex) {
		if (mIsPlaying)
			return;

		int_play(songIndex, MusicManager.NO_GROUP_INDEX);
	}

	public void play(int songIndex, int groupIndex) {
		if (mIsPlaying)
			return;

		mCurrentGroupIndex = groupIndex;

		int_play(songIndex, groupIndex);
	}

	public void playFromGroup(int songIndex, String groupName) {
		final var lMusicGroupIndex = mMusicManager.getMusicGroupIndexByName(groupName);
		if (lMusicGroupIndex != MusicManager.NO_GROUP_INDEX) {
			play(songIndex, lMusicGroupIndex);
			return;
		}

		play(songIndex, MusicManager.NO_GROUP_INDEX);
	}

	private void int_play(int songIndex, int groupIndex) {
		if (mMusicManager.getNumberSondsLoaded() == 0)
			return;

		if (!mIsPaused)
			mBank0Active = !mBank0Active;

		if (mIsPaused) {
			if (mBank0Active) {
				if (mCurrentSongIndex == songIndex) {
					mMusicManager.audioSourceBank0().resume();
					mIsPlaying = true;
					mIsPaused = false;
					return;
				}
			} else {
				if (mCurrentSongIndex == songIndex) {
					mMusicManager.audioSourceBank0().resume();
					mIsPlaying = true;
					mIsPaused = false;
					return;
				}
			}
		}

		AudioDataBase lNextSongAudioData = null;

		if (groupIndex != MusicManager.NO_GROUP_INDEX) {
			// resolve the next song from the group indices
			final var lMusicGroup = mMusicManager.getMusicGroupByIndex(groupIndex);
			if (lMusicGroup == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "MusicController requested invalid music group by id: " + groupIndex);
				mCurrentGroupIndex = MusicManager.NO_GROUP_INDEX;
				nextSong();
				return;
			}

			final var lNumSongsInGroup = lMusicGroup.mSongIndices.size();
			songIndex = songIndex % lNumSongsInGroup;
			final var lSongIndex = lMusicGroup.mSongIndices.get(songIndex);
			lNextSongAudioData = mMusicManager.getAudioDataByIndex(lSongIndex);

		} else {
			lNextSongAudioData = mMusicManager.getAudioDataByIndex(songIndex);
		}

		if (mBank0Active)
			mMusicManager.audioSourceBank0().play(lNextSongAudioData.bufferID());
		else
			mMusicManager.audioSourceBank1().play(lNextSongAudioData.bufferID());

		mIsPlaying = true;

		mCurrentSongIndex = songIndex;
		mCurrentGroupIndex = groupIndex;
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

	public void resume() {
		if (!mIsPlaying && !mAutoResumeAfterEnabled)
			return;

		mIsPlaying = true;
		mIsPaused = false;
		mAutoResumeAfterEnabled = false;

		if (mBank0Active)
			mMusicManager.audioSourceBank0().resume();
		else
			mMusicManager.audioSourceBank1().resume();
	}

	public void pause() {
		if (!mIsPlaying)
			return;

		mAutoResumeAfterEnabled = true;
		mIsPlaying = false;
		mIsPaused = true;

		if (mBank0Active)
			mMusicManager.audioSourceBank0().pause();
		else
			mMusicManager.audioSourceBank1().pause();
	}

	public void nextSong() {
		var lCurrentSongIndex = mCurrentSongIndex;

		if (mCurrentGroupIndex == MusicManager.NO_GROUP_INDEX) {
			final int lNumberSongs = mMusicManager.getNumberSondsLoaded();
			if (lCurrentSongIndex >= lNumberSongs - 1)
				lCurrentSongIndex = 0;
			else
				lCurrentSongIndex++;
		} else {
			// constrain song indices to those within the current group of music
			final var lMusicGroup = mMusicManager.getMusicGroupByIndex(mCurrentGroupIndex);
			final var lNumSongInGroup = lMusicGroup.mSongIndices.size();
			lCurrentSongIndex++;
			lCurrentSongIndex = lCurrentSongIndex % lNumSongInGroup;
		}

		Debug.debugManager().logger().i(getClass().getSimpleName(), ".nextSong -> .play(" + lCurrentSongIndex + ")");

		stop();
		int_play(lCurrentSongIndex, mCurrentGroupIndex);
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
		int_play(lCurrentSongIndex, mCurrentGroupIndex);
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer() {
		resetCoolDownTimer(IInputProcessor.INPUT_COOLDOWN_TIME);
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = cooldownInMs;
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