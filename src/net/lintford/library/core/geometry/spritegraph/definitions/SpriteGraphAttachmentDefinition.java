package net.lintford.library.core.geometry.spritegraph.definitions;

import net.lintford.library.core.entity.definitions.BaseDefinition;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphInstance;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;

/***
 * The {@link SpriteGraphAttachmentDefinition} defines properties for instantiating {@link SpriteGraphNodeInstance}s, which can be added onto an instrances of {@link SpriteGraphInstance}. 
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

	protected int baseColorTintR = 255;
	protected int baseColorTintG = 255;
	protected int baseColorTintB = 255;

	protected boolean useDynamicSpritesheetNames;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public String attachmentName() {
		return name;
	}

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
		return (baseColorTintR << 16) | (baseColorTintG << 8) | baseColorTintB;
	}

	@Override
	public void setColorTint(int r, int g, int b) {
		baseColorTintR = r;
		baseColorTintG = g;
		baseColorTintB = b;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SpriteGraphAttachmentDefinition(String pAttachmentDefinitionName) {
		name = pAttachmentDefinitionName;
	}

}