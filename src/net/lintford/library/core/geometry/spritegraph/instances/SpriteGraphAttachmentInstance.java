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

	private String nodeAttachmentName;
	private boolean mIsInitialized;
	private transient SpriteSheetDefinition mSpritesheetDefinition;
	public String dfaultSpriteName;
	public String spritesheetDefinitionName;
	public boolean resolvedSpritesheetDefinitionName;
	public boolean attachmentIsRemovable;
	public int attachmentCategory;
	public int zDepth;
	public int attachmentColorTint;
	public boolean useDynamicNames;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String attachedNodeName() {
		return nodeAttachmentName;
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

		nodeAttachmentName = pAttachmentDefinition.attachmentName();
		spritesheetDefinitionName = pAttachmentDefinition.spritesheetName();
		dfaultSpriteName = pAttachmentDefinition.defaultSpriteName();
		attachmentIsRemovable = pAttachmentDefinition.isAttachmentRemovable();
		attachmentCategory = pAttachmentDefinition.attachmentCategory();
		zDepth = pAttachmentDefinition.relativeZDepth();
		attachmentColorTint = pAttachmentDefinition.colorTint();
		useDynamicNames = pAttachmentDefinition.useDynamicSpritesheetName();
		resolvedSpritesheetDefinitionName = false;

		mIsInitialized = true;
	}

	public void unload() {
		nodeAttachmentName = null;
		dfaultSpriteName = null;
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