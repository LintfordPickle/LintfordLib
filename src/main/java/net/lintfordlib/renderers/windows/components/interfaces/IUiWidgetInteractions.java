package net.lintfordlib.renderers.windows.components.interfaces;

import net.lintfordlib.core.input.InputManager;

public interface IUiWidgetInteractions {

	public abstract void widgetOnDataChanged(InputManager inputManager, int entryUid);

	public abstract void widgetOnClick(InputManager inputManager, int entryUid);

}
