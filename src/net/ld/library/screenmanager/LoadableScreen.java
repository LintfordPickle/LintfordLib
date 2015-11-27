package net.ld.library.screenmanager;

import java.util.List;

public interface LoadableScreen {

	public abstract List<GameLoaderPart> partsToLoad();
	
}
