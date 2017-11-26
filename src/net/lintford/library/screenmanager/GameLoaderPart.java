package net.lintford.library.screenmanager;

import net.lintford.library.core.graphics.ResourceManager;

public interface GameLoaderPart {

	public abstract boolean isLoaded(); // prevents loading resources twice
	public abstract void loadContent(ResourceManager pResourceManager);
	
	public abstract String getTitle();
	
}
