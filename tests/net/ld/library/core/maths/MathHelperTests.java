package net.ld.library.core.maths;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathHelperTests {

	// Missing static method tests:
	//   Hermite
	//   Catmullrom spline
	//   SmoothStep

	@Test
	public void wrapAngleTest() {
		// Arrange
		final float LOW_OOB_ANGLE = MathHelper.toRadians(-370);
		final float LOW_ANGLE = MathHelper.toRadians(-100);
		final float HIGH_ANGLE = MathHelper.toRadians(120);
		final float HIGH_OOB_ANGLE = MathHelper.toRadians(380);

		// Act

		// Assert
		assertEquals(MathHelper.wrapAngle(LOW_OOB_ANGLE), MathHelper.toRadians(-10), 0.001f);
		assertEquals(MathHelper.wrapAngle(LOW_ANGLE), MathHelper.toRadians(-100), 0.001f);
		assertEquals(MathHelper.wrapAngle(HIGH_ANGLE), MathHelper.toRadians(120), 0.001f);
		assertEquals(MathHelper.wrapAngle(HIGH_OOB_ANGLE), MathHelper.toRadians(20), 0.001f);
		
	}

	@Test
	public void clampTest() {
		assert (MathHelper.clamp(0.4f, 0.0f, 1.0f) == 0.4f) : "Clamp wrong return (inner range)";
		assert (MathHelper.clamp(3.4f, 2.0f, 4.0f) == 3.4f) : "Clamp wrong return (inner range)";

		assert (MathHelper.clamp(-3.4f, -4.0f, -2.0f) == -3.4f) : "Clamp wrong return (.neg. inner range)";

		assert (MathHelper.clamp(1.4f, 2.0f, 4.0f) == 2.0f) : "Clamp wrong return (low outer range)";
		assert (MathHelper.clamp(4.4f, 2.0f, 4.0f) == 4.0f) : "Clamp wrong return (high outer range)";

		assert (MathHelper.clamp(-1.4f, -4.0f, -2.0f) == -2.0f) : "Clamp wrong return (neg. low outer range)";
		assert (MathHelper.clamp(-4.4f, -4.0f, -2.0f) == -4.0f) : "Clamp wrong return (neg. high outer range)";

	}

	@Test
	public void distanceTest() {
		assert (MathHelper.distance(0, 0) == 0) : "Distance incorrect";
		assert (MathHelper.distance(150, 150) == 0) : "Distance incorrect";

		assert (MathHelper.distance(100, 200) == 100) : "Distance incorrect";

		assert (MathHelper.distance(-100, 200) == 300) : "Distance incorrect";
		assert (MathHelper.distance(200, -200) == 400) : "Distance incorrect";

		assert (MathHelper.distance(-100, -200) == 100) : "Distance incorrect";
	}

	@Test
	public void lerpTest() {
		assert (MathHelper.lerp(0.0f, 10.0f, 0.1f) == 1.0f) : "lerp not working";
		assert (MathHelper.lerp(-0.0f, -10.0f, 0.1f) == -1.0f) : "lerp not working";

	}

	@Test
	public void maxTest() {
		assert (MathHelper.max(120.1f, 101.2f) == 120.1f) : "Max returned incorrect version";
		assert (MathHelper.max(101.2f, 120.1f) == 120.1f) : "Max returned incorrect version";

		assert (MathHelper.max(-120.1f, 101.2f) == 101.2f) : "Max returned incorrect version";
		assert (MathHelper.max(-120.1f, -101.2f) == -101.2f) : "Max returned incorrect version";

		assert (MathHelper.max(2f, 2f) == 2f) : "Max returned incorrect version";

	}

	@Test
	public void minTest() {
		assert (MathHelper.min(120.1f, 101.2f) == 101.2f) : "Max returned incorrect version";
		assert (MathHelper.min(101.2f, 120.1f) == 101.2f) : "Max returned incorrect version";

		assert (MathHelper.min(-120.1f, 101.2f) == -120.1f) : "Max returned incorrect version";
		assert (MathHelper.min(-120.1f, -101.2f) == -120.1f) : "Max returned incorrect version";

		assert (MathHelper.min(2f, 2f) == 2f) : "Max returned incorrect version";

	}

}
