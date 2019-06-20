package net.lintford.library.core.maths;

import java.io.Serializable;

public class Edge implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7381110307007707589L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Vector2f start;
	public Vector2f end;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Edge() {
		start = new Vector2f();
		end = new Vector2f();

	}

	public Edge(Vector2f pStart, Vector2f pEnd) {
		start = pStart;
		end = pEnd;

	}

}
