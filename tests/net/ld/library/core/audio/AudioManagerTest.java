package net.ld.library.core.audio;

import org.junit.Test;

public class AudioManagerTest {

	/** Test the initialization and cleanup of the AudioManager */
	@Test
	public void audioManagerCreationTest() {

		AudioManager.init();

		assert (AudioManager.audioManager().isInitialised()) : "AudioManager was not properly initialised.";

		AudioManager.cleanUp();

		assert (!AudioManager.audioManager().isInitialised()) : "AudioManager was not properly disposed.";

	}

}
