package net.lintford.library.core.geometry.spritegraph;

import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInst;

public interface SpriteGraphNodeListener {

	public abstract void onStateChange(SpriteGraphNodeInst pNode, String pState);
	
	public abstract void onStateStarted(SpriteGraphNodeInst pNode, String pState);
	
	public abstract void onStateStopped(SpriteGraphNodeInst pNode, String pState);
	
}
