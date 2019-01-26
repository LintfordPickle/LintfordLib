package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInst;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;

public interface ISpriteNodeInstanceAnimator {

	public void animate(LintfordCore pCore, SpriteGraphInst pParentGraph, SpriteGraphNodeInst pParentNode, SpriteGraphNodeInst pNode, SpriteSheetDef pSpriteSheet);

}
