package net.ld.library.core.audio;

import org.junit.Test;

public class AudioManagerTest {

	/** Test the initialization and cleanup of the AudioManager */
	@Test
	public void audioManagerCreationTest() {

		final AudioManager AUDIO_MANAGER = new AudioManager(); 
		AUDIO_MANAGER.initialise();

		assert (AUDIO_MANAGER.isInitialised()) : "AudioManager was not properly initialised.";

		AUDIO_MANAGER.cleanUp();

		assert (!AUDIO_MANAGER.isInitialised()) : "AudioManager was not properly disposed.";

	}

}
