package net.ld.library.renderers.windows.components;

import net.ld.library.core.maths.Rectangle;

public interface IScrollBarArea {

	public abstract float currentYPos();

	public abstract void RelCurrentYPos(float pAmt);

	public abstract void AbsCurrentYPos(float pValue);

	public abstract Rectangle windowArea();

	public abstract ScrollBarContentRectangle contentArea();

}
