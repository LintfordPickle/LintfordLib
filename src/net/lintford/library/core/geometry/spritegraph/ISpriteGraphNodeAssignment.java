package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public interface ISpriteGraphNodeAssignment {

	public abstract SpriteSheetDefinition spriteSheetDefinition();

	public abstract String spriteFrameName();

	public abstract SpriteFrame spriteFrame();

}
