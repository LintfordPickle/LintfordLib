package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.geometry.AARectangle;

public interface IScrollBarArea {

	public abstract float currentYPos();

	public abstract void RelCurrentYPos(float pAmt);

	public abstract void AbsCurrentYPos(float pValue);

	public abstract AARectangle windowArea();

	public abstract ScrollBarContentRectangle contentArea();

}
