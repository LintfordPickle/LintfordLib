package net.lintfordlib.core.binpacking;

public interface IBinPackedItem {

	void assignToBin(String binName, float x, float y, float width, float height);

	void unassign();

	boolean isAssigned();

	String getBinName();

	float binPositionX();

	float binPositionY();

	float binWidth();

	float binHeight();

	float itemWidth();

	float itemHeight();

}
