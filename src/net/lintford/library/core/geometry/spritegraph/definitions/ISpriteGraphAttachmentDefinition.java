package net.lintford.library.core.geometry.spritegraph.definitions;

public interface ISpriteGraphAttachmentDefinition {

	public abstract String spritesheetName();

	public abstract boolean isAttachmentRemovable();

	public abstract int attachmentCategory();

	public abstract String defaultSpriteName();

	public abstract int relativeZDepth();

	public default int colorTint() {
		return 0xffffffff;
	}

	public boolean useDynamicSpritesheetName();
	
}