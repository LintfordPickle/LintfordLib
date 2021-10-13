package net.lintford.library.core.graphics.sprites.spritesheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.sprites.SpriteDefinition;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;

/** A {@link SpriteSheetDefinition} contains a collection of {@link SpriteFrame}s (which each define a source rectangle) and an associated {@link Texture} instance. */
public class SpriteSheetDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Textures referenced within this spritesheet need to be referenced with the correct entity group Id. */
	private transient int mEntityGroupID;

	/** The unique name given to this {@link SpriteSheetDefinition}. */
	public String spriteSheetName;
	public boolean reloadable;
	public String spriteSheetFilename;

	/** The name of the {@link Texture} associated to this {@link SpriteSheetDefinition} */
	protected String textureName;

	/** The {@link Texture} instance associated with this {@link SpriteSheetDefinition} */
	private transient Texture texture;

	/**
	 * In order to detect changes to the SpriteSheet when trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	private long mFileSizeOnLoad;

	/** A collection of {@link ISprite} instances contained within this {@link SpriteSheetDefinition} */
	private SpriteFrame[] spriteFrames;

	// SpriteDefinition
	public Map<String, SpriteDefinition> animationFramesMap;
	protected List<SpriteInstance> spriteInstancePool;

	/** The width of the associated texture. */
	public transient float textureWidth;

	/** The height of the associated texture. */
	public transient float textureHeight;

	public String spriteGraphNodeName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public long fileSizeOnLoad() {
		return mFileSizeOnLoad;
	}

	public void fileSizeOnLoad(long v) {
		mFileSizeOnLoad = v;
	}

	/** Returns true if this {@link SpriteSheetDefinition}'s GL resources have been laoded, false otherwise. */
	public boolean isLoaded() {
		return texture != null;
	}

	/** Returns the texture loaded for this {@link SpriteSheetDefinition}. */
	public Texture texture() {
		return texture;
	}

	/** Returns the number of {@link SpriteFrame}s assigned to the {@link SpriteSheetDefinition}. */
	public int getSpriteCount() {
		return animationFramesMap.size();
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

	public SpriteSheetDefinition() {
		animationFramesMap = new HashMap<>();
		spriteInstancePool = new ArrayList<>();
	}

	/** Creates a new instance of {@link SpriteSheetDefinition} as assigns it the given name. */
	public SpriteSheetDefinition(final String pSpriteSheetName) {
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
		if (textureName == null || textureName.length() == 0) {
			System.err.println("SpriteSheet texture name and filename cannot be null!");
			return;

		}

		mEntityGroupID = pEntityGroupID;

		var lTextureManager = pResourceManager.textureManager();
		texture = lTextureManager.getTexture(textureName, mEntityGroupID);

		if (texture == null || texture.name().equals(TextureManager.TEXTURE_NOT_FOUND_NAME)) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Spritesheet '%s' cannot locate texture %s in EntityGroupID %d.", spriteSheetName, textureName, pEntityGroupID));
		}

		textureWidth = texture.getTextureWidth();
		textureHeight = texture.getTextureHeight();

		if (spriteFrames == null) {
			spriteFrames = new SpriteFrame[0];
		}

		if (spriteInstancePool == null) {
			spriteInstancePool = new ArrayList<>();
		}

		if (animationFramesMap == null) {
			animationFramesMap = new HashMap<>();

		} else {
			// If the SpriteSheet definition had animations, then iterate over them
			// Resolve the Sprite references in the Animations
			for (Map.Entry<String, SpriteDefinition> entry : animationFramesMap.entrySet()) {
				final var lSpriteDefinition = entry.getValue();
				lSpriteDefinition.name = entry.getKey();
				lSpriteDefinition.loadContent(this);
			}
		}

		// Create an AnimationFrames objects for each single SpriteFrame
		final int lSpriteFrameArrayCount = spriteFrames.length;
		for (int i = 0; i < lSpriteFrameArrayCount; i++) {
			final String lSpriteFrameName = spriteFrames[i].name();

			if (animationFramesMap.containsKey(lSpriteFrameName)) {
				continue;
			}

			final SpriteFrame lSpriteFrame = spriteFrames[i];

			SpriteDefinition lNewSprite = new SpriteDefinition();
			lNewSprite.name = lSpriteFrameName;
			lNewSprite.addFrame(lSpriteFrame);

			animationFramesMap.put(lSpriteFrameName, lNewSprite);
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

	public SpriteFrame getSpriteFrame(int pIndex) {
		if (pIndex < 0 || pIndex >= spriteFrames.length)
			return null;

		return spriteFrames[pIndex];
	}

	public int getSpriteFrameIndexByName(String pFrameName) {
		if (pFrameName == null || pFrameName.length() == 0)
			return -1;
		final int lFrameCount = spriteFrames.length;
		for (int i = 0; i < lFrameCount; i++) {
			if (spriteFrames[i].equals(pFrameName))
				return i;
		}
		return -1;
	}

	/** Adds a new sprite definition to this SpriteSheet. */
	public void addSpriteDefinition(final String pNewName, final SpriteDefinition pNewSprite) {
		animationFramesMap.put(pNewName, pNewSprite);

	}

	public SpriteDefinition getSpriteDefinition(final String pSpriteName) {
		return animationFramesMap.get(pSpriteName);

	}

	/** Returns a new {@link SpriteInstance} based on the {@link ISpriteDefinition} of the name provided. Null is returned if the {@link SpriteSheetDefinition} doesn*t contains a Sprite instance of the given name. */
	public SpriteInstance getSpriteInstance(final String pSpriteName) {
		if (animationFramesMap.containsKey(pSpriteName)) {
			return getSpriteInstance(animationFramesMap.get(pSpriteName));
		}

		return null;
	}

	/** Returns a new {@link SpriteInstance} based on the {@link ISpriteDefinition} provided. Null is returned if the {@link SpriteSheetDefinition} doesn*t contains a Sprite instance of the given name. */
	public SpriteInstance getSpriteInstance(final SpriteDefinition pSpriteDefinition) {
		if (pSpriteDefinition == null) {
			return null;

		}

		final var lReturnSpriteInstance = getFreeInstance();
		lReturnSpriteInstance.init(pSpriteDefinition);

		return lReturnSpriteInstance;

	}

	public void releaseInstance(SpriteInstance pInstance) {
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