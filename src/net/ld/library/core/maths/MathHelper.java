package net.ld.library.core.maths;

public class MathHelper {

	public static float catmullRom(float value1, float value2, float value3, float value4, float amount) {
		float num = amount * amount;
		float num2 = amount * num;
		return (0.5f * ((((2f * value2) + ((-value1 + value3) * amount))
				+ (((((2f * value1) - (5f * value2)) + (4f * value3)) - value4) * num))
				+ ((((-value1 + (3f * value2)) - (3f * value3)) + value4) * num2)));
	}

	/** Clamps the value within the given range. */
	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public static float distance(float value1, float value2) {
		return Math.abs((value1 - value2));
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

	public static float smoothStep(float value1, float value2, float amount) {
		float returnValue = MathHelper.clamp(amount, 0f, 1f);
		returnValue = MathHelper.hermite(value1, 0f, value2, 0f, amount);

		return returnValue;
	}

	public static float lerp(float value1, float value2, float amount) {
		return (value1 + ((value2 - value1) * amount));
	}

	public static float max(float value1, float value2) {
		return Math.max(value1, value2);
	}

	public static float min(float value1, float value2) {
		return Math.min(value1, value2);
	}

	public static float toDegrees(float radians) {
		return (radians * 57.29578f);
	}

	public static float toRadians(float degrees) {
		return (degrees * 0.01745329f);
	}

	public static float wrapAngle(float angle) {
		angle = (float) Math.IEEEremainder(angle, 6.2831854820251465);
		if (angle <= -3.141593f) {
			angle += 6.283185f;
			return angle;
		}
		if (angle > 3.141593f) {
			angle -= 6.283185f;
		}
		return angle;
	}

}
