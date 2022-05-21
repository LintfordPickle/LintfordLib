package net.lintford.library.screenmanager;

import net.lintford.library.core.input.IInputClickedFocusTracker;

public interface IInputClickedFocusManager {

	public abstract void setTrackedClickedFocusControl(IInputClickedFocusTracker pControlToTrack);

	public abstract IInputClickedFocusTracker getTrackedClickedFocusControl();

}
