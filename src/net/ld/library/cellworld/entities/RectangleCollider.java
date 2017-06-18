package net.ld.library.cellworld.entities;

public abstract interface RectangleCollider {

	public abstract float getWidth();

	public abstract float getHeight();

	public abstract float setWidth(float pNewWidth);

	public abstract float setHeight(float pNewHeight);

	// --------------------------------------
	// Methods
	// --------------------------------------

	public default void setDimensions(float pWidth, float pHeight) {
		setWidth(pWidth);
		setHeight(pHeight);

	}

}
