package net.lintford.library.core.entity;

public abstract class PolyEntity extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7167698484857683874L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float[] mVertices;
	protected float mWidth;
	protected float mHeight;
	protected float mRotOriginX, mRotOriginY;
	protected float mScale;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float w() {
		return mWidth;
	}

	public float h() {
		return mHeight;
	}

	public void setVertices(float[] vertices) {
		mScale = 1;
		mVertices = vertices;
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

	public void setDimensions(float width, float height) {
		mWidth = width;
		mHeight = height;
	}
}