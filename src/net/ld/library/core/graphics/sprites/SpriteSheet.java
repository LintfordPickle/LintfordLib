package net.ld.library.core.graphics.sprites;

import java.util.HashMap;
import java.util.Map;

import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;

/** A {@link SpriteSheet} contains a collection of {@link Sprite} (which each define a source rectangle) and an associated {@link Texture} instance. */
public class SpriteSheet {

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** The {@link Texture} instance associated with this {@link SpriteSheet} */
	private transient Texture texture;

	/** The unique name given to this {@link SpriteSheet}. */
	public String spriteSheetName;

	/** The name of the {@link Texture} associated to this {@link SpriteSheet} */
	private String textureName;

	/** The filename of the {@link Texture} associated to this {@link SpriteSheet} */
	private String textureFilename;

	/** A collection of {@link ISprite} instances contained within this {@link SpriteSheet} */
	private Map<String, Sprite> spriteMap;

	private Map<String, AnimatedSprite> animationMap;

	/** The width of the associated texture. */
	public transient float textureWidth;

	/** The height of the associated texture. */
	public transient float textureHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if this {@link SpriteSheet}'s GL resources have been laoded, false otherwise. */
	public boolean isLoaded() {
		return this.texture != null;
	}

	/** Returns the texture loaded for this {@link SpriteSheet}. */
	public Texture texture() {
		return this.texture;
	}

	/** Returns the number of {@link Sprite}s assigned to the {@link SpriteSheet}. */
	public int getSpriteCount() {
		return this.spriteMap.size();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteSheet() {
		this.spriteMap = new HashMap<>();

	}

	/** Creates a new instance of {@link SpriteSheet} as assigns it the given name. */
	public SpriteSheet(final String pSpriteSheetName) {
		this();
		this.spriteSheetName = pSpriteSheetName;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	/** Loads the associated texture. */
	public void loadGLContent() {
		// All SpriteSheets require a valid texture
		if (this.textureName == null || this.textureName.length() == 0 || this.textureFilename == null || this.textureFilename.length() == 0) {
			System.err.println("SpriteSheet texture name and filename cannot be null!");
			return;

		}

		// If the texture has already been loaded, the TextureManager will return the texture instance so we can store it in this SpriteSheet instance.
		this.texture = TextureManager.textureManager().loadTextureFromFile(this.textureName, this.textureFilename);

		// Check that the texture was loaded correctly.
		if (this.texture == null) {
			System.err.println("Error while loading texture: " + this.textureFilename);
			return;

		}

		this.textureWidth = this.texture.getTextureWidth();
		this.textureHeight = this.texture.getTextureHeight();

		// If the SpriteSheet definition had animations, then interate them
		if (animationMap != null) {
			// Resolve the Sprite references in the Animations
			for (AnimatedSprite aSprite : animationMap.values()) {
				aSprite.loadContent(this);

			}

		}

	}

	/** unloads the GL Content created by this SpriteSheet. */
	public void unloadGLContent() {
		this.texture = null;
		this.textureWidth = -1;
		this.textureHeight = -1;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Adds a new sprite definition to this SpriteSheet. */
	public void addSpriteDefinition(final String pNewName, final Sprite pNewSprite) {
		this.spriteMap.put(pNewName, pNewSprite);

	}

	/** Returns the ISprite identified by the given name. null is returned if the {@link SpriteSheet} doesn*t contains a Sprite instance of the given name. */
	public Sprite getSprite(final String pSpriteName) {

		if (this.spriteMap.containsKey(pSpriteName)) {
			return this.spriteMap.get(pSpriteName);
		}

		// No matching ISprite found.
		return null;
	}

	/** Returns the ISprite identified by the given name. null is returned if the {@link SpriteSheet} doesn*t contains a Sprite instance of the given name. */
	public AnimatedSprite getAnimation(final String pAnimationName) {
		if (this.animationMap.containsKey(pAnimationName)) {
			return this.animationMap.get(pAnimationName);
		}

		// No matching ISprite found.
		return null;
	}

}