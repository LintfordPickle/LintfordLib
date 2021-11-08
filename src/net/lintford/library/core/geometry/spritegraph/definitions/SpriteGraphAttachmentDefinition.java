package net.lintford.library.core.geometry.spritegraph.definitions;

import net.lintford.library.core.entity.definitions.BaseDefinition;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphInstance;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;

/***
 * The {@link SpriteGraphAttachmentDefinition} defines attachments which can be added onto an instrances of {@link SpriteGraphInstance}. 
 * More specifically, spritegraph attachments are added onto the {@link SpriteGraphNodeInstance}.
 */
public abstract class SpriteGraphAttachmentDefinition extends BaseDefinition implements ISpriteGraphAttachmentDefinition {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected String mSpritesheetName;
	protected String mDefaultSpriteName;
	protected boolean mIsAttachmentRemovable;
	protected int mRelativeZDepth;
	protected int mAttachmentCategory;
	protected int mAttachmentColorTint = 0xFFFFFFFF;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public int relativeZDepth() {
		return mRelativeZDepth;
	}

	@Override
	public int attachmentCategory() {
		return mAttachmentCategory;
	}

	@Override
	public String spritesheetName() {
		return mSpritesheetName;
	}

	@Override
	public String defaultSpriteName() {
		return mDefaultSpriteName;
	}

	@Override
	public boolean isAttachmentRemovable() {
		return mIsAttachmentRemovable;
	}

	@Override
	public int colorTint() {
		return mAttachmentColorTint;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SpriteGraphAttachmentDefinition(short pDefinitionUid) {

	}
}