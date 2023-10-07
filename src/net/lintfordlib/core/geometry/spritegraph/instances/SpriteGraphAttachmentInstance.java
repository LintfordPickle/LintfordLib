package net.lintfordlib.core.geometry.spritegraph.instances;

import java.io.Serializable;

import net.lintfordlib.core.geometry.spritegraph.definitions.ISpriteGraphAttachmentDefinition;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class SpriteGraphAttachmentInstance implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -812751203902239187L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mAttachmentDefinitionName;
	private boolean mIsInitialized;
	private transient SpriteSheetDefinition mSpritesheetDefinition;
	private String mDefaultSpriteName;
	private String mDefaultAnimationName = "idle";
	private String mSpritesheetDefinitionName;
	private transient boolean mResolvedSpritesheetDefinitionName;
	private boolean mAttachmentIsRemovable;
	private int mAttachmentCategory;
	private int mZDepth;
	private int mAttachmentColorTint;
	private boolean mUseDynamicNames;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int zDepth() {
		return mZDepth;
	}

	public void zDepth(int zDepth) {
		mZDepth = zDepth;
	}

	public boolean attachmentIsRemovable() {
		return mAttachmentIsRemovable;
	}

	public void attachmentIsRemovable(boolean attachmentIsRemovable) {
		mAttachmentIsRemovable = attachmentIsRemovable;
	}

	public int attachmentColorTint() {
		return mAttachmentColorTint;
	}

	public void attachmentColorTint(int attachmentColorTint) {
		mAttachmentColorTint = attachmentColorTint;
	}

	public int attachmentCategory() {
		return mAttachmentCategory;
	}

	public void attachmentCategory(int attachmentCategory) {
		mAttachmentCategory = attachmentCategory;
	}

	public String defaultSpriteName() {
		return mDefaultSpriteName;
	}

	public void defaultSpriteName(String defaultSpriteName) {
		mDefaultSpriteName = defaultSpriteName;
	}

	public String defaultAnimationName() {
		return mDefaultAnimationName;
	}

	public void defaultAnimationName(String defaultAnimationName) {
		mDefaultAnimationName = defaultAnimationName;
	}

	public String spritesheetDefinitionName() {
		return mSpritesheetDefinitionName;
	}

	public void spritesheetDefinitionName(String spritesheetDefinitionName) {
		mSpritesheetDefinitionName = spritesheetDefinitionName;
	}

	public boolean useDynamicNames() {
		return mUseDynamicNames;
	}

	public void useDynamicNames(boolean useDynamicNames) {
		mUseDynamicNames = useDynamicNames;
	}

	public boolean resolvedSpritesheetDefinitionName() {
		return mResolvedSpritesheetDefinitionName;
	}

	public void resolvedSpritesheetDefinitionName(boolean resolvedSpritesheetDefinitionName) {
		mResolvedSpritesheetDefinitionName = resolvedSpritesheetDefinitionName;
	}

	public String attachedNodeName() {
		return mAttachmentDefinitionName;
	}

	public boolean isAttachmentInUse() {
		return mAttachmentCategory > 0;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	public SpriteSheetDefinition spritesheetDefinition() {
		return mSpritesheetDefinition;
	}

	public void spritesheetDefinition(SpriteSheetDefinition spriteSheetDefinition) {
		mSpritesheetDefinition = spriteSheetDefinition;
	}

	public boolean spritesheetResourceLoaded() {
		return mSpritesheetDefinition != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphAttachmentInstance() {

	}

	public SpriteGraphAttachmentInstance(ISpriteGraphAttachmentDefinition attachmentDefinition) {
		initialize(attachmentDefinition);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(ISpriteGraphAttachmentDefinition attachmentDefinition) {
		if (attachmentDefinition == null) {
			unload();
			return;
		}

		mAttachmentDefinitionName = attachmentDefinition.attachmentName();
		mSpritesheetDefinitionName = attachmentDefinition.spritesheetName();
		mDefaultSpriteName = attachmentDefinition.defaultSpriteName();
		mAttachmentIsRemovable = attachmentDefinition.isAttachmentRemovable();
		mAttachmentCategory = attachmentDefinition.attachmentCategory();
		mZDepth = attachmentDefinition.relativeZDepth();
		mAttachmentColorTint = attachmentDefinition.colorTint();
		mUseDynamicNames = attachmentDefinition.useDynamicSpritesheetName();
		mResolvedSpritesheetDefinitionName = false;

		mIsInitialized = true;
	}

	public void unload() {
		mAttachmentDefinitionName = null;
		mDefaultSpriteName = null;
		mSpritesheetDefinitionName = null;
		mSpritesheetDefinition = null;
		mAttachmentIsRemovable = true;
		mAttachmentCategory = -1;
		mZDepth = 0;
		mUseDynamicNames = false;
		mAttachmentColorTint = 0xffffffff;

		mIsInitialized = false;
	}
}