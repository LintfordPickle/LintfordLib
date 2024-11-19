package tests.net.lintfordlib.screenmanager.entries.animators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.entries.animators.ScaleAnimator;

class ScaleAnimatorTests {

	private static MenuEntry mockEntry;

	@BeforeAll
	public static void setup() {
		mockEntry = Mockito.spy(new MenuEntry(null, null, null));
	}

	// --------------------------------------
	// Tests
	// --------------------------------------

	@Test
	void animate_ScaleValueIsLoopable_ScaleShouldBe1WhenAnimationIsFinished() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(1000);

		// act
		sut.animate(mockEntry, 1000);

		// assert
		assertEquals(1.f, mockEntry.scale(), "Scale was not as expected.");
	}

	@Test
	void animate_ScaleValueIsLoopable_ScaleShouldBe1WhenAnimationIsStarted() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(1000);

		// act
		sut.animate(mockEntry, 0);

		// assert
		assertEquals(1.f, mockEntry.scale(), "Scale was not as expected.");
	}

	@Test
	void animate_ScaleValueIsLoopable_ScaleShouldBeAsStartedWhenNegativeElapsedTimeProvided() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(1000);

		// act
		sut.animate(mockEntry, -100);

		// assert
		assertEquals(1.f, mockEntry.scale(), "Scale was not as expected.");
	}

	@Test
	void animate_ScaleValueIsProperlySet_ScaleShouldBeHalfWhenTimeHalfIsReached() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(1000);
		sut.magnitude(1.f);

		// act
		sut.animate(mockEntry, 500);

		// assert
		assertEquals(2.f, mockEntry.scale(), 2 * Math.ulp(.5f), "Scale was not as expected.");
	}

	@Test
	void animate_ScaleMagnitudeIsCorrectlyUsed_TheScaleAnimatorCorrecltyAppliesTheMagnitude() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(1000);
		sut.magnitude(2.f);

		// act
		sut.animate(mockEntry, 500);

		// assert
		assertEquals(3.f, mockEntry.scale(), 2 * Math.ulp(.5f), "Scale was not as expected.");
	}

	@Test
	void animate_ScaleValueIsProperlySetAccordingToAnimationLength_ScaleShouldBeeQuarterWayWhenQuarterTimeHalfIsReached() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(2000);
		sut.magnitude(1.f);

		// act
		sut.animate(mockEntry, 500);

		// assert
		assertEquals(1.5f, mockEntry.scale(), 2 * Math.ulp(.5f), "Scale was not as expected.");
	}

	// ---

	@Test
	void normlizeRuningTime_ReturnsTheNormalizedRunningTime_ReturnValueShouldBeEqual0IfNegativeInput() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(2000);
		sut.magnitude(1.f);

		// act
		final var result = sut.normalizedRunningTime(-500);

		// assert
		assertEquals(0f, result, 2 * Math.ulp(.5f), "NormalizedRuningTime was not as expected.");
	}
	
	@Test
	void normlizeRuningTime_ReturnsTheNormalizedRunningTime_ReturnValueShouldBeEqual1IfHigherThanUpperBoundInput() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(2000);
		sut.magnitude(1.f);

		// act
		final var result = sut.normalizedRunningTime(4700);

		// assert
		assertEquals(1f, result, 2 * Math.ulp(.5f), "NormalizedRuningTime was not as expected.");
	}
	
	@Test
	void normlizeRuningTime_ReturnsTheNormalizedRunningTime_ReturnValueShouldBeQuarterIfQuartterTimeElapsed() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(2000);
		sut.magnitude(1.f);

		// act
		final var result = sut.normalizedRunningTime(500);

		// assert
		assertEquals(.25f, result, 2 * Math.ulp(.5f), "NormalizedRuningTime was not as expected.");
	}
	
	@Test
	void normlizeRuningTime_ReturnsTheNormalizedRunningTime_ReturnValueShouldBeCorrectPercentageOfAnimationLength() {
		// arrange
		final var sut = new ScaleAnimator();
		sut.animationLengthMs(10000);

		// act
		final var result = sut.normalizedRunningTime(9500);

		// assert
		assertEquals(.95f, result, 2 * Math.ulp(.5f), "NormalizedRuningTime was not as expected.");
	}
}
