package net.lintford.library.core.graphics.sprites.spritesheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.sprites.SpriteDefinition;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.textures.Texture;

/** A {@link SpriteSheetDef} contains a collecetion of {@link SpriteFrame}s (which each define a source rectangle) and an associated {@link Texture} instance. */
public class SpriteSheetDef {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mEntityGroupID;

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
		return texture != null;
	}

	/** Returns the texture loaded for this {@link SpriteSheetDef}. */
	public Texture texture() {
		return texture;
	}

	/** Returns the number of {@link SpriteFrame}s assigned to the {@link SpriteSheetDef}. */
	public int getSpriteCount() {
		return spriteMap.size();
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
		frameMap = new HashMap<>();
		spriteMap = new HashMap<>();
		spriteInstancePool = new ArrayList<>();

	}

	/** Creates a new instance of {@link SpriteSheetDef} as assigns it the given name. */
	public SpriteSheetDef(final String pSpriteSheetName) {
		this();
		spriteSheetName = pSpriteSheetName;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		loadGLContent(pResourceManager, mEntityGroupID);

	}

	/** Loads the associated texture. */
	public void loadGLContent(ResourceManager pResourceManager, int pEntityGroupID) {
		// All SpriteSheets require a valid texture
		if (textureName == null || textureName.length() == 0 || textureFilename == null || textureFilename.length() == 0) {
			System.err.println("SpriteSheet texture name and filename cannot be null!");
			return;

		}

		mEntityGroupID = pEntityGroupID;

		// If the texture has already been loaded, the TextureManager will return the texture instance so we can store it in this SpriteSheet instance.
		texture = pResourceManager.textureManager().loadTexture(textureName, textureFilename, mEntityGroupID);

		// Check that the texture was loaded correctly.
		if (texture == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Cannot load SpriteSheetDef '%s' texture %s for EntityGroupID %d.", spriteSheetName, textureName, pEntityGroupID));
			return;

		}

		textureWidth = texture.getTextureWidth();
		textureHeight = texture.getTextureHeight();

		if (frameMap == null) {
			frameMap = new HashMap<>();
		}

		if (spriteInstancePool == null) {
			spriteInstancePool = new ArrayList<>();
		}

		if (spriteMap == null) {
			spriteMap = new HashMap<>();

		} else {
			// If the SpriteSheet definition had animations, then interate them
			// Resolve the Sprite references in the Animations
			for (SpriteDefinition aSprite : spriteMap.values()) {
				aSprite.loadContent(this);

			}

		}

		// Finally, create a single SpriteDefinition for each SpriteFrame
		for (Entry<String, SpriteFrame> entry : frameMap.entrySet()) {
			if (spriteMap.containsKey(entry.getKey()))
				continue;

			SpriteDefinition lNewSprite = new SpriteDefinition();
			lNewSprite.addFrame(entry.getValue());

			spriteMap.put(entry.getKey(), lNewSprite);

		}

	}

	/** unloads the GL Content created by this SpriteSheet. */
	public void unloadGLContent() {
		texture = null;
		textureWidth = -1;
		textureHeight = -1;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Adds a new sprite definition to this SpriteSheet. */
	public void addSpriteDefinition(final String pNewName, final SpriteDefinition pNewSprite) {
		spriteMap.put(pNewName, pNewSprite);

	}

	public SpriteDefinition getSpriteDefinition(final String pSpriteName) {
		return spriteMap.get(pSpriteName);

	}

	/** Returns a new {@link SpriteInstance} based on the {@link ISpriteDefinition} of the name provided. Null is returned if the {@link SpriteSheetDef} doesn*t contains a Sprite instance of the given name. */
	public SpriteInstance getSpriteInstance(final String pSpriteName) {
		if (spriteMap.containsKey(pSpriteName)) {
			SpriteInstance lReturnInstance = getFreeInstance();
			lReturnInstance.init(spriteMap.get(pSpriteName));
			return lReturnInstance;

		}

		// No matching ISprite found.
		return null;

	}

	public SpriteFrame getSpriteFrame(final String pFrameName) {
		return frameMap.get(pFrameName);

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

		if (spriteInstancePool == null)
			spriteInstancePool = new ArrayList<>();

		final int POOL_SIZE = spriteInstancePool.size();
		for (int i = 0; i < POOL_SIZE; i++) {
			SpriteInstance lSprite = spriteInstancePool.get(i);

			if (lSprite.isFree()) {
				lReturnInstance = lSprite;
				break;

			}

		}

		if (lReturnInstance != null) {
			spriteInstancePool.remove(lReturnInstance);
			return lReturnInstance;

		}

		return extendInstancePool(4);

	}

	private SpriteInstance extendInstancePool(int pAmt) {
		for (int i = 0; i < pAmt; i++) {
			spriteInstancePool.add(new SpriteInstance());

		}

		return new SpriteInstance();

	}

}