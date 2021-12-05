package net.lintford.library.core.geometry.spritegraph.attachment;

import java.io.Serializable;

import net.lintford.library.core.geometry.spritegraph.definitions.ISpriteGraphAttachmentDefinition;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class SpriteGraphNodeAttachment implements ISpriteGraphAttachmentDefinition, Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -812751203902239187L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mDefaultSpriteName;
	private String mSpritesheetDefinitionName;
	private transient SpriteSheetDefinition mSpritesheetDefinition;
	private boolean mIsRemovable;
	private int mAttachmentCategory;
	private int mZDepth;
	private int mColorTint;
	private boolean mIsInitialized;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAttachmentInUse() {
		return attachmentCategory() > 0;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	@Override
	public int attachmentCategory() {
		return mAttachmentCategory;
	}

	@Override
	public String spritesheetName() {
		return mSpritesheetDefinitionName;
	}

	@Override
	public int relativeZDepth() {
		return mZDepth;
	}

	@Override
	public int colorTint() {
		return mColorTint;
	}

	@Override
	public String defaultSpriteName() {
		return mDefaultSpriteName;
	}

	public SpriteSheetDefinition spritesheetDefinition() {
		return mSpritesheetDefinition;
	}

	public void spritesheetDefinition(SpriteSheetDefinition pSpriteSheetDefinition) {
		mSpritesheetDefinition = pSpriteSheetDefinition;
	}

	@Override
	public boolean isAttachmentRemovable() {
		return mIsRemovable;
	}

	public boolean spritesheetResourceLoaded() {
		return mSpritesheetDefinition != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphNodeAttachment() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(ISpriteGraphAttachmentDefinition pAttachmentDefinition) {
		if (pAttachmentDefinition == null) {
			unload();
			return;
		}

		mSpritesheetDefinitionName = pAttachmentDefinition.spritesheetName();
		mDefaultSpriteName = pAttachmentDefinition.defaultSpriteName();
		mIsRemovable = pAttachmentDefinition.isAttachmentRemovable();
		mAttachmentCategory = pAttachmentDefinition.attachmentCategory();
		mZDepth = pAttachmentDefinition.relativeZDepth();
		mColorTint = pAttachmentDefinition.colorTint();

		mIsInitialized = true;
	}

	public void unload() {
		mDefaultSpriteName = null;
		mSpritesheetDefinitionName = null;
		mSpritesheetDefinition = null;
		mIsRemovable = true;
		mAttachmentCategory = -1;
		mZDepth = 0;
		mColorTint = 0x0;

		mIsInitialized = false;
	}

}