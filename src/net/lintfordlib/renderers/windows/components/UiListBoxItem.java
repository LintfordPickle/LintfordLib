package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.geometry.Rectangle;

public class UiListBoxItem extends Rectangle implements Comparable<UiListBoxItem> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -3976341798893720687L;

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

	public void setAsset(String definitionName, String displayName) {
		this.definitionName = definitionName;

		this.displayName = displayName;
	}

	@Override
	public int compareTo(UiListBoxItem o) {
		return listOrderIndex - o.listOrderIndex;
	}
}
