package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.screenmanager.MenuEntry;

public interface EntryInteractions {

	public void menuEntryOnClick(InputManager inputManager, int entryUid);

	public default void onMenuEntryChanged(MenuEntry menuEntry) {
	}

	public default boolean isActionConsumed() {
		return false;
	}

	public default void onMenuEntryActivated(MenuEntry activeEntry) {
	}

	public default void onMenuEntryDeactivated(MenuEntry activeEntry) {
	}

}
