package net.lintfordlib.renderers.windows.components.interfaces;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;

public interface IScrollBarArea {

	public default int parentScreenHash() {
		return this.hashCode();
	}

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
