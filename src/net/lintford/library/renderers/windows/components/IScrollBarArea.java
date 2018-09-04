package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.geometry.Rectangle;

public interface IScrollBarArea {

	public abstract float currentYPos();

	public abstract void RelCurrentYPos(float pAmt);

	public abstract void AbsCurrentYPos(float pValue);

	/**
	 * This is the area within which you want to contrain the data to be displayed. 
	 * @return
	 */
	public abstract Rectangle contentDisplayArea();

	/**
	 * This is the area of which the content takes up in its entireity.
	 * @return
	 */
	public abstract ScrollBarContentRectangle fullContentArea();

}
