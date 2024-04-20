package net.lintfordlib.screenmanager;

import net.lintfordlib.core.input.IInputClickedFocusTracker;

public interface IInputClickedFocusManager {

	public abstract void setTrackedClickedFocusControl(IInputClickedFocusTracker controlToTrack);

	public abstract IInputClickedFocusTracker getTrackedClickedFocusControl();

}
