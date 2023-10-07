package net.lintfordlib.core.input;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

public interface IInputClickedFocusTracker {

	/** Implementing class should return true if the input was handled in the core frame, false otherwise */
	boolean inputHandledInCoreFrame();

	/** Returns the hashcode of the control's parent screen */
	int parentScreenHash();

	boolean handleInput(LintfordCore core, IInputClickedFocusManager trackedControlManager);

	void resetInputHandledInCoreFrameFlag();
}
