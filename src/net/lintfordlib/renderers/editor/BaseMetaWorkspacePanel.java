package net.lintfordlib.renderers.editor;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.definitions.MetaFileHeader;
import net.lintfordlib.core.entities.definitions.MetaFileHeaderIo;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.options.ResourcePathsConfig;
import net.lintfordlib.renderers.editor.panels.UiPanel;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiHorizontalEntryGroup;
import net.lintfordlib.renderers.windows.components.UiInputFile;
import net.lintfordlib.renderers.windows.components.UiLabel;
import net.lintfordlib.renderers.windows.components.UiListBoxItem;
import net.lintfordlib.renderers.windows.components.UiSeparator;
import net.lintfordlib.renderers.windows.components.UiVerticalTextListBox;
import net.lintfordlib.renderers.windows.components.interfaces.IUiListBoxListener;

public abstract class BaseMetaWorkspacePanel extends UiPanel implements IUiListBoxListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int META_FILE_SELECTION_ENTRY = 10;
	public static final int META_ENTRY_SELECTION_ENTRY = 11;

	public static final int FILEPATH_SELECTION_ENTRY = 12;

	public static final int METAFILE_SAVE_ENTRY = 13;
	public static final int METAFILE_REVERT_ENTRY = 14;

	public static final int ITEM_ADD_ENTRY = 15;
	public static final int ITEM_DUP_ENTRY = 16;
	public static final int ITEM_DEL_ENTRY = 17;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected UiInputFile mMetaFileSelectionEntry;
	protected String mMetaFileExtension;
	protected ResourcePathsConfig mResourcePathsConfig;
	protected UiLabel mListBoxLabel;
	protected UiVerticalTextListBox mMetaFileItemsList;
	protected UiButton mAddItemButton;
	protected UiButton mDupItemButton;
	protected UiButton mDelItemButton;
	protected UiInputFile mMetaItemNameEntry;
	protected UiButton mSaveMetaFileButton;
	protected UiButton mRevertMetaFileButton;

	protected MetaFileHeader mSelectedAssetMetaHeader;
	protected UiListBoxItem mSelectedMetaItem;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return hashCode();
	}

	public File metaFile() {
		return mMetaFileSelectionEntry.file();
	}

	public String metaFileExtension() {
		return mMetaFileExtension;
	}

	public void metaFileExtension(String metaFileExtension) {
		mMetaFileExtension = metaFileExtension;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseMetaWorkspacePanel(UiWindow parentWindow, String panelName, String fileExt, int entityGroupUid) {
		super(parentWindow, panelName, entityGroupUid);

		mRenderPanelTitle = true;
		mIsExpandable = true;

		underlineTitle(true);

		mMetaFileExtension = fileExt;

		mMetaFileSelectionEntry = new UiInputFile(parentWindow, "Meta File");
		mMetaFileSelectionEntry.setUiWidgetListener(this, META_FILE_SELECTION_ENTRY);

		mListBoxLabel = new UiLabel(parentWindow, "Meta Filenames:");

		mMetaFileItemsList = new UiVerticalTextListBox(parentWindow, entityGroupUid);
		mMetaFileItemsList.addCallbackListener(this);
		mMetaFileItemsList.desiredHeight(210);
		mMetaFileItemsList.showReorderButtons(true);

		mAddItemButton = new UiButton(parentWindow, "Add");
		mAddItemButton.setUiWidgetListener(this, ITEM_ADD_ENTRY);
		mDupItemButton = new UiButton(parentWindow, "Dup");
		mDupItemButton.setUiWidgetListener(this, ITEM_DUP_ENTRY);
		mDelItemButton = new UiButton(parentWindow, "Del");
		mDelItemButton.setUiWidgetListener(this, ITEM_DEL_ENTRY);

		final var lHorizontalGroupEntry0 = new UiHorizontalEntryGroup(parentWindow);
		lHorizontalGroupEntry0.widgets().add(mAddItemButton);
		lHorizontalGroupEntry0.widgets().add(mDupItemButton);
		lHorizontalGroupEntry0.widgets().add(mDelItemButton);

		mMetaItemNameEntry = new UiInputFile(parentWindow, "Asset Filename");
		mMetaItemNameEntry.setUiWidgetListener(this, FILEPATH_SELECTION_ENTRY);

		mSaveMetaFileButton = new UiButton(parentWindow, "Save");
		mSaveMetaFileButton.setUiWidgetListener(this, METAFILE_SAVE_ENTRY);
		mRevertMetaFileButton = new UiButton(parentWindow, "Revert");
		mRevertMetaFileButton.setUiWidgetListener(this, METAFILE_REVERT_ENTRY);

		final var lHorizontalGroupEntry1 = new UiHorizontalEntryGroup(parentWindow);
		lHorizontalGroupEntry1.widgets().add(mSaveMetaFileButton);
		lHorizontalGroupEntry1.widgets().add(mRevertMetaFileButton);

		addWidget(mMetaFileSelectionEntry);
		addWidget(lHorizontalGroupEntry1);
		addWidget(new UiSeparator(parentWindow));
		addWidget(mListBoxLabel);
		addWidget(mMetaFileItemsList);
		addWidget(lHorizontalGroupEntry0);
		addWidget(mMetaItemNameEntry);

		resolveResourcePathsConfig();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case META_FILE_SELECTION_ENTRY:
			newMetaFileSelected();
			break;

		case FILEPATH_SELECTION_ENTRY:
			// Get the desired name from the input field
			final var lNewFileName = mMetaItemNameEntry.inputString().toString();
			if (lNewFileName == null || lNewFileName.length() == 0)
				return;

			// The only requirement for renaming a file is that the destination file doesn't already exist.
			final var lWorkspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
			final var lDstFile = new File(lWorkspacePath, mSelectedAssetMetaHeader.assetRootDirectory() + lNewFileName);

			// Set the new name of the item in the list
			final var lSelectedItem = mMetaFileItemsList.getSelectedItem();

			if (lDstFile.exists()) {
				mMetaItemNameEntry.inputString(lSelectedItem.displayName);
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set item filename because the destination file already exists!");
				// options for overwriting ?
				return;
			}

			lSelectedItem.displayName = lDstFile.getName() + "*";

			break;
		}
	}

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case METAFILE_SAVE_ENTRY:
			saveMetaFileChanges();
			break;

		case ITEM_ADD_ENTRY:
			if (mSelectedAssetMetaHeader == null)
				return;

			createNewMetaDataItem();

			break;

		case ITEM_DUP_ENTRY:
			if (mSelectedAssetMetaHeader == null)
				return;

			duplicateSelectedMetaDataItem();
			break;

		case ITEM_DEL_ENTRY:
			if (mSelectedAssetMetaHeader == null)
				return;

			deleteSelectedMetaDataItem();
			break;

		}
	}

	// --------------------------------------

	protected void newMetaFileSelected() {
		var lNewSystemsMetaFile = mMetaFileSelectionEntry.file();
		if (lNewSystemsMetaFile == null)
			return;

		if (lNewSystemsMetaFile.exists()) {
			final var lAssetMetaHeader = MetaFileHeaderIo.loadFromFilepath(lNewSystemsMetaFile);
			mSelectedAssetMetaHeader = lAssetMetaHeader;

			reloadListBoxItemsFromMetadata();
			newAssetPackHeaderSelected(lNewSystemsMetaFile);
			metaDataListChanged();
		} else {
			mSelectedAssetMetaHeader = new MetaFileHeader();
			final var lWorkspaceDirectory = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);

			var lWorkspacePath = Paths.get(lWorkspaceDirectory);
			var lMetaFilePath = Paths.get(lNewSystemsMetaFile.getAbsolutePath());

			if (lMetaFilePath.startsWith(lWorkspacePath)) {
				lMetaFilePath = lWorkspacePath.relativize(lMetaFilePath);
				lMetaFilePath = lMetaFilePath.getParent();
			}

			mSelectedAssetMetaHeader.assetRootDirectory(lMetaFilePath.toString());

			MetaFileHeaderIo.saveDefinitionsToMetadataFile(mSelectedAssetMetaHeader, lNewSystemsMetaFile.getAbsolutePath());

			reloadListBoxItemsFromMetadata();
			newAssetPackHeaderSelected(lNewSystemsMetaFile);
			metaDataListChanged();
		}
	}

	protected void reloadListBoxItemsFromMetadata() {
		mMetaFileItemsList.items().clear();

		final var lWorkspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
		final var lNumItems = mSelectedAssetMetaHeader.numItems();

		for (int i = 0; i < lNumItems; i++) {
			final var lItemFileToImport = mSelectedAssetMetaHeader.itemFilepaths().get(i);
			final var lAssetItemFile = new File(lWorkspacePath, mSelectedAssetMetaHeader.assetRootDirectory() + lItemFileToImport);

			String itemName = lAssetItemFile.getName();
			if (lAssetItemFile.exists() == false)
				itemName += "*";

			final var lNewItem = new UiListBoxItem(i, itemName);
			lNewItem.data = lAssetItemFile;

			mMetaFileItemsList.items().add(lNewItem);
		}
	}

	protected void newEntryItemSelected(UiListBoxItem selectedItem) {
		String displayName = selectedItem.displayName;
		if (displayName.length() > 0 && displayName.endsWith("*"))
			displayName = displayName.substring(0, displayName.length() - 1);

		mMetaItemNameEntry.inputString(displayName);
	}

	protected void assetItemsSaved() {

	}

	protected void metaDataListChanged() {

	}

	protected void newAssetPackHeaderSelected(File selectedMetaFile) {
		// Try and save the newly selected item in the resources file
		if (getResourceConfigKeyName() != null) {
			final var lMetaFile = mMetaFileSelectionEntry.file();
			if (lMetaFile == null)
				return;

			mResourcePathsConfig.insertOrUpdateValue(getResourceConfigKeyName(), lMetaFile.getAbsolutePath());
			mResourcePathsConfig.saveConfig();
		}
	}

	protected void createNewMetaDataItem() {
		var lNewItemEntryName = "not set";
		final var lNewItemIndex = mMetaFileItemsList.items().size();

		mSelectedAssetMetaHeader.itemFilepaths().add(lNewItemEntryName);

		final var lWorkspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);

		var lAssetRootDirectory = mSelectedAssetMetaHeader.assetRootDirectory();
		if (lAssetRootDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false)
			lAssetRootDirectory += FileUtils.FILE_SEPERATOR;
		final var lAssetItemFile = new File(lWorkspacePath, lAssetRootDirectory + lNewItemEntryName);

		lNewItemEntryName += "*";
		final var lNewListBoxItem = new UiListBoxItem(lNewItemIndex, lNewItemEntryName);

		lNewListBoxItem.data = lAssetItemFile;
		mMetaFileItemsList.items().add(lNewListBoxItem);

		mSelectedMetaItem = lNewListBoxItem;
		newEntryItemSelected(lNewListBoxItem);
	}

	protected void duplicateSelectedMetaDataItem() {
		if (mSelectedMetaItem == null)
			return;

		var oldAssetFile = (File) mSelectedMetaItem.data;
		if (!oldAssetFile.exists())
			return;

		// list stuff
		final var lNewItemIndex = mMetaFileItemsList.items().size();
		final var lNewItemEntryName = "copy of " + oldAssetFile.getName();

		// file stuff
		final var lWorkspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
		var lAssetRootDirectory = mSelectedAssetMetaHeader.assetRootDirectory();
		if (lAssetRootDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false)
			lAssetRootDirectory += FileUtils.FILE_SEPERATOR;

		// copy old file to new file name
		final var newAssetFile = new File(lWorkspacePath, lAssetRootDirectory + lNewItemEntryName);
		FileUtils.copyFile(oldAssetFile, newAssetFile);

		final var lNewListBoxItem = new UiListBoxItem(lNewItemIndex, lNewItemEntryName + "*");
		lNewListBoxItem.data = newAssetFile;
		mMetaFileItemsList.items().add(lNewListBoxItem);

		mSelectedMetaItem = lNewListBoxItem;
		newEntryItemSelected(lNewListBoxItem);
	}

	protected void deleteSelectedMetaDataItem() {
		if (mSelectedMetaItem == null)
			return;

		if (confirmAffirmataiveAction("Delete File?", "The meta item and its associated file will be deleted, are you sure?") == false)
			return;

		var lAssetFile = (File) mSelectedMetaItem.data;
		if (lAssetFile.exists()) {
			lAssetFile.delete();

		}

		mSelectedAssetMetaHeader.itemFilepaths().remove(mSelectedMetaItem.itemUid);

		mMetaFileItemsList.removeItem(mSelectedMetaItem);
		mSelectedMetaItem = null;

		// Currently, removing an item from the meta data header saves the entire meta file.
		// I don't think this is a problem because, ultimately, you can only work on one PS/Emitter at a time
		// anyway. Bu the design is funky chips.

		saveMetaFileChanges();

		reloadListBoxItemsFromMetadata();
		metaDataListChanged();
	}

	private boolean confirmAffirmataiveAction(String title, String message) {
		var dialogResult = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION)
			return true;

		return false;
	}

	protected void saveMetaFileChanges() {

		// run through each of the items in the list and save changes to their names (the difference between the entry name and the underlying file)
		final var lWorkspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
		final var lListItems = mMetaFileItemsList.items();
		final var lNumItems = lListItems.size();

		// This is the array of filepaths (to ParticleSystemDefinitions) that is saved within the meta file.
		final var lItemsArrayToSave = new ArrayList<String>();
		final var lDstMetaFilepath = mMetaFileSelectionEntry.file().getAbsolutePath();

		// First save all changes to the filenames made in the list
		for (int i = 0; i < lNumItems; i++) {
			final var lItemFileToImport = lListItems.get(i);
			var lDisplayName = lItemFileToImport.displayName;

			if (lDisplayName == null) {
				lDisplayName = "not_set_" + i;
			}

			// Ending with * means definition has no matching file?
			if (lDisplayName.endsWith("*")) {
				final var lNewDstFilename = lDisplayName.subSequence(0, lDisplayName.length() - 1);

				// Check valid file extension
				var lAssetRootDirectory = mSelectedAssetMetaHeader.assetRootDirectory();
				final var lDstFile = new File(lWorkspacePath, lAssetRootDirectory + lNewDstFilename);
				final var lSrcItemFile = (File) lItemFileToImport.data;

				if (lDstFile.exists()) {
					// We cannot check if the existing file belong to the definition we are trying to asave, or is
					// a different definition which shares a filename. If the file is present, then we don't need/want
					// to mark the displayname with star.
					lItemsArrayToSave.add(lNewDstFilename.toString());
					lItemFileToImport.data = lDstFile;
					lItemFileToImport.displayName = lDstFile.getName();
					continue;
				}

				if (lSrcItemFile == null || lSrcItemFile.exists() == false) {
					// nothing to do - the Ps will be saved when the system is saved
					lItemFileToImport.data = lDstFile;
				} else {
					lSrcItemFile.renameTo(lDstFile);
					lItemFileToImport.data = lDstFile;
					lItemFileToImport.displayName = lDstFile.getName();
				}

				lItemsArrayToSave.add(lNewDstFilename.toString());
			} else {
				lItemsArrayToSave.add(lItemFileToImport.displayName);
			}
		}

		mSelectedAssetMetaHeader.setNetAssetFilepaths(lItemsArrayToSave);
		MetaFileHeaderIo.saveDefinitionsToMetadataFile(mSelectedAssetMetaHeader, lDstMetaFilepath);

		assetItemsSaved();
	}

	/**
	 * Used to restrieve a previously saved asset meta file location from the resources.ini config file.
	 */
	protected String getResourceConfigKeyName() {
		return null;
	}

	private void resolveResourcePathsConfig() {
		final var lCore = mParentWindow.rendererManager().core();
		mResourcePathsConfig = lCore.config().resourcePaths();

		final var lLastWorkspacePathname = mResourcePathsConfig.getKeyValue(getResourceConfigKeyName());
		if (lLastWorkspacePathname != null) {
			final var lFile = new File(lLastWorkspacePathname);
			if (lFile != null) {
				var lParentFolder = lFile.getAbsolutePath();
				if (lFile.isDirectory() == false) {
					lParentFolder = lFile.getParentFile().getAbsolutePath();
				}

				mMetaFileSelectionEntry.baseDirectory(lParentFolder);
			}
		}
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	public void onItemSelected(UiListBoxItem selectedItem) {
		mSelectedMetaItem = selectedItem;

		newEntryItemSelected(selectedItem);
	}

	public void onItemAdded(UiListBoxItem newItem) {

	}

	public void onItemRemoved(UiListBoxItem oldItem) {

	}

}
