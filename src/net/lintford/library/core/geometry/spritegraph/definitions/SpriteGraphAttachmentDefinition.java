package net.lintford.library.core.geometry.spritegraph.definitions;

import com.google.gson.annotations.SerializedName;

import net.lintford.library.core.entities.definitions.BaseDefinition;
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

	@SerializedName(value = "spriteGraphSpritesheetName")
	protected String mSpriteGraphSpritesheetName;

	@SerializedName(value = "spriteGraphAttachmentCategory")
	protected int mSpriteGraphAttachmentCategory;

	@SerializedName(value = "defaultSpriteName")
	protected String mDefaultSpriteName;

	@SerializedName(value = "isAttachmentRemovable")
	protected boolean mIsAttachmentRemovable = true;

	@SerializedName(value = "relativeZDepth")
	protected int mRelativeZDepth;

	@SerializedName(value = "baseColorTintR")
	protected int mBaseColorTintR = 255;

	@SerializedName(value = "baseColorTintG")
	protected int mBaseColorTintG = 255;

	@SerializedName(value = "baseColorTintB")
	protected int mBaseColorTintB = 255;

	@SerializedName(value = "useDynamicSpritesheetNames")
	protected boolean mUseDynamicSpritesheetNames;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public String attachmentName() {
		return name;
	}

	public boolean useDynamicSpritesheetName() {
		return mUseDynamicSpritesheetNames;
	}

	@Override
	public int relativeZDepth() {
		return mRelativeZDepth;
	}

	@Override
	public int attachmentCategory() {
		return mSpriteGraphAttachmentCategory;
	}

	@Override
	public String spritesheetName() {
		return mSpriteGraphSpritesheetName;
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
		return (mBaseColorTintR << 16) | (mBaseColorTintG << 8) | mBaseColorTintB;
	}

	@Override
	public void setColorTint(int red, int green, int blue) {
		mBaseColorTintR = red;
		mBaseColorTintG = green;
		mBaseColorTintB = blue;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SpriteGraphAttachmentDefinition(String attachmentDefinitionName) {
		name = attachmentDefinitionName;
	}
}