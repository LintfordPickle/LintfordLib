package net.lintfordlib.core.audio.music;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.audio.AudioManager;
import net.lintfordlib.core.audio.AudioManager.AudioMetaData;
import net.lintfordlib.core.audio.AudioSource;
import net.lintfordlib.core.audio.data.AudioFileBase;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

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
	private final List<AudioFileBase> mAudioDataBuffers = new ArrayList<>();
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

	public AudioFileBase getAudioDataByIndex(int index) {
		if (index < 0 || index >= getNumberSondsLoaded()) {
			return null;
		}

		return mAudioDataBuffers.get(index);
	}

	public int getMusicIndexByName(String bufferName) {
		if (bufferName == null || bufferName.length() == 0)
			return NO_MUSIC_INDEX;

		final var numberAudioDataBuffers = mAudioDataBuffers.size();
		for (int i = 0; i < numberAudioDataBuffers; i++) {
			if (mAudioDataBuffers.get(i).name().equals(bufferName)) {
				return i;
			}
		}

		return NO_MUSIC_INDEX;
	}

	public AudioFileBase getMusicDataByName(String bufferName) {
		if (bufferName == null || bufferName.length() == 0)
			return null;

		final var numberAudioDataBuffers = mAudioDataBuffers.size();
		for (int i = 0; i < numberAudioDataBuffers; i++) {
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
		final int numGroups = mMusicGroups.size();
		for (int i = 0; i < numGroups; i++) {
			if (mMusicGroups.get(i).name.equals(name)) {
				return i;
			}
		}

		return MusicManager.NO_GROUP_INDEX;
	}

	public MusicGroup getMusicGroupByName(String name) {
		final int numGroups = mMusicGroups.size();
		for (int i = 0; i < numGroups; i++) {
			if (mMusicGroups.get(i).name.equals(name)) {
				return mMusicGroups.get(i);
			}
		}

		return null;
	}

	public MusicGroup getOrCreateMusicGroup(String newGroupName) {
		final var groupExists = getMusicGroupByName(newGroupName);
		if (groupExists != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Requested new music group denied - group name already exists");
			return groupExists;
		}

		final var newMusicGroup = new MusicGroup(newGroupName);
		mMusicGroups.add(newMusicGroup);
		return newMusicGroup;
	}

	public void removeMusicGroup(String groupNameToRemove) {
		final var foundMusicGroup = getMusicGroupByName(groupNameToRemove);
		if (foundMusicGroup != null) {
			foundMusicGroup.removeAllSongIndices();

			mMusicGroups.remove(foundMusicGroup);
		}
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public MusicManager(AudioManager audioManager) {
		mAudioManager = audioManager;
		mIsMusicEnabled = audioManager.audioConfig().settings().musicEnabled();
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

		final var gson = new GsonBuilder().create();

		var metaFileContentsString = (String) null;
		var audioMetaObject = (AudioMetaData) null;

		try {
			metaFileContentsString = FileUtils.loadString(metaFileLocation);
			audioMetaObject = gson.fromJson(metaFileContentsString, AudioMetaData.class);
		} catch (FileNotFoundException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Could not find music_meta file at: " + metaFileLocation);
			return;
		}

		if (audioMetaObject == null || audioMetaObject.AudioMetaDefinitions == null || audioMetaObject.AudioMetaDefinitions.length == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the music meta file");
			return;
		}

		final int numberOfFontUnitDefinitions = audioMetaObject.AudioMetaDefinitions.length;
		for (int i = 0; i < numberOfFontUnitDefinitions; i++) {
			final var audioDataDefinition = audioMetaObject.AudioMetaDefinitions[i];

			final var soundName = audioDataDefinition.soundname;
			final var filepath = audioDataDefinition.filepath;
			final var reload = audioDataDefinition.reload;
			final var groupName = audioDataDefinition.group;

			var audioDataBuffer = getMusicDataByName(soundName);
			if (reload || audioDataBuffer == null) {
				audioDataBuffer = mAudioManager.loadAudioFile(soundName, filepath, reload);
				mAudioDataBuffers.add(audioDataBuffer);

				if (groupName != null && groupName.length() > 0) {
					final var lMusicGroup = getOrCreateMusicGroup(groupName);

					lMusicGroup.addSongIndex(mAudioDataBuffers.size() - 1);
				}

				Debug.debugManager().logger().i(getClass().getSimpleName(), "Added AudioData file to music playlist: " + soundName);
			}
		}
	}
}
