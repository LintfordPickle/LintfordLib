package net.lintfordlib.core.maths;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MathHelperTests {

	@Test
	void clampi_ChecksIntegerValueAgainstLowerAndUpperBound_ShouldReturnTheUnmodifiedInputValue() {
		// arrange
		final int low = 2;
		final int high = 18;
		final int value = 10;

		// act
		final int result = MathHelper.clampi(value, low, high);

		// assert
		assertEquals(value, result, "clampi didn't return the input value as expected.");
	}

	@Test
	void clampi_ChecksIntegerValueAgainstTheLowerBound_ShouldClampToLowerBound() {
		// arrange
		final int low = 2;
		final int high = 18;
		final int value = -4;

		// act
		final int result = MathHelper.clampi(value, low, high);

		// assert
		assertEquals(low, result, "clampi failed to clamp the input to the lower bound.");
	}

	@Test
	void clampi_ChecksIntegerValueAgainstTheUpperBound_ShouldClampToUpperBound() {
		// arrange
		final int low = 2;
		final int high = 18;
		final int value = 22;

		// act
		final int result = MathHelper.clampi(value, low, high);

		// assert
		assertEquals(high, result, "clampi failed to clamp the input to the upper bound.");
	}

	// --

	@Test
	void clamp_ChecksValueAgainstLowerAndUpperBound_ShouldReturnValidValue() {
		// arrange
		final float low = 2;
		final float high = 18;
		final float value = 10;

		// act
		final float result = MathHelper.clamp(value, low, high);

		// assert
		assertEquals(value, result, "clamp didn't return the input value as expected.");
	}

	@Test
	void clamp_ChecksValueAgainstTheLowerBound_ShouldClampToLowerBound() {
		// arrange
		final float low = 2;
		final float high = 18;
		final float value = -4;

		// act
		final float result = MathHelper.clamp(value, low, high);

		// assert
		assertEquals(low, result, "clamp failed to clamp the input to the lower bound.");
	}

	@Test
	void clamp_ChecksValueAgainstTheUpperBound_ShouldClampToUpperBound() {
		// arrange
		final float low = 2;
		final float high = 18;
		final float value = 22;

		// act
		final float result = MathHelper.clamp(value, low, high);

		// assert
		assertEquals(high, result, "clamp failed to clamp the input to the upper bound.");
	}

	// --

	@Test
	void clampd_ChecksValueAgainstLowerAndUpperBound_ShouldReturnValidValue() {
		// arrange
		final double low = 2;
		final double high = 18;
		final double value = 10;

		// act
		final double result = MathHelper.clampd(value, low, high);

		// assert
		assertEquals(value, result, "clampd didn't return the input value as expected.");
	}

	@Test
	void clampd_ChecksValueAgainstTheLowerBound_ShouldClampToLowerBound() {
		// arrange
		final double low = 2;
		final double high = 18;
		final double value = -4;

		// act
		final double result = MathHelper.clampd(value, low, high);

		// assert
		assertEquals(low, result, "clampd failed to clamp the input to the lower bound.");
	}

	@Test
	void clampd_ChecksValueAgainstTheUpperBound_ShouldClampToUpperBound() {
		// arrange
		final double low = 2;
		final double high = 18;
		final double value = 22;

		// act
		final double result = MathHelper.clampd(value, low, high);

		// assert
		assertEquals(high, result, "clampd failed to clamp the input to the upper bound.");
	}

	// --

	@Test
	void distance_ComputesDistanceBetweenTwoPositiveNumbers_ReturnsTheLengthBetweenTheNumbers() {
		// arange
		final float p0 = 100;
		final float p1 = 150;

		// act
		final float result = MathHelper.distance(p0, p1);

		// assert
		final float expected = Math.abs(p1 - p0);
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance was not as expected.");
	}

	@Test
	void distance_ComputesDistanceBetweenTwoRealNumbers_ReturnsTheLengthBetweenTheNumbers() {
		// arange
		final float p0 = -145.87f;
		final float p1 = 215.45f;

		// act
		final float result = MathHelper.distance(p0, p1);

		// assert
		final float expected = Math.abs(p1 - p0);
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance was not as expected.");
	}

	@Test
	void distance_ComputesNegativeDistanceBetweenTwoNegativeRealNumbers_ReturnsTheLengthBetweenTheInvertedNumbers() {
		// arange
		final float p0 = -645.87f;
		final float p1 = -215.45f;

		// act
		final float result = MathHelper.distance(p0, p1);

		// assert
		final float expected = Math.abs(p1 - p0);
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance was not as expected.");
	}

	@Test
	void distance_ComputesNegativeDistanceBetweenTwoInvertedInputs_ReturnsTheLengthBetweenTheInvertedNumbers() {
		// arange
		final float p0 = -645.87f;
		final float p1 = -215.45f;

		// act
		final float result = MathHelper.distance(p0, p1);

		// assert
		final float expected = Math.abs(p1 - p0);
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance was not as expected.");
	}

	@Test
	void distance_ComputesNegativeDistanceBetweenTwoRealNumbersAcrossZero_ReturnsTheLengthBetweenTheNumbers() {
		// arange
		final float p1 = -145.87f;
		final float p0 = 215.45f;

		// act
		final float result = MathHelper.distance(p0, p1);

		// assert
		final float expected = Math.abs(p1 - p0);
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance was not as expected.");
	}

	@Test
	void distance_ComputesNegativeDistanceBetweenTwoExtremeRealNumbers_ReturnsTheLengthBetweenTheNumbers() {
		// arange
		final float p0 = Float.MIN_VALUE;
		final float p1 = Float.MAX_VALUE;

		// act
		final double result = MathHelper.distance(p0, p1);

		// assert
		final double expected = Math.abs(p1 - p0);
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance was not as expected.");
	}

	@Test
	void distance_ComputesNegativeDistanceBetweenTwoExtremeRealNumbers2_ReturnsTheLengthBetweenTheNumbers() {
		// arange
		final float p0 = Float.MAX_VALUE;
		final float p1 = Float.MIN_VALUE;

		// act
		final double result = MathHelper.distance(p0, p1);

		// assert
		final double expected = Math.abs(p1 - p0);
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance was not as expected.");
	}

	// --

	@Test
	void distance2_ComputesTheDistanceSquaredBetweenTwoPositive2DPoints_ReturnsTheLengthBetweenTheVectorPositions() {
		// arrange
		final float point0_x = 10;
		final float point0_y = 10;

		final float point1_x = 120;
		final float point1_y = 120;

		// act
		final float result = MathHelper.distance2(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 24200.f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance squared was not as expected.");
	}

	@Test
	void distance2_ComputesTheDistanceSquaredBetweenTwoNegative2DPoints_ReturnsTheLengthBetweenTheVectorPositions() {
		// arrange
		final float point0_x = -210.77f;
		final float point0_y = -845.88f;

		final float point1_x = -554.14f;
		final float point1_y = -444.7f;

		// act
		final float result = MathHelper.distance2(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 278848.3493f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance squared was not as expected.");
	}

	@Test
	void distance2_ComputesTheDistanceSquaredBetweenTwoNegativeAndPositive2DPoints_ReturnsTheLengthBetweenTheVectorPositions() {
		// arrange
		final float point0_x = -210.77f;
		final float point0_y = 845.88f;

		final float point1_x = 554.14f;
		final float point1_y = -444.7f;

		// act
		final float result = MathHelper.distance2(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 2250684.0445f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance squared was not as expected.");
	}

	// --

	@Test
	void distance_ComputesTheDistanceSquaredBetweenTwoPositive2DPoints_ReturnsTheLengthBetweenTheVectorPositions() {
		// arrange
		final float point0_x = 10;
		final float point0_y = 10;

		final float point1_x = 120;
		final float point1_y = 120;

		// act
		final float result = MathHelper.distance(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 155.56349186104f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance squared was not as expected.");
	}

	@Test
	void distance_ComputesTheDistanceSquaredBetweenTwoNegative2DPoints_ReturnsTheLengthBetweenTheVectorPositions() {
		// arrange
		final float point0_x = -210.77f;
		final float point0_y = -845.88f;

		final float point1_x = -554.14f;
		final float point1_y = -444.7f;

		// act
		final float result = MathHelper.distance(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 528.06093332115f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance squared was not as expected.");
	}

	@Test
	void distance_ComputesTheDistanceSquaredBetweenTwoNegativeAndPositive2DPoints_ReturnsTheLengthBetweenTheVectorPositions() {
		// arrange
		final float point0_x = -210.77f;
		final float point0_y = 845.88f;

		final float point1_x = 554.14f;
		final float point1_y = -444.7f;

		// act
		final float result = MathHelper.distance(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 1500.2279975057f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned distance squared was not as expected.");
	}

	// ---

	@Test
	void max_TheLargerOfTheTwoNumbersIsReturned_ReturnsTheLargerNumber() {
		// arrange
		final float n0 = -210.77f;
		final float n1 = 845.88f;

		// act
		final float result = MathHelper.max(n0, n1);

		// assert
		final float expected = n1;
		assertEquals(expected, result, "The returned value was not the larger of the two.");
	}

	@Test
	void max_TheLargerOfTwoNegativeNumbersIsReturned_ReturnsTheLargerNumber() {
		// arrange
		final float n0 = -210.77f;
		final float n1 = -845.88f;

		// act
		final float result = MathHelper.max(n0, n1);

		// assert
		final float expected = n0;
		assertEquals(expected, result, "The returned value was not the larger of the two.");
	}

	// --

	@Test
	void min_TheLowerOfTheTwoNumbersIsReturned_ReturnsTheLowerNumber() {
		// arrange
		final float n0 = -210.77f;
		final float n1 = 845.88f;

		// act
		final float result = MathHelper.min(n0, n1);

		// assert
		final float expected = n0;
		assertEquals(expected, result, "The returned value was not the lower of the two.");
	}

	@Test
	void min_TheLowerOfTheTwoNegativeNumbersIsReturned_ReturnsTheLowerNumber() {
		// arrange
		final float n0 = -210.77f;
		final float n1 = -845.88f;

		// act
		final float result = MathHelper.min(n0, n1);

		// assert
		final float expected = n1;
		assertEquals(expected, result, "The returned value was not the lower of the two.");
	}

	// --

	@Test
	void floor_TheIntegerPartOfAPositiveRealNumberIsReturned_ReturnsTheIntegerPart() {
		// arange
		final float real = 210.77f;

		// act
		final int result = MathHelper.floor(real);

		// arrange
		final int expected = 210;
		assertEquals(expected, expected, result, "The returned value was not the integer part of the real number.");
	}

	@Test
	void floor_TheIntegerPartOfANegativeRealNumberIsReturned_ReturnsTheIntegerPart() {
		// arange
		final float real = -845.77f;

		// act
		final int result = MathHelper.floor(real);

		// arrange
		final int expected = -846;
		assertEquals(expected, result, "The returned value was not the integer part of the real number.");
	}

	// --

	@Test
	void round_TheLowerIntegerPartOfARealNumberIsReturned_ReturnsTheLowerIntegerPart() {
		// arange
		final float real = 85.27f;

		// act
		final int result = MathHelper.round(real);

		// arrange
		final int expected = 85;
		assertEquals(expected, result, "The returned value was not rounded down correctly.");
	}

	@Test
	void round_TheLowerIntegerPartOfANegativeRealNumberIsReturned_ReturnsTheLowerIntegerPart() {
		// arange
		final float real = -85.77f;

		// act
		final int result = MathHelper.round(real);

		// arrange
		final int expected = -86;
		assertEquals(expected, result, "The returned value was not rounded down correctly.");
	}

	@Test
	void round_TheUpperIntegerPartOfARealNumberIsReturned_ReturnsTheHigherIntegerPart() {
		// arange
		final float real = 85.827f;

		// act
		final int result = MathHelper.round(real);

		// arrange
		final int expected = 86;
		assertEquals(expected, result, "The returned value was not rounded down correctly.");
	}

	@Test
	void round_TheUpperIntegerPartOfANegativeRealNumberIsReturned_ReturnsTheHigherIntegerPart() {
		// arange
		final float real = -85.21f;

		// act
		final int result = MathHelper.round(real);

		// arrange
		final int expected = -85;
		assertEquals(expected, result, "The returned value was not rounded down correctly.");
	}

	// --

	@Test
	void abs_TheAbsoluteValueOfAPositiveInputValueIsReturnUnmodified_TheInputIsReturnUnmodified() {
		// arrange
		final float input = 45.4f;

		// act
		final float result = MathHelper.abs(input);

		// Assert
		final float expected = input;
		assertEquals(expected, result, "The expected value was not returned (the input was already positive).");
	}

	@Test
	void abs_TheAbsoluteValueOfANegativeInputValueIsReturnUnmodified_TheAbsoluteValueOfTheInputIsReturned() {
		// arrange
		final float input = -845.87f;

		// act
		final float result = MathHelper.abs(input);

		// Assert
		final float expected = 845.87f;
		assertEquals(expected, result, "The expected value was not returned.");
	}

	@Test
	void abs_TheAbsoluteValueOfZero_ZeroShouldBeReturned() {
		// arrange
		final float input0 = 0f;
		final float input1 = -0f;

		// act
		final float result0 = MathHelper.abs(input0);
		final float result1 = MathHelper.abs(input1);

		// Assert
		final float expected0 = 0f;
		final float expected1 = -0f;

		assertEquals(expected0, result0, "The expected value was not returned.");
		assertEquals(expected1, result1, "The expected value was not returned.");
	}

	// --

	@Test
	void toDegrees_TheInputRadiansAreConvertedToDegrees_TheDegreeEquivalentIsReturned() {
		// arrange
		final float radiansToConvert = 4.5f;

		// act
		final float result = MathHelper.toDegrees(radiansToConvert);

		// assert
		final float expected = 257.831008f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned value was not correct.");
	}

	@Test
	void toDegrees_TheInputRadiansHigherThanPIAreConvertedToDegrees_TheDegreeEquivalentIsReturned() {
		// arrange
		final float radiansToConvert = 45.2f;

		// act
		final float result = MathHelper.toDegrees(radiansToConvert);

		// assert
		final float expected = 2589.769234f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned value was not correct.");
	}

	@Test
	void toDegrees_TheNegativeInputRadiansAreConvertedToDegrees_TheDegreeEquivalentIsReturned() {
		// arrange
		final float radiansToConvert = -2.2f;

		// act
		final float result = MathHelper.toDegrees(radiansToConvert);

		// assert
		final float expected = -126.050715f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned value was not correct.");
	}

	@Test
	void toDegrees_TheZeroInputRadiansIsHandledCorrectly_TheDegreeEquivalentIsReturned() {
		// arrange
		final float radiansToConvert = 0f;

		// act
		final float result = MathHelper.toDegrees(radiansToConvert);

		// assert
		final float expected = 0f;
		assertEquals(expected, result, "The returned value was not correct.");
	}

	// --

	@Test
	void toRadians_TheInputDegreesAreConvertedToRadians_TheRadianEquivalentIsReturned() {
		// arrange
		final float degreesToConvert = 4.5f;

		// act
		final float result = MathHelper.toRadians(degreesToConvert);

		// assert
		final float expected = 0.0785398f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned value was not correct.");
	}

	@Test
	void toRadians_TheInputDegreesHigherThan360AreConvertedToRadians_TheRadianEquivalentIsReturned() {
		// arrange
		final float degreesToConvert = 458.2f;

		// act
		final float result = MathHelper.toRadians(degreesToConvert);

		// assert
		final float expected = 7.9970986f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned value was not correct.");
	}

	@Test
	void toRadians_TheNegativeInputRadiansAreConvertedToDegrees_TheRadianEquivalentIsReturned() {
		// arrange
		final float degreesToConvert = -2.2f;

		// act
		final float result = MathHelper.toRadians(degreesToConvert);

		// assert
		final float expected = -0.03839724f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned value was not correct.");
	}

	@Test
	void toRadians_TheZeroInputRadiansIsHandledCorrectly_TheRadianEquivalentIsReturned() {
		// arrange
		final float degreesToConvert = 0f;

		// act
		final float result = MathHelper.toRadians(degreesToConvert);

		// assert
		final float expected = 0f;
		assertEquals(expected, result, "The returned value was not correct.");
	}

	// --

	@Test
	void wrapAngle_TheInputAnglesWithinNegativePiToPositivePIAreReturnUnmodified_TheUnmodifiedAnglesAreReturned() {
		// arrange
		final float a0 = 1.1f;
		final float a1 = 0.f;
		final float a2 = (float) Math.PI - 0.1f;

		// act
		final float result0 = MathHelper.wrapAngle(a0);
		final float result1 = MathHelper.wrapAngle(a1);
		final float result2 = MathHelper.wrapAngle(a2);

		// asert
		assertEquals(a0, result0, "The returned value was not correct.");
		assertEquals(a1, result1, "The returned value was not correct.");
		assertEquals(a2, result2, "The returned value was not correct.");
	}

	@Test
	void wrapAngle_TheInputAnglesAbovePiAreWrapped_TheWrappedAnglesAreReturned() {
		// arrange
		final float a0 = (float) (Math.PI + 1.1);
		final float a1 = (float) (Math.PI);
		final float a2 = (float) (Math.PI * 3. + .2);

		// act
		final float result0 = MathHelper.wrapAngle(a0);
		final float result1 = MathHelper.wrapAngle(a1);
		final float result2 = MathHelper.wrapAngle(a2);

		// asert
		assertEquals(-2.041592f, result0, 3 * Math.ulp(result0), "The returned value was not correct.");
		assertEquals(-Math.PI, result1, 3 * Math.ulp(result1), "The returned value was not correct.");
		assertEquals(-2.941593f, result2, 3 * Math.ulp(result2), "The returned value was not correct.");
	}

	@Test
	void wrapAngle_TheInputAnglesBelowPiAreWrapped_TheWrappedAnglesAreReturned() {
		// arrange
		final float a0 = (float) (-Math.PI - 1.1);
		final float a1 = (float) (-Math.PI);
		final float a2 = (float) (-Math.PI * 3. - .2);

		// act
		final float result0 = MathHelper.wrapAngle(a0);
		final float result1 = MathHelper.wrapAngle(a1);
		final float result2 = MathHelper.wrapAngle(a2);

		// asert
		assertEquals(2.041592f, result0, 3 * Math.ulp(result0), "The returned value was not correct.");
		assertEquals(Math.PI, result1, 3 * Math.ulp(result1), "The returned value was not correct.");
		assertEquals(2.941593f, result2, 3 * Math.ulp(result2), "The returned value was not correct.");
	}

	// --

	@Test
	void even_PositiveIntegersAreIdentifiedCorrectlyAsEven_TrueIsReturned() {
		// arrange
		final int a0 = 154;
		final int a1 = 57;
		final int a2 = 56;
		final int a3 = 754;
		final int a4 = 121;
		final int a5 = 0;

		// act
		final boolean result0 = MathHelper.isEven(a0);
		final boolean result1 = MathHelper.isEven(a1);
		final boolean result2 = MathHelper.isEven(a2);
		final boolean result3 = MathHelper.isEven(a3);
		final boolean result4 = MathHelper.isEven(a4);
		final boolean result5 = MathHelper.isEven(a5);

		// asert
		assertEquals(true, result0, "The returned value was not correct.");
		assertEquals(false, result1, "The returned value was not correct.");
		assertEquals(true, result2, "The returned value was not correct.");
		assertEquals(true, result3, "The returned value was not correct.");
		assertEquals(false, result4, "The returned value was not correct.");
		assertEquals(true, result5, "The returned value was not correct.");
	}

	@Test
	void even_NegativeIntegersAreIdentifiedCorrectlyAsEven_TrueIsReturned() {
		// arrange
		final int a0 = -154;
		final int a1 = -57;
		final int a2 = -56;
		final int a3 = -754;
		final int a4 = -121;

		// act
		final boolean result0 = MathHelper.isEven(a0);
		final boolean result1 = MathHelper.isEven(a1);
		final boolean result2 = MathHelper.isEven(a2);
		final boolean result3 = MathHelper.isEven(a3);
		final boolean result4 = MathHelper.isEven(a4);

		// asert
		assertEquals(true, result0, "The returned value was not correct.");
		assertEquals(false, result1, "The returned value was not correct.");
		assertEquals(true, result2, "The returned value was not correct.");
		assertEquals(true, result3, "The returned value was not correct.");
		assertEquals(false, result4, "The returned value was not correct.");
	}

	// --

	@Test
	void odd_PositiveIntegersAreIdentifiedCorrectlyAsEven_TrueIsReturned() {
		// arrange
		final int a0 = 154;
		final int a1 = 57;
		final int a2 = 56;
		final int a3 = 754;
		final int a4 = 121;
		final int a5 = 0;

		// act
		final boolean result0 = MathHelper.isOdd(a0);
		final boolean result1 = MathHelper.isOdd(a1);
		final boolean result2 = MathHelper.isOdd(a2);
		final boolean result3 = MathHelper.isOdd(a3);
		final boolean result4 = MathHelper.isOdd(a4);
		final boolean result5 = MathHelper.isOdd(a5);

		// asert
		assertEquals(false, result0, "The returned value was not correct.");
		assertEquals(true, result1, "The returned value was not correct.");
		assertEquals(false, result2, "The returned value was not correct.");
		assertEquals(false, result3, "The returned value was not correct.");
		assertEquals(true, result4, "The returned value was not correct.");
		assertEquals(false, result5, "The returned value was not correct.");
	}

	@Test
	void odd_NegativeIntegersAreIdentifiedCorrectlyAsEven_TrueIsReturned() {
		// arrange
		final int a0 = -154;
		final int a1 = -57;
		final int a2 = -56;
		final int a3 = -754;
		final int a4 = -121;

		// act
		final boolean result0 = MathHelper.isOdd(a0);
		final boolean result1 = MathHelper.isOdd(a1);
		final boolean result2 = MathHelper.isOdd(a2);
		final boolean result3 = MathHelper.isOdd(a3);
		final boolean result4 = MathHelper.isOdd(a4);

		// asert
		assertEquals(false, result0, "The returned value was not correct.");
		assertEquals(true, result1, "The returned value was not correct.");
		assertEquals(false, result2, "The returned value was not correct.");
		assertEquals(false, result3, "The returned value was not correct.");
		assertEquals(true, result4, "The returned value was not correct.");
	}

	// --

	@Test
	void dot_CalculateDotProductBetweenTwoPositiveVectors_DotProductIsReturned() {
		// arrange
		final float point0_x = 10.f;
		final float point0_y = 10.f;

		final float point1_x = 56.f;
		final float point1_y = 57.f;

		// act
		final float result = MathHelper.dot(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 1130;
		assertEquals(expected, result, "The resulting dot product is not correct.");
	}

	@Test
	void dot_CalculateDotProductBetweenTwoNegativeVectors_DotProductIsReturned() {
		// arrange
		final float point0_x = -84.f;
		final float point0_y = -12.f;

		final float point1_x = -13.f;
		final float point1_y = -4.f;

		// act
		final float result = MathHelper.dot(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = 1140;
		assertEquals(expected, result, "The resulting dot product is not correct.");
	}

	@Test
	void dot_CalculateDotProductBetweenTwoVectorsAcrossOrigin_DotProductIsReturned() {
		// arrange
		final float point0_x = -84.f;
		final float point0_y = 13.f;

		final float point1_x = 13.f;
		final float point1_y = -8.f;

		// act
		final float result = MathHelper.dot(point0_x, point0_y, point1_x, point1_y);

		// assert
		final float expected = -1196;
		assertEquals(expected, result, "The resulting dot product is not correct.");
	}

	// --

	@Test
	void cross_() {
		// TODO: Unfinished unit test for 2d cross product

	}

	// --

	@Test
	void scaleToRange_AValueWithinTheNewBoundsIsCorrectlyScaled_TheScaledValueIsReturned() {
		// arrange
		final float oldMin = 0.f;
		final float oldMax = 100.f;
		final float newMin = 0.f;
		final float newMax = 10.f;
		final float value = 10.f;

		// act
		final float result = MathHelper.scaleToRange(value, oldMin, oldMax, newMin, newMax);

		// asert
		final float expected = 1.f;
		assertEquals(expected, result, "The returned scaled value was not correct.");
	}

	@Test
	void scaleToRange_AValueWithinTheNewBoundsIsCorrectlyScaledWhenTheScaledAreNegative_TheScaledValueIsReturned() {
		// arrange
		final float oldMin = -50.f;
		final float oldMax = 100.f;
		final float newMin = 0.f;
		final float newMax = 25.f;
		final float value = 0.f;

		// act
		final float result = MathHelper.scaleToRange(value, oldMin, oldMax, newMin, newMax);

		// asert
		final float expected = 8.33333f;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned scaled value was not correct.");
	}

	@Test
	void scaleToRange_AValueOutsideLowerBoundIsCorrectlyMatchToNewMin_TheScaledValueIsReturned() {
		// arrange
		final float oldMin = -50.f;
		final float oldMax = 100.f;
		final float newMin = 0.f;
		final float newMax = 25.f;
		final float value = -60.f;

		// act
		final float result = MathHelper.scaleToRange(value, oldMin, oldMax, newMin, newMax);

		// asert
		final float expected = newMin;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned scaled value was not correct.");
	}
	
	@Test
	void scaleToRange_AValueOutsideUpperBoundIsCorrectlyMatchToNewMax_TheScaledValueIsReturned() {
		// arrange
		final float oldMin = -50.f;
		final float oldMax = 120.f;
		final float newMin = 0.f;
		final float newMax = 25.f;
		final float value = 140.f;

		// act
		final float result = MathHelper.scaleToRange(value, oldMin, oldMax, newMin, newMax);

		// asert
		final float expected = newMax;
		assertEquals(expected, result, 3 * Math.ulp(expected), "The returned scaled value was not correct.");
	}

}
