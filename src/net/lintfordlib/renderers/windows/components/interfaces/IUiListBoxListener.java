package net.lintfordlib.renderers.windows.components.interfaces;

import net.lintfordlib.renderers.windows.components.UiListBoxItem;

public interface IUiListBoxListener {

	void onItemSelected(UiListBoxItem selectedItem);

	void onItemAdded(UiListBoxItem newItem);

	void onItemRemoved(UiListBoxItem oldItem);

}
