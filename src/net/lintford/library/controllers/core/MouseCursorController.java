package net.lintford.library.controllers.core;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.mouse.MouseCursor;

public class MouseCursorController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Custom Cursor Controller";

	public static final String DEFAULT_CURSOR_NAME = "default";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private long mWindowId;
	private MouseCursor mDefaultCursor;
	private Map<String, MouseCursor> mCursorMap;
	private boolean mIsCustomMouseEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isCustomMouseEnabled() {
		return mIsCustomMouseEnabled;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MouseCursorController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mCursorMap = new HashMap<>();
	}

	// --------------------------------------
	// Core-Methodss
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);
		mWindowId = core.config().display().windowID();

		mDefaultCursor = MouseCursor.loadCursorFromResource("default", "/res/cursors/cursorDefault.png", 0, 0);

		mCursorMap.put(DEFAULT_CURSOR_NAME, mDefaultCursor);
		setCursor(DEFAULT_CURSOR_NAME);
	}

	@Override
	public void unload() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadCursorFromFile(String cursorName, String filename, int hotspotX, int hotspotY) {
		var lCustomCursor = MouseCursor.loadCursorFromFile(cursorName, filename, hotspotX, hotspotY);
		if (lCustomCursor.isLoaded())
			mCursorMap.put(cursorName, lCustomCursor);
	}

	public void loadCursorFromResources(String cursorName, String resourceName, int hotspotX, int hotspotY) {
		var lCustomCursor = MouseCursor.loadCursorFromResource(cursorName, resourceName, hotspotX, hotspotY);
		if (lCustomCursor.isLoaded())
			mCursorMap.put(cursorName, lCustomCursor);
	}

	public void setCursor(String cursorName) {
		if (cursorName == null) {
			GLFW.glfwSetCursor(mWindowId, mDefaultCursor.cursorUid());
			return;
		}

		var lCustomCursor = mCursorMap.get(cursorName);

		if (lCustomCursor != null && lCustomCursor.isLoaded())
			GLFW.glfwSetCursor(mWindowId, lCustomCursor.cursorUid());
		else
			GLFW.glfwSetCursor(mWindowId, mDefaultCursor.cursorUid());

		mIsCustomMouseEnabled = true;
	}

	public void disableCustomMouse() {
		mIsCustomMouseEnabled = false;
	}
}