package net.ld.library.core.maths;

public class MathHelper {

	/** A catmulrom interpolation method. */
	public static float catmullRom(float value1, float value2, float value3, float value4, float amount) {
		float num = amount * amount;
		float num2 = amount * num;
		return (0.5f * ((((2f * value2) + ((-value1 + value3) * amount)) + (((((2f * value1) - (5f * value2)) + (4f * value3)) - value4) * num)) + ((((-value1 + (3f * value2)) - (3f * value3)) + value4) * num2)));
	}

	/** A hermite spline interpolation method */
	public static float hermite(float value1, float tangent1, float value2, float tangent2, float amount) {
		float num3 = amount;
		float num = num3 * num3;
		float num2 = num3 * num;
		float num7 = ((2f * num2) - (3f * num)) + 1f;
		float num6 = (-2f * num2) + (3f * num);
		float num5 = (num2 - (2f * num)) + num3;
		float num4 = num2 - num;
		return ((((value1 * num7) + (value2 * num6)) + (tangent1 * num5)) + (tangent2 * num4));
	}

	/** A smooth interpolation method using the hermite spline. */
	public static float smoothStep(float value1, float value2, float amount) {
		float returnValue = MathHelper.clamp(amount, 0f, 1f);
		returnValue = MathHelper.hermite(value1, 0f, value2, 0f, amount);

		return returnValue;
	}

	/**
	 * Clamps the value within the given range.
	 * 
	 * @param pValue
	 *            the value to be clamped
	 * @param pMin
	 *            the minimum bound
	 * @param pMax
	 *            the maximum bound
	 * @return the new value clamped between pMin and pMax
	 */
	public static float clamp(float pValue, float pMin, float pMax) {
		return Math.max(pMin, Math.min(pMax, pValue));
	}

	/**
	 * Returns the distance between two points.
	 * 
	 * @param pValue1
	 *            the first point
	 * @param pValue2
	 *            the second point
	 * @return the distance between the points
	 */
	public static float distance(float value1, float value2) {
		return Math.abs((value1 - value2));
	}

	/**
	 * linearly interpolates between value1 and value2 by the given amount.
	 * 
	 * @param pMinValue
	 *            the starting bound
	 * @param pMaxValue
	 *            the ending bound
	 * @param pAmount
	 *            The normalized [0,1] amount to interpolate between the values by
	 * 
	 * @return the interpolated value
	 */
	public static float lerp(float pMinValue, float pMaxValue, float pAmount) {
		return (pMinValue + ((pMaxValue - pMinValue) * pAmount));
	}

	/** Returns the higher value of the two given values. */
	public static float max(float value1, float value2) {
		return Math.max(value1, value2);
	}

	/** Returns the lower value of the two given values. */
	public static float min(float value1, float value2) {
		return Math.min(value1, value2);
	}

	/**
	 * Converts a given radian angle into a degree angle
	 * 
	 * @param pRadians
	 *            the angle in degrees to be converted to radians.
	 * @return The angle in radians
	 */
	public static float toDegrees(float pRadians) {
		// π rad = 180°
		// 1 rad = 180°/π = 57.295779513°
		return (float) (pRadians * (180 / Math.PI));
	}

	/**
	 * Converts a given degree angle into a radian angle
	 * 
	 * @param pDegrees
	 *            the angle in degrees to be converted to radians.
	 * @return The angle in radians
	 */
	public static float toRadians(float pDegrees) {
		return (pDegrees * 0.01745329f);
	}

	/**
	 * Wraps the given angle around a circle and returns the new, normalized value.
	 * 
	 * @param pAngle
	 *            the angle to wrap around a circle, in radians
	 * @return The normalized angle in radians in the range [-2PI, 2PI]
	 */
	public static float wrapAngle(float pAngle) {
		pAngle = (float) Math.IEEEremainder(pAngle, Math.PI * 2);
		if (pAngle <= -Math.PI) {
			pAngle += Math.PI * 2;
			return pAngle;
		}
		if (pAngle > Math.PI) {
			pAngle -= Math.PI * 2;
		}
		return pAngle;
	}

	/** Scales the given value from one range into another range */
	public static float scaleToRange(final float oldValue, final float oldMin, final float oldMax, final float newMin, final float newMax) {
		float oldRange = oldMax - oldMin;
		float newRange = newMax - newMin;

		float newValue = ((oldValue - oldMin) * newRange / oldRange) + newMin;
		return newValue;
	}

	public static float fade(float t) { return t * t * t * (t * (t * 6 - 15) + 10); }
	
}
