package net.ld.library.core.time;

import org.junit.Test;

/** Tests the {@link GameTime} implementation of the LDLibrary core */
public class GameTimeTest {

	/** Tests the progression of time in the {@link GameTime} class. */
	@Test
	public void elapsedTimeTest() {
		// Assign
		GameTime lGameTime = new GameTime();

		// Act
		long lTimeBefore = System.nanoTime();
		lGameTime.update();
		long lTimeAfter = System.nanoTime();

		// Assert
		assert (lGameTime.elapseGameTime() > 0) : "Elapsed time hasn't progressed time";
		assert (lGameTime.elapseGameTime() < (lTimeAfter - lTimeBefore)) : "Elapsed time took too lng";

	}

}
