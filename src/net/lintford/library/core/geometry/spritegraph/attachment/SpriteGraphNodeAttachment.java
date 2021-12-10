package net.lintford.library.core.geometry.spritegraph.attachment;

import java.io.Serializable;

import net.lintford.library.core.geometry.spritegraph.definitions.ISpriteGraphAttachmentDefinition;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class SpriteGraphNodeAttachment implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -812751203902239187L;

	// --------------------------------------
	// Variables
	// --------------------------------------

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

	public SpriteGraphNodeAttachment() {

	}

	public SpriteGraphNodeAttachment(ISpriteGraphAttachmentDefinition pAttachmentDefinition) {
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