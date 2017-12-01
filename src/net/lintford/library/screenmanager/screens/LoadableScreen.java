package net.lintford.library.screenmanager.screens;

import java.util.List;

import net.lintford.library.screenmanager.GameLoaderPart;

public interface LoadableScreen {

	public abstract List<GameLoaderPart> partsToLoad();
	
}
