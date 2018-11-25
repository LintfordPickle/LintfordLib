package net.lintford.library.core.box2d.definition;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class Jb2dJsonImage {

	String name;
	String path;
	String file;
	Body body;
	Vec2 center;
	float angle;
	float scale;
	float aspectScale;
	boolean flip;
	float opacity;
	int filter; // 0 = nearest, 1 = linear
	float renderOrder;
	int colorTint[];

	Vec2 corners[];

	int numPoints;
	float points[];
	float uvCoords[];
	int numIndices;
	short indices[];

	public Jb2dJsonImage() {
		colorTint = new int[4];
	}

}
