package net.lintfordlib.core.maths;

import net.lintfordlib.ConstantsMath;

public class MathHelper {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final float RadiansPos90 = (float) Math.toRadians(90);
	public static final float RadiansMinus90 = (float) Math.toRadians(-90);

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static float clamp(float value, float min, float max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}
	
	public static double clampd(double value, double min, double max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}

	public static int clampi(int value, int min, int max) {
		value = (value > max) ? max : value;
		value = (value < min) ? min : value;
		return value;
	}

	public static float distance(float value1, float value2) {
		return Math.abs((value1 - value2));
	}

	public static float distance2(float pX1, float pY1, float pX2, float pY2) {
		float lengthX = (pX2 - pX1) * (pX2 - pX1);
		float lengthY = (pY2 - pY1) * (pY2 - pY1);
		return lengthX + lengthY;

	}

	public static float distance(float pX1, float pY1, float pX2, float pY2) {
		return (float) Math.sqrt(distance2(pX1, pY1, pX2, pY2));

	}

	public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static float min(float a, float b) {
		return a < b ? a : b;
	}

	public static final int floor(float f) {
		return f >= 0 ? (int) f : (int) f - 1;
	}

	public static final int round(float f) {
		return f >= 0 ? (int) (f + 0.5f) : (int) (f - 0.5f);
	}

	public static final float abs(float f) {
		return f < 0 ? -f : f;
	}

	public static float toDegrees(float radians) {
		return (radians * 57.29578f);
	}

	public static float toRadians(float degrees) {
		return (degrees * 0.01745329f);
	}

	public static float wrapAngle(float angle) {
		angle = (float) Math.IEEEremainder(angle, Math.PI * 2.);
		if (angle <= -Math.PI) {
			angle += Math.PI * 2.;
			return angle;
		}
		if (angle > Math.PI) {
			angle -= Math.PI * 2.;
		}
		return angle;
	}

	public static final boolean isEven(final int pV) {
		return pV % 2 == 0;
	}

	public static final boolean isOdd(final int pV) {
		return pV % 2 == 1;
	}

	public static float dot(final float pXA, final float pYA, final float pXB, final float pYB) {
		return pXA * pXB + pYA * pYB;
	}

	public static float cross(final float pXA, final float pYA, final float pXB, final float pYB) {
		return pXA * pYB - pXB * pYA;
	}

	public static float scaleToRange(final float oldValue, final float oldMin, final float oldMax, final float newMin, final float newMax) {
		if (oldMax == oldMin)
			return 1.f;

		float oldRange = oldMax - oldMin;
		float newRange = newMax - newMin;

		float newValue = ((oldValue - oldMin) * newRange / oldRange) + newMin;

		return clamp(newValue, newMin, newMax);
	}

	public static float round(float value, int precision) {
		final var lScale = (int) Math.pow(10, precision);
		return (float) Math.round(value * lScale) / lScale;
	}

	/**
	 * Inverts the given angle (in radians) over the X axis
	 */
	public static float invertAngleXAxis(float angleInRadians) {
		float lAngle = normalizeAngle(angleInRadians);
		if (lAngle == 0)
			return 0.f;
		lAngle = ConstantsMath.TwoPi - lAngle;

		return lAngle;
	}

	/**
	 * Inverts the given angle (in radians) over the Y axis
	 */
	public static float invertAngleYAxis(float angleInRadians) {
		float lAngle = normalizeAngle(angleInRadians);

		if (lAngle < ConstantsMath.Pi) {
			lAngle = ConstantsMath.Pi - lAngle;
		} else {
			lAngle = ConstantsMath.TwoPi - lAngle + ConstantsMath.Pi;
		}

		return lAngle;
	}

	/***
	 * Normalizes the angle to within [0, TwoPi]
	 * 
	 * @param angle The angle to be normalized.
	 * @return The new angle scaled to within [0, TwoPi]
	 */
	public static float normalizeAngle(float angle) {
		if (angle < 0) {
			final var lBackRevolutions = (int) (-angle / ConstantsMath.TwoPi);
			return angle + ConstantsMath.TwoPi * (lBackRevolutions + 1);
		}

		return angle % ConstantsMath.TwoPi;

	}

	public static float turnToFace(float positionX, float positionY, float faceThisX, float faceThisY, float currentAngle, float turnSpeed) {
		final var lWorldX = faceThisX - positionX;
		final var lWorldY = faceThisY - positionY;

		final var lDesiredAngle = (float) Math.atan2(lWorldY, lWorldX);
		final var lDifference = clamp(wrapAngle(lDesiredAngle - currentAngle), -turnSpeed, turnSpeed);

		return wrapAngle(currentAngle + lDifference);
	}

	// polygon

	public static boolean isCcwWinding(Vector2f a, Vector2f b, Vector2f c) {
		return !isCwWinding(a, b, c);
	}

	public static boolean isCwWinding(Vector2f a, Vector2f b, Vector2f c) {
		final var aXLen = Vector2f.dst2(a.x, a.y, b.x, b.y);
		if (aXLen == 0)
			return false;

		final var aXx = (b.x - a.x) / aXLen;
		final var aXy = (b.y - a.y) / aXLen;

		final var aYLen = Vector2f.dst2(a.x, a.y, c.x, c.y);
		if (aYLen == 0)
			return false;

		final var aYx = (c.x - a.x) / aYLen;
		final var aYy = (c.y - a.y) / aYLen;

		return Vector2f.cross(aXx, aXy, aYx, aYy) > 0;
	}

	/** returns true if the order of the vertices are Ccw, otherwise false */
	public static boolean isTriangleCcw(float ax, float ay, float bx, float by, float cx, float cy) {
		final float abx = cx - ax;
		final float aby = cy - ay;

		final float acx = bx - ax;
		final float acy = by - ay;

		float z = windingOrderZFromCross(abx, aby, acx, acy);
		return z > 0;
	}

	public static float windingOrderZFromCross(float ax, float ay, float bx, float by) {
		return (ax * by) - (ay * bx);
	}

	// ---

	public static boolean withinEpsilon(float a) {
		return Math.abs(a) < ConstantsMath.EPSILON;
	}
	
	public static boolean equalWithinEpsilon(float a, float b) {
		return Math.abs(a - b) < ConstantsMath.EPSILON;
	}

	public static boolean equalWithinEpsilon(float p1x, float p1y, float p2x, float p2y) {
		final float xx = p1x - p2x;
		final float yy = p1y - p2y;
		return (xx * xx + yy * yy) < ConstantsMath.EPSILON * ConstantsMath.EPSILON;
	}
}
