package net.lintfordlib.renderers.editor.panels;

import java.io.File;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorResourceMapController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.options.ResourcePathsConfig;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiInputFile;

public class WorkspacePanel extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String LAST_RESMAP_PATHNAME = "LastResMapLocation";

	public static final int WORKSPACE_DIRECTORY_ENTRY = 10;
	public static final int RESMAP_FILE_ENTRY = 11;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiInputFile mWorkspaceLocation; // this should be set to the project location (other paths are relative to this)
	private UiInputFile mResMapLocation;
	private ResourcePathsConfig mResourcePathsConfig;
	private ResourceManager mResourceManager;
	private EditorResourceMapController mEditorResourceMapController;

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
		super(parentWindow, "Work Space Panel", entityGroupUid);

		mRenderPanelTitle = true;
		mIsExpandable = true;
		underlineTitle(true);

		mWorkspaceLocation = new UiInputFile(parentWindow);
		mWorkspaceLocation.label("Workspace Folder");
		mWorkspaceLocation.setUiWidgetListener(this, WORKSPACE_DIRECTORY_ENTRY);
		mWorkspaceLocation.directorySelection(true);

		mResMapLocation = new UiInputFile(parentWindow);
		mResMapLocation.label("Res Map File");
		mResMapLocation.setUiWidgetListener(this, RESMAP_FILE_ENTRY);
		mResMapLocation.directorySelection(false);

		addWidget(mWorkspaceLocation);
		addWidget(mResMapLocation);

		resolveResourcePathsConfig();
	}

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mEditorResourceMapController = (EditorResourceMapController) lControllerManager.getControllerByNameRequired(EditorResourceMapController.CONTROLLER_NAME, mEntityGroupUid);

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

		// TODO: use the mEditorResourceMapController to load the resmap in the main editor screen

		final var lLastResmapPathname = mResourcePathsConfig.getKeyValue(LAST_RESMAP_PATHNAME);
		if (lLastResmapPathname != null) {
			final var lFile = new File(lLastResmapPathname);

			if (lFile == null || lFile.isDirectory())
				return;

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Restoring previous resmap file as " + lLastWorkspacePathname);

			mResMapLocation.inputString(lLastWorkspacePathname);
			mResMapLocation.baseDirectory(lLastWorkspacePathname);

			System.setProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME, lLastWorkspacePathname);
		}
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mResourceManager = resourceManager;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		if (entryUid == WORKSPACE_DIRECTORY_ENTRY) {
			final var lWorkspaceDirectory = mWorkspaceLocation.file();
			setWorkspaceDirectory(lWorkspaceDirectory);

			return;
		}

		if (entryUid == RESMAP_FILE_ENTRY) {
			final var lResMapFile = mResMapLocation.file();
			setNewResMapFile(lResMapFile);
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
			mResMapLocation.baseDirectory(lFilepath);
		}
	}

	private void setNewResMapFile(File file) {
		if (file != null && file.exists() && file.isFile()) {
			mResourceManager.loadResourcesFromResMap(file, mEntityGroupUid);

			// Save the entry so its automatically reloaded next time
			mResourcePathsConfig.insertOrUpdateValue(LAST_RESMAP_PATHNAME, file.getAbsolutePath());
			mResourcePathsConfig.saveConfig();
		} else {
			// Create new res_map at this location
		}
	}

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {

	}
}
