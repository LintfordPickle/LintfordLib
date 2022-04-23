package net.lintford.library.core.entity;

public abstract class PolyEntity extends WorldEntity {

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PolyEntity() {
		super();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;
	}

}