package net.ld.library.screenmanager;

import net.ld.library.core.graphics.ResourceManager;

public interface GameLoaderPart {

	public abstract boolean isLoaded(); // prevents loading resources twice
	public abstract void loadContent(ResourceManager pResourceManager);
	
	public abstract String getTitle();
	
}
