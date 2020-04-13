package net.lintford.library.core.audio.music;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.audio.AudioManager;
import net.lintford.library.core.audio.AudioManager.AudioMetaData;
import net.lintford.library.core.audio.AudioSource;
import net.lintford.library.core.audio.data.AudioData;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class MusicManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AudioManager mAudioManager;

	/** The music audio data */
	private List<AudioData> mAudioDataBuffers;

	private AudioSource mAudioSourceBank0;
	private AudioSource mAudioSourceBank1;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public AudioSource audioSourceBank0() {
		return mAudioSourceBank0;

	}

	public AudioSource audioSourceBank1() {
		return mAudioSourceBank1;

	}

	public int getNumberSondsLoaded() {
		return mAudioDataBuffers.size();

	}

	public AudioData getAudioDataByIndex(int pListIndex) {
		if (pListIndex < 0 || pListIndex >= getNumberSondsLoaded()) {
			return null;

		}

		return mAudioDataBuffers.get(pListIndex);

	}

	public AudioData getAudioDataByName(String pName) {
		if (pName == null || pName.length() == 0)
			return null;

		final int lNumberAudioDataBuffers = mAudioDataBuffers.size();
		for (int i = 0; i < lNumberAudioDataBuffers; i++) {
			if (mAudioDataBuffers.get(i).equals(pName)) {
				return mAudioDataBuffers.get(i);

			}

		}

		return null;

	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public MusicManager(AudioManager pAudioManager) {
		mAudioManager = pAudioManager;

		mAudioDataBuffers = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadALContent(ResourceManager pResourceManager) {
		mAudioSourceBank0 = mAudioManager.getAudioSource(hashCode());
		mAudioSourceBank1 = mAudioManager.getAudioSource(hashCode());

	}

	public void unloadALContent() {
		mAudioDataBuffers.clear();
		mAudioSourceBank0.unassign();
		mAudioSourceBank1.unassign();

	}

	public void loadMusicFromMetaFile(String pMetaFileLocation) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading music files from meta-file %s", pMetaFileLocation));

		final Gson GSON = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		AudioMetaData lAudioMetaObject = null;

		lMetaFileContentsString = FileUtils.loadString(pMetaFileLocation);
		lAudioMetaObject = GSON.fromJson(lMetaFileContentsString, AudioMetaData.class);

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

			var lAudioDataBuffer = getAudioDataByName(lSoundName);
			if (lReload || lAudioDataBuffer == null) {
				lAudioDataBuffer = mAudioManager.loadAudioFile(lSoundName, lFilepath, lReload);
				mAudioDataBuffers.add(lAudioDataBuffer);

				Debug.debugManager().logger().i(getClass().getSimpleName(), "Added AudioData file to music playlist: " + lSoundName);

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
