package net.lintford.library.core.collisions.shapes;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.maths.Vector2f;

public class CircleShape extends Shape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	public static final int NUM_VERTICES = 1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mX;
	private float mY;
	private List<Vector2f> mVertices;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<Vector2f> getVertices() {
		return mVertices;
	}

	public float centerX() {
		return mX;
	}

	public float centerY() {
		return mY;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public CircleShape() {
		this(0, 0, 10);

	}

	public CircleShape(float centerX, float centerY, float radius) {
		mX = centerX;
		mY = centerY;
		mRadius = radius;

		mVertices = new ArrayList<>(NUM_VERTICES);
		mVertices.add(new Vector2f(mX, mY));
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Vector2f[] getAxes() {
		return null; // Circle has infinite axis
	}

	public Vector2f getNearestVertex(Shape otherShape, Vector2f toFill) {
		float min = Float.MAX_VALUE;

		final int NUM_VERTICES = otherShape.getVertices().size();
		for (int i = 0; i < NUM_VERTICES; i++) {
			float distTo = Vector2f.distance(mX, mY, otherShape.getVertices().get(i).x, otherShape.getVertices().get(i).y);

			if (distTo < min) {
				toFill.x = otherShape.getVertices().get(i).x - mX;
				toFill.y = otherShape.getVertices().get(i).y - mY;

				min = distTo;
			}
		}

		toFill.nor();

		return toFill;
	}

	@Override
	public Vector2f project(Vector2f axis, Vector2f toFill) {

		float c = Vector2f.dot(mX, mY, axis.x, axis.y);

		toFill.x = c - mRadius;
		toFill.y = c + mRadius;

		return toFill;
	}

	/**
	 * Returns true if this circle's radius is zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return (this.mRadius == 0);
	}

	/**
	 * Centers the center of the circle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setPosition(float positionX, float positionY) {
		set(positionX, positionY, mRadius);
	}

	public void setRadius(float radius) {
		set(mX, mY, radius);
	}

	public void set(float x, float y, float radius) {
		mX = x;
		mY = y;
		mRadius = radius;
	}

	public void set(CircleShape otherCicle) {
		mX = otherCicle.mX;
		mY = otherCicle.mY;

		mRadius = otherCicle.mRadius;
	}

	public void expand(float expandByAmount) {
		mRadius += expandByAmount;
	}

	@Override
	public void rotateRel(float relativeRotationAmount) {
		mRotation += relativeRotationAmount;
	}

	@Override
	public void rotateAbs(float absolutionRotationAmount) {
		mRotation = absolutionRotationAmount;
	}
}
