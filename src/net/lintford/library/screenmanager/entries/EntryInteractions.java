package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;

public interface EntryInteractions {

	/** called when the state of the {@link MenuEntry} is changed. n.b. you can get the MenuEntry Uid using e.entryId() */
	public default void menuEntryChanged(MenuEntry e) {

	}

	public boolean hasUnconsumedAction();
	
	public default void menuEntryOnClick(InputManager pInputState, int pEntryID) {

	}

}
