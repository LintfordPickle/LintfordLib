package net.lintfordlib.renderers.windows.components;

public class UiListBoxItem implements Comparable<UiListBoxItem> {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int itemUid;
	public int listOrderIndex;

	public String definitionName;
	public String displayName;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiListBoxItem(int itemUid) {
		this.itemUid = itemUid;
	}

	public UiListBoxItem(int itemUid, String displayName) {
		this.itemUid = itemUid;
		this.displayName = displayName;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void set(String definitionName, String displayName) {
		this.definitionName = definitionName;

		this.displayName = displayName;
	}

	@Override
	public int compareTo(UiListBoxItem o) {
		return listOrderIndex - o.listOrderIndex;
	}
}
