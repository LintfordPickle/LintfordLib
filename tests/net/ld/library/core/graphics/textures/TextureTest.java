package net.ld.library.core.graphics.textures;

import org.junit.Test;

public class TextureTest {

	@Test
	public void textureCreationTest() {

		// Arrange
		Texture lTexture = new Texture(13, "TestTexture.png", 128, 128, 1);
	
		// Act
		
		// Assert
		assert(lTexture.getTextureID() == 13) : "TextureID returned incorrect value";
		assert(lTexture.textureLocation().equals("TestTexture.png")) : "Texture location returned incorrect result";
		assert(lTexture.getTextureWidth() == 128) : "Texture width returned incorrect result";
		assert(lTexture.getTextureHeight() == 128) : "Texture height returned incorrect result";
		assert(lTexture.getFilter() == 1) : "Texture filter returned incorrect result";
		
	}

}
