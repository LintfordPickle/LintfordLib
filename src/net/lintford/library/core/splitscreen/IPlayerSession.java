package net.lintford.library.core.splitscreen;

public interface IPlayerSession {

	void enablePlayer(boolean playerEnabled);

	boolean isPlayerEnabled();
	
	PlayerSessionViewContainer getViewContainer();

}
