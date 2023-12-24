package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.graphics.sprites.SpriteContainer;

public class UiListBoxImageItem extends UiListBoxItem {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7910727546367475548L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public SpriteContainer iconContainer;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiListBoxImageItem(int itemUid) {
		super(itemUid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setAsset(String definitionName, String displayName) {
		this.definitionName = definitionName;
		this.displayName = displayName;
	}

	public void setIcon(String spriteName, String spritesheetDefinitionName, String spritesheetFilename) {
		if (iconContainer == null)
			iconContainer = new SpriteContainer();

		iconContainer.reset();

		iconContainer.spriteName = spriteName;
		iconContainer.spritesheetDefinitionFilename = spritesheetDefinitionName;
		iconContainer.spritesheetDefinitionName = spritesheetFilename;
	}

	public void setIconFrom(SpriteContainer iconSprite) {
		if (iconContainer == null)
			iconContainer = new SpriteContainer();

		iconContainer.reset();

		iconContainer.spriteName = iconSprite.spriteName;
		iconContainer.spritesheetDefinitionFilename = iconSprite.spritesheetDefinitionFilename;
		iconContainer.spritesheetDefinitionName = iconSprite.spritesheetDefinitionName;
	}

}
