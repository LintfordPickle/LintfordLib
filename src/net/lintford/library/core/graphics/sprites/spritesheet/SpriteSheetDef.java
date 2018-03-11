package net.lintford.library.core.graphics.sprites.spritesheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lintford.library.core.graphics.sprites.SpriteDefinition;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;

/** A {@link SpriteSheetDef} contains a collecetion of {@link SpriteFrame}s (which each define a source rectangle) and an associated {@link Texture} instance. */
public class SpriteSheetDef {

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** The {@link Texture} instance associated with this {@link SpriteSheetDef} */
	private transient Texture texture;

	/** The unique name given to this {@link SpriteSheetDef}. */
	public String spriteSheetName;

	/** The name of the {@link Texture} associated to this {@link SpriteSheetDef} */
	protected String textureName;

	/** The filename of the {@link Texture} associated to this {@link SpriteSheetDef} */
	protected String textureFilename;
	protected boolean reloadable;
	protected String spriteSheetFilename;

	/**
	 * In order to detect changes to the SpriteSheet when trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	private long mFileSizeOnLoad;

	/** A collection of {@link ISprite} instances contained within this {@link SpriteSheetDef} */
	protected Map<String, SpriteFrame> frameMap;
	protected Map<String, SpriteDefinition> spriteMap;
	protected List<SpriteInstance> spriteInstancePool;

	/**
	 * A collection of names for the types of sprite contained within this {@link SpriteSheetDef}. Types could be, for example, "Red", "Green", "00", "01", "02" etc.)
	 */
	protected String[] spriteTypes;

	/** The width of the associated texture. */
	public transient float textureWidth;

	/** The height of the associated texture. */
	public transient float textureHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public long fileSizeOnLoad() {
		return mFileSizeOnLoad;
	}

	public void fileSizeOnLoad(long v) {
		mFileSizeOnLoad = v;
	}

	/** Returns an array of the types of the item defined within the spritesheet. E.g. for hats, there may be 5 different types, 00 through 04. */
	public String[] types() {
		return spriteTypes;
	}

	/** Returns true if this {@link SpriteSheetDef}'s GL resources have been laoded, false otherwise. */
	public boolean isLoaded() {
		return this.texture != null;
	}

	/** Returns the texture loaded for this {@link SpriteSheetDef}. */
	public Texture texture() {
		return this.texture;
	}

	/** Returns the number of {@link SpriteFrame}s assigned to the {@link SpriteSheetDef}. */
	public int getSpriteCount() {
		return this.spriteMap.size();
	}

	public boolean reloadable() {
		return reloadable;
	}

	public void reloadable(boolean pNewValue) {
		reloadable = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteSheetDef() {
		this.frameMap = new HashMap<>();
		this.spriteMap = new HashMap<>();
		this.spriteInstancePool = new ArrayList<>();

	}

	/** Creates a new instance of {@link SpriteSheetDef} as assigns it the given name. */
	public SpriteSheetDef(final String pSpriteSheetName) {
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
		this.texture = TextureManager.textureManager().loadTexture(this.textureName, this.textureFilename);

		// Check that the texture was loaded correctly.
		if (this.texture == null) {
			System.err.println("Error while loading texture: " + this.textureFilename);
			return;

		}

		this.textureWidth = this.texture.getTextureWidth();
		this.textureHeight = this.texture.getTextureHeight();

		// If the SpriteSheet definition had animations, then interate them
		if (this.spriteMap != null) {
			// Resolve the Sprite references in the Animations
			for (SpriteDefinition aSprite : this.spriteMap.values()) {
				aSprite.loadContent(this);

			}

		}

		// Finally, create a single SpriteDefinition for each SpriteFrame
		for (Entry<String, SpriteFrame> entry : this.frameMap.entrySet()) {
			if (this.spriteMap.containsKey(entry.getKey()))
				continue;

			SpriteDefinition lNewSprite = new SpriteDefinition();
			lNewSprite.addFrame(entry.getValue());

			this.spriteMap.put(entry.getKey(), lNewSprite);

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
	public void addSpriteDefinition(final String pNewName, final SpriteDefinition pNewSprite) {
		this.spriteMap.put(pNewName, pNewSprite);

	}

	public SpriteDefinition getSpriteDefinition(final String pSpriteName) {
		return this.spriteMap.get(pSpriteName);

	}

	/** Returns a new {@link SpriteInstance} based on the {@link ISpriteDefinition} of the name provided. Null is returned if the {@link SpriteSheetDef} doesn*t contains a Sprite instance of the given name. */
	public SpriteInstance getSpriteInstance(final String pSpriteName) {
		if (this.spriteMap.containsKey(pSpriteName)) {
			SpriteInstance lReturnInstance = getFreeInstance();
			lReturnInstance.init(this.spriteMap.get(pSpriteName));
			return lReturnInstance;

		}

		// No matching ISprite found.
		return null;

	}

	public SpriteFrame getSpriteFrame(final String pFrameName) {
		return this.frameMap.get(pFrameName);

	}

	public void releaseInistance(SpriteInstance pInstance) {
		if (pInstance == null)
			return;

		pInstance.kill();

		if (!spriteInstancePool.contains(pInstance)) {
			spriteInstancePool.add(pInstance);

		}

	}

	private SpriteInstance getFreeInstance() {
		SpriteInstance lReturnInstance = null;

		final int POOL_SIZE = this.spriteInstancePool.size();
		for (int i = 0; i < POOL_SIZE; i++) {
			SpriteInstance lSprite = this.spriteInstancePool.get(i);

			if (lSprite.isFree()) {
				lReturnInstance = lSprite;
				break;

			}

		}

		if (lReturnInstance != null) {
			this.spriteInstancePool.remove(lReturnInstance);
			return lReturnInstance;

		}

		return extendInstancePool(4);

	}

	private SpriteInstance extendInstancePool(int pAmt) {
		for (int i = 0; i < pAmt; i++) {
			this.spriteInstancePool.add(new SpriteInstance());

		}

		return new SpriteInstance();

	}

}