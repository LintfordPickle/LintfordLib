package net.lintfordlib.core.graphics.sprites;

import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class SpriteContainer extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -6704747167617991756L;

	public static final int SPRITE_CONTAINER_UNLOADED = 0;
	public static final int SPRITE_CONTAINER_LOADED = 1;
	public static final int SPRITE_CONTAINER_UNSUCCESSFUL = 2;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String spritesheetDefinitionName;
	public String spritesheetDefinitionFilename;
	public String spriteName;

	public transient SpriteSheetDefinition spriteSheetDefinition;
	public transient SpriteInstance spriteInstance;

	public int state = SPRITE_CONTAINER_UNLOADED;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLoaded() {
		return spriteSheetDefinition != null;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public SpriteContainer() {
	}

	public SpriteContainer(String spritesheetDefinitionName, String spritesheetDefinitionFilename, String spriteName) {
		this.spritesheetDefinitionName = spritesheetDefinitionName;
		this.spritesheetDefinitionFilename = spritesheetDefinitionFilename;
		this.spriteName = spriteName;
	}

	public SpriteContainer(SpriteContainer other) {
		spritesheetDefinitionName = other.spritesheetDefinitionName;
		spritesheetDefinitionFilename = other.spritesheetDefinitionFilename;
		spriteName = other.spriteName;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager, int entityGroupUid) {
		if (state != SPRITE_CONTAINER_UNLOADED)
			return;

		if (spritesheetDefinitionName == null || spritesheetDefinitionFilename == null) {
			state = SPRITE_CONTAINER_UNSUCCESSFUL;
			return;
		}

		spriteSheetDefinition = resourceManager.spriteSheetManager().loadSpriteSheet(spritesheetDefinitionName, spritesheetDefinitionFilename, entityGroupUid);
		if (spriteSheetDefinition == null) {
			state = SPRITE_CONTAINER_UNSUCCESSFUL;
			return;
		}

		spriteInstance = spriteSheetDefinition.getSpriteInstance(spriteName);

		state = spriteInstance != null ? SPRITE_CONTAINER_LOADED : SPRITE_CONTAINER_UNSUCCESSFUL;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {
		spritesheetDefinitionName = null;
		spritesheetDefinitionFilename = null;
		spriteName = null;

		spriteSheetDefinition = null;
		spriteInstance = null;

		state = SPRITE_CONTAINER_UNLOADED;
	}

}
