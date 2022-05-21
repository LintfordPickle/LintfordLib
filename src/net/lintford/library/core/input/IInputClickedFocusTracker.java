package net.lintford.library.core.input;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.screenmanager.IInputClickedFocusManager;

public interface IInputClickedFocusTracker {

	/** Implementing class should return true if the input was handled in the core frame, false otherwise */
	boolean inputHandledInCoreFrame();

	/** Returns the hashcode of the control's parent screen */
	int parentScreenHash();

	boolean handleInput(LintfordCore pCore, IInputClickedFocusManager pTrackedControlManager);

	void resetInputHandledInCoreFrameFlag();
}
