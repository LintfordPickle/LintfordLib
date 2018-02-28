package net.lintford.library.core.graphics.sprites;

import net.lintford.library.core.geometry.spritegraph.SpriteGraphAnchorDef;

/** An interface for rendering sprites. A Sprite contains all information about the source rectangle for within a texture when rendering. */
public interface ISprite {

	/** get the source X of the sprite */
	public abstract float getSrcX();

	/** get the source Y of the sprite */
	public abstract float getSrcY();

	/** get the source width of the sprite */
	public abstract float getSrcWidth();

	/** get the source height of the sprite */
	public abstract float getSrcHeight();
	
	/** gets the rotation amount defined in this sprite */
	public abstract float getRotation();

	/** get the anchor point X */
	public default SpriteGraphAnchorDef getAnchorPoint(String pName) {
		return null;
	}

	/** get the pivot point X */
	public default float getPivotPointX() {
		return getSrcWidth() * 0.5f;
	}

	/** get the pivot point Y */
	public default float getPivotPointY() {
		return getSrcHeight() * 0.5f;
	}

}
