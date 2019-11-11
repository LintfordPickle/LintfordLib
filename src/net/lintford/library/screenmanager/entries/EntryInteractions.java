package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;

public interface EntryInteractions {

	public default void menuEntryChanged(MenuEntry e) {

	}

	public default void menuEntryOnClick(InputManager pInputState, int pEntryID) {

	}

}
