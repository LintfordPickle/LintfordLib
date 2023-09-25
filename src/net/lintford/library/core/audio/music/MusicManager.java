package net.lintford.library.core.audio.music;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.audio.AudioManager;
import net.lintford.library.core.audio.AudioManager.AudioMetaData;
import net.lintford.library.core.audio.AudioSource;
import net.lintford.library.core.audio.data.AudioData;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class MusicManager {

	public class MusicGroup {
		public final String name;
		public final List<Integer> mSongIndices = new ArrayList<>(); // Audio Data Buffer (song) indices
		public boolean mShuffle;

		public MusicGroup(String name) {
			this.name = name;
		}

		public void addSongIndex(int songIndex) {
			mSongIndices.add(songIndex);
		}

		public void removeSongIndex(int songIndex) {
			mSongIndices.remove((Integer) songIndex);
		}

		public void removeAllSongIndices() {
			mSongIndices.clear();
		}

		public boolean shuffle() {
			return mShuffle;
		}

		public void shuffle(boolean newValue) {
			mShuffle = newValue;
		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_MUSIC_INDEX = -1;
	public static final int NO_GROUP_INDEX = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AudioManager mAudioManager;
	private boolean mIsMusicEnabled;
	private final List<AudioData> mAudioDataBuffers = new ArrayList<>();
	private final List<MusicGroup> mMusicGroups = new ArrayList<>();

	private AudioSource mAudioSourceBank0;
	private AudioSource mAudioSourceBank1;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isMusicEnabled() {
		return mIsMusicEnabled;
	}

	public void isMusicEnabled(boolean enableMusic) {
		mIsMusicEnabled = enableMusic;
	}

	public AudioSource audioSourceBank0() {
		return mAudioSourceBank0;
	}

	public AudioSource audioSourceBank1() {
		return mAudioSourceBank1;
	}

	public int getNumberSondsLoaded() {
		return mAudioDataBuffers.size();
	}

	public AudioData getAudioDataByIndex(int index) {
		if (index < 0 || index >= getNumberSondsLoaded()) {
			return null;
		}

		return mAudioDataBuffers.get(index);
	}

	public int getMusicIndexByName(String bufferName) {
		if (bufferName == null || bufferName.length() == 0)
			return NO_MUSIC_INDEX;

		final var lNumberAudioDataBuffers = mAudioDataBuffers.size();
		for (int i = 0; i < lNumberAudioDataBuffers; i++) {
			if (mAudioDataBuffers.get(i).name().equals(bufferName)) {
				return i;
			}
		}

		return NO_MUSIC_INDEX;
	}

	public AudioData getMusicDataByName(String bufferName) {
		if (bufferName == null || bufferName.length() == 0)
			return null;

		final var lNumberAudioDataBuffers = mAudioDataBuffers.size();
		for (int i = 0; i < lNumberAudioDataBuffers; i++) {
			if (mAudioDataBuffers.get(i).name().equals(bufferName)) {
				return mAudioDataBuffers.get(i);
			}
		}

		return null;
	}

	public MusicGroup getMusicGroupByIndex(int index) {
		if (index >= 0 && index < mMusicGroups.size()) {
			return mMusicGroups.get(index);
		}

		return null;
	}

	public int getMusicGroupIndexByName(String name) {
		final int lNumGroups = mMusicGroups.size();
		for (int i = 0; i < lNumGroups; i++) {
			if (mMusicGroups.get(i).name.equals(name)) {
				return i;
			}
		}

		return MusicManager.NO_GROUP_INDEX;
	}

	public MusicGroup getMusicGroupByName(String name) {
		final int lNumGroups = mMusicGroups.size();
		for (int i = 0; i < lNumGroups; i++) {
			if (mMusicGroups.get(i).name.equals(name)) {
				return mMusicGroups.get(i);
			}
		}

		return null;
	}

	public MusicGroup getOrCreateMusicGroup(String newGroupName) {
		final var lGroupExists = getMusicGroupByName(newGroupName);
		if (lGroupExists != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Requested new music group denied - group name already exists");
			return lGroupExists;
		}

		final var lNewMusicGroup = new MusicGroup(newGroupName);
		mMusicGroups.add(lNewMusicGroup);
		return lNewMusicGroup;
	}

	public void removeMusicGroup(String groupNameToRemove) {
		final var lFoundMusicGroup = getMusicGroupByName(groupNameToRemove);
		if (lFoundMusicGroup != null) {
			lFoundMusicGroup.removeAllSongIndices();

			mMusicGroups.remove(lFoundMusicGroup);
		}
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public MusicManager(AudioManager audioManager) {
		mAudioManager = audioManager;
		mIsMusicEnabled = audioManager.audioConfig().masterEnabled();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadALContent(ResourceManager resourceManager) {
		mAudioSourceBank0 = mAudioManager.getAudioSource(hashCode(), AudioManager.AUDIO_SOURCE_TYPE_MUSIC);
		mAudioSourceBank1 = mAudioManager.getAudioSource(hashCode(), AudioManager.AUDIO_SOURCE_TYPE_MUSIC);
	}

	public void unloadALContent() {
		mAudioDataBuffers.clear();
		mAudioSourceBank0.unassign();
		mAudioSourceBank1.unassign();
	}

	public void loadMusicFromMetaFile(String metaFileLocation) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading music files from meta-file %s", metaFileLocation));

		final var lGson = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		AudioMetaData lAudioMetaObject = null;

		lMetaFileContentsString = FileUtils.loadString(metaFileLocation);
		lAudioMetaObject = lGson.fromJson(lMetaFileContentsString, AudioMetaData.class);

		if (lAudioMetaObject == null || lAudioMetaObject.AudioMetaDefinitions == null || lAudioMetaObject.AudioMetaDefinitions.length == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the music meta file");
			return;
		}

		final int lNumberOfFontUnitDefinitions = lAudioMetaObject.AudioMetaDefinitions.length;
		for (int i = 0; i < lNumberOfFontUnitDefinitions; i++) {
			final var lAudioDataDefinition = lAudioMetaObject.AudioMetaDefinitions[i];

			final var lSoundName = lAudioDataDefinition.soundname;
			final var lFilepath = lAudioDataDefinition.filepath;
			final var lReload = lAudioDataDefinition.reload;
			final var lGroupName = lAudioDataDefinition.group;

			var lAudioDataBuffer = getMusicDataByName(lSoundName);
			if (lReload || lAudioDataBuffer == null) {
				lAudioDataBuffer = mAudioManager.loadAudioFile(lSoundName, lFilepath, lReload);
				mAudioDataBuffers.add(lAudioDataBuffer);

				if (lGroupName != null && lGroupName.length() > 0) {
					final var lMusicGroup = getOrCreateMusicGroup(lGroupName);

					lMusicGroup.addSongIndex(mAudioDataBuffers.size() - 1);
				}

				Debug.debugManager().logger().i(getClass().getSimpleName(), "Added AudioData file to music playlist: " + lSoundName);
			}
		}
	}
}
