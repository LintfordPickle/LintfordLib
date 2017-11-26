package net.lintford.library.core.sounds;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	// =============================================
	// Variables
	// =============================================

	private Clip mSoundClip;

	// =============================================
	// Methods
	// =============================================

	public static Sound loadFromResource(String pFileName) {
		Sound lSound = new Sound();

		try {
			File lSoundFile = new File(pFileName);
			AudioInputStream lInputStream = AudioSystem.getAudioInputStream(lSoundFile);

			Clip lClip = AudioSystem.getClip();
			lClip.open(lInputStream);
			lSound.mSoundClip = lClip;

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

		return lSound;
	}

	public void play() {
		if (mSoundClip != null) {
			new Thread() {
				public void run() {
					synchronized (mSoundClip) {
						mSoundClip.stop();
						mSoundClip.setFramePosition(0);
						mSoundClip.start();
					}
				}
			}.start();
		}
	}

	public void unload() {
		// TODO: Unload sounds
	}
}
