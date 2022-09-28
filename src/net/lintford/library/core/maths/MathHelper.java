package net.lintford.library.core.maths;

public class MathHelper {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final float RadiansPos90 = (float) Math.toRadians(90);
	public static final float RadiansMinus90 = (float) Math.toRadians(-90);

	// --------------------------------------
	// Methods
	// --------------------------------------

	public final static float weight(float T) {
		return ((2.0f * Math.abs(T) - 3.0f) * (T) * (T) + 1.0f);
	}

	public static float clamp(float value, float min, float max) {
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

	public static float lerp(float value1, float value2, float amount) {
		return (value1 + ((value2 - value1) * amount));
	}

	public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static float min(float a, float b) {
		return a < b ? a : b;
	}

	public static final float bias(float b, float t) {
		return (float) Math.pow(t, Math.log(b) / Math.log(0.5));
	}

	public static final float pingPong(float t) {
		t -= (int) (t * 0.5f) * 2;
		return t < 1 ? t : 2 - t;
	}

	public static final int floor(float f) {
		return f >= 0 ? (int) f : (int) f - 1;
	}

	public static final int round(float f) {
		return f >= 0 ? (int) (f + 0.5f) : (int) (f - 0.5f);
	}

	public static final float interpHermite(float t) {
		return t * t * (3 - 2 * t);
	}

	public static final float interpQuintic(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	public static final float cubicLerp(float a, float b, float c, float d, float t) {
		float p = (d - c) - (a - b);
		return t * t * t * p + t * t * ((a - b) - p) + t * (c - a) + b;
	}

	public static final float abs(float f) {
		return f < 0 ? -f : f;
	}

	public static float mix(float x, float y, float a) {
		return x * (1.f - a) + y * a;
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

	public static float bezier4CurveTo(float t, float p0, float p1, float p2, float p3) {
		float c = 3 * (p1 - p0);
		float b = 3 * (p2 - p1) - c;
		float a = p3 - p0 - c - b;

		float cube = t * t * t;
		float square = t * t;

		return (a * cube) + (b * square) + (c * t) + p0;
	}

	public static float bezier3CurveTo(float t, float p0, float p1, float p2) {
		float u = 1 - t;
		float tt = t * t;
		float uu = u * u;

		float p = uu * p0; // first term
		p += 2 * u * t * p1; // second term
		p += tt * p2; // third term

		return p;
	}

	/**
	 * Gradually changes a value towards a desired goal over time.
	 * 
	 * @param current         The current position
	 * @param target          The position we are trying to reach
	 * @param currentVelocity The current velocity (ref set by this method)
	 * @param smoothTime      Approximately the time it will take to reach the target. Smaller values will reach the target faster
	 * @param maxSpeed        optionally allows you to clamp the maximum speed
	 * @param deltaTime       The time since the last call to this method.
	 * @return The new position
	 */
	public static float SmoothDamp(float current, float target, float currentVelocity, float smoothTime, float maxSpeed, float deltaTime) {
		smoothTime = Math.max(0.0001f, smoothTime);
		float num = 2f / smoothTime;
		float num2 = num * deltaTime;
		float num3 = 1f / (1f + num2 + 0.48f * num2 * num2 + 0.235f * num2 * num2 * num2);
		float num4 = current - target;
		float num5 = target;
		float num6 = maxSpeed * smoothTime;
		num4 = clamp(num4, -num6, num6);
		target = current - num4;
		float num7 = (currentVelocity + num * num4) * deltaTime;
		currentVelocity = (currentVelocity - num * num7) * num3;
		float num8 = target + (num4 + num7) * num3;
		if (num5 - current > 0f == num8 > num5) {
			num8 = num5;
			currentVelocity = (num8 - num5) / deltaTime;
		}
		return num8;
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

	/** A fade function, taken from Ken Perlin's noise implementation. It eases values towards integrals */
	public static double fade(double value) {
		return value * value * value * (value * (value * 6 - 15) + 10);
	}

	// simple linear tweening - no easing, no acceleration
	public static double linearInOut(float time, float start, float changeInValue, float duration) {
		return changeInValue * time / duration + start;
	};

	// sinusoidal easing in/out - accelerating until halfway, then decelerating
	public static double sinusoidalInOut(float time, float start, float changeInValue, float duration) {
		return -changeInValue / 2 * (Math.cos(Math.PI * time / duration) - 1) + start;
	};

	public static float round(float value, int precision) {
		final var lScale = (int) Math.pow(10, precision);
		return (float) Math.round(value * lScale) / lScale;
	}

	public static float bellCurve(float normalizedValue) {
		float lBellCurveModifyAmt = .125f;
		float lValue = normalizedValue;
		lValue *= 2f + (lBellCurveModifyAmt * 2);
		lValue -= 1f + lBellCurveModifyAmt;
		lValue = MathHelper.clamp(1.0f - Math.abs(lValue), 0, 1);

		return lValue;
	}

	/**
	 * Inverts the given angle (in radians) over the X axis
	 */
	public static float invertAngleXAxis(float angleInRadians) {
		float lAngle = normalizeAngle(angleInRadians);
		if (lAngle == 0)
			return 0.f;
		lAngle = MathConstants.TwoPi - lAngle;

		return lAngle;
	}

	/**
	 * Inverts the given angle (in radians) over the Y axis
	 */
	public static float invertAngleYAxis(float angleInRadians) {
		float lAngle = normalizeAngle(angleInRadians);

		if (lAngle < MathConstants.Pi) {
			lAngle = MathConstants.Pi - lAngle;
		} else {
			lAngle = MathConstants.TwoPi - lAngle + MathConstants.Pi;
		}

		return lAngle;
	}

	/**
	 * Brings the angle within [0, TwoPi]
	 */
	public static float normalizeAngle(float angle) {
		if (angle < 0) {
			final var lBackRevolutions = (int) (-angle / MathConstants.TwoPi);
			return angle + MathConstants.TwoPi * (lBackRevolutions + 1);
		} else {
			return angle % MathConstants.TwoPi;
		}
	}

	public static float turnToFace(float positionX, float positionY, float faceThisX, float faceThisY, float currentAngle, float turnSpeed) {
		final var lWorldX = faceThisX - positionX;
		final var lWorldY = faceThisY - positionY;

		final var lDesiredAngle = (float) Math.atan2(lWorldY, lWorldX);
		final var lDifference = clamp(wrapAngle(lDesiredAngle - currentAngle), -turnSpeed, turnSpeed);

		return wrapAngle(currentAngle + lDifference);
	}
}
