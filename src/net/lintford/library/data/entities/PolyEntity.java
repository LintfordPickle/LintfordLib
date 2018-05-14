package net.lintford.library.data.entities;

public class PolyEntity extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7167698484857683874L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float[] vertices;
	public float width;
	public float height;
	public float rotOriginX, rotOriginY;
	public float rotation;
	public float scale;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float w() {
		return width;
	}

	public float h() {
		return height;
	}

	public void setVertices(float[] pVertices) {
		scale = 1;
		vertices = pVertices;

	}

	@Override
	public float maxLength() {
		return Math.max(width, height);
	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public boolean intersects(WorldEntity pOther) {
		// Poly
		if (pOther instanceof PolyEntity) {
			// TODO: Poly <-> Poly collision
		}

		// Rect
		else if (pOther instanceof RectangleEntity) {
			// TODO: Poly <-> Rectangle collision
		}

		// Circle
		else if (pOther instanceof CircleEntity) {
			// TODO: Poly <-> Circle collision
		}

		// no collision
		return false;
	}

}
