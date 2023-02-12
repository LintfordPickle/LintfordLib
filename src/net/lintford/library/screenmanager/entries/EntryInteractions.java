package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;

public interface EntryInteractions {

	public void menuEntryOnClick(InputManager inputManager, int entryUid);

	public default void onMenuEntryChanged(MenuEntry menuEntry) {
	}

	public default boolean isActionConsumed() {
		return false;
	}

	public default void onMenuEntryActivated(MenuEntry activeEntry) {
	};

	public default void onMenuEntryDeactivated(MenuEntry activeEntry) {
	};

}
