package net.lintford.library.renderers.windows.components;

import net.lintford.library.renderers.windows.UIRectangle;

public interface IScrollBarArea {

	public abstract float currentYPos();

	public abstract void RelCurrentYPos(float pAmt);

	public abstract void AbsCurrentYPos(float pValue);

	public abstract UIRectangle windowArea();

	public abstract ScrollBarContentRectangle contentArea();

}
