package net.lintfordlib.core.maths;

public class InterpolationHelper {

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

	// amount is normalized amount [0, 1]
	public static float lerp(float value1, float value2, float amount) {
		return (value1 + ((value2 - value1) * amount));
	}

	public static final float weight(float t) {
		return ((2.0f * Math.abs(t) - 3.0f) * (t) * (t) + 1.0f);
	}

	public static float mix(float x, float y, float a) {
		return x * (1.f - a) + y * a;
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
	public static float smoothDamp(float current, float target, float currentVelocity, float smoothTime, float maxSpeed, float deltaTime) {
		smoothTime = Math.max(0.0001f, smoothTime);
		float num = 2f / smoothTime;
		float num2 = num * deltaTime;
		float num3 = 1f / (1f + num2 + 0.48f * num2 * num2 + 0.235f * num2 * num2 * num2);
		float num4 = current - target;
		float num5 = target;
		float num6 = maxSpeed * smoothTime;
		num4 = MathHelper.clamp(num4, -num6, num6);
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

	public static float catmullRom(float value1, float value2, float value3, float value4, float amount) {
		float num = amount * amount;
		float num2 = amount * num;
		return (0.5f * ((((2f * value2) + ((-value1 + value3) * amount)) + (((((2f * value1) - (5f * value2)) + (4f * value3)) - value4) * num)) + ((((-value1 + (3f * value2)) - (3f * value3)) + value4) * num2)));
	}

	/***
	 * Hermite interpolation
	 */
	public static float hermite(float t) {
		return t * t * (3 - 2 * t);
	}

	/***
	 * A fade function that eases values towards integrals.
	 */
	public static float quintic(float t) {
		return t * t * t * (t * (t * 6.f - 15.f) + 10.f);
	}

	/***
	 * A simple linear tweening - no easing, no acceleration.
	 */
	public static float linearInOut(float time, float start, float changeInValue, float duration) {
		return changeInValue * time / duration + start;
	};

	/***
	 * Sinusoidal easing in/out - accelerating until halfway, then decelerating.
	 */
	public static float sinusoidalInOut(float time, float start, float changeInValue, float duration) {
		return -changeInValue / 2 * (float) (Math.cos(Math.PI * time / duration) - 1) + start;
	};

	public static final float cubic(float a, float b, float c, float d, float t) {
		float p = (d - c) - (a - b);
		return t * t * t * p + t * t * ((a - b) - p) + t * (c - a) + b;
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

	public static float bellCurve(float normalizedValue) {
		float lBellCurveModifyAmt = .125f;
		float lValue = normalizedValue;
		lValue *= 2f + (lBellCurveModifyAmt * 2);
		lValue -= 1f + lBellCurveModifyAmt;
		lValue = MathHelper.clamp(1.0f - Math.abs(lValue), 0, 1);

		return lValue;
	}

	public static float bias(float b, float t) {
		return (float) Math.pow(t, Math.log(b) / Math.log(0.5));
	}

	public static float pingPong(float t) {
		t -= (int) (t * 0.5f) * 2;
		return t < 1 ? t : 2 - t;
	}

}
