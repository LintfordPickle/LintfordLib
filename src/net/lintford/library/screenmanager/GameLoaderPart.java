package net.lintford.library.screenmanager;

import net.lintford.library.core.ResourceManager;

public interface GameLoaderPart {

	public abstract boolean isLoaded();

	public abstract void loadContent(ResourceManager resourceManager);

	public abstract String getTitle();

}
