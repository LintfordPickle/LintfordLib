package net.ld.library.core.graphics.textures;


public class TextureMetaItem {

	// =============================================
	// Variables
	// =============================================
	
	public final String textureName;
	public final String textureLocation;
	public final int filterType; 

	// =============================================
	// Constructors
	// =============================================
	
	public TextureMetaItem(final String pTextureName, final String pTextureLocation, int pFilterType){
		textureName = pTextureName;
		textureLocation = pTextureLocation;
		filterType = pFilterType;
	}
}
