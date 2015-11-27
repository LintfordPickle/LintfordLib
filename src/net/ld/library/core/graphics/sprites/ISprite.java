package net.ld.library.core.graphics.sprites;

import net.ld.library.core.time.GameTime;

public interface ISprite {
	
	public abstract float getX();
	public abstract float getY();
	public abstract int getWidth();
	public abstract int getHeight();
	public abstract void update(GameTime pGameTime);
	public abstract ISprite getSprite();
	public abstract ISprite copy();
	
}
