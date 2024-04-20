package net.lintfordlib.screenmanager;

public interface IToolTipProvider {

	public abstract String toolTipText();

	public abstract boolean isMouseOver();
	
	public abstract boolean hasFocus();

	public abstract boolean isParentActive();

	public abstract boolean isTopHalfOfScreen();

}
