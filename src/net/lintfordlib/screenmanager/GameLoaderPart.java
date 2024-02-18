package net.lintfordlib.screenmanager;

import net.lintfordlib.assets.ResourceManager;

public interface GameLoaderPart {

	public abstract boolean isLoaded();

	public abstract void loadContent(ResourceManager resourceManager);

	public abstract String getTitle();

}
