package net.ld.library.core.graphics.textures;

import org.junit.Test;

public class TextureManagerTest {

	/**
	 * Tests the singleton pattern of TextureManager and the lazy intialization
	 * of its private instance.
	 */
	@Test
	public void textuerManagerInitialisationTest() {
		TextureManager lTextureManager = TextureManager.textureManager();

		assert (lTextureManager != null) : "Couldn't get instance of singleton TextureManager.";

	}

}
