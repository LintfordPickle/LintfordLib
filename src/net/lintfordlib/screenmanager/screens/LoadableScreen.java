package net.lintfordlib.screenmanager.screens;

import java.util.List;

import net.lintfordlib.screenmanager.GameLoaderPart;

public interface LoadableScreen {

	public abstract List<GameLoaderPart> partsToLoad();
	
}
