package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.input.InputManager;

public interface IUiWidgetInteractions {

	public abstract void widgetOnDataChanged(InputManager inputManager, int entryUid);

	public abstract void widgetOnClick(InputManager inputManager, int entryUid);

}
