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

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MouseCursorController(ControllerManager pControllerManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mCursorMap = new HashMap<>();

	}

	// --------------------------------------
	// Core-Methodss
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mWindowId = pCore.config().display().windowID();

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

	public void loadCursorFromFile(String pCursorName, String pFilename, int pHotX, int pHotY) {
		var lCustomCursor = MouseCursor.loadCursorFromFile(pCursorName, pFilename, pHotX, pHotY);

		if (lCustomCursor.isLoaded()) {
			mCursorMap.put(pCursorName, lCustomCursor);

		}

	}

	public void loadCursorFromResources(String pCursorName, String pResourceName, int pHotX, int pHotY) {
		var lCustomCursor = MouseCursor.loadCursorFromResource(pCursorName, pResourceName, pHotX, pHotY);

		if (lCustomCursor.isLoaded()) {
			mCursorMap.put(pCursorName, lCustomCursor);

		}

	}

	public void setCursor(String pCursorName) {
		if (pCursorName == null) {
			GLFW.glfwSetCursor(mWindowId, mDefaultCursor.cursorUid());
			return;

		}

		var lCustomCursor = mCursorMap.get(pCursorName);

		if (lCustomCursor != null && lCustomCursor.isLoaded()) {
			GLFW.glfwSetCursor(mWindowId, lCustomCursor.cursorUid());
		} else {
			GLFW.glfwSetCursor(mWindowId, mDefaultCursor.cursorUid());
		}

		mIsCustomMouseEnabled = true;

	}

	public void disableCustomMouse() {
		mIsCustomMouseEnabled = false;

	}

}
