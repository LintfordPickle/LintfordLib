package net.lintfordlib.screenmanager.entries.input;

import net.lintfordlib.screenmanager.MenuEntry;

public interface IBindingCallback {

	void finishedBinding();
	
	void setIsBinding(MenuEntry entry);
	
}
