package net.lintford.library.core.geometry.spritegraph.attachment;

import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public interface ISpriteGraphNodeAttachment {

	public abstract String spriteGraphSpriteSheetName();

	public abstract SpriteSheetDefinition spriteSheetDefinition();

	public abstract void spriteSheetDefinition(SpriteSheetDefinition pSpriteSheetDefinition);

	public abstract boolean isRemovable();

	public abstract int attachmentCategory();

	public abstract String defaultSpriteName();

	public abstract int relativeZDepth();

}
