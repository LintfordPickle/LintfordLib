package net.lintford.library.core.audio;

import java.util.ArrayList;
import java.util.List;

public class MusicManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<AudioData> mMusicTracks;
	private boolean mIsPlaying;
	private int mCurrentTrack;
	
	// --------------------------------------
	// Properties
	// --------------------------------------

	public int numTracks() {
		return mMusicTracks.size();
		
	}
	
	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MusicManager() {
		mMusicTracks = new ArrayList<>();
		
	}

	
	// --------------------------------------
	// Methods
	// --------------------------------------

	public void nextSong() {
		if(mMusicTracks.size() < 2) return;
		mCurrentTrack++;
		if(mCurrentTrack >= mMusicTracks.size()) {
			mCurrentTrack = 0;
			
		}
		
	}
	
}
