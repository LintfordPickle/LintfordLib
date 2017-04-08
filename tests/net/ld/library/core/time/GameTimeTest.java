package net.ld.library.core.time;

import org.junit.Test;

/** Tests the {@link GameTime} implementation of the LDLibrary core */
public class GameTimeTest {

	/**
	 * Test that the {@link GameTime} class correctly initializes the
	 * elapsedTime and totalGameTime to zero.
	 */
	@Test
	public void creationTest() {
		// Arrange
		GameTime lGameTime = new GameTime();

		// Assert
		assert (lGameTime.elapseGameTime() == 0) : "Elapsed time isn't initialised with 0";
		assert (lGameTime.totalGameTime() == 0) : "Total time isn't initialised with 0";
	}

	/** Tests the progression of elapsed time in the {@link GameTime} class. */
	@Test
	public void elapsedTimeTest() {
		// Arrange
		GameTime lGameTime = new GameTime();

		// Act
		long lTimeBefore = System.nanoTime();
		lGameTime.update();
		long lTimeAfter = System.nanoTime();

		// Assert
		assert (lGameTime.elapseGameTime() > 0) : "Elapsed time hasn't progressed time";
		assert (lGameTime.elapseGameTime() < (lTimeAfter - lTimeBefore)) : "Elapsed time took too lng";

	}

	/**
	 * Tests the progression of total game time in the {@link GameTime} class.
	 */
	@Test
	public void totalGameTimeTest() {
		// Arrange
		GameTime lGameTime = new GameTime();
		double timer = lGameTime.totalGameTime();

		// Act
		lGameTime.update();
		timer += lGameTime.elapseGameTime();

		// Check after a single update of the GameTime instance
		assert (lGameTime.totalGameTime() == timer) : "The total game time doesn't match the amount of time elapsed";

		lGameTime.update();
		timer += lGameTime.elapseGameTime();

		lGameTime.update();
		timer += lGameTime.elapseGameTime();

		lGameTime.update();
		timer += lGameTime.elapseGameTime();

		// Assert
		// Check after multiple calls of the update method (the cumulative
		// effect of time)
		assert (lGameTime.totalGameTime() == timer) : "The total game time doesn't match the amount of time elapsed";

	}

}
