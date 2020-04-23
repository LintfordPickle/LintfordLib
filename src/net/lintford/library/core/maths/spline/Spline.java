package net.lintford.library.core.maths.spline;

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
		calculateSegmentLength();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Spline() {
		this(null);
	}

	public Spline(SplinePoint[] pPoints) {
		if (pPoints == null) {
			mPoints = new ArrayList<>();
		} else {
			mPoints = Arrays.asList(pPoints);

		}

		calculateSegmentLength();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SplinePoint getControlPoint(int pIndex) {
		return mPoints.get(pIndex);
	}

	public SplinePoint getPointOnSpline(float t) {
		int p0, p1, p2, p3;

		if (!mIsLooped) {
			p1 = (int) t + 1;
			p2 = p1 + 1;
			p3 = p2 + 1;
			p0 = p1 - 1;

		} else {
			p1 = (int) t;
			p2 = (p1 + 1) % mPoints.size();
			p3 = (p2 + 1) % mPoints.size();
			p0 = p1 >= 1 ? p1 - 1 : mPoints.size() - 1;
		}

		t = t - (int) t;

		float tt = t * t;
		float ttt = tt * t;

		// influencial field values
		float q0 = -ttt + 2.f * tt - t;
		float q1 = 3.f * ttt - 5.f * tt + 2.f;
		float q2 = -3.f * ttt + 4.f * tt + t;
		float q3 = ttt - tt;

		float tx = 0.5f * (mPoints.get(p0).x * q0 + mPoints.get(p1).x * q1 + mPoints.get(p2).x * q2 + mPoints.get(p3).x * q3);
		float ty = 0.5f * (mPoints.get(p0).y * q0 + mPoints.get(p1).y * q1 + mPoints.get(p2).y * q2 + mPoints.get(p3).y * q3);

		mReturnSplinePoint.set(tx, ty);

		return mReturnSplinePoint;
	}

	public SplinePoint getSplineGradient(float t) {
		int p0, p1, p2, p3;

		if (!mIsLooped) {
			p1 = (int) t + 1;
			p2 = p1 + 1;
			p3 = p2 + 1;
			p0 = p1 - 1;

		} else {
			p1 = (int) t;
			p2 = (p1 + 1) % mPoints.size();
			p3 = (p2 + 1) % mPoints.size();
			p0 = p1 >= 1 ? p1 - 1 : mPoints.size() - 1;
		}

		t = t - (int) t;

		float tt = t * t;

		// influencial field values
		float q0 = -3.f * tt + 4.f * t - 1;
		float q1 = 9.f * tt - 10.f * t;
		float q2 = -9.f * tt + 8.f * t + 1.f;
		float q3 = 3.f * tt - 2.f * t;

		float tx = 0.5f * (mPoints.get(p0).x * q0 + mPoints.get(p1).x * q1 + mPoints.get(p2).x * q2 + mPoints.get(p3).x * q3);
		float ty = 0.5f * (mPoints.get(p0).y * q0 + mPoints.get(p1).y * q1 + mPoints.get(p2).y * q2 + mPoints.get(p3).y * q3);

		mReturnSplineGradient.set(tx, ty);

		return mReturnSplineGradient;
	}

	public float getNormalizedOffset(float pDistance) {
		int i = 0;
		while (pDistance > mPoints.get(i).length) {
			pDistance -= mPoints.get(i).length;
			i++;
		}

		return (float) i + (pDistance / mPoints.get(i).length);

	}

	public void calculateSegmentLength() {
		mTotalSplineLength = 0;
		
		final int lIdOffset = mIsLooped ? 0 : 3;
		for (int i = 0; i < mPoints.size() - lIdOffset - 1; i++) {
			final float lSegmentLength = calculateSegmentLength(i);
			mPoints.get(i).accLength = mTotalSplineLength;
			mTotalSplineLength += lSegmentLength;
		}

	}

	public float calculateSegmentLength(int pNode) {
		float lLength = 0f;
		float lStepSize = 0.005f;

		final SplinePoint lOldPoint = new SplinePoint();
		final SplinePoint lNewPoint = new SplinePoint();
		lOldPoint.set(getPointOnSpline((float) pNode));

		for (float t = 0; t < 1f; t += lStepSize) {
			lNewPoint.set(getPointOnSpline((float) pNode + t));

			lLength += Math.sqrt((lNewPoint.x - lOldPoint.x) * (lNewPoint.x - lOldPoint.x) + (lNewPoint.y - lOldPoint.y) * (lNewPoint.y - lOldPoint.y));

			lOldPoint.set(lNewPoint);

		}
		mPoints.get(pNode).length = lLength;

		return lLength;

	}

	public static float catmullRom(float value1, float value2, float value3, float value4, float amount) {
		float num = amount * amount;
		float num2 = amount * num;
		return (0.5f * ((((2f * value2) + ((-value1 + value3) * amount)) + (((((2f * value1) - (5f * value2)) + (4f * value3)) - value4) * num)) + ((((-value1 + (3f * value2)) - (3f * value3)) + value4) * num2)));
	}

	public float getNormalizedPositionAlongSpline(int pFromNode, float pPosX, float pPosY) {

		final var lControlPoint0 = getControlPoint(pFromNode);

		final int lNextNodeId = pFromNode >= numberSplineControlPoints() - 1 ? 0 : pFromNode + 1;
		final var lControlPoint1 = getControlPoint(lNextNodeId);

		float lVectorBetweenX = lControlPoint1.x - lControlPoint0.x;
		float lVectorBetweenY = lControlPoint1.y - lControlPoint0.y;

		Vec2 v1 = new Vec2(lVectorBetweenX, lVectorBetweenY);
		Vec2 v2 = new Vec2(pPosX - lControlPoint0.x, pPosY - lControlPoint0.y);

		float veLength = v1.normalize();
		float lResult = Vector2f.dot(v1.x, v1.y, v2.x, v2.y) / veLength;

		return MathHelper.clamp(lResult, 0.f, 1.f);
	}

}
