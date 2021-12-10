package net.lintford.library.core.geometry.spritegraph.definitions;

import net.lintford.library.core.entity.definitions.BaseDefinition;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphInstance;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;

/***
 * The {@link SpriteGraphAttachmentDefinition} defines attachments which can be added onto an instrances of {@link SpriteGraphInstance}. 
 * More specifically, spritegraph attachments are added onto the {@link SpriteGraphNodeInstance}.
 */
public class SpriteGraphAttachmentDefinition extends BaseDefinition implements ISpriteGraphAttachmentDefinition {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected String spriteGraphSpritesheetName;
	protected int spriteGraphAttachmentCategory;
	protected String defaultSpriteName;
	protected boolean isAttachmentRemovable = true;
	protected int relativeZDepth;
	protected int attachmentColorTintR = 255;
	protected int attachmentColorTintG = 255;
	protected int attachmentColorTintB = 255;

	protected boolean useDynamicSpritesheetNames;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean useDynamicSpritesheetName() {
		return useDynamicSpritesheetNames;
	}

	@Override
	public int relativeZDepth() {
		return relativeZDepth;
	}

	@Override
	public int attachmentCategory() {
		return spriteGraphAttachmentCategory;
	}

	@Override
	public String spritesheetName() {
		return spriteGraphSpritesheetName;
	}

	@Override
	public String defaultSpriteName() {
		return defaultSpriteName;
	}

	@Override
	public boolean isAttachmentRemovable() {
		return isAttachmentRemovable;
	}

	@Override
	public int colorTint() {
		return (attachmentColorTintR << 16) | (attachmentColorTintG << 8) | attachmentColorTintB;
	}
}