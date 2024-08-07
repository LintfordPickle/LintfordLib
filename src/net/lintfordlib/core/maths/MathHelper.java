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
		final float lengthX = (pX2 - pX1) * (pX2 - pX1);
		final float lengthY = (pY2 - pY1) * (pY2 - pY1);
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

	public static float wrapAngle(float angleInRadians) {

		if ((angleInRadians > -Math.PI) && (angleInRadians <= Math.PI))
			return angleInRadians;

		angleInRadians = (float) Math.IEEEremainder(angleInRadians, Math.PI * 2.);

		if (angleInRadians <= -Math.PI)
			return (float) (angleInRadians + Math.PI * 2.);

		if (angleInRadians > Math.PI)
			return (float) (angleInRadians - Math.PI * 2.);

		return angleInRadians;

	}

	public static final boolean isEven(int pV) {
		return pV % 2 == 0;
	}

	public static final boolean isOdd(int pV) {
		return (pV & 1) == 1;
	}

	public static float dot(float pXA, float pYA, float pXB, float pYB) {
		return pXA * pXB + pYA * pYB;
	}

	// @formatter:off
	/**
	 * Returns the magnitude of the vector that would result from a regular 3D cross product of the 
	 * input vectors, taking their Z values implicitly as 0 (i.e. treating the 2D space as a plane 
	 * in the 3D space). The 3D cross product will be perpendicular to that plane, and thus have 
	 * 0 X & Y components (thus the scalar returned is the Z value of the 3D cross product vector).
	 * 
	 * Note that the magnitude of the vector resulting from 3D cross product is also equal to the area 
	 * of the parallelogram between the two vectors, which gives Implementation 1 another purpose. 
	 * In addition, this area is signed and can be used to determine whether rotating from V1 to V2 
	 * moves in an counter clockwise or clockwise direction.
	 */
	// @formatter:on
	public static float cross(float v0_x, float v0_y, float v1_x, float v1_y) {
		return v0_x * v1_y - v1_x * v0_y;
	}

	public static float scaleToRange(float oldValue, float oldMin, float oldMax, float newMin, float newMax) {
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
		if (angleInRadians == 0)
			return 0.f;

		return normalizeAngle(ConstantsMath.TwoPi - angleInRadians);
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

	/**
	 * Returns a new angle between the heading vector (faceThis - position) and the current angle, taking into consideration a maximum turn amount.
	 */
	public static float turnToFace(float positionX, float positionY, float faceThisX, float faceThisY, float currentAngle, float turnSpeed) {
		final var lDirX = faceThisX - positionX;
		final var lDirY = faceThisY - positionY;

		final var lDesiredAngle = (float) Math.atan2(lDirY, lDirX);
		final var lDifference = clamp(wrapAngle(lDesiredAngle - currentAngle), -turnSpeed, turnSpeed);

		return wrapAngle(currentAngle + lDifference);
	}

	/**
	 * Returns an amount to adjust the an angle by towards moving it to the target angle.
	 */
	public static float turnToFace(float trackHeading, float currentAngle, float turnSpeed) {
		float difference = wrapAngle(trackHeading - currentAngle);

		difference = MathHelper.clamp(difference, -turnSpeed, turnSpeed);

		return wrapAngle(difference);
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

		return cross(abx, aby, acx, acy) > 0;
	}

	// ---

	/** Checks the equality of two given values to within epsilon. */
	public static boolean equalWithinEpsilon(float a, float b) {
		return Math.abs(a - b) < ConstantsMath.EPSILON;
	}

	/** Checks the spatial equality of two given vectors to within epsilon. */
	public static boolean equalWithinEpsilon(float p1x, float p1y, float p2x, float p2y) {
		final float xx = p1x - p2x;
		final float yy = p1y - p2y;
		return (xx * xx + yy * yy) < ConstantsMath.EPSILON * ConstantsMath.EPSILON;
	}
}
