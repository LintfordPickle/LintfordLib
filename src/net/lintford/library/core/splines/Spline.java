package net.lintford.library.core.splines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jbox2d.common.Vec2;

import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.Vector2f;

public class Spline {

	// --------------------------------------
	// Variables
	// --------------------------------------

	// Don't forget, these two are shared objects. Subsequent calls of getPointOnSpline / getgradientOnSpline will overwrite previous results
	private final SplinePoint mReturnSplinePoint = new SplinePoint();
	private final SplinePoint mReturnSplineGradient = new SplinePoint();

	private List<SplinePoint> mPoints;

	private float mTotalSplineLength;
	private boolean mIsLooped;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float totalSplineLength() {
		return mTotalSplineLength;
	}

	public int numberSplineControlPoints() {
		return mPoints.size();
	}

	public List<SplinePoint> points() {
		return mPoints;
	}

	public boolean isLooped() {
		return mIsLooped;
	}

	public void isLooped(boolean pIsLooped) {
		mIsLooped = pIsLooped;
		calculateSplineLength();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Spline() {
		this(null);
	}

	public Spline(SplinePoint[] points) {
		if (points == null) {
			mPoints = new ArrayList<>();
		} else {
			mPoints = Arrays.asList(points);
		}

		mIsLooped = true;
		calculateSplineLength();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SplinePoint getControlPoint(int nodeIndex) {
		return mPoints.get(nodeIndex);
	}

	/*
	 * This returns a point on the spline as a whole. The integer part references a node on the spline, and the fraction part is the the distaince between this node and the next (the distance into the segment).
	 */
	public SplinePoint getPointOnSpline(float nodeIndexAndFraction) {
		int p0, p1, p2, p3;

		if (!mIsLooped) {
			p1 = (int) nodeIndexAndFraction + 1;
			p2 = p1 + 1;
			p3 = p2 + 1;
			p0 = p1 - 1;

		} else {
			p1 = (int) nodeIndexAndFraction;
			p2 = (p1 + 1) % mPoints.size();
			p3 = (p2 + 1) % mPoints.size();
			p0 = p1 >= 1 ? p1 - 1 : mPoints.size() - 1;
		}

		// fraction [0,1] - for between segments
		float lFraction = nodeIndexAndFraction - (int) nodeIndexAndFraction;

		float tt = lFraction * lFraction;
		float ttt = tt * lFraction;

		// influencial field values
		float q0 = -ttt + 2.f * tt - lFraction;
		float q1 = 3.f * ttt - 5.f * tt + 2.f;
		float q2 = -3.f * ttt + 4.f * tt + lFraction;
		float q3 = ttt - tt;

		float tx = 0.5f * (mPoints.get(p0).x * q0 + mPoints.get(p1).x * q1 + mPoints.get(p2).x * q2 + mPoints.get(p3).x * q3);
		float ty = 0.5f * (mPoints.get(p0).y * q0 + mPoints.get(p1).y * q1 + mPoints.get(p2).y * q2 + mPoints.get(p3).y * q3);

		mReturnSplinePoint.set(tx, ty);

		return mReturnSplinePoint;
	}

	public float getSplineGradient(float nodeIndexAndFraction) {
		final var lSplineGradient = getSplineGradientPoint(nodeIndexAndFraction);
		return (float) Math.atan2(lSplineGradient.y, lSplineGradient.x);
	}

	/*
	 * This returns a gradient at a point on the spline as a whole. The integer part references a node on the spline, and the fraction part is the the distaince between this node and the next (the distance into the segment).
	 */
	public SplinePoint getSplineGradientPoint(float nodeIndexAndFraction) {
		int p0, p1, p2, p3;

		if (!mIsLooped) {
			p1 = (int) nodeIndexAndFraction + 1;
			p2 = p1 + 1;
			p3 = p2 + 1;
			p0 = p1 - 1;

		} else {
			p1 = (int) nodeIndexAndFraction;
			p2 = (p1 + 1) % mPoints.size();
			p3 = (p2 + 1) % mPoints.size();
			p0 = p1 >= 1 ? p1 - 1 : mPoints.size() - 1;
		}

		// fraction [0,1] - for between segments
		float lFraction = nodeIndexAndFraction - (int) nodeIndexAndFraction;
		float tt = lFraction * lFraction;

		// influencial field values
		float q0 = -3.f * tt + 4.f * lFraction - 1.f;
		float q1 = 9.f * tt - 10.f * lFraction;
		float q2 = -9.f * tt + 8.f * lFraction + 1.f;
		float q3 = 3.f * tt - 2.f * lFraction;

		float tx = 0.5f * (mPoints.get(p0).x * q0 + mPoints.get(p1).x * q1 + mPoints.get(p2).x * q2 + mPoints.get(p3).x * q3);
		float ty = 0.5f * (mPoints.get(p0).y * q0 + mPoints.get(p1).y * q1 + mPoints.get(p2).y * q2 + mPoints.get(p3).y * q3);

		mReturnSplineGradient.set(tx, ty);

		return mReturnSplineGradient;
	}

	/*
	 * Returns the nodeIndex and fraction for a point at the give distance into the spline from the start.
	 */
	public float getNormalizedOffset(float distance) {
		int i = 0;
		while (distance > mPoints.get(i).length) {
			distance -= mPoints.get(i).length;
			i++;
		}

		return (float) i + (distance / mPoints.get(i).length);
	}

	public void recalculate() {
		calculateSplineLength();
	}

	/*
	 * Calculates the total length of the spline (only needed one per track-change
	 */
	public void calculateSplineLength() {
		mTotalSplineLength = 0;

		final int lIdOffset = mIsLooped ? 0 : 3;
		for (int i = 0; i <= mPoints.size() - lIdOffset - 1; i++) {
			final float lSegmentLength = calculateSegmentLength(i);
			mPoints.get(i).accLength = mTotalSplineLength;
			mTotalSplineLength += lSegmentLength;
		}
	}

	/*
	 * Calculates the length of the segment at the given node index.
	 */
	public float calculateSegmentLength(int nodeIndex) {
		nodeIndex %= mPoints.size();

		float lLength = 0f;
		float lStepSize = 0.005f;

		final SplinePoint lOldPoint = new SplinePoint();
		final SplinePoint lNewPoint = new SplinePoint();
		lOldPoint.set(getPointOnSpline((float) nodeIndex));

		for (float t = 0; t < 1f - lStepSize; t += lStepSize) {
			lNewPoint.set(getPointOnSpline((float) nodeIndex + t));

			lLength += Math.sqrt((lNewPoint.x - lOldPoint.x) * (lNewPoint.x - lOldPoint.x) + (lNewPoint.y - lOldPoint.y) * (lNewPoint.y - lOldPoint.y));

			lOldPoint.set(lNewPoint);
		}
		mPoints.get(nodeIndex).length = lLength;

		return lLength;
	}

	public float getNormalizedPositionAlongSpline(int fromNode, float positionX, float positionY) {
		final var lControlPoint0 = getControlPoint(fromNode);

		final int lNextNodeId = fromNode >= numberSplineControlPoints() - 1 ? 0 : fromNode + 1;
		final var lControlPoint1 = getControlPoint(lNextNodeId);

		float lVectorBetweenX = lControlPoint1.x - lControlPoint0.x;
		float lVectorBetweenY = lControlPoint1.y - lControlPoint0.y;

		Vec2 v1 = new Vec2(lVectorBetweenX, lVectorBetweenY);
		Vec2 v2 = new Vec2(positionX - lControlPoint0.x, positionY - lControlPoint0.y);

		float veLength = v1.normalize();
		float lResult = Vector2f.dot(v1.x, v1.y, v2.x, v2.y) / veLength;

		return MathHelper.clamp(lResult, 0.f, 1.f);
	}
}
