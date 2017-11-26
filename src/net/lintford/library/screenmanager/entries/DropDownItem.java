package net.lintford.library.screenmanager.entries;

public class DropDownItem {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mName;
	private int mIndex;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mName;
	}

	public void name(String pNewVaue) {
		mName = pNewVaue;
	}

	public int index() {
		return mIndex;
	}

	public void index(int pNewValue) {
		mIndex = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DropDownItem(String pName, int pIndex) {
		mName = pName;
		mIndex = pIndex;
	}

}
