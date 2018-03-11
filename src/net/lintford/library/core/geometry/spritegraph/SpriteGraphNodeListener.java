package net.lintford.library.core.geometry.spritegraph;

public interface SpriteGraphNodeListener {

	public abstract void onStateChange(SpriteGraphNodeInst pNode, String pState);
	
	public abstract void onStateStarted(SpriteGraphNodeInst pNode, String pState);
	
	public abstract void onStateStopped(SpriteGraphNodeInst pNode, String pState);
	
}
