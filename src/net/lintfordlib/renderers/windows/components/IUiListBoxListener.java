package net.lintfordlib.renderers.windows.components;

public interface IUiListBoxListener {

	void onItemSelected(UiListBoxItem selectedItem);

	void onItemAdded(UiListBoxItem newItem);

	void onItemRemoved(UiListBoxItem oldItem);

}
