package net.lintford.library.core.geometry.spritegraph.instances;

import java.io.Serializable;

import net.lintford.library.core.geometry.spritegraph.definitions.ISpriteGraphAttachmentDefinition;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class SpriteGraphAttachmentInstance implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -812751203902239187L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String attachmentDefinitionName;
	private boolean mIsInitialized;
	private transient SpriteSheetDefinition mSpritesheetDefinition;
	public String defaultSpriteName;
	public String defaultAnimationName = "idle";
	public String spritesheetDefinitionName;
	public transient boolean resolvedSpritesheetDefinitionName;
	public boolean attachmentIsRemovable;
	public int attachmentCategory;
	public int zDepth;
	public int attachmentColorTint;
	public boolean useDynamicNames;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String attachedNodeName() {
		return attachmentDefinitionName;
	}

	public boolean isAttachmentInUse() {
		return attachmentCategory > 0;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	public SpriteSheetDefinition spritesheetDefinition() {
		return mSpritesheetDefinition;
	}

	public void spritesheetDefinition(SpriteSheetDefinition pSpriteSheetDefinition) {
		mSpritesheetDefinition = pSpriteSheetDefinition;
	}

	public boolean spritesheetResourceLoaded() {
		return mSpritesheetDefinition != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphAttachmentInstance() {

	}

	public SpriteGraphAttachmentInstance(ISpriteGraphAttachmentDefinition pAttachmentDefinition) {
		initialize(pAttachmentDefinition);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(ISpriteGraphAttachmentDefinition pAttachmentDefinition) {
		if (pAttachmentDefinition == null) {
			unload();
			return;
		}

		attachmentDefinitionName = pAttachmentDefinition.attachmentName();
		spritesheetDefinitionName = pAttachmentDefinition.spritesheetName();
		defaultSpriteName = pAttachmentDefinition.defaultSpriteName();
		attachmentIsRemovable = pAttachmentDefinition.isAttachmentRemovable();
		attachmentCategory = pAttachmentDefinition.attachmentCategory();
		zDepth = pAttachmentDefinition.relativeZDepth();
		attachmentColorTint = pAttachmentDefinition.colorTint();
		useDynamicNames = pAttachmentDefinition.useDynamicSpritesheetName();
		resolvedSpritesheetDefinitionName = false;

		mIsInitialized = true;
	}

	public void unload() {
		attachmentDefinitionName = null;
		defaultSpriteName = null;
		spritesheetDefinitionName = null;
		mSpritesheetDefinition = null;
		attachmentIsRemovable = true;
		attachmentCategory = -1;
		zDepth = 0;
		useDynamicNames = false;
		attachmentColorTint = 0xffffffff;

		mIsInitialized = false;
	}
}