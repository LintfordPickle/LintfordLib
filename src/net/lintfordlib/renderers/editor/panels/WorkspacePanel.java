package net.lintfordlib.renderers.editor.panels;

import java.io.File;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.options.ResourcePathsConfig;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiCheckBox;
import net.lintfordlib.renderers.windows.components.UiInputFile;

public class WorkspacePanel extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int WORKSPACE_DIRECTORY_ENTRY = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiInputFile mWorkspaceLocation; // this should be set to the project location (other paths are relative to this)
	private UiCheckBox mResMapAvailable;
	private ResourcePathsConfig mResourcePathsConfig;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public WorkspacePanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Workspace", entityGroupUid);

		mRenderPanelTitle = true;
		mIsExpandable = true;
		mShowActiveLayerButton = false;
		mShowShowLayerButton = false;

		underlineTitle(true);

		mWorkspaceLocation = new UiInputFile(parentWindow);
		mWorkspaceLocation.label("Workspace Folder");
		mWorkspaceLocation.setUiWidgetListener(this, WORKSPACE_DIRECTORY_ENTRY);
		mWorkspaceLocation.directorySelection(true);
		mWorkspaceLocation.isReadonly(true);

		mResMapAvailable = new UiCheckBox(parentWindow);
		mResMapAvailable.label("Res Map Available");
		mResMapAvailable.isReadonly(true);

		addWidget(mWorkspaceLocation);
		addWidget(mResMapAvailable);

		resolveResourcePathsConfig();
	}

	private void resolveResourcePathsConfig() {
		final var lCore = mParentWindow.rendererManager().core();
		mResourcePathsConfig = lCore.config().resourcePaths();

		final var lLastWorkspacePathname = mResourcePathsConfig.getKeyValue(ResourcePathsConfig.LAST_WORKSPACE_PATHNAME);
		if (lLastWorkspacePathname != null) {
			final var lFile = new File(lLastWorkspacePathname);

			if (lFile == null || lFile.isDirectory() == false)
				return;

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Restoring previous workspace location to " + lLastWorkspacePathname);

			mWorkspaceLocation.inputString(lLastWorkspacePathname);
			mWorkspaceLocation.baseDirectory(lLastWorkspacePathname);
			System.setProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME, lLastWorkspacePathname);
		}

		updateResMapAvailableDisplay();

	}

	private void updateResMapAvailableDisplay() {
		final var lWorkspacePathname = mResourcePathsConfig.getKeyValue(ResourcePathsConfig.LAST_WORKSPACE_PATHNAME);
		final var lFile = new File(lWorkspacePathname);
		if (lFile == null || !lFile.exists() || lFile.isDirectory() == false) {
			mResMapAvailable.isChecked(false);
			return;
		}

		mResMapAvailable.isChecked(true);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		if (entryUid == WORKSPACE_DIRECTORY_ENTRY) {
			final var lWorkspaceDirectory = mWorkspaceLocation.file();
			setWorkspaceDirectory(lWorkspaceDirectory);

			// TODO: update all resources and res_map stuff

			updateResMapAvailableDisplay();

			return;
		}
	}

	private void setWorkspaceDirectory(File file) {
		if (file != null && file.exists() && file.isDirectory()) {
			final var lFilepath = file.getAbsolutePath();

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Setting user.dir to " + lFilepath);

			System.setProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME, lFilepath);

			mResourcePathsConfig.insertOrUpdateValue(ResourcePathsConfig.LAST_WORKSPACE_PATHNAME, lFilepath);
			mResourcePathsConfig.saveConfig();

			mWorkspaceLocation.baseDirectory(lFilepath);

			updateResMapAvailableDisplay();
		}
	}

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {

	}
}
