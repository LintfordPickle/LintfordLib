package net.lintford.library.core.geometry;

import java.io.Serializable;

public class Anchor implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final Anchor ZERO_ANCHOR = new Anchor();

	private static final long serialVersionUID = 5862113195698770627L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public float x;
	public float y;
	public float rot; // default rotation of anchored sprite

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Anchor() {

	}

	public Anchor(String pName, float pX, float pY, float pRot) {
		name = pName;
		x = pX;
		y = pY;
		rot = pRot;
	}

}
