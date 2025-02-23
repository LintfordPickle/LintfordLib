package net.lintfordlib.core.graphics.sprites.spritesheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.sprites.SpriteDefinition;
import net.lintfordlib.core.graphics.sprites.SpriteFrame;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.graphics.textures.TextureManager;

/** A {@link SpriteSheetDefinition} contains a collection of {@link SpriteFrame}s (which each define a source rectangle) and an associated {@link Texture} instance. */
public class SpriteSheetDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Textures referenced within this spritesheet need to be referenced with the correct entity group Id. */
	protected transient int mEntityGroupUid;

	/** The unique name given to this {@link SpriteSheetDefinition}. */
	@SerializedName(value = "spriteSheetName")
	protected String mSpriteSheetName;

	@SerializedName(value = "reloadable")
	protected boolean mIsReloadable;

	@SerializedName(value = "spriteSheetFilename")
	protected String mSpriteSheetFilename;

	/** The name of the {@link Texture} associated to this {@link SpriteSheetDefinition} */
	@SerializedName(value = "textureName")
	protected String mTextureName;

	/** The {@link Texture} instance associated with this {@link SpriteSheetDefinition} */
	protected transient Texture mTexture;

	/**
	 * In order to detect changes to the SpriteSheet when trying to reload textures, we will store the file size of the texture each time it is loaded.
	 */
	protected long mFileSizeOnLoad;

	/** A collection of {@link ISprite} instances contained within this {@link SpriteSheetDefinition} */
	@SerializedName(value = "spriteFrames")
	protected SpriteFrame[] mSpriteFrames;

	/** The width of the associated texture. */
	protected transient int mTextureWidth;

	/** The height of the associated texture. */
	protected transient int mTextureHeight;

	@SerializedName(value = "animationFramesMap")
	protected Map<String, SpriteDefinition> mAnimationFramesMap;
	protected List<SpriteInstance> mSpriteInstancePool;

	@SerializedName(value = "spriteGraphNodeName")
	protected String mSpriteGraphNodeName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Map<String, SpriteDefinition> animationFrameMap() {
		return mAnimationFramesMap;
	}

	public long fileSizeOnLoad() {
		return mFileSizeOnLoad;
	}

	public void fileSizeOnLoad(long filesizeOnLoad) {
		mFileSizeOnLoad = filesizeOnLoad;
	}

	/** Returns true if this {@link SpriteSheetDefinition}'s GL resources have been laoded, false otherwise. */
	public boolean isLoaded() {
		return mTexture != null;
	}

	/** Returns the texture loaded for this {@link SpriteSheetDefinition}. */
	public Texture texture() {
		return mTexture;
	}

	/** Returns the number of {@link SpriteFrame}s assigned to the {@link SpriteSheetDefinition}. */
	public int getSpriteCount() {
		return mAnimationFramesMap.size();
	}

	public int getSpriteFrameCount() {
		return mSpriteFrames.length;
	}

	public boolean reloadable() {
		return mIsReloadable;
	}

	public void reloadable(boolean newValue) {
		mIsReloadable = newValue;
	}

	public int textureWidth() {
		return mTextureWidth;
	}

	public int textureHeight() {
		return mTextureHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteSheetDefinition() {
		mAnimationFramesMap = new HashMap<>();
		mSpriteInstancePool = new ArrayList<>();
	}

	/** Creates a new instance of {@link SpriteSheetDefinition} as assigns it the given name. */
	public SpriteSheetDefinition(final String pSpriteSheetName) {
		this();
		mSpriteSheetName = pSpriteSheetName;
	}

	public void copyFrom(SpriteSheetDefinition otherSpritesheet) {
		mSpriteSheetName = otherSpritesheet.mSpriteSheetName;
		mIsReloadable = otherSpritesheet.mIsReloadable;
		mSpriteSheetFilename = otherSpritesheet.mSpriteSheetFilename;
		mTextureName = otherSpritesheet.mTextureName;
		mTexture = otherSpritesheet.mTexture;
		mFileSizeOnLoad = otherSpritesheet.fileSizeOnLoad();
		mSpriteFrames = otherSpritesheet.mSpriteFrames;
		mAnimationFramesMap = otherSpritesheet.mAnimationFramesMap;
		mTextureWidth = otherSpritesheet.mTextureWidth;
		mTextureHeight = otherSpritesheet.mTextureHeight;
		mSpriteGraphNodeName = otherSpritesheet.mSpriteGraphNodeName;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		loadResources(resourceManager, mEntityGroupUid);
	}

	/** Loads the associated texture. */
	public void loadResources(ResourceManager resourceManager, int entityGroupUid) {
		if (mTextureName == null || mTextureName.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "SpriteSheet texture name and filename cannot be null!");
			return;
		}

		mEntityGroupUid = entityGroupUid;

		final var lTextureManager = resourceManager.textureManager();
		mTexture = lTextureManager.getTexture(mTextureName, mEntityGroupUid);
		if (mTexture == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Spritesheet '%s' cannot locate texture %s in EntityGroupID %d.", mSpriteSheetName, mTextureName, entityGroupUid));
			return;
		}

		if (mTexture.name().equals(TextureManager.TEXTURE_NOT_FOUND_NAME)) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Spritesheet '%s' cannot locate texture %s in EntityGroupID %d.", mSpriteSheetName, mTextureName, entityGroupUid));
			return;
		}

		mTextureWidth = mTexture.getTextureWidth();
		mTextureHeight = mTexture.getTextureHeight();
		if (mSpriteFrames == null) {
			mSpriteFrames = new SpriteFrame[0];
		}

		if (mSpriteInstancePool == null) {
			mSpriteInstancePool = new ArrayList<>();
		}

		if (mAnimationFramesMap == null) {
			mAnimationFramesMap = new HashMap<>();
		} else {
			for (Map.Entry<String, SpriteDefinition> entry : mAnimationFramesMap.entrySet()) {
				final var lSpriteDefinition = entry.getValue();
				lSpriteDefinition.name(entry.getKey());
				lSpriteDefinition.loadContent(this);
			}
		}

		// Process Spriteframes
		// TODO: convert the coordinates from normal to opengl (top-left to bottom-right)
		// Create an AnimationFrame object for each single SpriteFrame, so that everything definition within a SpritesheetDefinition is accessible from within the mAnimationFramesMap
		final int lNumSpriteFrames = mSpriteFrames.length;
		for (int i = 0; i < lNumSpriteFrames; i++) {
			final var lSpriteFrame = mSpriteFrames[i];
			final var lNewY = lSpriteFrame.y() + lSpriteFrame.height();
			lSpriteFrame.y(mTextureHeight - lNewY);

			final String lSpriteFrameName = lSpriteFrame.name();
			if (mAnimationFramesMap.containsKey(lSpriteFrameName)) {
				continue;
			}
			final var lNewSprite = new SpriteDefinition();
			lNewSprite.name(lSpriteFrameName);
			lNewSprite.addFrame(lSpriteFrame);

			mAnimationFramesMap.put(lSpriteFrameName, lNewSprite);
		}
	}

	/** unloads the GL Content created by this SpriteSheet. */
	public void unloadResources() {
		mTexture = null;
		mTextureWidth = -1;
		mTextureHeight = -1;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteFrame getSpriteFrame(int index) {
		if (index < 0 || index >= mSpriteFrames.length)
			return null;

		return mSpriteFrames[index];
	}

	public SpriteFrame getSpriteFrame(String spriteName) {
		final var lSpriteIndex = getSpriteFrameIndexByName(spriteName);
		if (lSpriteIndex < 0 || lSpriteIndex >= mSpriteFrames.length)
			return null;

		return mSpriteFrames[lSpriteIndex];
	}

	public int getSpriteFrameIndexByName(String frameName) {
		if (frameName == null || frameName.length() == 0)
			return -1;

		final int lFrameCount = mSpriteFrames.length;
		for (int i = 0; i < lFrameCount; i++) {
			if (mSpriteFrames[i].name().equals(frameName))
				return i;
		}

		return -1;
	}

	/** Adds a new sprite definition to this SpriteSheet. */
	public void addSpriteDefinition(final String definitionName, final SpriteDefinition newSpriteDefinition) {
		mAnimationFramesMap.put(definitionName, newSpriteDefinition);
	}

	public SpriteDefinition getSpriteDefinition(final String spriteName) {
		return mAnimationFramesMap.get(spriteName);
	}

	/** Returns a new {@link SpriteInstance} based on the {@link ISpriteDefinition} of the name provided. Null is returned if the {@link SpriteSheetDefinition} doesn*t contains a Sprite instance of the given name. */
	public SpriteInstance getSpriteInstance(final String spriteName) {
		if (mAnimationFramesMap.containsKey(spriteName)) {
			return getSpriteInstance(mAnimationFramesMap.get(spriteName));
		}

		return null;
	}

	/** Returns a new {@link SpriteInstance} based on the {@link ISpriteDefinition} provided. Null is returned if the {@link SpriteSheetDefinition} doesn*t contains a Sprite instance of the given name. */
	public SpriteInstance getSpriteInstance(final SpriteDefinition spriteDefinition) {
		if (spriteDefinition == null)
			return null;

		final var lReturnSpriteInstance = getFreeInstance();
		lReturnSpriteInstance.init(spriteDefinition);

		return lReturnSpriteInstance;
	}

	public void releaseInstance(SpriteInstance spriteInstance) {
		if (spriteInstance == null)
			return;

		spriteInstance.kill();
		if (!mSpriteInstancePool.contains(spriteInstance)) {
			mSpriteInstancePool.add(spriteInstance);
		}
	}

	private SpriteInstance getFreeInstance() {
		SpriteInstance lReturnInstance = null;

		if (mSpriteInstancePool == null)
			mSpriteInstancePool = new ArrayList<>();

		final int lNumSpriteInPool = mSpriteInstancePool.size();
		for (int i = 0; i < lNumSpriteInPool; i++) {
			final var lSpriteInstance = mSpriteInstancePool.get(i);
			if (lSpriteInstance.isFree()) {
				lReturnInstance = lSpriteInstance;
				break;
			}
		}

		if (lReturnInstance != null) {
			mSpriteInstancePool.remove(lReturnInstance);
			return lReturnInstance;
		}

		return extendInstancePool(4);
	}

	private SpriteInstance extendInstancePool(int extendByAmount) {
		for (int i = 0; i < extendByAmount; i++) {
			mSpriteInstancePool.add(new SpriteInstance());
		}
		return new SpriteInstance();
	}
}