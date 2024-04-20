package net.lintfordlib.controllers.editor;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.data.editor.EditorLayerBrush;

public class EditorBrushController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Editor Brush Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private IBrushModeCallback mIBrushModeCallback;
	private String mDoingWhatMessage;
	private EditorLayerBrush mEditorBrush;

	private float mCursorWorldX;
	private float mCursorWorldY;

	private boolean mShowCursorPosition;
	private boolean mShowCursorGridUid;
	private boolean mShowHeight;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean showPosition() {
		return mShowCursorPosition;
	}

	public void showPosition(boolean newValue) {
		mShowCursorPosition = newValue;
	}

	public boolean showGridUid() {
		return mShowCursorGridUid;
	}

	public void showGridUid(boolean newValue) {
		mShowCursorGridUid = newValue;
	}

	public boolean showHeight() {
		return mShowHeight;
	}

	public void showHeight(boolean newValue) {
		mShowHeight = newValue;
	}

	public void setCursor(float worldX, float worldY) {
		mCursorWorldX = worldX;
		mCursorWorldY = worldY;
	}

	public float cursorWorldX() {
		return mCursorWorldX;
	}

	public float cursorWorldY() {
		return mCursorWorldY;
	}

	public EditorLayerBrush brush() {
		return mEditorBrush;
	}

	public String doingWhatMessage() {
		return mDoingWhatMessage;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorBrushController(ControllerManager controllerManager, EditorLayerBrush editorBrush, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mEditorBrush = editorBrush;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void clearAction(int ownerHash) {
		if (mEditorBrush == null)
			return;

		if (mEditorBrush.isOwner(ownerHash) == false)
			return; // don't own layer

		if (mEditorBrush.isActionSet() == false)
			return; // no action set

		if (mIBrushModeCallback != null)
			mIBrushModeCallback.onLayerDeselected();

		mDoingWhatMessage = null;

		mEditorBrush.clearAction();
	}

	public void clearLayerMode() {
		if (mEditorBrush == null)
			return;

		if (mIBrushModeCallback != null)
			mIBrushModeCallback.onLayerDeselected();

		mIBrushModeCallback = null;
		mDoingWhatMessage = null;

		mEditorBrush.clearLayer();
	}

	public void clearActiveLayer(int ownerHash) {
		if (mEditorBrush == null)
			return;

		mEditorBrush.clearLayer();

		if (mIBrushModeCallback != null)
			mIBrushModeCallback.onLayerDeselected();

		mIBrushModeCallback = null;
	}

	public void setActiveLayer(IBrushModeCallback callback, int newLayerUid, int ownerHash) {
		if(mEditorBrush == null)
			return;
		
		clearActiveLayer(ownerHash);

		if (mEditorBrush.isBrushLayer(newLayerUid))
			return; // already set

		if (mEditorBrush.isBrushLayer(EditorLayerBrush.NO_LAYER_UID) == false)
			return; // something else is set

		mEditorBrush.brushLayer(newLayerUid, ownerHash);

		mIBrushModeCallback = callback;

		if (mIBrushModeCallback != null)
			mIBrushModeCallback.onLayerSelected();
	}

	public boolean isLayerActive(int layerUid) {
		if(mEditorBrush == null)
			return false;
		
		return mEditorBrush.isBrushLayer(layerUid);
	}

	public boolean setAction(int actionUid, String actionString, int ownerHash) {
		if (mEditorBrush.isOwner(ownerHash) == false)
			return false; // don't own layer

		if (mEditorBrush.isActionSet() && mEditorBrush.brushActionUid() != actionUid)
			return false; // already doing something

		mDoingWhatMessage = actionString;

		mEditorBrush.brushActionUid(actionUid);

		return true;
	}

	public void finishAction(int ownerHash) {
		if (mEditorBrush.isOwner(ownerHash) == false)
			return; // don't own layer

		mDoingWhatMessage = null;

		mEditorBrush.brushActionUid(EditorLayerBrush.NO_ACTION_UID);
	}

	public void setHeightProfilePoint(float mMouseX, float mMouseY) {
//		final var lNearbyEntities = hashGrid().findNearbyEntities(mMouseY, mMouseX, 2.f);
//
//		// TODO: Cache results
//
//		final var lHeightProfileEntities = mPointHeightProfile.heightProfileEntities();
//		lHeightProfileEntities.clear();
//
//		final int lNumFoundEntities = lNearbyEntities.size();
//		for (int i = 0; i < lNumFoundEntities; i++) {
//			final var lEntity = lNearbyEntities.get(i);
//			if (lEntity.intersectAndGetHeight(mMouseX, mMouseY)) {
//				lHeightProfileEntities.add(lEntity);
//			}
//		}
//
//		Collections.sort(lNearbyEntities, HeightComparer);
	}
}
