package net.lintfordlib.core.maths;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;

class RandomNumbersTests {

	@RepeatedTest(10)
	void random_ReturnsARandomBoundedIntValue_ShouldReturnAnIntWithinTheSpecifiedBounds() {
		// arrange
		final int low = 2;
		final int high = 18;

		// act
		final int result = RandomNumbers.random(low, high);

		// assert
		assertTrue(result >= low, "Random integer value was lower than low bound.");
		assertTrue(result < high, "Random integer value was higher than high bound.");

	}

	@RepeatedTest(10)
	void random_ReturnsARandomBoundedFloatValue_ShouldReturnAFloatWithinTheSpecifiedBounds() {
		// arrange
		final float low = 2.f;
		final float high = 18.f;

		// act
		final float result = RandomNumbers.random(low, high);

		// assert
		assertTrue(result >= low, "Random float value was lower than low bound.");
		assertTrue(result < high, "Random float value was higher than high bound.");

	}
}
