package net.lintfordlib.core.geometry.spritegraph.definitions;

public interface ISpriteGraphAttachmentDefinition {

	public abstract String attachmentName();

	public abstract String spritesheetName();

	public abstract boolean isAttachmentRemovable();

	public abstract int attachmentCategory();

	public abstract String defaultSpriteName();

	public abstract int relativeZDepth();

	public default int colorTint() {
		return 0xffffffff;
	}

	public default void setColorTint(int red, int green, int blue) {
	}

	public boolean useDynamicSpritesheetName();

}